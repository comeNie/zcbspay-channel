/* 
 * WithholdingProducer.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.unionpay;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.google.common.base.Charsets;
import com.zcbspay.platform.channel.unionpay.bean.ResultBean;
import com.zcbspay.platform.channel.unionpay.enums.WithholdingTagsEnum;
import com.zcbspay.platform.channel.unionpay.interfaces.Producer;
import com.zcbspay.platform.channel.unionpay.redis.RedisFactory;
import com.zcbspay.platform.channel.unionpay.util.DateStyle;
import com.zcbspay.platform.channel.unionpay.util.DateUtil;

/**
 * 银联代扣生产者
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午9:57:27
 * @since
 */
public class WithholdingProducer implements Producer {

    private final static Logger logger = LoggerFactory.getLogger(WithholdingProducer.class);
    private static final String KEY = "UPWITHHOLDING:";
    private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("producer_unionpay");

    // RocketMQ消费者客户端
    private DefaultMQProducer producer;
    // 主题
    private String topic;
    private WithholdingTagsEnum tags;

    public WithholdingProducer(String namesrvAddr, WithholdingTagsEnum tags) throws MQClientException {
        logger.info("【初始化WithholdingProducer】");
        logger.info("【namesrvAddr】" + namesrvAddr);
        producer = new DefaultMQProducer(RESOURCE.getString("unionpay.withholding.producer.group"));
        producer.setNamesrvAddr(namesrvAddr);
        Random random = new Random();
        producer.setInstanceName(RESOURCE.getString("unionpay.withholding.instancename") + random.nextInt(9999));
        topic = RESOURCE.getString("unionpay.withholding.subscribe");
        this.tags = tags;
        logger.info("【初始化SimpleOrderProducer结束】");
    }

    /**
     *
     * @param message
     * @return
     * @throws MQClientException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQBrokerException
     */
    @Override
    public SendResult sendJsonMessage(String message, Object tags) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        if (producer == null) {
            throw new MQClientException(-1, "SimpleOrderProducer为空");
        }
        producer.start();
        WithholdingTagsEnum withholdingTagsEnum = (WithholdingTagsEnum) tags;
        Message msg = new Message(topic, withholdingTagsEnum.getCode(), message.getBytes(Charsets.UTF_8));
        SendResult sendResult = producer.send(msg);
        return sendResult;
    }

    /**
	 *
	 */
    @Override
    public void closeProducer() {
        producer.shutdown();
        producer = null;
    }

    /**
     *
     * @param sendResult
     * @return
     */
    @Override
    public ResultBean queryReturnResult(SendResult sendResult) {
        logger.info("【SimpleOrderCallback receive Result message】{}", JSON.toJSONString(sendResult));
        logger.info("msgID:{}", sendResult.getMsgId());

        for (int i = 0; i < 100; i++) {
            String json = getJsonByCycle(sendResult.getMsgId());
            logger.info("从redis中取得key【{}】值为{}", KEY + sendResult.getMsgId(), json);
            if (StringUtils.isNotEmpty(json)) {
                ResultBean resultBean = JSON.parseObject(json, ResultBean.class);

                logger.info("msgID:{},结果数据:{}", sendResult.getMsgId(), JSON.toJSONString(resultBean));
                return resultBean;
            }
            else {
                try {
                    TimeUnit.MILLISECONDS.sleep(900);
                }
                catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.info("end time {}", System.currentTimeMillis());
        return null;
    }

    private String getJsonByCycle(String msgId) {
        Jedis jedis = RedisFactory.getInstance().getRedis();
        // String tn = jedis.get(KEY+msgId);
        List<String> brpop = jedis.brpop(40, KEY + msgId);
        if (brpop.size() > 0) {
            String tn = brpop.get(1);
            if (StringUtils.isNotEmpty(tn)) {
                return tn;
            }
        }
        jedis.close();
        return null;
    }

    public static void main(String[] args) {
        String namesrvAddr = "192.168.1.26:9876";
        testWithholding(namesrvAddr);
        // testQueryWthDrh(namesrvAddr);
        // testApplyAccChecking(namesrvAddr);
    }

    private static void testApplyAccChecking(String namesrvAddr) {
        WithholdingTagsEnum tags = WithholdingTagsEnum.fromValue("TAG_009");
        WithholdingProducer producer = null;
        try {
            producer = new WithholdingProducer(namesrvAddr, tags);
            String message = "{\"queryDt\":\"20170221\",\"transDt\":\"20170221\"}";
            SendResult sendResult = producer.sendJsonMessage(message, tags);
            TimeUnit.MILLISECONDS.sleep(5000);
            ResultBean resultBean = producer.queryReturnResult(sendResult);
            System.out.println("===result bean:" + resultBean.toString());
        }
        catch (MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
            e.printStackTrace();
        }
    }

    private static void testWithholding(String namesrvAddr) {
        WithholdingTagsEnum tags = WithholdingTagsEnum.fromValue("TAG_001");
        WithholdingProducer producer = null;
        try {
            producer = new WithholdingProducer(namesrvAddr, tags);
            String message = "{\"txnseqno\":\"test0222002\",\"cardNo\":\"6228480018543668977\",\"acctName\":\"马小明\",\"mobile\":\"13910249966\",\"certId\":\"110112198706266666\",\"amount\":1001}";
            SendResult sendResult = producer.sendJsonMessage(message, tags);
            TimeUnit.MILLISECONDS.sleep(5000);
            ResultBean resultBean = producer.queryReturnResult(sendResult);
            System.out.println("===result bean:" + resultBean.toString());
        }
        catch (MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
            e.printStackTrace();
        }
    }

    private static void testQueryWthDrh(String namesrvAddr) {
        WithholdingTagsEnum tags = WithholdingTagsEnum.fromValue("TAG_004");
        WithholdingProducer producer = null;
        try {
            producer = new WithholdingProducer(namesrvAddr, tags);
            String message = "{\"refOrderId\":\"1702219900000047\"}";
            SendResult sendResult = producer.sendJsonMessage(message, tags);
            TimeUnit.MILLISECONDS.sleep(5000);
            ResultBean resultBean = producer.queryReturnResult(sendResult);
            System.out.println("===result bean:" + resultBean.toString());
        }
        catch (MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
            e.printStackTrace();
        }
    }

}

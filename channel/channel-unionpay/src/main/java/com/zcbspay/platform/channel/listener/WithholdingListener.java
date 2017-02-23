/* 
 * WithholdingListener.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.listener;

import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.zcbspay.platform.channel.common.bean.ApplyAccCheckUP;
import com.zcbspay.platform.channel.common.bean.QueryTradeBeanUP;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;
import com.zcbspay.platform.channel.enums.WithholdingTagsEnum;
import com.zcbspay.platform.channel.unionpay.withholding.withholding.service.WithholdingCacheResultService;
import com.zcbspay.platform.channel.unionpay.withholding.withholding.service.ZcbsToUnionPayWithholdingService;

/**
 * 民生代扣监听器
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午10:52:26
 * @since
 */
@Service("withholdingListener")
public class WithholdingListener implements MessageListenerConcurrently {

    private static final Logger log = LoggerFactory.getLogger(WithholdingListener.class);
    private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("consumer_unionpay");
    private static final String KEY = "UPWITHHOLDING:";

    @Autowired
    private ZcbsToUnionPayWithholdingService zcbsToUnionPayWithholdingService;
    @Autowired
    private WithholdingCacheResultService withholdingCacheResultService;

    /**
     * @param msgs
     * @param context
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        String json = null;
        log.info("======msgs:" + msgs.toString());
        for (MessageExt msg : msgs) {
            if (msg.getTopic().equals(RESOURCE.getString("unionpay.withholding.subscribe"))) {
                WithholdingTagsEnum withholdingTagsEnum = WithholdingTagsEnum.fromValue(msg.getTags());
                if (withholdingTagsEnum == WithholdingTagsEnum.WITHHOLDING) {
                    // 代扣
                    json = new String(msg.getBody(), Charsets.UTF_8);
                    log.info("接收到的MSG:" + json);
                    log.info("接收到的MSGID:" + msg.getMsgId());
                    TradeBean tradeBean = JSON.parseObject(json, TradeBean.class);
                    if (tradeBean == null) {
                        log.warn("MSGID:{}JSON转换后为NULL,无法生成订单数据,原始消息数据为{}", msg.getMsgId(), json);
                        break;
                    }
                    ResultBean resultBean = null;
                    try {
                        resultBean = zcbsToUnionPayWithholdingService.withholding(tradeBean);
                        withholdingCacheResultService.saveWithholdingResult(KEY + msg.getMsgId(), JSON.toJSONString(resultBean));
                    }
                    catch (Throwable e) {
                        log.error(e.getMessage(), e);
                        resultBean = new ResultBean("T000", e.getLocalizedMessage());
                    }
                }else if(withholdingTagsEnum == WithholdingTagsEnum.QUERY_TRADE){
                    // 代扣-查询
                    json = new String(msg.getBody(), Charsets.UTF_8);
                    log.info("接收到的MSG:" + json);
                    log.info("接收到的MSGID:" + msg.getMsgId());
                    QueryTradeBeanUP qryTrdBeanUP = JSON.parseObject(json, QueryTradeBeanUP.class);
                    if (qryTrdBeanUP == null) {
                        log.warn("MSGID:{}JSON转换后为NULL,无法进行代扣建议查询", msg.getMsgId(), json);
                        break;
                    }
                    ResultBean resultBean = null;
                    try {
                        resultBean = zcbsToUnionPayWithholdingService.queryTrade(qryTrdBeanUP);
                        withholdingCacheResultService.saveWithholdingResult(KEY + msg.getMsgId(), JSON.toJSONString(resultBean));
                    }
                    catch (Throwable e) {
                        log.error(e.getMessage(), e);
                        resultBean = new ResultBean("T000", e.getLocalizedMessage());
                    }
                }else if(withholdingTagsEnum == WithholdingTagsEnum.APPLY_ACC_CHECK){
                    // 申请下载对账文件
                    json = new String(msg.getBody(), Charsets.UTF_8);
                    log.info("接收到的MSG:" + json);
                    log.info("接收到的MSGID:" + msg.getMsgId());
                    ApplyAccCheckUP applyAccCheckUP = JSON.parseObject(json, ApplyAccCheckUP.class);
                    if (applyAccCheckUP == null) {
                        log.warn("MSGID:{}JSON转换后为NULL,无法进行代扣建议查询", msg.getMsgId(), json);
                        break;
                    }
                    ResultBean resultBean = null;
                    try {
                        resultBean = zcbsToUnionPayWithholdingService.applyAccChecking(applyAccCheckUP);
                        withholdingCacheResultService.saveWithholdingResult(KEY + msg.getMsgId(), JSON.toJSONString(resultBean));
                    }
                    catch (Throwable e) {
                        log.error(e.getMessage(), e);
                        resultBean = new ResultBean("T000", e.getLocalizedMessage());
                    }
                }
            }
            else {
                log.error("RocketMQ银联通道无指定的tag");
            }
            log.info(Thread.currentThread().getName() + " Receive New Messages: " + msgs);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

}

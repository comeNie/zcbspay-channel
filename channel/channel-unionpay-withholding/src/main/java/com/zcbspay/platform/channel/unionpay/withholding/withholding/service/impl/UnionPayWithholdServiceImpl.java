package com.zcbspay.platform.channel.unionpay.withholding.withholding.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.unionpay.dk.vo.MerchantResponse;
import com.unionpay.dk.vo.QueryResponse;
import com.unionpay.dk.vo.RspRoot;
import com.unionpay.dk.webservice.TranWebService;
import com.zcbspay.platform.channel.common.bean.AlyAccChkRespBean;
import com.zcbspay.platform.channel.common.bean.ApplyAccCheckUP;
import com.zcbspay.platform.channel.common.bean.QueryTradeBeanUP;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.ReturnInfo;
import com.zcbspay.platform.channel.common.bean.TradeBeanUP;
import com.zcbspay.platform.channel.common.bean.TxnsLogUpBean;
import com.zcbspay.platform.channel.common.bean.unionpay.DownloadRequest;
import com.zcbspay.platform.channel.common.bean.unionpay.DownloadResponse;
import com.zcbspay.platform.channel.common.bean.unionpay.DwnReqRoot;
import com.zcbspay.platform.channel.common.bean.unionpay.MerchantRequest;
import com.zcbspay.platform.channel.common.bean.unionpay.QReqRoot;
import com.zcbspay.platform.channel.common.bean.unionpay.QueryRequest;
import com.zcbspay.platform.channel.common.bean.unionpay.ReqRoot;
import com.zcbspay.platform.channel.common.enums.UPRespInfo;
import com.zcbspay.platform.channel.common.enums.UPRespStatus;
import com.zcbspay.platform.channel.unionpay.withholding.dao.TxnsUnionPayDao;
import com.zcbspay.platform.channel.unionpay.withholding.enums.ErrorCodeUP;
import com.zcbspay.platform.channel.unionpay.withholding.enums.FactorId;
import com.zcbspay.platform.channel.unionpay.withholding.enums.TransType;
import com.zcbspay.platform.channel.unionpay.withholding.exception.UnionPayException;
import com.zcbspay.platform.channel.unionpay.withholding.pojo.PojoTxnsLogUp;
import com.zcbspay.platform.channel.unionpay.withholding.service.ChlSeqNumCheckService;
import com.zcbspay.platform.channel.unionpay.withholding.service.ChlSeqNumRecService;
import com.zcbspay.platform.channel.unionpay.withholding.service.SerialNumberService;
import com.zcbspay.platform.channel.unionpay.withholding.utils.CertHelper;
import com.zcbspay.platform.channel.unionpay.withholding.utils.DateStyle;
import com.zcbspay.platform.channel.unionpay.withholding.utils.DateTimeUtils;
import com.zcbspay.platform.channel.unionpay.withholding.utils.DateUtil;
import com.zcbspay.platform.channel.unionpay.withholding.utils.ParamsUtil;
import com.zcbspay.platform.channel.unionpay.withholding.utils.RespStatusUtil;
import com.zcbspay.platform.channel.unionpay.withholding.utils.XMLUtils;
import com.zcbspay.platform.channel.unionpay.withholding.withholding.service.UnionPayWithholdService;

@Service("unionPayWithholdService")
public class UnionPayWithholdServiceImpl implements UnionPayWithholdService {

    private static final Logger logger = LoggerFactory.getLogger(UnionPayWithholdServiceImpl.class);

    @Autowired
    private ChlSeqNumCheckService chlSeqNumCheckService;
    @Autowired
    private ChlSeqNumRecService chlSeqNumRecService;
    @Autowired
    TxnsUnionPayDao txnsUnionPayDao;
    @Autowired
    private SerialNumberService serialNumberService;

    @Override
    public ResultBean withholding(TradeBeanUP tradeBean) {
        ResultBean resultBean = null;
        String orderId = null;
        try {
            // 判断是否重复代扣
            chlSeqNumCheckService.isRepeatRequest(tradeBean.getTxnseqno());
            // 登记银联代扣流水信息
            tradeBean.setFactorId(FactorId.CARDNUM_NAME_ID_PHO.getValue());
            tradeBean.setTransType(TransType.WITHDRAW.getValue());
            PojoTxnsLogUp pojoTxnsLogUp = new PojoTxnsLogUp();
            BeanUtils.copyProperties(tradeBean, pojoTxnsLogUp);
            orderId = chlSeqNumRecService.recordSeqNum(pojoTxnsLogUp);
            // 调用银联代扣接口
            resultBean = withholdingToUnionPay(tradeBean, orderId);
            // 同步轮训查询结果
            String tradeStatus = RespStatusUtil.getTradeStatus(resultBean);
            if (UPRespStatus.UNKNOWN.getValue().equals(tradeStatus)) {
                resultBean = cycleSysStatus(orderId, tradeBean.getTransTm());
            }
        }
        catch (UnionPayException e) {
            logger.error(e.getMessage(), e);
            // 非轮训时出现的异常直接返回错误信息
            if (!ErrorCodeUP.INTERRUPT_EXP.getValue().equals(e.getErrCode())) {
                resultBean = new ResultBean(e.getErrCode(), e.getErrMsg());
                return resultBean;
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultBean = new ResultBean(UPRespInfo.UNKNOWN_TIMEOUT.getValue(), UPRespInfo.UNKNOWN_TIMEOUT.getDisplayName());
        }
        // 更新代扣水流信息
        chlSeqNumRecService.updateSeqNumStatusWithhold(orderId, resultBean);
        return resultBean;
    }

    /**
     * 轮训同步代扣交易状态，时间以2的幂次递增，不超过40s
     * 
     * @param orderId
     * @param transTm
     * @return
     * @throws UnionPayException
     */
    @SuppressWarnings("unchecked")
    private ResultBean cycleSysStatus(String orderId, String transTm) throws UnionPayException {
        QueryTradeBeanUP queryTradeBean = new QueryTradeBeanUP();
        queryTradeBean.setRefOrderId(orderId);
        queryTradeBean.setTransTm(transTm);
        ResultBean resultBean = null;
        String status = null;
        int time = 2000;
        int cycTimes = 1;
        try {
            do {
                TimeUnit.MILLISECONDS.sleep(time);
                resultBean = queryTradeToUnionPay(queryTradeBean);
                List<ReturnInfo> retInfos = (List<ReturnInfo>) resultBean.getResultObj();
                String repCode = retInfos.get(1).getRepCode();
                status = RespStatusUtil.getTradeStatus(repCode);
                time = 2 * time;
                if (++cycTimes > 4) {
                    break;
                }
            } while (UPRespStatus.UNKNOWN.getValue().equals(status));
        }
        catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new UnionPayException(ErrorCodeUP.INTERRUPT_EXP.getValue(), ErrorCodeUP.INTERRUPT_EXP.getDisplayName());
        }
        return resultBean;
    }

    /**
     * 调用银联代扣接口
     * 
     * @param tradeBean
     * @return
     * @throws UnionPayException
     */
    private ResultBean withholdingToUnionPay(TradeBeanUP tradeBean, String orderId) throws UnionPayException {

        String transTm = null;

        ClientProxyFactoryBean factory = createFactoryBean();
        TranWebService service = (TranWebService) factory.create();
        String certFilePath = UnionPayWithholdServiceImpl.class.getResource(ParamsUtil.getInstance().getCertPath()).getFile();
        CertHelper certHelper = new CertHelper(certFilePath, ParamsUtil.getInstance().getCertPasswd());

        // 发起扣款请求
        ReqRoot reqRoot = createWthdrwReq(ParamsUtil.getInstance().getMerchantId(), ParamsUtil.getInstance().getCertId(), orderId);
        MerchantRequest merchantRequest = new MerchantRequest();
        merchantRequest.setRoot(reqRoot);

        // 要素编号
        reqRoot.setFactorId(FactorId.CARDNUM_NAME_ID_PHO.getValue());
        // 被扣款卡号
        reqRoot.setPriAcctId(tradeBean.getPriAcctId());
        // 被扣款人姓名
        reqRoot.setName(tradeBean.getName());
        // 身份证号
        reqRoot.setIdCard(tradeBean.getIdCard());
        // 被扣款卡号预留手机号
        reqRoot.setPhone(tradeBean.getPhone());
        // 扣款类型
        reqRoot.setDkType(ParamsUtil.getInstance().getDkType());
        // 扣款金额，单位：分
        reqRoot.setTransAt(Long.toString(tradeBean.getTransAt()));
        reqRoot.setAtType(ParamsUtil.getInstance().getAtType());
        reqRoot.setTransTm(tradeBean.getTransTm());

        String payXML = null;
        try {
            payXML = certHelper.addSignature(merchantRequest);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UnionPayException(ErrorCodeUP.SIGN_FAIL.getValue());
        }
        logger.info("外发代扣请求参数： " + payXML);
        String payXMLResp = service.launchTran(payXML);
        logger.info("外发代扣返回结果： " + payXMLResp);

        MerchantResponse merchantResponse = new MerchantResponse();
        RspRoot rspRoot = new RspRoot();
        merchantResponse.setRoot(rspRoot);
        try {
            merchantResponse = XMLUtils.converyToJavaBean(payXMLResp.trim(), MerchantResponse.class);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UnionPayException(ErrorCodeUP.XML2JB_FAIL.getValue());
        }
        logger.info("交易的返回码： " + merchantResponse.getRoot().getRespCod());
        logger.info("交易的返回描述：" + merchantResponse.getRoot().getRespMsg());

        ResultBean resultBean = new ResultBean();

        if (UPRespInfo.TRADE_SUCESS.getValue().equals(merchantResponse.getRoot().getRespCod())) {
            ReturnInfo retInfo = new ReturnInfo(merchantResponse.getRoot().getRespCod(), merchantResponse.getRoot().getRespMsg());
            resultBean.setResultObj(retInfo);
            resultBean.setResultBool(true);
        }
        else {
            resultBean.setErrCode(merchantResponse.getRoot().getRespCod());
            resultBean.setErrMsg(merchantResponse.getRoot().getRespMsg());
        }
        return resultBean;

    }

    private static ClientProxyFactoryBean createFactoryBean() {
        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();

        factory.setServiceClass(TranWebService.class);
        factory.setAddress(ParamsUtil.getInstance().getAddress());
        if (factory.getProperties() == null) {
            Map<String, Object> properties = new HashMap<String, Object>();
            factory.setProperties(properties);
        }
        factory.getProperties().put("set-jaxb-validation-event-handler", "false");

        return factory;
    }

    public static ReqRoot createWthdrwReq(String merchantId, String certId, String orderId) {
        ReqRoot root = new ReqRoot();
        root.setVersion(ParamsUtil.getInstance().getVersion());
        root.setEncoding(ParamsUtil.getInstance().getEncoding());
        root.setSignMethod(ParamsUtil.getInstance().getSignMethod());
        root.setTransType(TransType.WITHDRAW.getValue());
        root.setBackUrl(ParamsUtil.getInstance().getBackUrl());
        root.setCertId(certId);
        root.setOrderId(orderId);
        root.setMchntCd(merchantId);
        return root;
    }

    @Override
    public ResultBean queryTrade(QueryTradeBeanUP queryTradeBean) {
        ResultBean resultBean = null;
        // 是否需要同步代扣交易状态
        PojoTxnsLogUp result = chlSeqNumCheckService.isSysthronizeStatus(queryTradeBean.getRefOrderId());
        if (result != null) {
            TxnsLogUpBean txnsLogUpBean = new TxnsLogUpBean();
            BeanUtils.copyProperties(result, txnsLogUpBean);
            resultBean = new ResultBean(txnsLogUpBean);
            return resultBean;
        }
        // 创建代扣查询流水
        queryTradeBean.setTransType(TransType.QUERY.getValue());
        PojoTxnsLogUp pojoTxnsLogUp = new PojoTxnsLogUp();
        BeanUtils.copyProperties(queryTradeBean, pojoTxnsLogUp);
        String orderId = chlSeqNumRecService.recordSeqNum(pojoTxnsLogUp);

        // 调用银联代扣接口
        try {
            resultBean = queryTradeToUnionPay(queryTradeBean);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultBean = new ResultBean(UPRespInfo.UNKNOWN_TIMEOUT.getValue(), UPRespInfo.UNKNOWN_TIMEOUT.getDisplayName());
        }
        // 更新代扣流水和查询流水状态
        chlSeqNumRecService.updateWthDrAndQrySeqNumStatus(orderId, queryTradeBean.getRefOrderId(), resultBean);

        PojoTxnsLogUp serialRec = txnsUnionPayDao.findByOrderId(orderId);
        TxnsLogUpBean txnsLogUpBean = new TxnsLogUpBean();
        BeanUtils.copyProperties(serialRec, txnsLogUpBean);
        resultBean.setResultObj(txnsLogUpBean);

        return resultBean;
    }

    /**
     * 调用银联代扣查询接口
     * 
     * @param queryTradeBean
     * @return
     * @throws UnionPayException
     */
    private ResultBean queryTradeToUnionPay(QueryTradeBeanUP queryTradeBean) throws UnionPayException {
        QueryRequest queryReq = new QueryRequest();
        QReqRoot qReqRoot = createQueryReq(ParamsUtil.getInstance().getMerchantId(), ParamsUtil.getInstance().getCertId(), queryTradeBean.getRefOrderId());
        qReqRoot.setTransTm(queryTradeBean.getTransTm());
        queryReq.setRoot(qReqRoot);

        ClientProxyFactoryBean factory = createFactoryBean();
        TranWebService service = (TranWebService) factory.create();
        String certFilePath = UnionPayWithholdServiceImpl.class.getResource(ParamsUtil.getInstance().getCertPath()).getFile();
        CertHelper certHelper = new CertHelper(certFilePath, ParamsUtil.getInstance().getCertPasswd());

        String xml = null;
        try {
            xml = certHelper.addSignatureQuery(queryReq);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UnionPayException(ErrorCodeUP.SIGN_FAIL.getValue());
        }

        logger.info("外发查询请求参数： " + xml);
        String xmlResp = service.queryTranResult(xml);
        logger.info("外发查询返回结果： " + xmlResp);

        // 解析返回的XML数据
        QueryResponse queryResponse;
        try {
            queryResponse = XMLUtils.converyToJavaBean(xmlResp.trim(), QueryResponse.class);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UnionPayException(ErrorCodeUP.XML2JB_FAIL.getValue());
        }

        logger.info("原交易应答码： " + queryResponse.getRoot().getOrigRespCode());
        logger.info("原交易应答信息：" + queryResponse.getRoot().getOrigRespMsg());
        logger.info("应答码： " + queryResponse.getRoot().getRespCod());
        logger.info("应答信息：" + queryResponse.getRoot().getRespMsg());

        ResultBean resultBean = new ResultBean();
        if (UPRespInfo.QUERY_SUCESS.getValue().equals(queryResponse.getRoot().getRespCod())) {
            ReturnInfo retInfoQry = new ReturnInfo(queryResponse.getRoot().getRespCod(), queryResponse.getRoot().getRespMsg());
            ReturnInfo retInfoWthDr = new ReturnInfo(queryResponse.getRoot().getOrigRespCode(), queryResponse.getRoot().getOrigRespMsg());
            List<ReturnInfo> retInfos = new ArrayList<ReturnInfo>();
            retInfos.add(retInfoQry);
            retInfos.add(retInfoWthDr);
            resultBean.setResultObj(retInfos);
            resultBean.setResultBool(true);
        }
        else {
            resultBean.setErrCode(queryResponse.getRoot().getRespCod());
            resultBean.setErrMsg(queryResponse.getRoot().getRespMsg());
        }
        return resultBean;
    }

    /**
     * 
     * @param merchantId
     * @param certId
     * @param orderId
     * @return
     */
    public QReqRoot createQueryReq(String merchantId, String certId, String orderId) {
        QReqRoot root = new QReqRoot();
        root.setVersion(ParamsUtil.getInstance().getVersion());
        root.setEncoding(ParamsUtil.getInstance().getEncoding());
        root.setSignMethod(ParamsUtil.getInstance().getSignMethod());
        root.setTransType(TransType.QUERY.getValue());
        root.setCertId(certId);
        root.setMchntCd(merchantId);
        root.setOrderId(orderId);
        root.setTransTm(DateTimeUtils.formatDateToString(new Date(), DateTimeUtils.FULLSECONDS));
        root.setTransQueryId(serialNumberService.generateTxnseqno());
        return root;
    }

    @Override
    public ResultBean applyAccChecking(ApplyAccCheckUP applyAccCheckUP) {
        ResultBean resultBean = null;
        AlyAccChkRespBean checkResp = null;
        // 在有效时间内是否已申请下载对账文件
        String queryTmBegin = DateUtil.DateToString(new Date(), DateStyle.YYYYMMDDHHMMSS.getValue());
        String queryTmEnd = DateUtil.addMinute(queryTmBegin, 5);
        PojoTxnsLogUp pojoTxnsLogUp = txnsUnionPayDao.getCheckRecord(TransType.ALYACCCHK.getValue(), UPRespStatus.SUCESS.getValue(), queryTmBegin, queryTmEnd);
        if (pojoTxnsLogUp != null && StringUtils.isNotEmpty(pojoTxnsLogUp.getDownloadUrl())) {
            logger.info("download url is effectived:" + pojoTxnsLogUp.getDownloadUrl());
            checkResp = new AlyAccChkRespBean();
            checkResp.setDownloadUrl(pojoTxnsLogUp.getDownloadUrl());
            checkResp.setQueryId(pojoTxnsLogUp.getOrderId());
            checkResp.setQueryDt(pojoTxnsLogUp.getQueryDt());
            checkResp.setTransDt(pojoTxnsLogUp.getTransDt());
            checkResp.setSendTm(pojoTxnsLogUp.getSendTm());
            resultBean = new ResultBean(checkResp);
            return resultBean;
        }
        // 创建流水记录
        applyAccCheckUP.setTransType(TransType.ALYACCCHK.getValue());
        PojoTxnsLogUp txnsLogUp = new PojoTxnsLogUp();
        BeanUtils.copyProperties(applyAccCheckUP, txnsLogUp);
        String orderId = chlSeqNumRecService.recordSeqNum(txnsLogUp);
        applyAccCheckUP.setQueryId(orderId);
        // 调用银联接口
        try {
            resultBean = applyAccCheckingToUnionPay(applyAccCheckUP);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultBean = new ResultBean(UPRespInfo.UNKNOWN_TIMEOUT.getValue(), UPRespInfo.UNKNOWN_TIMEOUT.getDisplayName());
        }
        // 更新流水信息
        chlSeqNumRecService.updateSeqNumInfoAlyAccChk(orderId, resultBean);

        return resultBean;
    }

    /**
     * 调用银联对账文件申请接口
     * 
     * @param applyAccCheckUP
     * @return
     * @throws UnionPayException
     */
    private ResultBean applyAccCheckingToUnionPay(ApplyAccCheckUP applyAccCheckUP) throws UnionPayException {

        ClientProxyFactoryBean factory = createFactoryBean();
        TranWebService service = (TranWebService) factory.create();
        String certFilePath = UnionPayWithholdServiceImpl.class.getResource(ParamsUtil.getInstance().getCertPath()).getFile();
        CertHelper certHelper = new CertHelper(certFilePath, ParamsUtil.getInstance().getCertPasswd());

        DownloadRequest downloadRequest = new DownloadRequest();
        DwnReqRoot dwnReqRoot = createDownCheckReq(ParamsUtil.getInstance().getMerchantId(), ParamsUtil.getInstance().getCertId());
        // 指定查询对账的交易日期
        dwnReqRoot.setTransDt(applyAccCheckUP.getTransDt());
        // 传入当日日期
        dwnReqRoot.setQueryDt(applyAccCheckUP.getQueryDt());
        dwnReqRoot.setQueryId(applyAccCheckUP.getQueryId());
        downloadRequest.setRoot(dwnReqRoot);
        String xml;
        try {
            xml = certHelper.addSignatureDownload(downloadRequest);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UnionPayException(ErrorCodeUP.SIGN_FAIL.getValue());
        }
        logger.info("外发查询请求参数： " + xml);
        String xmlResp = service.downloadTrans(xml);
        logger.info("外发查询返回结果： " + xmlResp);

        // 解析返回的XML数据
        DownloadResponse downloadResponse;
        try {
            downloadResponse = XMLUtils.converyToJavaBean(xmlResp.trim(), DownloadResponse.class);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UnionPayException(ErrorCodeUP.XML2JB_FAIL.getValue());
        }

        logger.info("文件下载地址： " + downloadResponse.getRoot().getDownloadUrl());
        logger.info("返回码： " + downloadResponse.getRoot().getRespCod());
        logger.info("返回描述：" + downloadResponse.getRoot().getRespMsg());

        ResultBean resultBean = new ResultBean();
        if (!StringUtils.isEmpty(downloadResponse.getRoot().getDownloadUrl())) {
            resultBean.setResultObj(downloadResponse.getRoot().getDownloadUrl());
            resultBean.setResultBool(true);
        }
        else {
            String respCode = downloadResponse.getRoot().getRespCod();
            // 对方应答码不规范，有时会把应答信息赋值到应答码中,此处做兼容处理
            if (respCode.getBytes().length == respCode.length()) {
                resultBean.setErrCode(respCode);
                resultBean.setErrMsg(downloadResponse.getRoot().getRespMsg());
            }
            else {
                resultBean.setErrMsg(respCode + ":" + downloadResponse.getRoot().getRespMsg());
            }
        }
        return resultBean;
    }

    public static DwnReqRoot createDownCheckReq(String merchantId, String certId) {
        DwnReqRoot root = new DwnReqRoot();
        root.setVersion(ParamsUtil.getInstance().getVersion());
        root.setEncoding(ParamsUtil.getInstance().getEncoding());
        root.setSignMethod(ParamsUtil.getInstance().getSignMethod());
        root.setTransType(TransType.ALYACCCHK.getValue());
        root.setCertId(certId);
        root.setMchntCd(merchantId);
        return root;
    }

    // public static void main(String[] args) {
    // ParamsUtil params = ParamsUtil.getInstance();
    // params.setMerchantId("000000000100001");
    // params.setCertId("68759663125");
    // params.setCertPath("/dev/acp_test_sign.pfx");
    // params.setCertPasswd("000000");
    // params.setAddress("http://111.161.76.56:22222/dkTranFlow?wsdl");
    // params.setVersion("1.0.0");
    // params.setEncoding("UTF-8");
    // params.setSignMethod("01");
    // params.setTransTypeQuery("00");
    // params.setTransTypePay("11");
    // params.setBackUrl("http://127.0.0.1:8080/dk-client-demo/notifyCallback");
    // params.setDkType("1");
    // params.setAtType("156");
    //
    // System.out.println("hello world~~~~~~");
    // // String json =
    // //
    // "{\"txnseqno\":\"test0000001\",\"priAcctId\":\"6217001370011446762\",\"name\":\"张三\",\"phone\":\"18912341234\",\"idCard\":\"321123198606045338\",\"transAt\":\"226\",\"transTm\":\"20170120\",\"backUrl\":\"\"}";
    // // TradeBeanUP tradeBeanUP = JSON.parseObject(json, TradeBeanUP.class);
    // // UnionPayWithholdServiceImpl unionpay = new
    // // UnionPayWithholdServiceImpl();
    // // try {
    // // ResultBean resultBean = unionpay.withholdingToUnionPay(tradeBeanUP);
    // // System.out.println("======resultBean:" + resultBean.toString());
    // // }
    // // catch (UnionPayException e) {
    // // // TODO Auto-generated catch block
    // // e.printStackTrace();
    // // }
    //
    // String json =
    // "{\"orderId\":\"1702069900000006\",\"transTm\":\"20170120\"}";
    // QueryTradeBeanUP queryTradeBean = JSON.parseObject(json,
    // QueryTradeBeanUP.class);
    // UnionPayWithholdServiceImpl unionpay = new UnionPayWithholdServiceImpl();
    // try {
    // ResultBean resultBean = unionpay.queryTradeToUnionPay(queryTradeBean);
    // System.out.println("======resultBean:" + resultBean.toString());
    // }
    // catch (UnionPayException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

}

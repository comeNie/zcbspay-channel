package com.zcbspay.platform.channel.unionpay.withholding.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcbspay.platform.channel.common.bean.unionpay.DownloadRequest;
import com.zcbspay.platform.channel.common.bean.unionpay.DwnReqRoot;
import com.zcbspay.platform.channel.common.bean.unionpay.MerchantRequest;
import com.zcbspay.platform.channel.common.bean.unionpay.QReqRoot;
import com.zcbspay.platform.channel.common.bean.unionpay.QueryRequest;
import com.zcbspay.platform.channel.common.bean.unionpay.ReqRoot;

/**
 * 签名工具类
 * 
 * @author
 * @since
 **/
public class CertHelper {

    private static final Logger logger = LoggerFactory.getLogger(CertHelper.class);

    private String certFilePath;
    private String certPasswd;

    public CertHelper(String certFilePath, String certPasswd) {
        this.certFilePath = certFilePath;
        this.certPasswd = certPasswd;
    }

    /**
     * 
     * @param data
     * @param encoding
     * @param certPath
     * @param certPwd
     * @param certId
     * @return
     */
    public String sign(String data, String encoding, String certPath, String certPwd, String certId) {
        String alias = "";
        try {
            KeyStore keyStore = getKeyInfo(certPath, certPwd, "PKCS12");
            Enumeration<String> aliasEnum = keyStore.aliases();
            if (aliasEnum.hasMoreElements()) {
                alias = (String) aliasEnum.nextElement();
            }
            certId = alias;
            InputStream in = new FileInputStream(certPath);
            keyStore.load(in, certPwd.toCharArray());
            in.close();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, certPwd.toCharArray());
            if (privateKey == null) {
                logger.error("privateKey is null!");
                return null;
            }
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
            String mySigAlg = cert.getSigAlgName();
            certId = cert.getSerialNumber().toString();
            Signature signAlg = Signature.getInstance(mySigAlg);
            signAlg.initSign(privateKey);
            byte[] lastData = this.convertByte(data, "sha1X16", encoding);
            signAlg.update(lastData);
            byte[] signature = signAlg.sign();
            return new String(SecureUtil.base64Encode(signature));
        }
        catch (Exception e) {
            logger.error("sign data failure! " + e, e);
        }
        return null;
    }

    public KeyStore getKeyInfo(String pfxKeyFile, String keyPwd, String type) throws IOException {
        FileInputStream fis = null;
        try {
            KeyStore ks = null;
            if ("JKS".equals(type)) {
                ks = KeyStore.getInstance(type);
            }
            else if ("PKCS12".equals(type)) {
                ks = KeyStore.getInstance(type);
            }
            fis = new FileInputStream(pfxKeyFile);

            if (null != ks) {
                ks.load(fis, keyPwd.toCharArray());
            }
            return ks;
        }
        catch (Exception e) {
            logger.error("getKeyInfo exception!!!", e);
        }
        finally {
            if (null != fis)
                fis.close();
        }
        return null;
    }

    public static boolean isEmpty(String s) {
        return null == s || "".equals(s.trim());
    }

    /**
     * 添加signature到应答报文
     *
     * @param response
     * @return
     * @throws Exception
     */
    public String addSignature(MerchantRequest response) throws Exception {
        ReqRoot root = response.getRoot();
        String rootStr = XMLUtils.convertToXmlWithoutHead(root);
        rootStr = rootStr.replaceAll("[\\r\\n\\t]", "").replaceAll(" ", "");
        String signature = sign(rootStr, "UTF-8", certFilePath, certPasswd, null);
        response.setSignature(signature);

        return XMLUtils.convertToXml(response);
    }

    /**
     * 添加signature到应答报文
     *
     * @param response
     * @return
     * @throws Exception
     */
    public String addSignatureQuery(QueryRequest response) throws Exception {
        QReqRoot root = response.getRoot();
        String rootStr = XMLUtils.convertToXmlWithoutHead(root);
        rootStr = rootStr.replaceAll("[\\r\\n\\t]", "").replaceAll(" ", "");
        String signature = sign(rootStr, "UTF-8", certFilePath, certPasswd, null);
        response.setSignature(signature);

        return XMLUtils.convertToXml(response);
    }

    /**
     * 添加signature到应答报文
     *
     * @param response
     * @return
     * @throws Exception
     */
    public String addSignatureDownload(DownloadRequest req) throws Exception {
        DwnReqRoot root = req.getRoot();
        String rootStr = XMLUtils.convertToXmlWithoutHead(root);
        rootStr = rootStr.replaceAll("[\\r\\n\\t]", "").replaceAll(" ", "");
        String signature = sign(rootStr, "UTF-8", certFilePath, certPasswd, null);
        req.setSignature(signature);

        return XMLUtils.convertToXml(req);
    }

    /**
     * 对信息进行加密转换
     *
     * @param data
     * @param method
     * @param encoding
     * @return
     * @throws Exception
     * 
     */
    private byte[] convertByte(String data, String method, String encoding) {
        if ("MD5".equalsIgnoreCase(method.trim())) {
            return SecureUtil.md5(data.getBytes());
        }
        if ("SHA-1".equalsIgnoreCase(method.trim()) || "SHA1".equalsIgnoreCase(method.trim())) {
            return SecureUtil.sha1(data.getBytes());
        }
        if ("md5X16".equalsIgnoreCase(method.trim())) {
            return SecureUtil.md5X16(data, encoding);
        }
        if ("sha1X16".equalsIgnoreCase(method.trim())) {
            return SecureUtil.sha1X16(data, encoding);
        }
        return data.getBytes();
    }
}

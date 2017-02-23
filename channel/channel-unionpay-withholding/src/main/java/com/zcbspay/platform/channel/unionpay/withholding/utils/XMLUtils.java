package com.zcbspay.platform.channel.unionpay.withholding.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


/**
 * 关于ws的工具类
 * 
 * @author 
 * @since 
 **/
public class XMLUtils {
	public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/**
	 * JavaBean转换成xml 默认编码UTF-8
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String convertToXml(Object obj) throws Exception {
		return convertToXml(obj, "UTF-8", true);
	}

	/**
	 * 不含xml头
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String convertToXmlWithoutHead(Object obj) throws Exception {
		return convertToXml(obj, "UTF-8", false);
	}

	/**
	 * JavaBean转换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String convertToXml(Object obj, String encoding, boolean hasHead) throws Exception {
		String result = null;
		JAXBContext context = JAXBContext.newInstance(obj.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		if (!hasHead) {
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		}

		StringWriter writer = new StringWriter();
		marshaller.marshal(obj, writer);
		result = writer.toString();
		return result;
	}

	/**
	 * xml转换成JavaBean
	 * 
	 * @param xml
	 * @param c
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T converyToJavaBean(String xml, Class<T> c) throws Exception {
		System.out.println(xml);
		T t = null;
		JAXBContext context = JAXBContext.newInstance(c);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		t = (T) unmarshaller.unmarshal(new StringReader(xml));
		return t;
	}

	/**
	 * 将bean转化为map
	 *
	 * @param javaBean
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> Bean2Map(Object javaBean) {
		Map<String, String> ret = new HashMap<String, String>();
		try {
			Method[] methods = javaBean.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("get")) {
					String field = method.getName();
					field = field.substring(field.indexOf("get") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					Object value = method.invoke(javaBean, (Object[]) null);
					ret.put(field, (null == value ? "" : value.toString()));
				}
			}
		} catch (Exception e) {
		}
		return ret;
	}

}

package commonFunctions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 封装签名相关函数
 * @author jc
 *
 */
public class BookSign {
	//不参与签名sign的字段
	private static List<String> unsignKeys = Arrays.asList("title", "content", "description", "authorDesc", "pics",
			"sign", "coverPic");

	/**
	 * 获取密码通过md5加密后的值，按照需要返回的md5值只有小写字母
	 * 
	 * @param source
	 * @return
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' }; // 用来将字节转换成16进制表示的字符
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
			// 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
				// 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,//
															// >>>
				// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换

			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 获取密码通过md5加密后的值，按照需要返回的md5值只有小写字母
	 * 
	 * @param s
	 * @return
	 */
	public final static String getMD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// System.out.println((int)b);
				// 将没个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 实现签名功能,参考《网易云阅读合作书籍网站书籍接入API》
	 * 
	 * @param httpMethod
	 *            http请求方式
	 * @param url
	 *            请求url
	 * @param keyValuelist
	 *            like k1=$v1$k2=$v2$k3=$v3$
	 * @param secretKey
	 * @return
	 **/
	public static String sign(String httpMethod, String url, Map<String, String> paramers, String secretKey) {
		StringBuilder rs = new StringBuilder();
		String sign = null;
		rs.append(httpMethod);
		rs.append(url);
		List<String> tempParamers = new ArrayList<String>();
		for (Map.Entry<String, String> entry:paramers.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (!unsignKeys.contains(key)) {
				String keyValue = key + "=" + value;
				tempParamers.add(keyValue);
			}
		}
		
		Collections.sort(tempParamers);
		
		for (int i = 0; i < tempParamers.size(); i++) {
			rs.append(tempParamers.get(i));
		}
		
		rs.append(secretKey);
		try {
			sign = getMD5(URLEncoder.encode(rs.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sign;
	}

	public static void main(String[] args) {
		String httpMethod = "POST";
		String url= "https://www.baidu.com";
		Map<String, String> paramers = new HashMap<String, String>();
		paramers.put("key", "value");
		String secretKey = "123456";
		String sign = sign(httpMethod, url, paramers, secretKey);
		System.out.println(sign);
	}

}

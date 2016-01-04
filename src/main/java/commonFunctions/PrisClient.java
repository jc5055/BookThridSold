package commonFunctions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;



/**
 * 
 * @author hzjiangcong
 *
 */
public class PrisClient {
	private static Logger logger = Logger.getLogger(PrisClient.class);
	private HttpClient client;
	private HostConfiguration hostcfg;
	public PrisClient() {
		
	}
	
	/**
	 * prisclient 构造函数
	 * @param host
	 * @param port
	 */
	public PrisClient(String host, int port) {
		hostcfg = new HostConfiguration();
		hostcfg.setHost(host, port);
		client = new HttpClient();
	}
	
	/**
	 * prisclient 构造函数
	 * @param host 域名
	 */
	public PrisClient(String host) {
		hostcfg = new HostConfiguration();
		hostcfg.setHost(host);
		client = new HttpClient();
	}
	
	
	/**
	 * 分销平台封装后的post请求
	 * @param url 接口名
	 * @param paramers 参数key value键值对
	 * @return
	 */
	public String myBookPost(String url, Map<String, String> paramers) {
		String resp = null;
		if (url != null && url.length() > 1) {
			PostMethod post = new PostMethod(url);
			if (!paramers.isEmpty()) {
				post.getParams().setContentCharset("UTF-8");
				//body数据设置
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry:paramers.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					params.add(new NameValuePair(key, value));
				}
				post.setRequestBody(params.toArray(new NameValuePair[0]));
				resp = execute(post, "获取分类失败!");
			}
			
		}
		return resp;
	}

	private String execute(HttpMethod method, String errMsg) {
		return execute(method, 200, errMsg);
	}
	
	private String execute(HttpMethod method, int code, String errMsg) {
		String resp = null;
		// 一般由于网络原因引起的异常，对于这种异常 （IOException），
		// HttpClient会根据你指定的恢复策略自动试着重新执行executeMethod方法。
		// HttpClient的恢复策略可以自定义（通过实现接口HttpMethodRetryHandler来实现）。
		// 通过httpClient的方法setParameter设置你实现的恢复策略，我们这里使用的是系统提供的默认恢复策略，
		// 该策略在碰到第二类异常的时候将自动重试3次
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {
			int statusCode = client.executeMethod(hostcfg, method);
			resp = getResponseBodyAsString(method);
			logger.info("statusCode=" + statusCode + "code=" + code);
			if (statusCode != code) {
				logger.info("result====" + resp);
			}

			// 如果请求期望是200的，返回不是200则抛出异常
			if (code == HttpStatus.SC_OK) {
				if (statusCode != code) {
					throw new Exception(errMsg);
				}
			} else {
				// 如果请求期望不是200的，则返回状态码进行对比
				method.abort();
				return String.valueOf(statusCode);
			}

			// if (statusCode != code) {
			// throw new Exception(errMsg);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			method.abort();
		}
		return resp;
	}

	private String getResponseBodyAsString(HttpMethod method) throws Exception {
		Header ce = method.getResponseHeader("Content-Encoding");
		if (ce != null && ce.getValue().toLowerCase().indexOf("gzip") > -1) {
			// For GZip response
			InputStream is = method.getResponseBodyAsStream();
			GZIPInputStream gzin = new GZIPInputStream(is);

			InputStreamReader isr = new InputStreamReader(gzin, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String tempbf;
			while ((tempbf = br.readLine()) != null) {
				sb.append(tempbf);
				sb.append("\r\n");
			}
			isr.close();
			gzin.close();
			return sb.toString();
		} else {
			return method.getResponseBodyAsString();
		}
	}


}

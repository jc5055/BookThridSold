package com.netease.pris.Book.ThridSold;

import java.util.HashMap;
import java.util.Map;

import commonFunctions.BookSign;
import commonFunctions.CommonString;
import commonFunctions.PrisClient;

public class BookPull {
	public static void main(String[] args) {
		String host = "testapi.yuedu.163.com";
		PrisClient client = new PrisClient(host);
		Map<String, String> paramers = new HashMap<String, String>();
		paramers.put("consumerKey", CommonString.CONSUMER_KEY);
		paramers.put("timestamp", String.valueOf(System.currentTimeMillis()));
		String url = "http://" + host + CommonString.BOOK_CATEGORY;
		String sign = BookSign.sign("POST", url, paramers, CommonString.CONSUMER_SECRET);
		paramers.put("sign", sign);
		String resp =  client.myBookPost(CommonString.BOOK_CATEGORY, paramers);
		System.out.println(resp);
	}
}

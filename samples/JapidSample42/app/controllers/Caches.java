package controllers;

import java.util.HashMap;

import play.cache.Cache;
import play.libs.Time;
import play.mvc.Result;
import cn.bran.japid.template.RenderResult;
import cn.bran.play.CachedItemStatus;
import cn.bran.play.CachedRenderResult;
import cn.bran.play.JapidController;
/**
 *  ! can we implement a cache sharding in the Cache impl? how about multiple key in one batch? A join fetch across multiple servers?
 *  
 *  
 * @author Bing Ran<bing_ran@hotmail.com>
 *
 */
public class Caches extends JapidController {
	private static final String CIS = "CachedItemStatus";
	private static final String KEY = "thisisarelativelylongkey";

	public static Result __init(int total) {
		long t = System.currentTimeMillis();
		for (int i = 0; i < total; i++) {
			String k = KEY + i;
//			System.out.println(k);
			Cache.set(k, k, Time.parseDuration("10min"));
		}
		String x = total + " cache init took: " + (System.currentTimeMillis() - t) + "ms";
		return renderText(x);
//		System.out.println(x);
	}

	public static Result find(int k) {
//		int total = 100000;
		long t = System.currentTimeMillis();
//		for (int i = 0; i < total; i++) {
//			String k = KEY + i;
//			Cache.add(k, k);
//		}
		Object o = "|";
		for(int i = 1000; i-- > 0;) {
			String key2 = KEY + (k + i);
//			System.out.println(key2);
			o = Cache.get(key2);
		}

		String x = o + " found. took: " + (System.currentTimeMillis() - t) + "ms";
		return renderText(x);
//		System.out.println(x);
	}

	public static Result f2(int k) {
//		int total = 100000;
		long t = System.currentTimeMillis();
//		for (int i = 0; i < total; i++) {
//			String k = KEY + i;
//			Cache.add(k, k);
//		}
		String key2 = KEY + k;
		System.out.println(key2);
		Object o = Cache.get(key2);
		String x = o + " found. took: " + (System.currentTimeMillis() - t) + "ms";
		return renderText(x);
//		System.out.println(x);
	}
	
	public static Result testCachedItemStatus() {
		CachedItemStatus cis = new CachedItemStatus(-1);
		Cache.set(CIS, cis, 10);
		CachedItemStatus o = (CachedItemStatus) Cache.get(CIS);
		if(o.isExpired())
			return renderText("good to know..");
		else
			return renderText("bad to know..");
			
	}
	
	public static Result testCachedRenderResult() {
		CachedItemStatus cis = new CachedItemStatus(0);
		HashMap<String, String> headers = new HashMap<String, String>();
		RenderResult rr = new RenderResult(headers, new StringBuilder(), 123);
		CachedRenderResult crr = new CachedRenderResult(cis, rr);
		Cache.set("crr", crr, Time.parseDuration("10s"));
		crr = (CachedRenderResult) Cache.get("crr");
		if(crr.isExpired())
			return renderText("good to know..");
		else
			return renderText("bad to know..");
	}

	public static Result testStringBuilder() {
		String str = "hello";
		StringBuilder sb = new StringBuilder(str);
		String key2 = "sb";
		Cache.set(key2, sb, Time.parseDuration("10s"));
		sb = (StringBuilder) Cache.get(key2);
		
		if (str.equals(sb.toString()))
			return renderText("StringBuilder works...");
		else
			return renderText("StringBuilder does not works...");
	}
	
	public static Result testRenderResult() {
		HashMap<String, String> headers = null; //new HashMap<String, String>();
		RenderResult rr = new RenderResult(headers, new StringBuilder(), 123);
//		RenderResult rr = new RenderResult(headers, null, 123);
		Cache.set("crr", rr, Time.parseDuration("10s"));
		rr = (RenderResult) Cache.get("crr");
		if (rr != null)
			return renderText("good to know 2...");
		else {
			return renderText("bad to know 2...");
		}
		
	}
	
	public Result testAction() {
		return renderJapid();
	}
}

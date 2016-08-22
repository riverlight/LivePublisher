package com.jutong.live.actilive;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class NetRequestTask implements Runnable {

	private static final int BITMAP_READY = 0;
	private boolean cancelled;
	private OnCompleteHandler onCompleteHandler;

	private String mUrl;
	private Map<String, String> mParams;// 请求参数
	private int METHOD = 0;// 0-GET 1-POST
	private static final int LOADING_THREADS = 4;
	private static ExecutorService threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
	private long mServiceTime = 0;
	private String mToken = "";

	private NetRequestTask(final String mUrl, Map<String, String> mParams2, int method, String token) {
		this.mUrl = mUrl;
		this.mParams = mParams2;
		this.METHOD = method;
		this.mToken = token;
		threadPool.execute(this);
	}

	public static NetRequestTask get(String mUrl, Map<String, String> mParams, String token) {
		return new NetRequestTask(mUrl, mParams, 0, token);
	}

	public static NetRequestTask post(String mUrl, Map<String, String> mParams2, String token) {
		return new NetRequestTask(mUrl, mParams2, 1, token);
	}

	@Override
	public void run() {

		try {
			if (!TextUtils.isEmpty(mUrl)) {
				if (mParams != null) {
//					mKey = (String) mParams.get(UrlContent.HTTP_HEADER_KEY);
//					mToken = (String) mParams.get(UrlContent.HTTP_HEADER_TOKEN);
//					mIdentity = (String) mParams.get(UrlContent.HTTP_HEADER_IDENTITY);// 用户唯一标示
				}
				if (METHOD == 0) {
//					HttpRequest request = HttpRequest.get(mUrl).headerKey(mKey).headerToken(mToken)
//							.headerIdentity(mIdentity);
					HttpRequest request = HttpRequest.get(mUrl);
					if (this.mToken!="") {
						request.headerToken(this.mToken);
					}
					
					request.acceptGzipEncoding().uncompress(true);// 设置gzip
					if (request.ok()) {
						mServiceTime = request.getConnection().getDate();
						complete(request.body());
					}
				} else {
					HttpRequest request = null; // = HttpRequest.post(mUrl);
					if (!TextUtils.isEmpty(this.mToken)) {
						request = HttpRequest.post(mUrl).headerToken(this.mToken);
						Log.i("fangfang", "************%%   " + this.mToken);
					}else {
						request = HttpRequest.post(mUrl);
						Log.i("fangfang", "************!!!   " + this.mToken + " url: " + mUrl);
					}
					request.acceptGzipEncoding().uncompress(true);// 设置gzip
					request.form(mParams);
					if (request.ok()) {
						mServiceTime = request.getConnection().getDate();
						complete(request.body());
					}
				}
			}
		} catch (Exception e) {
			complete("errorData");

			e.printStackTrace();
		}

	}

	public long getServiceTime() {
		return mServiceTime;
	}

	public void setOnCompleteHandler(OnCompleteHandler handler) {
		onCompleteHandler = handler;
	}

	public void cancel() {
		cancelled = true;
	}

	public void complete(final String mUrl) {
		if (onCompleteHandler != null && !cancelled) {
			Message message = onCompleteHandler.obtainMessage(BITMAP_READY, mUrl);
			onCompleteHandler.sendMessage(message);
			cancel();
		}
	}

	public static abstract class OnCompleteHandler extends Handler {

		@Override
		public void handleMessage(final Message message) {
			String mJson = (String) message.obj;
			onComplete(mJson);
		}

		public abstract void onComplete(final String mJson);

	}

	public abstract static class OnCompleteListener {
		public abstract void onComplete();
	}

}
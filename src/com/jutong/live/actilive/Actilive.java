package com.jutong.live.actilive;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jutong.live.actilive.NetRequestTask.OnCompleteHandler;

public class Actilive {
	private String mUrlSignin = "http://liveapi.videojj.com/api/v1/signin";
	private String mUrlSetting = "http://liveapi.videojj.com/api/v1/setting";
	private String mUrlCreateTag = "http://liveapi.videojj.com/api/v1/tags";
	JSONArray mBalls;
	String mToken;
	
	public void StartActilive(final Handler handler) {
		Signin(handler);
	}
	
	private void Signin(final Handler handle) {
		Map<String, String> mapBody = new HashMap<String, String>();
		mapBody.put("platformId", "556c38e7ec69d5bf655a0fb2");
		mapBody.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwbGF0Zm9ybUlkIjoiNTU2YzM4ZTdlYzY5ZDViZjY1NWEwZmIyIiwicGxhdGZvcm1Vc2VySWQiOiIzNyIsImlhdCI6MTQ2Nzc5MjgwMn0.MEQIMriQxpJ5auEuSGCS9K8oUDKJlZjKs1uT1ys4W8U");
		NetRequestTask mTask = NetRequestTask.post(mUrlSignin, mapBody, "");
		mTask.setOnCompleteHandler( new OnCompleteHandler() {
			
			@Override
			public void onComplete(String mJson){
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject=new JSONObject(mJson);
					Actilive.this.mToken = jsonObject.optString("token");
					
					Log.i("fangfang", Actilive.this.mToken);
					Log.i("fangfang",mJson);
					GetBall(handle, Actilive.this.mToken);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private void GetBall(final Handler handle, String token) {
		Map<String, String> mapHeader = new HashMap<String, String>();
		NetRequestTask task = NetRequestTask.get(mUrlSetting, mapHeader, token);
		task.setOnCompleteHandler( new OnCompleteHandler() {
			
			@Override
			public void onComplete(String mJson) {
				// TODO Auto-generated method stub
				try {
					JSONObject mJsonObject= new JSONObject(mJson);
					mBalls = mJsonObject.getJSONArray("myBalls");
					Log.i("fangfang", mJson);
					
					Message msg = new Message();
					msg.what = 10001;
					msg.obj = mBalls;
					handle.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void CreateTag(int nIndex, double x, double y, double w, double h) {
		String ballID = "";
		
		try {
			JSONObject ball = (JSONObject)(this.mBalls.get(nIndex));
			ballID = ball.optString("_id");
			Log.i("fangfang", "************* ball id : " + ballID);
		} catch  (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("fangfang", "************* printStackTrace ");
			e.printStackTrace();
		}
		
		Log.i("fangfang", "************* ball id : " + ballID);
		Log.i("fangfang", "************* token : " + Actilive.this.mToken);
		// post
		Map<String, String> mapBody = new HashMap<String, String>();
		mapBody.put("ball", ballID);
		mapBody.put("x", String.valueOf(x));
		mapBody.put("y", String.valueOf(y));
		mapBody.put("w", String.valueOf(w));
		mapBody.put("h", String.valueOf(h));
		NetRequestTask task = NetRequestTask.post(mUrlCreateTag, mapBody, Actilive.this.mToken);
		task.setOnCompleteHandler( new OnCompleteHandler() {
			
			@Override
			public void onComplete(String mJson) {
				// TODO Auto-generated method stub
				Log.i("fangfang", "################  " + mJson);
			}
		}); 
	}
}

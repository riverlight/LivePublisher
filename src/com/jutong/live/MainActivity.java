package com.jutong.live;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.myrtmp.R;
import com.jutong.live.actilive.Actilive;

@SuppressLint("CutPasteId") public class MainActivity extends Activity implements OnClickListener,
		Callback, LiveStateChangeListener {
	
	private Actilive actilive = new Actilive();
	JSONArray mBalls;
	private Button button01,mBtnTag;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private boolean isStart;
	private LivePusher livePusher;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case -100:
				Toast.makeText(MainActivity.this, "视频预览开始失败", 0).show();
				livePusher.stopPusher();
				break;
			case -101:
				Toast.makeText(MainActivity.this, "音频录制失败", 0).show();
				livePusher.stopPusher();
				break;
			case -102:
				Toast.makeText(MainActivity.this, "音频编码器配置失败", 0).show();
				livePusher.stopPusher();
				break;
			case -103:
				Toast.makeText(MainActivity.this, "视频频编码器配置失败", 0).show();
				livePusher.stopPusher();
				break;
			case -104:
				Toast.makeText(MainActivity.this, "流媒体服务器/网络等问题", 0).show();
				livePusher.stopPusher();
				break;
			// startActilive 成功
			case 10001:
				mBalls= (JSONArray) msg.obj;
				//Log.e("fangfang", balls.toString());
				Log.e("fangfang", "============ 10001");
				try {
					for ( int i=0; i<mBalls.length(); i++ ) {
						JSONObject ball = (JSONObject) mBalls.get(i);
						Log.i("fangfang", ball.optString("title"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//
				break;
			}
			button01.setText("推流");
			isStart = false;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button01 = (Button) findViewById(R.id.button_first);
		button01.setOnClickListener(this);
		mBtnTag=(Button) findViewById(R.id.btn);
		mBtnTag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				actilive.CreateTag(0, 0.1, 0.1, 0.1, 0.1);
			}
		});
		findViewById(R.id.button_take).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						livePusher.switchCamera();
					}
				});
		mSurfaceView = (SurfaceView) this.findViewById(R.id.surface);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		livePusher = new LivePusher(this, 960, 720, 512000, 15,
				CameraInfo.CAMERA_FACING_FRONT);
		livePusher.setLiveStateChangeListener(this);
		livePusher.prepare(mSurfaceHolder);

		// actilive
		actilive.StartActilive(mHandler);
		
	}
	// @Override
	// public void onRequestPermissionsResult(int requestCode,
	// String[] permissions, int[] grantResults) {
	// super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		livePusher.relase();
	}

	@Override
	public void onClick(View v) {
		if (isStart) {
			button01.setText("推流");
			isStart = false;
			livePusher.stopPusher();
		} else {
			button01.setText("停止");
			isStart = true;
			mBtnTag.setVisibility(View.VISIBLE);
			livePusher.startPusher("rtmp://video-center.alivecdn.com/app-name/live-m?vhost=live.videojj.com");// TODO: 设置流媒体服务器地址

		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("MAIN: CREATE");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("MAIN: CHANGE");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("MAIN: DESTORY");
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onErrorPusher(int code) {
		System.out.println("code:" + code);
		mHandler.sendEmptyMessage(code);
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onStartPusher() {
		Log.d("MainActivity", "开始推流");
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onStopPusher() {
		Log.d("MainActivity", "结束推流");
	}

}

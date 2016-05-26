package com.mobiledog.seekbarpackaging;

import com.mobiledog.seekbarpackaging.SeekBarLongClick.onChange;
import com.mobiledog.seekbarpackaging.SeekBarLongClick.onLong;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private SeekBar sb;//普通的SeekBar
	private  int oldsign;
	private  int newsign;
	private SeekBarLongClick sb2; //可长按 不可点击的SeekBar

	private SeekBarView barView; //可长按 可添加关键点 可拖动 不可点击
	private PlayerTimer playerTimer;

	/**
	 * @param hasFocus
	 *
	 * 第一次界面渲染完成时 对自定义的SeekBarView进行初始化 和 测量。
	 */
	boolean isOne=false;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(isOne){
			isOne=false;
			barView.initView(barView);
			barView.setMax(1000);
			//添加关键点。不添加即无效果
			barView.addKeyPoint(new int[]{100,300,620,530,1000,700});
			
			barView.setOnLongSeekBarClick(new SeekBarView.onLong() {
				@Override
				public boolean onLongClick(SeekBarView seekBar) {
					Log.e("Chuck", "onLongClick() 方法触发  progress = " +seekBar. getProgress());
					return false;
				}
			});
			barView.setOnSeekBarChange(new SeekBarView.onChange() {
				
				//手指离开屏幕，可能长按，可能拖动，判断按下屏幕之前 计时器的状态 进行相应的操作
				@Override
				public void onStopTrackingTouch(SeekBarView seekBar) {
					Log.e("Chuck", "onStopTrackingTouch()方法触发    progress = " + seekBar.getProgress());	
					if(!playerTimer.isStarted()){
						   playerTimer.start();
						}
				}
				@Override
				public void onStartTrackingTouch(SeekBarView seekBar) {
				}
				
				//此处只监听 进度条拖动的情况，回调此方法时 应该首先判断 当前微课是否在播放 如果在 立即暂停播放
				//fromUser 只有拖动的时候为true，长按不触发此方法，点击关键点时为false.
				@Override
				public void onProgressChanged(SeekBarView seekBar, int progress,
						boolean fromUser) {
					if(fromUser){
						if(playerTimer.isStarted()){
						   playerTimer.stop();
						}
						tv.setText(progress+"----------------"+barView.getMax());
					}
					
					Log.e("Chuck", "onProgressChanged()方法触发  progress=" + progress + "  "+"fromUser="+fromUser );
				}
			});
			playerTimer.start();
		}
		super.onWindowFocusChanged(hasFocus);
	}
	
	
	// 计时器 handler
		Handler handlerTimer = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if(barView.getProgress()==barView.getMax()){playerTimer.stop();}
					barView.setProgress(barView.getProgress() + 1);
					tv.setText(barView.getProgress()+"---"+barView.getMax());
				
					break;
				}
				super.handleMessage(msg);
			}

		};
	
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv=(TextView) findViewById(R.id.tv);
		
		playerTimer=new PlayerTimer(handlerTimer);
		barView = (SeekBarView) findViewById(R.id.seekbarview);
		isOne=true;
		sb = (SeekBar) findViewById(R.id.sb);
		sb2 = (SeekBarLongClick) findViewById(R.id.sb2);
		sb2.setOnLongSeekBarClick(new onLong() {
			@Override
			public boolean onLongClick(SeekBarLongClick seekBar) {
			Log.e("Chuck", "SeekBarDemo onLongClick ---- ");
				return false;
			}
		});
		sb2.setOnSeekBarChange(new onChange() {
			
			@Override
			public void onStopTrackingTouch(SeekBarLongClick seekBar) {
             
				Log.e("Chuck", "SeekBarDemo onStopTrackingTouch ---- ");
			}
			
			@Override
			public void onStartTrackingTouch(SeekBarLongClick seekBar) {
				Log.e("Chuck", "SeekBarDemo onStartTrackingTouch ---- ");
			}
			
			@Override
			public void onProgressChanged(SeekBarLongClick seekBar, int progress,
										  boolean fromUser) {
				Log.e("Chuck", "SeekBarDemo onProgressChanged ---- ");
			}
		});
		//sb为原生的SeekBar
		oldsign = sb.getProgress();
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
//				Toast.makeText(MainActivity.this, "onProgressChanged"+progress, 0).show();
				Log.e("Chuck", "onProgressChanged");
				if(progress>oldsign+3||progress<oldsign-3){
					seekBar.setProgress(oldsign);
					return;
				}
				seekBar.setProgress(progress);
				oldsign = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.e("Chuck", "onStartTrackingTouch");
					seekBar.setProgress(oldsign);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.e("Chuck", "onStopTrackingTouch");
			}
			
		});
	}
	

}

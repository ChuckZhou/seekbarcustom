package com.mobiledog.seekbarpackaging;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

/**
 * 计时器 进度条显示的时间
 */
public class PlayerTimer {
	public boolean isStarted() {
		return timer != null;
	}
	Handler handler ;
	public PlayerTimer(Handler handler){
		this.handler=handler;
	}
	
	
	private Timer timer = null;
	private TimerTask timerTask = null;

	public void start() {
		stop();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(1);
			}
		};
		timer.schedule(timerTask, 1000, 1000);
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}
}
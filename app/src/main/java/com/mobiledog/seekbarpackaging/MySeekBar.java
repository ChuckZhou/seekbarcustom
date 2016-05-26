package com.mobiledog.seekbarpackaging;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;


/**
 * 不可点击 长按无效的seekbar
 */
public class MySeekBar extends SeekBar  {
	private  int oldsign;
	
	public MySeekBar(Context context) {
		super(context);
		init();
	}
	public MySeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MySeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init() {
		setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress>oldsign+3||progress<oldsign-3){
					seekBar.setProgress(oldsign);
					return;
				}
				seekBar.setProgress(progress);
				oldsign = progress;
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				seekBar.setProgress(oldsign);
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
			
		});
	}

}

package com.mobiledog.seekbarpackaging;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class SeekBarView extends FrameLayout {

	private onLong longClick; // 长按监听
	private MotionEvent motionEvent;// 移动监听的对象
	private float scale; // 当前比例
	private Context context;
	boolean isMove = false; // 当前是否是移动动作

	float xDown, yDown, xUp;
	private SeekBar seekBar;
	private ImageView image;
	private RelativeLayout rl_container;
	private int marginLeft;
	private int seekBarWidth;
	private int moveWidth;
	private boolean longClicked;
	/**
	 * 长按接口
	 * 
	 * @author terry
	 * 
	 */
	public interface onLong {
		public boolean onLongClick(SeekBarView seekBar);
	}

	private onChange SeekBarChange;// SeekBar进度监听

	/**
	 * 进度改变接口，仿SeekBar
	 * 
	 * @author terry
	 * 
	 */
	public interface onChange {
		public void onStopTrackingTouch(SeekBarView seekBar);

		public void onStartTrackingTouch(SeekBarView seekBar);

		/**
		 * @param seekBar
		 *            SeekBarView对象
		 * @param progress
		 *            进度条
		 * @param fromUser
		 *            只有拖动 为true，长按 不触发此方法，点击关键点为false.
		 */
		public void onProgressChanged(SeekBarView seekBar, int progress,
				boolean fromUser);
	}

	public SeekBarView(Context context) {
		super(context);
	}

	public SeekBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		View view = LayoutInflater.from(context).inflate(R.layout.view_seekbar,
				null);
//		initView(view);
		addView(view);

	}

	public SeekBarView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public void initView(View view) {
		seekBarWidth = getWidth();
		int [] ints=new int[2];
		getLocationOnScreen(ints); 
		marginLeft = ints[0];
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		moveWidth = seekBarWidth - dip2px(context, 20);
		// 播放时间为1000秒，
		seekBar = (SeekBar) view.findViewById(R.id.seekbar);
		scale = (float) seekBar.getMax() / (float) moveWidth;
		rl_container = (RelativeLayout) view.findViewById(R.id.rl_container);
		image = (ImageView) view.findViewById(R.id.image);
		//motionEvent获取到的是Activity 的 坐标······ 与Seekbar的坐标 x 有margin距离
		image.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				int x = (int) motionEvent.getX();
				if (Math.abs(x - xDown - marginLeft) < 10) {
					seekBar.setProgress((int) ((int) (x - marginLeft) * scale));
					isMove = false;
					if (longClick != null) {
						longClick.onLongClick(SeekBarView.this);
						longClicked=true;
					}
				}
				return false;
			}
		});
		image.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (motionEvent == null) {
					motionEvent = event;
				}
				// 当按下时处理
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					xDown = event.getX();
					yDown = event.getY();

				}// 松开处理
				if (event.getAction() == MotionEvent.ACTION_UP) {
					isMove = false;
					xUp = event.getX();
					if (SeekBarChange != null&&!longClicked) {
						SeekBarChange.onStopTrackingTouch(SeekBarView.this);
					}
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
 
					float x = event.getX();
					final int progress = seekBar.getProgress();
					// 如果点击位置是当前进度位置对应坐标的20px以内就认为点击到了当前位置 ，拖动可调整进度条
					if (!isMove && Math.abs(progress - scale * xDown) <= 20) {
						isMove = true;
					}
					if (isMove) {
						seekBar.setProgress((int) (scale * x));
						if (SeekBarChange != null) {
							SeekBarChange.onProgressChanged(SeekBarView.this,
									(int) (scale * x), true);
						}
					} else {
						// 其他模式
					}
				}
				return false;
			}
		});
	}
	public void setMax(int max) {
		seekBar.setMax(max);
		scale= (float) seekBar.getMax() / (float) moveWidth ;
	}

	public int getMax() {
		return seekBar.getMax();
	}

	public void setProgress(int progress) {
		seekBar.setProgress(progress);
	}

	public int getProgress() {
		return seekBar.getProgress();
	}

	public void addKeyPoint(final int[] pointIndexs) {
		for (int i = 0; i < pointIndexs.length; i++) {
			final int tempI = i;
			ImageView imageView = new ImageView(context);
			int imgSize = dip2px(context, 20);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imgSize, imgSize);
			// 距离左边的位置···因为图标本身长度为20dp 所以这里需要减去10dp
			int marginLeft = (int) (pointIndexs[i] / scale);
			layoutParams.leftMargin = marginLeft;
			imageView.setLayoutParams(layoutParams);
			imageView.setImageResource(R.drawable.main_homework_up);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			rl_container.addView(imageView);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					seekBar.setProgress(pointIndexs[tempI]);
					if (SeekBarChange != null) {
						SeekBarChange.onProgressChanged(SeekBarView.this,
								pointIndexs[tempI], false);
					}
				}
			});
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	private int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	/**
	 * 设置长按事件
	 * 
	 * @param longClick
	 */
	public void setOnLongSeekBarClick(onLong longClick) {
		this.longClick = longClick;
	}

	/**
	 * 设置进度改变事件
	 * 
	 * @param change
	 */
	public void setOnSeekBarChange(onChange change) {
		this.SeekBarChange = change;
	}
}

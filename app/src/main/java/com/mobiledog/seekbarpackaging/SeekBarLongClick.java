package com.mobiledog.seekbarpackaging;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;

public class SeekBarLongClick extends SeekBar implements OnTouchListener {

    private onLong longClick;
    private  int oldsign;//获取上次更改后的点击状态
    /**
     * 长按接口
     */
    public interface onLong {
        public boolean onLongClick(SeekBarLongClick seekBar);
    }

    private onChange SeekBarChange;

    /**
     * 进度改变接口
     */
    public interface onChange {
        public void onStopTrackingTouch(SeekBarLongClick seekBar);

        public void onStartTrackingTouch(SeekBarLongClick seekBar);

        public void onProgressChanged(SeekBarLongClick seekBar, int progress,
                                      boolean fromUser);
    }

     
    private Handler hand;
    private Runnable runable;
    private Thread th;
    public static int i = 0;
    private boolean isStop = false;
    public static int pp = 0;
    public int index = 0;

    public SeekBarLongClick(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public SeekBarLongClick(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                if (SeekBarChange != null) {
                    SeekBarChange.onStopTrackingTouch(SeekBarLongClick.this);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            	seekBar.setProgress(oldsign);
                if (SeekBarChange != null) {
                    SeekBarChange.onStartTrackingTouch(SeekBarLongClick.this);
                }
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar,
                    final int progress, boolean fromUser) {
				
            	if(progress>oldsign+3||progress<oldsign-3){
					seekBar.setProgress(oldsign);
				}else{
				seekBar.setProgress(progress);
				oldsign = progress;
				}
				if (SeekBarChange != null) {
                    SeekBarChange.onProgressChanged(SeekBarLongClick.this, oldsign,
                            fromUser);
                }
                hand = getHandler(1, SeekBarLongClick.this, oldsign);
            }
        });
        /**
         * 为runable 赋值
         */
        runable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                do {
                    i++;
                    try {
                        Thread.sleep(400);
                        Message msg = hand.obtainMessage();
                        msg.arg1 = i;
                        msg.sendToTarget();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } while (isStop);
            }
        };
    }

    /**
     * 获取一个handler 对象
     * @param  j 0代表onTouch 1代表onChange
     * @param  v 视图对象
     * @param progress 进度
     * @return 返回一个handler对象
     */
    public Handler getHandler(final int j, final View v, final int progress) {
        Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (j) {
                case 0:
                    if (msg.arg1 == 3) {
                        if (longClick != null) {
                            longClick.onLongClick(SeekBarLongClick.this);
                            isStop = false;
                        }
                    }
                    break;
                case 1:
                    if (msg.arg1 == 1) {
                        pp = progress;
                    }
                    if (msg.arg1 == 2) {
                        if (pp != progress) {
                            i = 0;
                        }
                    }
                    if (msg.arg1 == 3) {
                        i = 0;
                        if (pp == progress) {
                            if (longClick != null) {
                                longClick.onLongClick(SeekBarLongClick.this);
                                isStop = false;
                            }
                        }
                    }
                    break;
                }
                super.handleMessage(msg);
            }
        };
        return h;
    }
    /**
     * 设置长按事件
     * @param longClick
     */
    public void setOnLongSeekBarClick(onLong longClick) {
        this.longClick = longClick;
    }

    /**
     * 设置进度改变事件
     * @param change
     */
    public void setOnSeekBarChange(onChange change) {
        this.SeekBarChange = change;
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	float x2 = event.getX();
        	float y2 = event.getY();
        	
            isStop = true;
            th = new Thread(runable);
            th.start();
            i = 0;
            hand = getHandler(0, v, 0);
            break;
        case MotionEvent.ACTION_UP:
            isStop = false;
            break;
        }
        return false;
    }
}
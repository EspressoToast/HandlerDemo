package com.example.ljw.handlerdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class BeatMouseActivity extends AppCompatActivity {
    private Handler handler = null;
    private ImageView image_main_mouse;
    private int position;
    private boolean flag = false;
    private int[][] positionArr;
    private int countNum;
    private int beatNum;
    private TextView mTvStop;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_mouse);

        findView();
        initData();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        image_main_mouse.setVisibility(View.VISIBLE);
                        // 获取0-8之间的随机数[0,8)，半闭合区间。目的是随机获取给定的8个坐标位置。
                        // 获取随机数有两种办法：
                        // 方法一：
                        // Math.random()*positionArr.length，注意伪随机数是个半闭合区间。即随机数不可能为positionArr.length
                        // 方法二：
                        // new Random().nextInt(positionArr.length);
                        position = (int) (Math.random() * positionArr.length);
                        image_main_mouse.setX(positionArr[position][0]);
                        image_main_mouse.setY(positionArr[position][1]);
                        break;
                    default:
                        break;
                }
            }
        };

        image_main_mouse = (ImageView) findViewById(R.id.image_main_mouse);


        image_main_mouse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                image_main_mouse.setVisibility(View.GONE);
                beatNum++;
                Toast.makeText(BeatMouseActivity.this, "总出现次数：" + countNum + ";击中次数：" + beatNum, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mTvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    mTvStop.setText("开始");
                } else {
                    mTvStop.setText("停止");
                }
                flag = !flag;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (flag) {
                            try {
                                countNum++;
                                // 获取0-500之间的随机数，再加上500，目的是让老鼠出现的间隙时间也随机，最短出现间隙为500毫秒，最长为999毫秒。
                                Thread.sleep(new Random().nextInt(500) + 500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(0);
                        }

                    }
                }).start();

            }
        });
    }

    private void initData() {
        positionArr = new int[8][2];
        for (int i = 0; i < positionArr.length; i++) {
            //设置X轴坐标
            positionArr[i][0] = getWindowWidth(this) / positionArr.length * i;
            //设置Y轴坐标
            positionArr[i][1] = getWindowHeight(this) / positionArr.length * i;

        }
    }

    private void findView() {
        image_main_mouse = (ImageView) findViewById(R.id.image_main_mouse);
        mTvStop = (TextView) findViewById(R.id.tv_stop);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getWindowWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getWindowHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

}

package com.example.ljw.handlerdemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 主界面介绍Handler简单使用
 **/
public class MainActivity extends AppCompatActivity {
    private TextView text_main_info;
    private ProgressDialog pDialog;
    private ImageView image_main;
    private Handler handler = null;
    public static final String urlString = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517382140&di=f11ad1bb20b04cfb4ebc96d337d0ff21&imgtype=jpg&er=1&src=http%3A%2F%2Fpic4.nipic.com%2F20091217%2F3885730_124701000519_2.jpg";

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_main_info = (TextView) findViewById(R.id.text_main_info);
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading...");
        image_main = (ImageView) findViewById(R.id.image_main);

        // 主线程中的handler对象会处理工作线程中发送的Message。根据Message的不同编号进行相应的操作。
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // 工作线程中要发送的信息全都被放到了Message对象中，也就是上面的参数msg中。要进行操作就要先取出msg中传递的数据。
                switch (msg.what) {
                    case 0:
                        // 工作线程发送what为0的信息代表线程开启了。主线程中相应的显示一个进度对话框
                        pDialog.show();

                        break;
                    case 1:
                        // 工作线程发送what为1的信息代表要线程已经将需要的数据加载完毕。本案例中就需要将该数据获取到，显示到指定ImageView控件中即可。
                        image_main.setImageBitmap((Bitmap) msg.obj);
                        break;
                    case 2:
                        // 工作线程发送what为2的信息代表工作线程结束。本案例中，主线程只需要将进度对话框取消即可。
                        pDialog.dismiss();
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 当工作线程刚开始启动时，希望显示进度对话框，此时让handler发送一个空信息即可。
                // 当发送这个信息后，主线程会回调handler对象中的handleMessage()方法。handleMessage()方法中
                // 会根据message的what种类来执行不同的操作。
                handler.sendEmptyMessage(0);

                // 工作线程执行访问网络，加载网络图片的任务。
                Bitmap bitmap = HttpClientHelper.getImageBitmap(urlString);
                // 工作线程将要发送给主线程的信息都放到一个Message信息对象中。
                // 而Message对象的构建建议使用obtain()方法生成，而不建议用new来生成。
                Message msgMessage = Message.obtain();
                // 将需要传递到主线程的数据放到Message对象的obj属性中，以便于传递到主线程。
                msgMessage.obj = bitmap;
                // Message对象的what属性是为了区别信息种类，而方便主线程中根据这些类别做相应的操作。
                msgMessage.what = 1;
                // handler对象携带着Message中的数据返回到主线程
                handler.sendMessage(msgMessage);

                // handler再发出一个空信息，目的是告诉主线程工作线程的任务执行完毕。一般主线程会接收到这个消息后，
                // 将进度对话框关闭
                handler.sendEmptyMessage(2);
            }
        }).start();

//        //定时更新数据（这里的url还是原来的url,如果有需要可以定义成动态Url）
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(0);
//                //sendEmptyMessage()方法等同于以下几句话。所以。如果只发送一个what，就可以使用sendEmptyMessage()。这样更简单。
//                //Message message = Message.obtain();
//                // Message message2 = handler.obtainMessage();
//                //message.what = 0;
//                //handler.sendMessage(message);
//            }
//        }, 1, 1500);

        text_main_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BeatMouseActivity.class));
            }
        });
    }


}

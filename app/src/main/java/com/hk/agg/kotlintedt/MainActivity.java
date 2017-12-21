package com.hk.agg.kotlintedt;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LoadView LoadView;
    int mProgress = 0;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (mProgress < 40) {
                        mProgress += 1;
                        // 随机800ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(0,
                                new Random().nextInt(800));
                        LoadView.setNewProgress(mProgress);
                    } else {
                        mProgress += 1;
                        // 随机1200ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(0,
                                new Random().nextInt(1200));
                        LoadView.setNewProgress(mProgress);

                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(this,KotlinActivity.class));
        LoadView = this.findViewById(R.id.LoadView);

            mHandler.sendEmptyMessageDelayed(0,1000);
    }
}

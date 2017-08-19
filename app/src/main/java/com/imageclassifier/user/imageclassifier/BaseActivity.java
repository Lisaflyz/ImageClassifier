package com.imageclassifier.user.imageclassifier;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by user on 2017/6/10.
 */


public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext = null;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = this;
        MyApplication.getInstance().mActivityAtack.addActivity(this);//将Activity入栈
        initContentView();
        ButterKnife.bind(this);
        initViews();
        initEvents();

    }

    protected void onStart(){
        super.onStart();
    }

    protected void onDestroy(){
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void finish(){
        super.finish();
        MyApplication.getInstance().mActivityAtack.removeActivity(this);
    }

    protected void init(){}

    protected abstract void initViews();

    protected abstract void initContentView();

    protected abstract void initEvents();
}

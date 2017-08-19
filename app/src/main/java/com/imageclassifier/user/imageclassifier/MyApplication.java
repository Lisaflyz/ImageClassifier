package com.imageclassifier.user.imageclassifier;

import android.app.Application;
import android.content.Context;

/**
 * Created by user on 2017/6/9.
 * 单例模式
 */

//当前应用中有一个活动栈 当前对象，上下文，ActivityStack
    //返回单例

public class MyApplication extends Application{
    protected static MyApplication  myApplication = null;
    protected Context mContext = null;
    public ActivityStack mActivityAtack = null;

    public void onCreate(){
        super.onCreate();
        myApplication = this;
        mContext = getApplicationContext();
        mActivityAtack = new ActivityStack();
        initConfiguration();

    }

   private void initConfiguration(){

   }

   //返回单例
   public static MyApplication getInstance(){
       return myApplication;
   }



}

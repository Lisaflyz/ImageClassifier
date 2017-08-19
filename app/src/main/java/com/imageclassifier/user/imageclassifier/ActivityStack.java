package com.imageclassifier.user.imageclassifier;

import android.app.Activity;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 2017/6/9.
 * 用于管理所有的Activity
 */

public class ActivityStack {
    public static LinkedList<Activity> activityList = null;
    public ActivityStack(){
        activityList = new LinkedList<Activity>();
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public Activity getLastActivity(){
        return activityList.getLast();
    }

    public void removeActivity(Activity activity){
        if(null != activity && activityList.contains(activity)){
            activityList.remove(activity);
        }
    }

    public boolean isActivityRunning(String className){
        if(className != null){
            for(Activity activity : activityList){
                if(activity.getClass().getName().equals(className)){
                    return true;
                }
            }
        }
        return false;
    }

    public void finishAllActivity(){
        for(Activity activity : activityList){
            if(null != activity){
                activity.finish();
            }
        }
    }

    public void AppExit(){
        try{
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

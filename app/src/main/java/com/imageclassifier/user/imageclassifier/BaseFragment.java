package com.imageclassifier.user.imageclassifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by user on 2017/6/11.
 */

public abstract class BaseFragment extends Fragment {

    protected static String TAG = "";

    protected Context mContext = null;

    protected Activity mActivity = null;

    private AlertDialog mAlertDialog;

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

    public void onAttach(Activity activity){//指定当前Fragment的上下文和Activity
        super.onAttach(activity);
        TAG = this.getClass().getSimpleName();
        mContext = activity;
        mActivity = activity;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        if(getContentViewId() != 0){
            return inflater.inflate(getContentViewId(), null);
        }else{
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        initViews(view);
        initData();
        initEvents();
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public void onStart(){
        super.onStart();
    }

    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroyView(){
        super.onDestroyView();
        ButterKnife.unbind(this);

    }

    public void onDestroy(){
        super.onDestroy();
    }

    public void onDetach(){
        super.onDetach();
    }

    protected void initToolbar(Toolbar toolbar){
        ((AppCompatActivity)mActivity).setSupportActionBar(toolbar);
    }

    protected void initToolbar(Toolbar toolbar, String title){
        toolbar.setTitle(title);
        initToolbar(toolbar);
    }

    protected void requestPermission(final String permission, String rationale, final int requestCode){
        if(shouldShowRequestPermissionRationale(permission)){
            showAlertDialog("权限需求",rationale, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    requestPermissions(new String[]{permission}, requestCode);
                }
            }, "确定", null, "取消");
        }else{
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    protected void showAlertDialog(String title, String message,
                                   DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   String positiveText,
                                   DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   String negativeText){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText,onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }


    public String getFragmentName(){
        return TAG;
    }
    public void init(){}

    protected abstract int getContentViewId();

    public abstract void initViews(View view);

    protected void initData(){}

    public abstract void initEvents();


}

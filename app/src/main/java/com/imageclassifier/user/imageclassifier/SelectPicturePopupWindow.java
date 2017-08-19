package com.imageclassifier.user.imageclassifier;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

/**
 * Created by user on 2017/6/11.
 */

public class SelectPicturePopupWindow extends PopupWindow
     implements View.OnClickListener{

    private Button takePhotoBtn, pickPictureBtn, cancelBtn;
    private View mMenuView;
    private PopupWindow popupWindow;
    private OnSelectedListener mOnSelectedListener;

    public SelectPicturePopupWindow(Context context){//构造函数，生成布局，绑定监听器
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.layout_picture_selector, null);
        takePhotoBtn = (Button) mMenuView.findViewById(R.id.picture_selector_take_photo_btn);
        pickPictureBtn = (Button) mMenuView.findViewById(R.id.picture_selector_pick_picture_btn);
        cancelBtn = (Button)mMenuView.findViewById(R.id.picture_selector_cancel_btn);
        takePhotoBtn.setOnClickListener(this);
        pickPictureBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

    }

    //显示动画
    public void showPopupWindow(Activity activity){
        popupWindow = new PopupWindow(mMenuView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER|Gravity.BOTTOM,0,0);
        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        popupWindow.update();
    }

    public void dismissPopupWindow(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.picture_selector_take_photo_btn:
                if(null != mOnSelectedListener){
                    mOnSelectedListener.OnSelected(v,0);
                }
                break;
            case R.id.picture_selector_pick_picture_btn:
                if(null != mOnSelectedListener){
                    mOnSelectedListener.OnSelected(v,1);
                }
                break;

            case R.id.picture_selector_cancel_btn:
                if(null != mOnSelectedListener){
                    mOnSelectedListener.OnSelected(v,2);
                }
                break;
        }
    }

    public void setOnSelectedListener(OnSelectedListener l){
        this.mOnSelectedListener = l;
    }

    public interface OnSelectedListener{
        void OnSelected(View v, int position);
    }

}

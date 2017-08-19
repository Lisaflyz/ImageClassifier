package com.imageclassifier.user.imageclassifier;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends BaseActivity {
    protected void initContentView(){setContentView(R.layout.activity_main);}

    protected void initViews(){
        initMainFragment();
    }

    public void initMainFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BaseFragment mFragment = MainFragment.newInstance();
        transaction.replace(R.id.main_act_container, mFragment, mFragment.getFragmentName());
        transaction.commit();
    }

    protected void initEvents(){}
}

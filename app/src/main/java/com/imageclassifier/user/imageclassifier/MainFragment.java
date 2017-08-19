package com.imageclassifier.user.imageclassifier;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.Bind;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2017/6/11.
 */

public class MainFragment extends PictureSelectFragment{
    public static final String[] classifiedLabels =
            {"baby", "food","party", "scenery", "selfie", "sports"};

    @Bind(R.id.main_frag_picture_iv)
    ImageView mPictureIv;

    @Bind(R.id.resultLabel)
    TextView tResultLabel;
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main;
    }

    @Override
    public void initViews(View view) {

    }

    public void initEvents() {
        // 点击 ImageView区域，弹出PopupWindow 设置图片点击监听
        mPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });
        // 实例化接口对象，同时定义接口的抽象方法
        //解码图片路径，并上传图片
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(String imagePath, Bitmap bitmap) {
                tResultLabel.setText("wait for result...");
               mPictureIv.setImageBitmap(bitmap);
                Log.i("imagePath = " , imagePath);
                uploadImage(imagePath);

            }
        });
    }

    private void uploadImage(String imagePath) {
        new NetworkTask().execute(imagePath);
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetworkTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        //调用图片上传函数，参数为图片路径
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        //返回的结果为result,对result进行处理
        @Override
        protected void onPostExecute(String result) {
            if(!"error".equals(result)) {
                String classifiedID = result.substring(result.length()-1, result.length());
                String classifiedResult = classifiedLabels[Integer.parseInt(classifiedID)];
                result = result.substring(0,result.length()-1);
                Log.i(TAG, "分类的结果为：" + classifiedResult);
                Log.i(TAG, "图片地址 " + Constant.BASE_URL + result);
             //  Glide.with(mContext)
                   //    .load(Constant.BASE_URL + result)
                   //     .into(mPictureIv);

                tResultLabel.setText(classifiedResult);
            }
        }


    }

    private String doPost(String imagePath) {//上传内容，得到相应结果
        OkHttpClient mOkHttpClient = new OkHttpClient();

        String result = "error";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 这里演示添加用户ID
//        builder.addFormDataPart("userId", "20160519142605");
        builder.addFormDataPart("image", imagePath,
                RequestBody.create(MediaType.parse("image/jpeg"), new File(imagePath)));

        RequestBody requestBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder
                .url(Constant.BASE_URL + "/uploadimage")
                .post(requestBody)
                .build();

        Log.d(TAG, "请求地址 " + Constant.BASE_URL + "/uploadimage");
        try{
            Response response = mOkHttpClient.newCall(request).execute();
            Log.d(TAG, "响应码 " + response.code());
            if (response.isSuccessful()) {
                String resultValue = response.body().string();
                Log.d(TAG, "响应体 " + resultValue);
                return resultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



}

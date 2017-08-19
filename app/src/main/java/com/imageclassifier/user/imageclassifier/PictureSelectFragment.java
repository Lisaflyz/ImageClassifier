package com.imageclassifier.user.imageclassifier;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class PictureSelectFragment
        extends BaseFragment implements SelectPicturePopupWindow.OnSelectedListener {

    private static final int GALLERY_REQUEST_CODE = 0;    // 相册选图标记
    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记
    // 拍照临时图片
    private String mTempPhotoPath;
    // 剪切后图像文件
    private Uri mDestinationUri;
    private Uri imageUri;
    public Bitmap bitmap = null;
    /**
     * 选择提示 PopupWindow
     */
    private SelectPicturePopupWindow mSelectPicturePopupWindow;
    /**
     * 图片选择的监听回调
     */
    //类中定义了接口，定义接口对象
    private OnPictureSelectedListener mOnPictureSelectedListener;

    /**
     * 剪切图片
     */
    protected void selectPicture() {
        mSelectPicturePopupWindow.showPopupWindow(mActivity);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDestinationUri = Uri.fromFile(new File(activity.getCacheDir(), "cropImage.jpeg"));//裁切后的文件的地址和文件名组成Uri
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        mSelectPicturePopupWindow = new SelectPicturePopupWindow(mContext);
        mSelectPicturePopupWindow.setOnSelectedListener(this);
    }

    @Override
    public void OnSelected(View v, int position) {//实现接口中的方法
        switch (position) {
            case 0:
                // "拍照"按钮被点击了
                takePhoto();
                break;
            case 1:
                // "从相册选择"按钮被点击了
                pickFromGallery();
                break;
            case 2:
                // "取消"按钮被点击了
                mSelectPicturePopupWindow.dismissPopupWindow();
                break;
        }
    }

    /**
     * 代码中的权限请求
     */
    @Override
    //@NonNull 加上注解，避免非空检查
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //拍照请求
    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_write_storage_rationale),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            mSelectPicturePopupWindow.dismissPopupWindow();
            File file=new File(mTempPhotoPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            imageUri = FileProvider.getUriForFile(this.mContext,
                    "com.imageclassifier.user.imageclassifier.fileprovider", file);
            Intent takeIntent = new Intent();
            takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            takeIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
            startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            mSelectPicturePopupWindow.dismissPopupWindow();
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == mActivity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:   // 调用相机拍照
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mOnPictureSelectedListener.onPictureSelected(mTempPhotoPath, bitmap);
                    break;
                case GALLERY_REQUEST_CODE:  // 直接从相册获取
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String imageGalleryPath = getRealPath(data.getData());
                    mOnPictureSelectedListener.onPictureSelected(imageGalleryPath, bitmap);
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPath(Uri imageUri){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = mContext.getContentResolver().query(imageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;

    }




    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    /**
     * 设置图片选择的回调监听
     *
     * @param l
     */
    public void setOnPictureSelectedListener(OnPictureSelectedListener l) {
        this.mOnPictureSelectedListener = l;
    }

    /**
     * 图片选择的回调接口，内部接口，
     */
    public interface OnPictureSelectedListener {
        /**
         * 图片选择的监听回调
         */
        void onPictureSelected(String imagePath, Bitmap bitmap);
    }

}
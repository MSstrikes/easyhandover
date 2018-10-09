package com.xiang.david.easyhandover;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * Created by msstrike on 2018/10/9.
 */

public class SplashActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_PERMISSION_CODE = 1;

    /**
     * 需要动态申请的权限
     */
    private final String[] mPermissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果是Android 6.0系统，那么就需要对权限做检查
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !EasyPermissions.hasPermissions(this, mPermissions)){
            PermissionRequest.Builder builder = new PermissionRequest.Builder(this,
                    REQUEST_PERMISSION_CODE, mPermissions);
            builder.setRationale("为保证应用正常运行，需要以下应用权限：定位、存储");
            EasyPermissions.requestPermissions(builder.build());
        } else {
            jumpToMainActivity();
        }
    }

    private void jumpToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = true;
        for(int result : grantResults){
            if (PackageManager.PERMISSION_GRANTED != result){
                isGranted = false;
            }
        }

        if (isGranted){
            jumpToMainActivity();
        } else {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.hasPermissions(this, mPermissions)) jumpToMainActivity();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            StringBuilder noticeBuilder = new StringBuilder("请在应用权限中设置以下权限：");
            for (String perm : perms) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(perm)) {
                    noticeBuilder.append("定位、");
                } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(perm)) {
                    noticeBuilder.append("存储、");
                }
            }
            String notice = noticeBuilder.toString();
            notice = notice.substring(0, notice.length() - 1);
            AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(this);
            builder.setTitle("提示")
                    .setRationale(notice)
                    .setPositiveButton("设置");
            builder.build().show();
        }
    }
}

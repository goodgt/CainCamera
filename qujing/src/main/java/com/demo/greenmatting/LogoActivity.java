package com.demo.greenmatting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.demo.greenmatting.network.BaseRes;
import com.demo.greenmatting.network.MyCallback;
import com.demo.greenmatting.network.NetworkRequestUtils;
import com.demo.greenmatting.utils.UpdateAppVersion;
import com.gt.utils.PermissionUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Map<String, Object> params = new HashMap<>();
        params.put("type", 0);
        new NetworkRequestUtils().simpleNetworkRequest("updateInfo", params, new MyCallback<BaseRes<UpdateAppVersion.UpdateInfoRes>>() {
            @Override
            public void loadingDataSuccess(BaseRes<UpdateAppVersion.UpdateInfoRes> result) {
                if (result.getCode() == 20 && result.getResult() != null && !TextUtils.isEmpty(result.getResult().getUrl())) {
                    new UpdateAppVersion(LogoActivity.this, result.getResult(), new UpdateAppVersion.OnUpdateVersionBackListener() {
                        @Override
                        public void onBackListener() {
                            PermissionUtils.requestPermission(LogoActivity.this, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
                                @Override
                                public void onPermissionGranted(int... requestCode) {
                                    startActivity(new Intent(LogoActivity.this, MainNewActivity.class));
                                    finish();
                                }
                            });
                        }
                    }).compareVersion();
                }
            }
        });
    }
}

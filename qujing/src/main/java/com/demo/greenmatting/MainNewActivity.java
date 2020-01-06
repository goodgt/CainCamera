package com.demo.greenmatting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cgfay.camera.activity.CameraActivity;
import com.cgfay.camera.fragment.CameraPreviewFragment;
import com.cgfay.facedetect.engine.FaceTracker;
import com.cgfay.uitls.utils.NotchUtils;
import com.demo.greenmatting.network.AddCookiesInterceptor;
import com.demo.greenmatting.network.BaseRes;
import com.demo.greenmatting.network.MyCallback;
import com.demo.greenmatting.network.NetworkRequestUtils;
import com.demo.greenmatting.utils.OtherUtils;
import com.demo.greenmatting.utils.SPUtil;
import com.google.android.material.navigation.NavigationView;
import com.gt.greenmatting.utils.SavePictureTask;
import com.gt.greenmatting.widget.MagicCameraView;
import com.gt.photopicker.ImageConfig;
import com.gt.photopicker.PhotoPickerActivity;
import com.gt.photopicker.SelectModel;
import com.gt.photopicker.intent.PhotoPickerIntent;
import com.gt.utils.FileUtils;
import com.gt.utils.PermissionUtils;
import com.gt.utils.view.CircleButtonView;
import com.gt.utils.view.OnNoDoubleClickListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainNewActivity extends AppCompatActivity {

    private static final String FRAGMENT_CAMERA = "fragment_camera";
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private CameraPreviewFragment mPreviewFragment;

    private TextView username;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);

        if (null == savedInstanceState && mPreviewFragment == null) {
            mPreviewFragment = new CameraPreviewFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(com.cgfay.cameralibrary.R.id.fragment_container, mPreviewFragment, FRAGMENT_CAMERA)
                    .commit();
        }
        faceTrackerRequestNetwork();

        username = navView.getHeaderView(0).findViewById(R.id.username);
        navView.getHeaderView(0).setOnClickListener(new OnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
//                if (TextUtils.isEmpty(LoginActivity.mUserName))
                startActivityForResult(new Intent(MainNewActivity.this, LoginActivity.class), 888);
            }
        });
//        navView.setItemTextColor(null);
//        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.about:
                        new AlertDialog.Builder(MainNewActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("关于")
                                .setMessage("当前版本：" + OtherUtils.getLocalVersionName(MainNewActivity.this))
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).create().show();
                        break;
                    case R.id.contact_us:
                        startActivity(new Intent(MainNewActivity.this, ContactUsActivity.class));
                        break;
                }
                return true;
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("username", SPUtil.getStringSpVal(this, "username"));
        params.put("pwd", SPUtil.getStringSpVal(this, "pwd"));
        new NetworkRequestUtils().networkRequest("userLogin", params, new MyCallback<BaseRes<LoginActivity.Result>>() {
            @Override
            public void loadingDataSuccess(BaseRes<LoginActivity.Result> result) {
                if (result.getCode() == 20) {
                    AddCookiesInterceptor.token = result.getResult().getToken();

                    username.setText(TextUtils.isEmpty(
                            SPUtil.getStringSpVal(MainNewActivity.this, "username")
                    ) ? "游客" : SPUtil.getStringSpVal(MainNewActivity.this, "username"));
                }
            }
        });
    }

    /**
     * 人脸检测SDK验证，可以替换成自己的SDK
     */
    private void faceTrackerRequestNetwork() {
        new Thread(() -> FaceTracker.requestFaceNetwork(MainNewActivity.this)).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleFullScreen();
        registerHomeReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterHomeReceiver();
    }

    private void handleFullScreen() {
        // 是否全面屏
        if (NotchUtils.hasNotchScreen(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                getWindow().setAttributes(lp);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mPreviewFragment != null && !mPreviewFragment.onBackPressed()) {
            super.onBackPressed();
            overridePendingTransition(0, com.cgfay.cameralibrary.R.anim.anim_slide_down);
        }
    }

    /**
     * 注册服务
     */
    private void registerHomeReceiver() {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomePressReceiver, homeFilter);
    }

    /**
     * 注销服务
     */
    private void unRegisterHomeReceiver() {
        unregisterReceiver(mHomePressReceiver);
    }

    /**
     * Home按键监听服务
     */
    private BroadcastReceiver mHomePressReceiver = new BroadcastReceiver() {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.isEmpty(reason)) {
                    return;
                }
                // 当点击了home键时需要停止预览，防止后台一直持有相机
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    CameraPreviewFragment fragment = (CameraPreviewFragment) getSupportFragmentManager()
                            .findFragmentByTag(FRAGMENT_CAMERA);
                    if (fragment != null) {
                        fragment.cancelRecordIfNeeded();
                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 888:
                username.setText(TextUtils.isEmpty(
                        SPUtil.getStringSpVal(MainNewActivity.this, "username")
                ) ? "游客" : SPUtil.getStringSpVal(MainNewActivity.this, "username"));
                break;
        }
    }
}
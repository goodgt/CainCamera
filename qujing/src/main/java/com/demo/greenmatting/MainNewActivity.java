package com.demo.greenmatting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cgfay.camera.PreviewEngine;
import com.cgfay.camera.fragment.CameraPreviewFragment;
import com.cgfay.camera.listener.OnPreviewCaptureListener;
import com.cgfay.camera.model.AspectRatio;
import com.cgfay.camera.render.gt.MyRenderManager;
import com.cgfay.facedetect.engine.FaceTracker;
import com.cgfay.facedetect.utils.FaceppConstraints;
import com.cgfay.filter.glfilter.resource.FilterHelper;
import com.cgfay.filter.glfilter.resource.MakeupHelper;
import com.cgfay.filter.glfilter.resource.ResourceHelper;
import com.cgfay.image.activity.ImageEditActivity;
import com.cgfay.uitls.utils.NotchUtils;
import com.cgfay.video.activity.VideoEditActivity;
import com.demo.greenmatting.network.AddCookiesInterceptor;
import com.demo.greenmatting.network.BaseRes;
import com.demo.greenmatting.network.MyCallback;
import com.demo.greenmatting.network.NetworkRequestUtils;
import com.demo.greenmatting.utils.OtherUtils;
import com.demo.greenmatting.utils.SPUtil;
import com.google.android.material.navigation.NavigationView;
import com.gt.utils.view.OnNoDoubleClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
            PreviewEngine.from(this)
                    .setCameraRatio(AspectRatio.Ratio_16_9)
                    .showFacePoints(true)
                    .showFps(true)
                    .backCamera(true)
                    .setPreviewCaptureListener((path, type) -> {
                        if (type == OnPreviewCaptureListener.MediaTypePicture) {
                            Intent intent = new Intent(MainNewActivity.this, ImageEditActivity.class);
                            intent.putExtra(ImageEditActivity.IMAGE_PATH, path);
                            intent.putExtra(ImageEditActivity.DELETE_INPUT_FILE, true);
                            startActivity(intent);
                        } else if (type == OnPreviewCaptureListener.MediaTypeVideo) {
                            Intent intent = new Intent(MainNewActivity.this, VideoEditActivity.class);
                            intent.putExtra(VideoEditActivity.VIDEO_PATH, path);
                            startActivity(intent);
                        }
                    });
            mPreviewFragment = new CameraPreviewFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mPreviewFragment, FRAGMENT_CAMERA)
                    .commit();
        }
        faceTrackerRequestNetwork();

        com.gt.utils.PermissionUtils.requestPermission(this, com.gt.utils.PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new com.gt.utils.PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int... requestCode) {
                initResources();
            }
        });

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
        initImageList();
    }

    /**
     * 初始化动态贴纸、滤镜等资源
     */
    private void initResources() {
        new Thread(() -> {
            ResourceHelper.initAssetsResource(this);
            FilterHelper.initAssetsFilter(this);
            MakeupHelper.initAssetsMakeup(this);
        }).start();
    }

    /**
     * 人脸检测SDK验证，可以替换成自己的SDK
     */
    private void faceTrackerRequestNetwork() {
        FaceppConstraints.API_KEY = "6G21adAvo5Cj6wwpOJsuGNanjELJhQUk";
        FaceppConstraints.API_SECRET = "So3kOWQT3rSkL0R6EudE9nijesIjPBUe";
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
            overridePendingTransition(0, R.anim.anim_slide_down);
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

    private void initImageList() {
        RecyclerView imgList = findViewById(R.id.img_list);
        LinearLayoutManager mBeautyLayoutManager = new LinearLayoutManager(this);
        mBeautyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        imgList.setLayoutManager(mBeautyLayoutManager);
        Vector<String> data = getVideoFileName(Environment.getExternalStorageDirectory().getPath() + "/qxb_images", ".jpg");
        imgList.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(MainNewActivity.this).inflate(R.layout.item_img_view, viewGroup, false);
                return new ImageHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageHolder holder = (ImageHolder) viewHolder;
                try {
                    FileInputStream fis = new FileInputStream(data.get(i));
                    holder.imageView.setImageBitmap(BitmapFactory.decodeStream(fis));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyRenderManager.getInstance() != null)
                            MyRenderManager.getInstance().loadTexture(BitmapFactory.decodeFile(data.get(i)));
                    }
                });
            }

            @Override
            public int getItemCount() {
                return data.size();
            }

            class ImageHolder extends RecyclerView.ViewHolder {

                ImageView imageView;

                public ImageHolder(@NonNull View itemView) {
                    super(itemView);
                    imageView = itemView.findViewById(R.id.img);
                }
            }
        });
    }

    public static Vector<String> getVideoFileName(String fileAbsolutePath, String suffix) {
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        if (file.exists()) {
            File[] subFile = file.listFiles();
            if (subFile != null) {
                for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                    // 判断是否为文件夹
                    if (!subFile[iFileLength].isDirectory()) {
                        String filename = subFile[iFileLength].getName();
                        // 判断是否为suffix结尾
                        if (filename.trim().toLowerCase().endsWith(suffix)) {
                            vecFile.add(subFile[iFileLength].getPath());
                        }
                    }
                }
            }
        }
        return vecFile;
    }
}
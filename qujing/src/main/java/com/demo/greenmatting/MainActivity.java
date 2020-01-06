package com.demo.greenmatting;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.root_view)
    FrameLayout rootView;
    @BindView(R.id.camera_view)
    MagicCameraView cameraView;
    @BindView(R.id.circle_button)
    CircleButtonView circleButton;
    @BindView(R.id.seek_bar_view)
    SeekBar seekBarView;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.video_edit_layout)
    FrameLayout videoEditLayout;
    @BindView(R.id.setting_layout)
    FrameLayout settingLayout;
    @BindView(R.id.image_mask)
    ImageView imageMask;
    @BindView(R.id.home_button)
    FrameLayout homeButton;
    @BindView(R.id.switch_photo)
    TextView switchPhoto;
    @BindView(R.id.switch_video)
    TextView switchVideo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.function_layout)
    LinearLayout functionLayout;

    private TextView username;
    private File videoFile;
    private CameraViewSize cameraViewSize = CameraViewSize.SIZE_3V4;
    private State mState = State.PHOTO;
    Timer timer;
    int minute = 0, second = 0;

    enum State {
        PHOTO,
        VIDEO
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        circleButton.setOnClickListener(new CircleButtonView.OnClickListener() {
            @Override
            public void onClick() {
                PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                    @Override
                    public void onPermissionGranted(int... requestCode) {
                        cameraView.savePicture(new SavePictureTask(MainActivity.this, getOutputMediaFile(".jpg"), new SavePictureTask.OnPictureSaveListener() {
                            @Override
                            public void onSaved(final String result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "保存成功 ==》" + result, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }));
                    }
                });
            }
        });
        circleButton.setOnLongClickListener(new CircleButtonView.OnLongClickListener() {
            @Override
            public void onLongClick() {
                PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                    @Override
                    public void onPermissionGranted(int... requestCode) {
                        PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_RECORD_AUDIO, new PermissionUtils.PermissionGrant() {
                            @Override
                            public void onPermissionGranted(int... requestCode) {
                                videoFile = getOutputMediaFile(".mp4");
                                cameraView.startRecord(videoFile);
                            }
                        });
                    }
                });
            }

            @Override
            public void onNoMinRecord(int currentTime) {

            }

            @Override
            public void onRecordFinished() {
                cameraView.stopRecord();
                videoEditLayout.setVisibility(View.VISIBLE);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (mState) {
                    case PHOTO:
                        PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                            @Override
                            public void onPermissionGranted(int... requestCode) {
                                cameraView.savePicture(new SavePictureTask(MainActivity.this, getOutputMediaFile(".jpg"), new SavePictureTask.OnPictureSaveListener() {
                                    @Override
                                    public void onSaved(final String result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "保存成功 ==》" + result, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }));
                            }
                        });
                        break;
                    case VIDEO:
                        if (cameraView.isRecord()) {
                            homeButton.getChildAt(0).setVisibility(View.GONE);
                            cameraView.stopRecord();
                            ((View) switchVideo.getParent()).setVisibility(View.VISIBLE);
                            functionLayout.setVisibility(View.VISIBLE);
                            videoEditLayout.setVisibility(View.VISIBLE);
                            timer.cancel();
                            timer = null;
                            minute = second = 0;
                            tvTime.setText("");
                        } else {
                            PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                                @Override
                                public void onPermissionGranted(int... requestCode) {
                                    PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_RECORD_AUDIO, new PermissionUtils.PermissionGrant() {
                                        @Override
                                        public void onPermissionGranted(int... requestCode) {
                                            videoFile = getOutputMediaFile(".mp4");
                                            cameraView.startRecord(videoFile);
                                            homeButton.getChildAt(0).setVisibility(View.VISIBLE);
                                            ((View) switchVideo.getParent()).setVisibility(View.GONE);
                                            functionLayout.setVisibility(View.GONE);
                                            timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            tvTime.setText(minute + (second < 10 ? ".0" : ".") + second);
                                                            second += 1;
                                                            if (second == 60) {
                                                                minute += 1;
                                                            }
                                                        }
                                                    });
                                                }
                                            }, 0, 1000);
                                        }
                                    });
                                }
                            });
                        }
                        break;
                }
            }
        });
        switchPhoto.setOnClickListener(new OnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mState = State.PHOTO;
                switchVideo.setTextColor(0xffaaaaaa);
                switchPhoto.setTextColor(0xffffffff);
            }
        });
        switchVideo.setOnClickListener(new OnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mState = State.VIDEO;
                switchVideo.setTextColor(0xffffffff);
                switchPhoto.setTextColor(0xffaaaaaa);
            }
        });


        cameraView.setOnErrorListener(new MagicCameraView.OnErrorListener() {
            @Override
            public void onError(final Throwable e) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        });
        username = navView.getHeaderView(0).findViewById(R.id.username);
        navView.getHeaderView(0).setOnClickListener(new OnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
//                if (TextUtils.isEmpty(LoginActivity.mUserName))
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 888);
            }
        });
//        navView.setItemTextColor(null);
//        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.about:
                        new AlertDialog.Builder(MainActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("关于")
                                .setMessage("当前版本：" + OtherUtils.getLocalVersionName(MainActivity.this))
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).create().show();
                        break;
                    case R.id.contact_us:
                        startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                        break;
                }
                return true;
            }
        });

        cameraView.setTransparency(SPUtil.getFloatSpVal(this, "transparency"));
        seekBarView.setProgress((int) (SPUtil.getFloatSpVal(this, "transparency") * seekBarView.getMax()));
        seekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float transparency = (float) progress / seekBarView.getMax();
                SPUtil.changeFloatSp(MainActivity.this, "transparency", transparency);
                cameraView.setTransparency(transparency);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                            SPUtil.getStringSpVal(MainActivity.this, "username")
                    ) ? "游客" : SPUtil.getStringSpVal(MainActivity.this, "username"));
                    cameraView.onResume();
                }
            }
        });

        rootView.post(new Runnable() {
            @Override
            public void run() {
                int width = rootView.getWidth();
                int height = rootView.getHeight();
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
                lp.height = (int) ((float) width / ((float) 3 / (float) 4));
                cameraView.setLayoutParams(lp);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 888:
                username.setText(TextUtils.isEmpty(
                        SPUtil.getStringSpVal(MainActivity.this, "username")
                ) ? "游客" : SPUtil.getStringSpVal(MainActivity.this, "username"));
                cameraView.onResume();
                break;
        }
    }

    public File getOutputMediaFile(String suffix) {
        File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        if ((!localFile.exists()) && (!localFile.mkdirs())) {
            return null;
        }
        String str = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        return new File(localFile.getPath() + File.separator + "GT_" + str + suffix);
    }

    @OnClick({R.id.btn_view_switch, R.id.btn_record_delete, R.id.btn_record_preview, R.id.video_edit_layout, R.id.btn_view_info,
            R.id.btn_view_photo, R.id.btn_setting_view, R.id.setting_layout, R.id.btn_view_size})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_view_switch:
                cameraView.switchCamera();
                break;
            case R.id.btn_record_delete:
            case R.id.video_edit_layout:
                videoFile.delete();
                videoEditLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_record_preview:
                videoEditLayout.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, " 保存成功", Toast.LENGTH_LONG).show();

                Uri contentUri = Uri.fromFile(videoFile);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
                sendBroadcast(mediaScanIntent);
                break;
            case R.id.btn_view_info:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.btn_view_photo:
                PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                    @Override
                    public void onPermissionGranted(int... requestCode) {
                        PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                        intent.setSelectModel(SelectModel.SINGLE);
                        intent.setShowCarema(true);
                        ImageConfig config = new ImageConfig();
                        config.mimeType = new String[]{"image/jpg", "image/jpeg", "image/png", "video/mp4"};
                        config.mediaType = new int[]{MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO};
                        intent.setImageConfig(config);
                        intent.gotoPhotoPickerActivity(MainActivity.this, new PhotoPickerActivity.OnSelectedCallbackListener() {
                            @Override
                            public void onSelectedCallback(ArrayList<String> resultList) {
                                String filePath = resultList.get(0);

                                String type = FileUtils.getMIMEType(filePath).split("/")[0];
                                switch (type) {
                                    case "image":
                                        cameraView.loadTexture(BitmapFactory.decodeFile(filePath));
                                        break;
                                    case "video":
                                        cameraView.startPlaying(filePath);
                                        break;
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.btn_setting_view:
                settingLayout.setVisibility(View.VISIBLE);

//                GPUImageFilter.bitmapToBase64(MainActivity.this, R.drawable.images);
                break;
            case R.id.setting_layout:
                settingLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_view_size:
                ImageView imageView = (ImageView) view;
                int width = rootView.getWidth();
                int height = rootView.getHeight();
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
                switch (cameraViewSize) {
                    case SIZE_9V16:
                        cameraViewSize = CameraViewSize.SIZE_3V4;
                        height = (int) ((float) width / ((float) 3 / (float) 4));
                        lp.height = height;
                        imageView.setImageResource(R.mipmap.ic_3v4);
                        break;
                    case SIZE_3V4:
                        cameraViewSize = CameraViewSize.SIZE_1V1;
                        height = width;
                        lp.height = height;
                        lp.topMargin = (int) ((float) height * ((float) 1 / (float) 5));
                        imageView.setImageResource(R.mipmap.ic_1v1);
                        break;
                    case SIZE_1V1:
                        cameraViewSize = CameraViewSize.SIZE_9V16;
                        lp.width = (int) ((float) height * ((float) 9 / (float) 16));
                        lp.gravity = Gravity.CENTER;
                        imageView.setImageResource(R.mipmap.ic_9v16);
                        break;
                }
                imageMask.setVisibility(View.VISIBLE);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cameraView.updateViewSize(lp, imageMask);
                            }
                        });
                    }
                }, 10);
                break;
        }
    }

    enum CameraViewSize {
        SIZE_9V16,
        SIZE_3V4,
        SIZE_1V1
    }
}
package com.cgfay.camera.render.gt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.SparseArray;
import android.view.Surface;

import com.cgfay.camera.camera.CameraParam;
import com.cgfay.camera.render.RenderIndex;
import com.cgfay.cameralibrary.R;
import com.cgfay.filter.glfilter.base.GLImageFilter;
import com.cgfay.filter.glfilter.base.GLImageOESInputFilter;
import com.cgfay.filter.glfilter.beauty.bean.IBeautify;
import com.cgfay.filter.glfilter.utils.TextureRotationUtils;
import com.gt.greenmatting.jni.GPUMattingFilter;
import com.gt.greenmatting.jni.GPUOesVideoFilter;
import com.gt.greenmatting.utils.Rotation;
import com.gt.greenmatting.utils.TextureRotationUtil;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;

/**
 * 渲染管理器
 */
public final class MyRenderManager extends RenderManager {

    private Context context;

    public GPUMattingFilter mattingFilter;
    private boolean isReplaceBag = false;

    private GPUOesVideoFilter gpuVideoFilter;
    private MediaPlayer player = new MediaPlayer();
    private SurfaceTexture videoTexture;
    private int videoTexturesId = -1;
    private SparseArray<GLImageFilter> mFilterArrays;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private FloatBuffer mDisplayVertexBuffer;
    private FloatBuffer mDisplayTextureBuffer;
    private CameraParam mCameraParam;

    public void init(Context context) {
        super.init(context);
        this.context = context;
        gpuVideoFilter = new GPUOesVideoFilter();
        gpuVideoFilter.init();
        if (videoTexturesId == -1) {
            videoTexturesId = gpuVideoFilter.createTextureID();
            if (videoTexturesId != -1) {
                videoTexture = new SurfaceTexture(videoTexturesId);
//                videoTexture.setOnFrameAvailableListener();

                player.setSurface(new Surface(videoTexture));
            }
        }
        mattingFilter = new GPUMattingFilter();
        mattingFilter.init();

        mCameraParam = CameraParam.getInstance();

        Class<RenderManager> aClass = (Class<RenderManager>) this.getClass().getSuperclass();
        try {
            Field field = aClass.getDeclaredField("mFilterArrays");
            field.setAccessible(true);
            mFilterArrays = (SparseArray<GLImageFilter>) field.get(this);

            field = aClass.getDeclaredField("mVertexBuffer");
            field.setAccessible(true);
            mVertexBuffer = (FloatBuffer) field.get(this);

            field = aClass.getDeclaredField("mTextureBuffer");
            field.setAccessible(true);
            mTextureBuffer = (FloatBuffer) field.get(this);

            field = aClass.getDeclaredField("mDisplayVertexBuffer");
            field.setAccessible(true);
            mDisplayVertexBuffer = (FloatBuffer) field.get(this);

            field = aClass.getDeclaredField("mDisplayTextureBuffer");
            field.setAccessible(true);
            mDisplayTextureBuffer = (FloatBuffer) field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextureSize(int width, int height) {
        super.setTextureSize(width, height);
        if (gpuVideoFilter != null)
            gpuVideoFilter.surfaceChanged(width, height);
        if (mattingFilter != null)
            mattingFilter.surfaceChanged(width, height);
    }

    public void setDisplaySize(int width, int height) {
        super.setDisplaySize(width, height);
        gpuVideoFilter.surfaceChanged(width, height);
        mattingFilter.surfaceChanged(width, height);
    }

    public void release() {
        super.release();
        if (mattingFilter != null)
            mattingFilter.release();
    }

    public int drawFrame(int inputTexture, float[] mMatrix) {
        int currentTexture = inputTexture;
        if (mFilterArrays.get(RenderIndex.CameraIndex) == null
                || mFilterArrays.get(RenderIndex.DisplayIndex) == null) {
            return currentTexture;
        }
        if (mFilterArrays.get(RenderIndex.CameraIndex) instanceof GLImageOESInputFilter) {
            ((GLImageOESInputFilter) mFilterArrays.get(RenderIndex.CameraIndex)).setTextureTransformMatrix(mMatrix);
        }
        currentTexture = mFilterArrays.get(RenderIndex.CameraIndex)
                .drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
        // 如果处于对比状态，不做处理
        if (!mCameraParam.showCompare) {
            // 美颜滤镜
            if (mFilterArrays.get(RenderIndex.BeautyIndex) != null) {
                if (mFilterArrays.get(RenderIndex.BeautyIndex) instanceof IBeautify
                        && mCameraParam.beauty != null) {
                    ((IBeautify) mFilterArrays.get(RenderIndex.BeautyIndex)).onBeauty(mCameraParam.beauty);
                }
                currentTexture = mFilterArrays.get(RenderIndex.BeautyIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
            }

            // 彩妆滤镜
            if (mFilterArrays.get(RenderIndex.MakeupIndex) != null) {
                currentTexture = mFilterArrays.get(RenderIndex.MakeupIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
            }

            // 美型滤镜
            if (mFilterArrays.get(RenderIndex.FaceAdjustIndex) != null) {
                if (mFilterArrays.get(RenderIndex.FaceAdjustIndex) instanceof IBeautify) {
                    ((IBeautify) mFilterArrays.get(RenderIndex.FaceAdjustIndex)).onBeauty(mCameraParam.beauty);
                }
                currentTexture = mFilterArrays.get(RenderIndex.FaceAdjustIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
            }

            // 绘制颜色滤镜
            if (mFilterArrays.get(RenderIndex.FilterIndex) != null) {
                currentTexture = mFilterArrays.get(RenderIndex.FilterIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
            }

            // 资源滤镜，可以是贴纸、滤镜甚至是彩妆类型
            if (mFilterArrays.get(RenderIndex.ResourceIndex) != null) {
                currentTexture = mFilterArrays.get(RenderIndex.ResourceIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
            }

            // 景深
            if (mFilterArrays.get(RenderIndex.DepthBlurIndex) != null) {
                mFilterArrays.get(RenderIndex.DepthBlurIndex).setFilterEnable(mCameraParam.enableDepthBlur);
                currentTexture = mFilterArrays.get(RenderIndex.DepthBlurIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
            }

            // 暗角
            if (mFilterArrays.get(RenderIndex.VignetteIndex) != null) {
                mFilterArrays.get(RenderIndex.VignetteIndex).setFilterEnable(mCameraParam.enableVignette);
                currentTexture = mFilterArrays.get(RenderIndex.VignetteIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer);
            }
            currentTexture = drawFrameBuffer(currentTexture);
        }

        // 显示输出，需要调整视口大小
        mFilterArrays.get(RenderIndex.DisplayIndex).drawFrame(currentTexture, mDisplayVertexBuffer, mDisplayTextureBuffer);

        return currentTexture;
    }

    public int drawFrameBuffer(int currentTexture) {
        loadTexture(R.drawable.a1);
        if (mattingFilter != null && isReplaceBag) {
            if (videoTexture != null && player.isPlaying() && player.getCurrentPosition() > 0) {
                videoTexture.updateTexImage();
                float[] mtx1 = new float[16];
                videoTexture.getTransformMatrix(mtx1);
                gpuVideoFilter.setTextureTransformMatrix(mtx1);
                int videoId = gpuVideoFilter.drawFrameBufferfv(videoTexturesId, TextureRotationUtil.CUBE,
                        TextureRotationUtil.getRotation(Rotation.ROTATION_180, true, false));
                mattingFilter.loadTextureVideo(videoId, player.getVideoWidth(), player.getVideoHeight());
            }
            mattingFilter.setBagTextureRotation(Rotation.ROTATION_180.asInt(), true, false);
            mattingFilter.setWatermarkTextureRotation(Rotation.ROTATION_180.asInt(), true, false);
            currentTexture = mattingFilter.drawFrameBuffer(currentTexture);
        }
        return currentTexture;
    }

    public void loadTexture(int resource) {
        isReplaceBag = true;
        mattingFilter.loadTextureBitmap(context, resource);
        if (player.isPlaying())
            player.pause();
    }

    public void loadTexture(Bitmap bitmap) {
        isReplaceBag = true;
        mattingFilter.loadTextureBitmap(bitmap);
        if (player.isPlaying())
            player.pause();
    }

    public void setTransparency(float value) {
        if (mattingFilter != null)
            mattingFilter.setTransparency(value);
    }

    public void startPlaying(String videoFilePath) {
        isReplaceBag = true;
        try {
            player.reset();
            player.setDataSource(videoFilePath);
            player.setLooping(true);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.start();
    }

    public void closeReplaceBag() {
        isReplaceBag = false;
    }

    public void onResume() {
        if (mattingFilter != null)
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mattingFilter.resume();
                    }
                }).start();
            } catch (Exception e) {

            }
    }
}

package com.cgfay.filter.glfilter.beauty.bean;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 美颜参数
 */
public class BeautyParam {
    // 磨皮程度 0.0 ~ 1.0f
    public float beautyIntensity;
    // 美肤程度 0.0 ~ 0.5f
    public float complexionIntensity;
    // 瘦脸程度 0.0 ~ 1.0f
    public float faceLift;
    // 削脸程度 0.0 ~ 1.0f
    public float faceShave;
    // 小脸 0.0 ~ 1.0f
    public float faceNarrow;
    // 下巴-1.0f ~ 1.0f
    public float chinIntensity;
    // 法令纹 0.0 ~ 1.0f
    public float nasolabialFoldsIntensity;
    // 额头 -1.0f ~ 1.0f
    public float foreheadIntensity;
    // 大眼 0.0f ~ 1.0f
    public float eyeEnlargeIntensity;
    // 眼距 -1.0f ~ 1.0f
    public float eyeDistanceIntensity;
    // 眼角 -1.0f ~ 1.0f
    public float eyeCornerIntensity;
    // 卧蚕 0.0f ~ 1.0f
    public float eyeFurrowsIntensity;
    // 眼袋 0.0 ~ 1.0f
    public float eyeBagsIntensity;
    // 亮眼 0.0 ~ 1.0f
    public float eyeBrightIntensity;
    // 瘦鼻 0.0 ~ 1.0f
    public float noseThinIntensity;
    // 鼻翼 0.0 ~ 1.0f
    public float alaeIntensity;
    // 长鼻子 0.0 ~ 1.0f
    public float proboscisIntensity;
    // 嘴型 0.0 ~ 1.0f;
    public float mouthEnlargeIntensity;
    // 美牙 0.0 ~ 1.0f
    public float teethBeautyIntensity;

    public BeautyParam() {
        reset();
    }

    /**
     * 重置为默认参数
     */
    public void reset() {
        beautyIntensity = 0.5f;
        complexionIntensity = 0.5f;
        faceLift = 0.0f;
        faceShave = 0.0f;
        faceNarrow = 0.0f;
        chinIntensity = 0.0f;
        nasolabialFoldsIntensity = 0.0f;
        foreheadIntensity = 0.0f;
        eyeEnlargeIntensity = 0.0f;
        eyeDistanceIntensity = 0.0f;
        eyeCornerIntensity = 0.0f;
        eyeFurrowsIntensity = 0.0f;
        eyeBagsIntensity = 0.0f;
        eyeBrightIntensity = 0.0f;
        noseThinIntensity = 0.0f;
        alaeIntensity = 0.0f;
        proboscisIntensity = 0.0f;
        mouthEnlargeIntensity = 0.0f;
        teethBeautyIntensity = 0.0f;
    }

    public void resetSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("BeautyParam", Context.MODE_MULTI_PROCESS);
        beautyIntensity = sp.getFloat("0", 0.5f);
        complexionIntensity = sp.getFloat("1", 0.5f);
        faceLift = sp.getFloat("2", 0.0f);
        faceShave = sp.getFloat("3", 0.0f);
        faceNarrow = sp.getFloat("4", 0.0f);
        chinIntensity = sp.getFloat("5", 0.5f);
        nasolabialFoldsIntensity = sp.getFloat("6", 0.0f);
        foreheadIntensity = sp.getFloat("7", 0.0f);
        eyeEnlargeIntensity = sp.getFloat("8", 0.0f);
        eyeDistanceIntensity = sp.getFloat("9", 0.0f);
        eyeCornerIntensity = sp.getFloat("10", 0.0f);
        eyeFurrowsIntensity = sp.getFloat("11", 0.0f);
        eyeBagsIntensity = sp.getFloat("12", 0.0f);
        eyeBrightIntensity = sp.getFloat("13", 0.0f);
        noseThinIntensity = sp.getFloat("14", 0.0f);
        alaeIntensity = sp.getFloat("15", 0.0f);
        proboscisIntensity = sp.getFloat("16", 0.0f);
        mouthEnlargeIntensity = sp.getFloat("17", 0.0f);
        teethBeautyIntensity = sp.getFloat("18", 0.0f);
    }
}

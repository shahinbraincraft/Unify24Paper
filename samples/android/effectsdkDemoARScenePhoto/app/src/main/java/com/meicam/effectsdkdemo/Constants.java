package com.meicam.effectsdkdemo;


/**
 * Created by admin on 2018-5-29.
 */

public class Constants {

    public static final int EDIT_MODE_CAPTION = 0;
    public static final int EDIT_MODE_STICKER = 1;
    public static final int EDIT_MODE_WATERMARK = 2;
    public static final int EDIT_MODE_THEMECAPTION = 3;
    public static final int EDIT_MODE_COMPOUND_CAPTION = 4;
    public static final int EDIT_MODE_EFFECT = 5;
    public static final int EDIT_MODE_TRANSITION = 6;

    /**
     * Click duration in microseconds
     */
    //
    public final static int HANDCLICK_DURATION = 200;
    /*
    * touch移动距离，单位像素值
    * touch movement distance, unit pixel value
    * */
    public final static double HANDMOVE_DISTANCE = 10.0;

    //调节属性待添加
    public final static String FX_COLOR_PROPERTY_DENOISE = "Denoise";
    public final static String FX_COLOR_PROPERTY_DEFINITION = "Definition";
    public final static String FX_COLOR_PROPERTY_BASIC = "BasicImageAdjust"; //亮度，对比度，饱和度，高光，阴影，褪色
    public final static String FX_ADJUST_KEY_INTENSITY = "Intensity";  //噪点程度 0 ~ 1   0.5
    public final static String FX_ADJUST_KEY_EXPOSURE = "Exposure";
    public final static String FX_ADJUST_KEY_HIGHLIGHT = "Highlight";  //高光 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_SHADOW = "Shadow";        //阴影 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_BRIGHTNESS = "Brightness";  //亮度 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_CONTRAST = "Contrast";     //对比度 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_BLACKPOINT = "Blackpoint";  //褪色 -10 ~ 10 0
    public final static String FX_ADJUST_KEY_SATURATION = "Saturation";  //饱和度 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_VIBRANCE = "Vibrance";
    public final static String FX_ADJUST_KEY_TINT = "Tint";
    public final static String FX_ADJUST_KEY_TEMPERATURE = "Temperature";
    /**
     * 暗角
     * Vignette
     */
    public final static String FX_VIGNETTE = "Vignette";
    public final static String FX_VIGNETTE_DEGREE = "Degree";
    /**
     * 锐度
     * Sharpen
     */
    public final static String FX_SHARPEN = "Sharpen";
    public final static String FX_SHARPEN_AMOUNT = "Amount";

}

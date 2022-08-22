package com.meishe.fxplugin.utils;


public class Constants {
    /**
     * sp中用到的key
     * key used in sp
     */
    public static final String KEY_PARAMTER = "paramter";

    public static final int EDIT_MODE_CAPTION = 0;
    public static final int EDIT_MODE_STICKER = 1;
    public static final int EDIT_MODE_WATERMARK = 2;
    public static final int EDIT_MODE_THEMECAPTION = 3;
    public static final int EDIT_MODE_COMPOUND_CAPTION = 4;

    public static final long NS_TIME_BASE = 1000000;

    public static final int MEDIA_TYPE_AUDIO = 1;

    public static final int ACTIVITY_START_CODE_MUSIC_SINGLE = 100;
    public static final int ACTIVITY_START_CODE_MUSIC_MULTI = 101;

    public static final String START_ACTIVITY_FROM_CAPTURE = "start_activity_from_capture";
    public static final String CAN_USE_ARFACE_FROM_MAIN = "can_use_arface_from_main";

    /*
    * 从主页面进入视频选择页面
    * Enter video selection page from main page
    * */
    public static final int VISIT_FROM_MAIN_ACTIVITY = 1001;
    /*
    * 从片段编辑页面进入视频选择页面
    * Go to video selection page from clip editing page
    * */
    public static final int FROMCLIPEDITACTIVITYTOVISIT = 1002;
    /*
    * 从画中画面进入视频选择页面
    * Enter the video selection page from the picture-in-picture
    * */
    public static final int FROMPICINPICACTIVITYTOVISIT = 1003;

    /**
     * 图片运动，区域显示
     * Picture movement, area display
     */
    public static final int EDIT_MODE_PHOTO_AREA_DISPLAY = 2001;
    /*
    * 图片运动，全图显示
    * Picture movement, full picture display
    * */
    public static final int EDIT_MODE_PHOTO_TOTAL_DISPLAY = 2002;

    /**
     * 自定义贴纸类型
     * Custom sticker type
     */
    public static final int CUSTOMSTICKER_EDIT_FREE_MODE = 2003;//Free
    public static final int CUSTOMSTICKER_EDIT_CIRCLE_MODE = 2004;//Circle
    public static final int CUSTOMSTICKER_EDIT_SQUARE_MODE = 2005;//Square

    /**
     * 无特效的ID
     * No effect ID
     */
    public static final String NO_FX = "None";

    /**
     * music
     */
    public static final String MUSIC_EXTRA_AUDIOCLIP = "extra";
    public static final String MUSIC_EXTRA_LAST_AUDIOCLIP = "extra_last";
    public static final long MUSIC_MIN_DURATION = 1000000;

    public static final String SELECT_MUSIC_FROM = "select_music_from";
    public static final int SELECT_MUSIC_FROM_DOUYIN = 5001;
    public static final int SELECT_MUSIC_FROM_EDIT = 5002;
    public static final int SELECT_MUSIC_FROM_MUSICLYRICS = 5003;

    /**
     * 视音频音量值
     *
     * Video and audio volume value
     */
    public static final float VIDEOVOLUME_DEFAULTVALUE = 1.0f;
    public static final float VIDEOVOLUME_MAXVOLUMEVALUE = 2.0f;
    public static final int VIDEOVOLUME_MAXSEEKBAR_VALUE = 100;

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

    public final static String FX_TRANSFORM_2D = "Transform 2D";
    public final static String FX_TRANSFORM_2D_SCALE_X = "Scale X";
    public final static String FX_TRANSFORM_2D_SCALE_Y = "Scale Y";
    /**
     * Color Property 颜色属性
     *
     * Color Property
     */
    public final static String FX_COLOR_PROPERTY = "Color Property";
    public final static String FX_COLOR_PROPERTY_BRIGHTNESS = "Brightness";
    public final static String FX_COLOR_PROPERTY_CONTRAST = "Contrast";
    public final static String FX_COLOR_PROPERTY_SATURATION = "Saturation";
    //todo:调节属性待添加
    public final static String FX_COLOR_PROPERTY_TODO = "To do";
    public final static String FX_COLOR_PROPERTY_DENOISE = "Denoise";
    public final static String FX_COLOR_PROPERTY_DEFINITION = "Definition";
    public final static String FX_COLOR_PROPERTY_BASIC = "BasicImageAdjust";
    public final static String FX_ADJUST_KEY_INTENSITY = "Intensity";
    public final static String FX_ADJUST_KEY_EXPOSURE = "Exposure";
    public final static String FX_ADJUST_KEY_HIGHLIGHT = "Highlight";
    public final static String FX_ADJUST_KEY_SHADOW = "Shadow";
    public final static String FX_ADJUST_KEY_BRIGHTNESS = "Brightness";
    public final static String FX_ADJUST_KEY_CONTRAST = "Contrast";
    public final static String FX_ADJUST_KEY_BLACKPOINT = "Blackpoint";
    public final static String FX_ADJUST_KEY_SATURATION = "Saturation";
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

    public final static String[] CaptionColors = {
            "#ffffffff", "#ff000000", "#ffd0021b",
            "#ff4169e1", "#ff05d109", "#ff02c9ff",
            "#ff9013fe", "#ff8b6508", "#ffff0080",
            "#ff02F78E", "#ff00FFFF", "#ffFFD709",
            "#ff4876FF", "#ff19FF2F", "#ffDA70D6",
            "#ffFF6347", "#ff5B45AE", "#ff8B1C62",
            "#ff8B7500", "#ff228B22", "#ffC0FF3E",
            "#ff00BFFF", "#ffABABAB", "#ff6495ED",
            "#ff0000E3", "#ffE066FF", "#ffF08080"
    };

    public final static String[] FilterColors = {
            "#80d0021b", "#804169e1", "#8005d109",
            "#8002c9ff", "#809013fe", "#808b6508",
            "#80ff0080", "#8002F78E", "#8000FFFF",
            "#80FFD709", "#804876FF", "#8019FF2F",
            "#80DA70D6", "#80FF6347", "#805B45AE",
            "#808B1C62", "#808B7500", "#80228B22",
            "#80C0FF3E", "#8000BFFF", "#80ABABAB",
            "#806495ED", "#800000E3", "#80E066FF",
            "#80F08080"
    };

    /**
     * 素材下载状态值
     * Material download status value
     */
    public static final int ASSET_LIST_REQUEST_SUCCESS = 106;
    public static final int ASSET_LIST_REQUEST_FAILED = 107;
    public static final int ASSET_DOWNLOAD_SUCCESS = 108;
    public static final int ASSET_DOWNLOAD_FAILED = 109;
    public static final int ASSET_DOWNLOAD_INPROGRESS = 110;
    public static final int ASSET_DOWNLOAD_START_TIMER = 111;

    /**
     * 拍摄
     *
     * Shoot
     */
    public static final int RECORD_TYPE_NULL = 3000;
    public static final int RECORD_TYPE_PICTURE = 3001;
    public static final int RECORD_TYPE_VIDEO = 3002;

    public static final String SELECT_MEDIA_FROM = "select_media_from"; // key
    /*
    *  从水印入口进入单个图片选择页面
    * Enter the single image selection page from the watermark entrance
    * */
    public static final int SELECT_IMAGE_FROM_WATER_MARK = 4001;
    /*
    * 从制作封面入口进入单个图片选择页面
    * Enter the single picture selection page from the production cover entrance
    * */
    public static final int SELECT_IMAGE_FROM_MAKE_COVER = 4002;
    /*
    * 从自定义贴纸入口进入单个图片选择页面
    * Go to the single picture selection page from the custom sticker entrance
    * */
    public static final int SELECT_IMAGE_FROM_CUSTOM_STICKER = 4003;
    /*
    * 从视频拍摄入口进入单个视频选择页面
    * Enter the single video selection page from the video shooting portal
    * */
    public static final int SELECT_VIDEO_FROM_DOUYINCAPTURE = 4004;

    /*
    *  从翻转字幕页面入口进入视频选择选择视频
    * Select the video from the flip subtitle page entry
    * */
    public static final int SELECT_VIDEO_FROM_FLIP_CAPTION = 4005;
    /*
    * 从音乐歌词入口进入视频选择选择视频
    * Select video from music lyrics entry
    * */
    public static final int SELECT_VIDEO_FROM_MUSIC_LYRICS = 4006;
    /*
    * 从影集入口进入视频选择选择视频
    * Enter video from album entrance
    * */
    public static final int SELECT_VIDEO_FROM_PHOTO_ALBUM = 4007;

    /*
    * 从粒子入口进入视频选择页面
    * Enter the video selection page from the particle portal
    * */
    public static final int SELECT_VIDEO_FROM_PARTICLE = 4010;

    /**
     * 拍摄-美型
     * Shooting,Beauty
     */
    public static final double NORMAL_VELUE_INTENSITY_FORHEAD = 0.5;
    public static final double NORMAL_VELUE_INTENSITY_CHIN = 0.5;
    public static final double NORMAL_VELUE_INTENSITY_MOUTH = 0.5;
    /**
     * 拍摄,变焦、曝光
     * Shoot, zoom, exposure
     */
    public static final int CAPTURE_TYPE_ZOOM = 2;
    public static final int CAPTURE_TYPE_EXPOSE = 3;
    /**
     * 拍摄-最小录制时长
     * Shooting, minimum recording duration
     */
    public static final int MIN_RECORD_DURATION = 1000000;

    /**
     * 人脸类型
     *
     * Face type
     */
    public static final int HUMAN_AI_TYPE_NONE = 0;//SDK regular version
    public static final int HUMAN_AI_TYPE_MS = 1;//SDK meishe face module
    public static final int HUMAN_AI_TYPE_FU = 2;//FU

    /**
     * 构建类型
     * Build type
     */
    public static final String BUILD_HUMAN_AI_TYPE_NONE = "NONE";//SDK regular version
    public static final String BUILD_HUMAN_AI_TYPE_MS = "MS";//SDK meishe face module
    public static final String BUILD_HUMAN_AI_TYPE_FU = "FaceU";//FU

    /**
     * Mimo
     */
    public static boolean MIMO_HAVE_REPLACE_ASSETS =false;
}

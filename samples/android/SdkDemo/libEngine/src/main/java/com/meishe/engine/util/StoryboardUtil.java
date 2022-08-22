package com.meishe.engine.util;

import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.CutData;
import com.meishe.engine.constant.NvsConstants;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * author：yangtailin on 2020/6/23 11:00
 */
public class StoryboardUtil {
    private final static String TAG = "StoryboardUtil";
    public final static String STORYBOARD_KEY_SCALE_X = "scaleX";
    public final static String STORYBOARD_KEY_SCALE_Y = "scaleY";
    public final static String STORYBOARD_KEY_ROTATION_Z = "rotationZ";
    public final static String STORYBOARD_KEY_TRANS_X = "transX";
    public final static String STORYBOARD_KEY_TRANS_Y = "transY";


}

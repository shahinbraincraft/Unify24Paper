package com.meishe.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.reflect.Field;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/29
 *     desc  : utils about toast
 * </pre>
 * 吐司工具类
 * Toast tool class
 */
public final class ToastUtils {

    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final String NULL = "null";

    private static IToast iToast;
    private static int sGravity = -1;
    private static int sXOffset = -1;
    private static int sYOffset = -1;
    private static int sBgColor = COLOR_DEFAULT;
    private static int sBgResource = -1;
    private static int sMsgColor = COLOR_DEFAULT;
    private static int sMsgTextSize = -1;

    private ToastUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
        sGravity = gravity;
        sXOffset = xOffset;
        sYOffset = yOffset;
    }

    public static void setBgColor(@ColorInt final int backgroundColor) {
        sBgColor = backgroundColor;
    }

    public static void setBgResource(@DrawableRes final int bgResource) {
        sBgResource = bgResource;
    }

    public static void setMsgColor(@ColorInt final int msgColor) {
        sMsgColor = msgColor;
    }

    public static void setMsgTextSize(final int textSize) {
        sMsgTextSize = textSize;
    }

    public static void showShort(final CharSequence text) {
        Toast.makeText(Utils.getApp(),text,Toast.LENGTH_SHORT).show();
    }

    public static void showShort(@StringRes final int resId) {
        CharSequence text = Utils.getApp().getResources().getText(resId);
        Toast.makeText(Utils.getApp(),text,Toast.LENGTH_SHORT).show();
    }

    public static void showShort(@StringRes final int resId, final Object... args) {
        show(resId, Toast.LENGTH_SHORT, args);
    }

    public static void showShort(final String format, final Object... args) {
        show(format, Toast.LENGTH_SHORT, args);
    }


    public static void showLong(final CharSequence text) {
        show(text == null ? NULL : text, Toast.LENGTH_LONG);
    }


    public static void showLong(@StringRes final int resId) {
        show(resId, Toast.LENGTH_LONG);
    }


    public static void showLong(@StringRes final int resId, final Object... args) {
        show(resId, Toast.LENGTH_LONG, args);
    }


    public static void showLong(final String format, final Object... args) {
        show(format, Toast.LENGTH_LONG, args);
    }


    public static View showCustomShort(@LayoutRes final int layoutId) {
        return showCustomShort(getView(layoutId));
    }


    public static View showCustomShort(final View view) {
        show(view, Toast.LENGTH_SHORT);
        return view;
    }


    public static View showCustomLong(@LayoutRes final int layoutId) {
        return showCustomLong(getView(layoutId));
    }


    public static View showCustomLong(final View view) {
        show(view, Toast.LENGTH_LONG);
        return view;
    }

    /**
     * Cancel the toast.
     * 取消土司
     */
    public static void cancel() {
        if (iToast != null) {
            iToast.cancel();
        }
    }

    private static void show(final int resId, final int duration) {
        show(resId, duration, (Object) null);
    }

    private static void show(final int resId, final int duration, final Object... args) {
        try {
            CharSequence text = Utils.getApp().getResources().getText(resId);
            if (args != null && args.length > 0) {
                text = String.format(text.toString(), args);
            }
            show(text, duration);
        } catch (Exception ignore) {
            show(String.valueOf(resId), duration);
        }
    }

    private static void show(final String format, final int duration, final Object... args) {
        String text = format;
        if (text == null) {
            text = NULL;
        } else {
            if (args != null && args.length > 0) {
                text = String.format(format, args);
            }
        }
        show(text, duration);
    }

    private static void show(final CharSequence text, final int duration) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                iToast = ToastFactory.makeToast(Utils.getApp(), text, duration);
                final View toastView = iToast.getView();
                if (toastView == null) {
                    return;
                }
                final TextView tvMessage = toastView.findViewById(android.R.id.message);
                if (sMsgColor != COLOR_DEFAULT) {
                    tvMessage.setTextColor(sMsgColor);
                }
                if (sMsgTextSize != -1) {
                    tvMessage.setTextSize(sMsgTextSize);
                }
                if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                    iToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setBg(tvMessage);
                iToast.show();
            }
        });
    }

    private static void show(final View view, final int duration) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancel();
                iToast = ToastFactory.newToast(Utils.getApp());
                iToast.setView(view);
                iToast.setDuration(duration);
                if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                    iToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setBg();
                iToast.show();
            }
        });
    }

    private static void setBg() {
        if (sBgResource != -1) {
            final View toastView = iToast.getView();
            toastView.setBackgroundResource(sBgResource);
        } else if (sBgColor != COLOR_DEFAULT) {
            final View toastView = iToast.getView();
            Drawable background = toastView.getBackground();
            if (background != null) {
                background.setColorFilter(
                        new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
                );
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    toastView.setBackground(new ColorDrawable(sBgColor));
                } else {
                    toastView.setBackgroundDrawable(new ColorDrawable(sBgColor));
                }
            }
        }
    }

    private static void setBg(final TextView tvMsg) {
        if (sBgResource != -1) {
            final View toastView = iToast.getView();
            toastView.setBackgroundResource(sBgResource);
            tvMsg.setBackgroundColor(Color.TRANSPARENT);
        } else if (sBgColor != COLOR_DEFAULT) {
            final View toastView = iToast.getView();
            Drawable tvBg = toastView.getBackground();
            Drawable msgBg = tvMsg.getBackground();
            if (tvBg != null && msgBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
                tvMsg.setBackgroundColor(Color.TRANSPARENT);
            } else if (tvBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else if (msgBg != null) {
                msgBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else {
                toastView.setBackgroundColor(sBgColor);
            }
        }
    }

    private static View getView(@LayoutRes final int layoutId) {
        LayoutInflater inflate =
                (LayoutInflater) Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate.inflate(layoutId, null);
    }

    /**
     * The type Toast factory.
     *  吐司工厂类
     */
    static class ToastFactory {

        static IToast makeToast(Context context, CharSequence text, int duration) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!PermissionUtils.isGrantedDrawOverlays()) {
                        return new SystemToast(makeNormalToast(context, text, duration));
                    }
                }
            }
            return new ToastWithoutNotification(makeNormalToast(context, text, duration));
        }

        static IToast newToast(Context context) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!PermissionUtils.isGrantedDrawOverlays()) {
                        return new SystemToast(new Toast(context));
                    }
                }
            }
            return new ToastWithoutNotification(new Toast(context));
        }

        private static Toast makeNormalToast(Context context, CharSequence text, int duration) {
            @SuppressLint("ShowToast")
            Toast toast = Toast.makeText(context, "", duration);
            toast.setText(text);
            return toast;
        }
    }

    /**
     * The type System toast.
     * 系统吐司类
     */
    static class SystemToast extends AbsToast {

        SystemToast(Toast toast) {
            super(toast);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                try {
                    /*
                    * noinspection JavaReflectionMemberAccess
                    * 没有检查Java反射成员访问
                    * */
                    Field mTNField = Toast.class.getDeclaredField("mTN");
                    mTNField.setAccessible(true);
                    Object mTN = mTNField.get(toast);
                    Field mTNmHandlerField = mTNField.getType().getDeclaredField("mHandler");
                    mTNmHandlerField.setAccessible(true);
                    Handler tnHandler = (Handler) mTNmHandlerField.get(mTN);
                    mTNmHandlerField.set(mTN, new SafeHandler(tnHandler));
                } catch (Exception ignored) {/**/}
            }
        }

        @Override
        public void show() {
            mToast.show();
        }

        @Override
        public void cancel() {
            mToast.cancel();
        }

        /**
         * The type Safe handler.
         * 安全处理程序类
         */
        static class SafeHandler extends Handler {
            private Handler impl;

            SafeHandler(Handler impl) {
                this.impl = impl;
            }

            @Override
            public void handleMessage(Message msg) {
                impl.handleMessage(msg);
            }

            @Override
            public void dispatchMessage(Message msg) {
                try {
                    impl.dispatchMessage(msg);
                } catch (Exception e) {
                    Log.e("ToastUtils", e.toString());
                }
            }
        }
    }

    /**
     * The type Toast without notification.
     * 吐司没有通知
     */
    static class ToastWithoutNotification extends AbsToast {

        private View mView;
        private WindowManager mWM;

        private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

        ToastWithoutNotification(Toast toast) {
            super(toast);
        }

        @Override
        public void show() {
            ThreadUtils.runOnUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    realShow();
                }
            }, 300);
        }

        private void realShow() {
            if (mToast == null) {
                return;
            }
            mView = mToast.getView();
            if (mView == null) {
                return;
            }
            final Context context = mToast.getView().getContext();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            } else if (PermissionUtils.isGrantedDrawOverlays()) {
                mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
            } else {
                new SystemToast(mToast).show();
              /*  Context topActivityOrApp = UtilsBridge.getTopActivityOrApp();
                if (!(topActivityOrApp instanceof Activity)) {
                    Log.w("ToastUtils", "Couldn't get top Activity.");
                    // try to use system toast
                    new SystemToast(mToast).show();
                    return;
                }
                Activity topActivity = (Activity) topActivityOrApp;
                if (topActivity.isFinishing() || topActivity.isDestroyed()) {
                    Log.w("ToastUtils", topActivity + " is useless");
                    // try to use system toast
                    new SystemToast(mToast).show();
                    return;
                }
                mWM = topActivity.getWindowManager();
                mParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
                UtilsBridge.addActivityLifecycleCallbacks(topActivity, getActivityLifecycleCallbacks());*/
            }

            setToastParams();

            try {
                if (mWM != null) {
                    mWM.addView(mView, mParams);
                }
            } catch (Exception ignored) {/**/}

            ThreadUtils.runOnUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    cancel();
                }
            }, mToast.getDuration() == Toast.LENGTH_SHORT ? 2000 : 3500);
        }

        private void setToastParams() {
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.format = PixelFormat.TRANSLUCENT;
            mParams.windowAnimations = android.R.style.Animation_Toast;
            mParams.setTitle("ToastWithoutNotification");
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams.packageName = Utils.getApp().getPackageName();

            mParams.gravity = mToast.getGravity();
            if ((mParams.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((mParams.gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }

            mParams.x = mToast.getXOffset();
            mParams.y = mToast.getYOffset();
            mParams.horizontalMargin = mToast.getHorizontalMargin();
            mParams.verticalMargin = mToast.getVerticalMargin();
        }

        private Utils.ActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
            return new Utils.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {
                    if (iToast == null) {
                        return;
                    }
                    activity.getWindow().getDecorView().setVisibility(View.GONE);
                    iToast.cancel();
                }
            };
        }

        @Override
        public void cancel() {
            try {
                if (mWM != null) {
                    mWM.removeViewImmediate(mView);
                }
            } catch (Exception ignored) {/**/}
            mView = null;
            mWM = null;
            mToast = null;
        }
    }

    /**
     * The type Abs toast.
     * Abs吐司类
     */
    static abstract class AbsToast implements IToast {

        Toast mToast;

        AbsToast(Toast toast) {
            mToast = toast;
        }

        @Override
        public void setView(View view) {
            mToast.setView(view);
        }

        @Override
        public View getView() {
            return mToast.getView();
        }

        @Override
        public void setDuration(int duration) {
            mToast.setDuration(duration);
        }

        @Override
        public void setGravity(int gravity, int xOffset, int yOffset) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }

        @Override
        public void setText(int resId) {
            mToast.setText(resId);
        }

        @Override
        public void setText(CharSequence s) {
            mToast.setText(s);
        }
    }

    /**
     * The interface Toast.
     * 吐司接口
     */
    interface IToast {

        /**
         * Show.
         * 展示
         */
        void show();

        /**
         * Cancel.
         * 取消
         */
        void cancel();

        /**
         * Sets view.
         * 设置视图
         * @param view the view
         */
        void setView(View view);

        /**
         * Gets view.
         * 获取视图
         * @return the view
         */
        View getView();

        /**
         * Sets  duration.
         * 设置时长
         * @param duration the duration
         */
        void setDuration(int duration);

        /**
         * Sets gravity.
         * 设置重力
         * @param gravity the gravity 重力
         * @param xOffset the x offset  x偏移
         * @param yOffset the y offset y偏移
         */
        void setGravity(int gravity, int xOffset, int yOffset);

        /**
         * Sets text.
         * 设置文本
         * @param resId the res id
         */
        void setText(@StringRes int resId);

        void setText(CharSequence s);
    }
}
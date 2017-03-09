package com.amsterly.lovecoder.lovecoder.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.amsterly.lovecoder.lovecoder.App;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UIUtil {
	
	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	
	public static float px2dip(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (px / scale);
	}
	
//	public static int getStatusBarHeight() {
//		final Resources res = Resources.getSystem();
//		int id = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
//		if (id > 0) {
//			return res.getDimensionPixelSize(id);
//		}
//		return 0;
//	}

	public static void setMIUIStatusBarDarkMode(boolean darkmode, Activity activity) {
		Class<? extends Window> clazz = activity.getWindow().getClass();
		try {
			int darkModeFlag = 0;
			Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
			Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
			darkModeFlag = field.getInt(layoutParams);
			Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
			extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void invalidateViewInViewGroup(View rootView){
		if(rootView == null){
			return ;
		}
		if(rootView instanceof ViewGroup){
			ViewGroup rootGroup = (ViewGroup) rootView;
			for(int i = 0 ; i < rootGroup.getChildCount() ;i++){
				invalidateViewInViewGroup(rootGroup.getChildAt(i));
			}
		}else{
			rootView.invalidate();
		}
	}
	public static void copyString(Context context,String text){
    	if(TextUtils.isEmpty(text)){
    		return ;
    	}
    	ClipboardManager clipboard = (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
//        Toast.makeText(context, R.string.text_copied_toast, Toast.LENGTH_SHORT).show();
    }
	
	public static float getTextPaintOffset(Paint paint) {
		FontMetrics fontMetrics = paint.getFontMetrics();
		return -(fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.top;
	}
	
	public static void toastDebug(Context context,String msg){
		if(context == null){
			return ;
		}
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	public static void logDebug(String tag,String msg){
		Log.d(tag, msg);
	}



	public static Resources getResources() {
		return App.sContext.getResources();
	}

	public static int getColor(Context context, int colorId) {
		return ContextCompat.getColor(context, colorId);
	}

	public static Drawable getDrawable(Context context, int colorId) {
		return ContextCompat.getDrawable(context, colorId);
	}

	public static int dipToPx(Context context, int dpId) {

		return (int) context.getResources().getDimension(dpId);

	}

	public static int dipToPx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int pxToDip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static Bitmap takeScreenShot(Activity activity) {

		View decorView = activity.getWindow().getDecorView();
		decorView.setDrawingCacheEnabled(true);
		decorView.buildDrawingCache();
		Rect rect = new Rect();
		decorView.getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		Bitmap cache = decorView.getDrawingCache();
		if (cache == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cache, 0, statusBarHeight, decorView.getMeasuredWidth(), decorView.getMeasuredHeight() - statusBarHeight);
		decorView.destroyDrawingCache();
		return bitmap;
	}


	/**
	 * Gets the width of the display, in pixels.
	 * <p/>
	 * Note that this value should not be used for computing layouts, since a
	 * device will typically have screen decoration (such as a status bar) along
	 * the edges of the display that reduce the amount of application space
	 * available from the size returned here. Layouts should instead use the
	 * window size.
	 * <p/>
	 * The size is adjusted based on the current rotation of the display.
	 * <p/>
	 * The size returned by this method does not necessarily represent the
	 * actual raw size (native resolution) of the display. The returned size may
	 * be adjusted to exclude certain system decoration elements that are always
	 * visible. It may also be scaled to provide compatibility with older
	 * applications that were originally designed for smaller displays.
	 *
	 * @return Screen width in pixels.
	 */
	public static int getScreenWidth(Context context) {
		return getScreenSize(context, null).x;
	}

	/**
	 * Gets the height of the display, in pixels.
	 * <p/>
	 * Note that this value should not be used for computing layouts, since a
	 * device will typically have screen decoration (such as a status bar) along
	 * the edges of the display that reduce the amount of application space
	 * available from the size returned here. Layouts should instead use the
	 * window size.
	 * <p/>
	 * The size is adjusted based on the current rotation of the display.
	 * <p/>
	 * The size returned by this method does not necessarily represent the
	 * actual raw size (native resolution) of the display. The returned size may
	 * be adjusted to exclude certain system decoration elements that are always
	 * visible. It may also be scaled to provide compatibility with older
	 * applications that were originally designed for smaller displays.
	 *
	 * @return Screen height in pixels.
	 */
	public static int getScreenHeight(Context context) {
		return getScreenSize(context, null).y;
	}

	/**
	 * Gets the size of the display, in pixels.
	 * <p/>
	 * Note that this value should not be used for computing layouts, since a
	 * device will typically have screen decoration (such as a status bar) along
	 * the edges of the display that reduce the amount of application space
	 * available from the size returned here. Layouts should instead use the
	 * window size.
	 * <p/>
	 * The size is adjusted based on the current rotation of the display.
	 * <p/>
	 * The size returned by this method does not necessarily represent the
	 * actual raw size (native resolution) of the display. The returned size may
	 * be adjusted to exclude certain system decoration elements that are always
	 * visible. It may also be scaled to provide compatibility with older
	 * applications that were originally designed for smaller displays.
	 *
	 * @param outSize null-ok. If it is null, will create a Point instance inside,
	 *                otherwise use it to fill the output. NOTE if it is not null,
	 *                it will be the returned value.
	 * @return Screen size in pixels, the x is the width, the y is the height.
	 */
	public static Point getScreenSize(Context context, Point outSize) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Point ret = outSize == null ? new Point() : outSize;
		final Display defaultDisplay = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= 13) {
			defaultDisplay.getSize(ret);
		} else {
			defaultDisplay.getSize(ret);
		}
		return ret;
	}

	public static int getDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}

	/**
	 * 获取状态栏高度
	 *
	 * @return
	 */
	public static int getStatusBarHeight() {
		int id = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
		if (id == 0) {
			return 0;
		} else {
			return Resources.getSystem().getDimensionPixelSize(id);
		}
	}
}

package com.demo.videoeditor.util;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static Bitmap getCompressedBitmap(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean isCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
        if (!isCompressed)
            return bitmap;
        Bitmap convertedBitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
        if (convertedBitmap == null)
            return bitmap;
        return convertedBitmap;
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int getScreeWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}


package ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;


public class BlurBuilder {

    private static final float BITMAP_SCALE = 0.6f;

    /**
     * if need raduis > 25, must be call again & againg result bitmap with the blur method | or call this with level {@link #multipleBlur(Context, Bitmap, int)}
     */
    private static final float DEFAULT_RADIUS = 20f;

    /**
     * blur image bit map
     *
     * @param context
     * @param image
     * @return Bitmap for set the result blur to ImageView with `imageView.setImageBitmap(resultBitmap)`
     */
    public static Bitmap blur(Context context, Bitmap image) {
        if (image == null)
            return null;

        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        intrinsicBlur.setRadius(DEFAULT_RADIUS);
        intrinsicBlur.setInput(tmpIn);
        intrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    /**
     * get Bitmap from ImageView to pass {@link #blur(Context, Bitmap)}
     *
     * @param image an instance of ImageView or childs
     * @return Bitmap
     */
    public static Bitmap getBitmapFromImage(@NonNull ImageView image) {
        return ((BitmapDrawable) image.getDrawable()).getBitmap();
    }

    /**
     * set multi level blur to image Bitmap
     *
     * @param context
     * @param imageBitmap
     * @param level       this determines how many levels should be set filter blurred
     * @return
     */
    public static Bitmap multipleBlur(Context context, Bitmap imageBitmap, int level) {
        for (int i = 0; i < level; i++)
            imageBitmap = blur(context, imageBitmap);

        return imageBitmap;
    }

    /**
     * Draw the view into a bitmap.
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Blur", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

}
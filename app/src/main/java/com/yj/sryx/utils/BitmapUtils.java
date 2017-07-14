package com.yj.sryx.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by eason.yang on 2017/7/14.
 */

public class BitmapUtils {
    public static Bitmap convert2Gray(Bitmap bitmap) {
        /**
         *  [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ] When applied to a color [r, g, b, a],
         *  the resulting color is computed as (after clamping)
         *  R' = a*R + b*G + c*B + d*A + e; G' = f*R + g*G + h*B + i*A + j;
         *  B' = k*R + l*G + m*B + n*A + o; A' = p*R + q*G + r*B + s*A + t;
         */
        ColorMatrix colorMatrix = new ColorMatrix();
        /**
         * Set the matrix to affect the saturation(饱和度) of colors.
         * A value of 0 maps the color to gray-scale（灰阶）. 1 is identity
         */
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(filter);

        Bitmap result = bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return result;
    }
}

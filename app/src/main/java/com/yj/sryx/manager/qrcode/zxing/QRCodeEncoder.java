package com.yj.sryx.manager.qrcode.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 作者:copy 邮件:2499551993@qq.com
 * 创建时间:16/4/8 下午11:22
 * 描述:创建二维码图片
 */
public class QRCodeEncoder {
    public static final Map<EncodeHintType, Object> HINTS = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);

    static {
        HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
        HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    }

    private QRCodeEncoder() {
    }

    /**
     * 创建黑色的二维码图片
     *
     * @param content
     * @param size     图片宽高，单位为px
     * @param delegate 创建二维码图片的代理
     */
    public static void encodeQRCode(String content, int size, Delegate delegate) {
        QRCodeEncoder.encodeQRCode(content, size, Color.BLACK, delegate);
    }

    /**
     * 创建指定颜色的二维码图片
     *
     * @param content
     * @param size     图片宽高，单位为px
     * @param color    二维码图片的颜色
     * @param delegate 创建二维码图片的代理
     */
    public static void encodeQRCode(final String content, final int size, final int color, final Delegate delegate) {
        encodeQRCode(content, size, color, null, delegate);
    }

    /**
     * 创建指定颜色的、带logo的二维码图片
     *
     * @param content
     * @param size     图片宽高，单位为px
     * @param color    二维码图片的颜色
     * @param logo     二维码图片的logo
     * @param delegate 创建二维码图片的代理
     */
    public static void encodeQRCode(final String content, final int size, final int color, final Bitmap logo, final Delegate delegate) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, HINTS);
                    int[] pixels = new int[size * size];
                    for (int y = 0; y < size; y++) {
                        for (int x = 0; x < size; x++) {
                            if (matrix.get(x, y)) {
                                pixels[y * size + x] = color;
                            } else {
                                pixels[y * size + x] = Color.WHITE;
                            }
                        }
                    }
                    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
                    return addLogoToQRCode(bitmap, logo);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (delegate != null) {
                    if (bitmap != null) {
                        delegate.onEncodeQRCodeSuccess(bitmap);
                    } else {
                        delegate.onEncodeQRCodeFailure();
                    }
                }
            }
        }.execute();
    }

    /**
     * 添加logo到二维码图片上
     *
     * @param src
     * @param logo
     * @return
     */
    private static Bitmap addLogoToQRCode(Bitmap src, Bitmap logo) {
        if (src == null || logo == null) {
            return src;
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
        }
        return bitmap;
    }

    public interface Delegate {
        /**
         * 创建二维码图片成功
         *
         * @param bitmap
         */
        void onEncodeQRCodeSuccess(Bitmap bitmap);

        /**
         * 创建二维码图片失败
         */
        void onEncodeQRCodeFailure();
    }

    /**
     * 生成条形码
     *
     * @param context
     * @param contents
     *            需要生成的内容
     * @param desiredWidth
     *            生成条形码的宽带
     * @param desiredHeight
     *            生成条形码的高度
     * @param displayCode
     *            是否在条形码下方显示内容
     * @return
     */
    public static Bitmap createBarcode(Context context, String contents,
                                      int desiredWidth, int desiredHeight, boolean displayCode) {
        Bitmap resultBitmap = null;
        /**
         * 图片两端所保留的空白的宽度
         */
        int marginW = 20;
        /**
         * 条形码的编码类型
         */
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

        if (displayCode) {
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
            Bitmap codeBitmap = createCodeBitmap(contents, desiredWidth + 2
                    * marginW, desiredHeight, context);
            resultBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
                    0, desiredHeight));
        } else {
            resultBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
        }

        return resultBitmap;
    }

    /**
     * 生成显示编码的Bitmap
     *
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    protected static Bitmap createCodeBitmap(String contents, int width,
                                            int height, Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    /**
     * 生成条形码的Bitmap
     *
     * @param contents
     *            需要生成的内容
     * @param format
     *            编码格式
     * @param desiredWidth
     * @param desiredHeight
     * @return
     * @throws WriterException
     */
    protected static Bitmap encodeAsBitmap(String contents,
                                           BarcodeFormat format, int desiredWidth, int desiredHeight) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(contents, format, desiredWidth,
                    desiredHeight, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 将两个Bitmap合并成一个
     *
     * @param first
     * @param second
     * @param fromPoint
     *            第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     * @return
     */
    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
                                          PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap.createBitmap(
                first.getWidth() + second.getWidth() + marginW,
                first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }

    public static Bitmap createQRImage(String url, int size) {
        try {
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
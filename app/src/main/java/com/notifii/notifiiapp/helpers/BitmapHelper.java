package com.notifii.notifiiapp.helpers;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.notifii.notifiiapp.utils.NTF_Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * A helper class for all the operations related to bitmap
 */
public class BitmapHelper implements NTF_Constants {

    public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Bitmap getResizedBitmap(Bitmap source, int newWidth, int newHeight) throws NullPointerException {
        if (source != null) {
            int width = source.getWidth();
            int height = source.getHeight();
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bitmap
            matrix.postScale(((float) newWidth) / width, ((float) newHeight) / height);
            // recreate the new Bitmap
            return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
        } else {
            throw new NullPointerException("Inputted Bitmap is NULL");
        }
    }

    public static String getBase64String(Bitmap bitmap) {
        try {
            Bitmap converetdImage = getResizedBitmap(bitmap, 600, (600 * bitmap.getHeight()) / bitmap.getWidth());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            converetdImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return encoded;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getRotatedBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle, 0, 0);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap getResizeBitmap(Bitmap bitmap) {

        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        int width = PIC_WIDTH;
        int height;

        if (bitmapWidth >= width) {
            float percentage = ((float) width) / bitmapWidth;
            height = Math.round(percentage * bitmapHeight);
        } else {
            width = bitmapWidth;
            height = bitmapHeight;
        }

        return getResizedBitmap(bitmap, width, height);

    }

    public static byte[] getByteArrayOfBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static Bitmap invert(Bitmap src)
    {
        int height = src.getHeight();
        int width = src.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        ColorMatrix matrixGrayscale = new ColorMatrix();
        matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    public static Bitmap reverseByVertical(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

}

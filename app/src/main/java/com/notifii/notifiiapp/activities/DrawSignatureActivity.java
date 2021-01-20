/////////////////////////////////////////////////////////////////
// CameraPreviewActivity.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.fragments.LogPackageOutFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DrawSignatureActivity extends NTF_BaseActivity {

    @BindView(R.id.signatureView)
    LinearLayout signatureCanvasLinearLayout;
    @BindView(R.id.done_button)
    Button doneButton;

    private SignaturePanelView signatureCanvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_signature);
        ButterKnife.bind(this);
        doneButton.setEnabled(false);
        signatureCanvasView = new SignaturePanelView(DrawSignatureActivity.this, null);
        signatureCanvasView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        signatureCanvasLinearLayout.addView(signatureCanvasView);
      //  LogPackageOutFragment.isServiceRequ = false;
    }

    @OnClick(R.id.clear_button)
    void onClearClicked() {
        signatureCanvasView.doClearCanvas();
    }

    @OnClick(R.id.done_button)
    void onDoneClicked() {
        signatureCanvasView.doSaveSignature();
    }

    @OnClick(R.id.closeImageView)
    void onCloseClicked() {
        finish();
    }

    private class SignaturePanelView extends View {
        float STROKE_WIDTH = 5f;
        float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();
        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();

        public SignaturePanelView(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }

        public void doClearCanvas() {
            path.reset();
            invalidate();
            signatureCanvasLinearLayout.invalidate();
            doneButton.setEnabled(false);
        }

        public void doSaveSignature() {
            try {
                Matrix matrix = new Matrix();
                matrix.postScale(signatureCanvasView.getWidth(), signatureCanvasView.getHeight());
                final Bitmap gridImageBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_grid_latest);
                Bitmap returnedBitmap = null;
                try {
                    returnedBitmap = Bitmap.createBitmap(signatureCanvasView.getWidth(), signatureCanvasView.getHeight(), gridImageBgBitmap.getConfig());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Bitmap finalReturnedBitmap = returnedBitmap;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalReturnedBitmap != null) {
                            final Canvas canvas = new Canvas(finalReturnedBitmap);
                            signatureCanvasView.setBackgroundResource(R.drawable.bg_grid_latest);
                            Drawable bgDrawable = signatureCanvasView.getBackground();
                            if (bgDrawable != null) {
                                bgDrawable.draw(canvas);
                            } else {
                                canvas.drawColor(Color.TRANSPARENT);
                            }
                            signatureCanvasView.draw(canvas);
                            Extras_Keys.IMAGE = finalReturnedBitmap;
                        }
                    }
                }, 40);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            doneButton.setEnabled(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
            }
            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

    }

}

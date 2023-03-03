package com.example.Test;

import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.graphics.Color;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import androidx.appcompat.app.AppCompatActivity;
public class SimpleDrawingView extends View {
    public double[] point_array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public double x = 0.0;
    // setup initial color
    private final int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaint;
    public SimpleDrawingView(Context context, AttributeSet attr) {
        super(context, attr);
        setupPaint();
    }
    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        //draw the points
        drawPaint.setColor(Color.GREEN);
        for (int i = 0; i < point_array.length; i++) {
            canvas.drawCircle(150  + (i * 15), (float)(190 + point_array[i]), 5, drawPaint);
        }
        drawPaint.setColor(Color.RED);
        canvas.drawCircle(770, (float)(190.0 + x), 10, drawPaint);
        // modify the array
        // move back by one
        for (int i = 0; i < point_array.length - 1; i++) {
            point_array[i] = point_array[i + 1];
        }
        point_array[point_array.length - 1] = x;
    }

}
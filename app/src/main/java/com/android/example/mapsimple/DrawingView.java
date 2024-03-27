package com.android.example.mapsimple; // Use your actual package name

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrawingView extends View {
    private Paint paint = new Paint();
    private Path path = new Path();
    private List<PointF> nodes = new ArrayList<>();
    private float nodeRadius = 20;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5f);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw nodes
        for (PointF node : nodes) {
            canvas.drawCircle(node.x, node.y, nodeRadius, paint);
        }

        canvas.drawPath(path, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            PointF touchPoint = new PointF(event.getX(), event.getY());

            // Check if touch is close to an existing node (for removal)
            for (Iterator<PointF> iterator = nodes.iterator(); iterator.hasNext();) {
                PointF node = iterator.next();
                if (Math.sqrt(Math.pow(touchPoint.x - node.x, 2) + Math.pow(touchPoint.y - node.y, 2)) <= nodeRadius) {
                    // If touch is within nodeRadius, remove the node
                    iterator.remove();
                    invalidate();
                    return true;
                }
            }

            // Add new node
            nodes.add(touchPoint);
            invalidate();
            return true;
        }
        return false;
    }

    public void drawPaths() {
        // Logic to connect nodes with lines
        // This can be triggered by a button press in AdminActivity
        path.reset();
        if (!nodes.isEmpty()) {
            path.moveTo(nodes.get(0).x, nodes.get(0).y);
            for (int i = 1; i < nodes.size(); i++) {
                path.lineTo(nodes.get(i).x, nodes.get(i).y);
            }
        }
        invalidate();
    }

    public void clearDrawing() {
        nodes.clear();
        path.reset();
        invalidate();
    }

    public List<PointF> getNodes() {
        return nodes;
    }



}

package com.android.example.mapsimple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class FloorplanView extends View {
    private Paint paint;
    private Path path;
    private Bitmap floorplanImage;
    private Matrix matrix;


    // Constructor for instantiating the view programmatically
    public FloorplanView(Context context) {
        super(context);
        init(null, 0);
    }

    // Constructor for inflating the view from an XML layout
    public FloorplanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    // Constructor needed for correct inflation from XML with a style
    public FloorplanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Initialize drawing objects
        paint = new Paint();
        paint.setColor(Color.RED); //  path color
        paint.setStrokeWidth(10); //  stroke width
        paint.setStyle(Paint.Style.STROKE);

        // Load the floorplan image
        floorplanImage = BitmapFactory.decodeResource(getResources(), R.drawable.sample_floorplan);
        matrix = new Matrix();


        // Initialize path
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Scale and draw the floorplan image to fit the view
        if (floorplanImage != null) {
            // Calculate the scale factor to fit the image within the view
            float scaleFactor = Math.min((float) getWidth() / floorplanImage.getWidth(),
                    (float) getHeight() / floorplanImage.getHeight());
            matrix.reset();
            matrix.setScale(scaleFactor, scaleFactor);

            // Draw the scaled image
            canvas.drawBitmap(floorplanImage, matrix, null);
        }

        // Draw the path over the floorplan
        // Adjust path drawing code as necessary
//        path.moveTo(300, 300); // Adjust this point according to the scaled image
//        path.lineTo(200, 200); // Adjust this point according to the scaled image
        canvas.drawPath(path, paint);
    }

    public void clearPaths() {
        // Reset the path
        path.reset();
        // Invalidate the view to trigger a redraw
        invalidate();
    }

    public void drawPath(List<PointF> pathCoordinates) {
        if (pathCoordinates == null || pathCoordinates.isEmpty()) {
            return;
        }

        clearPaths(); // Clear existing paths first

        boolean firstPoint = true;
        for (PointF point : pathCoordinates) {
            if (firstPoint) {
                path.moveTo(point.x, point.y);
                firstPoint = false;
            } else {
                path.lineTo(point.x, point.y);
            }
        }

        invalidate(); // Trigger a redraw to show the new path
    }



}

package com.utyf.spline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Utyf on 13.11.2016.
 *
 */

public class RailView extends View implements View.OnTouchListener {

    Paint paint;
    ExtPath path;
    PointF[] pp;
    final static int MAX_DIST = 30;
    PointF shift = new PointF(-120, -120);
    float  scale = 10;
    float railWidth, stairsWidth, stairsStep;
    float[] coordinates;

    public RailView(Context context) {
        super(context);
        init();
    }

    public RailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setOnTouchListener(this);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        //String str="22, 22, 5, 81,189, 86,165, 98,146, 116,132";
        String str="22, 22, 5, 175,132, 193,146, 205,165, 210,189, 201,207";

        str = str.replace(" ", "");
        String[] strs = str.split(",");

        railWidth   = Float.parseFloat( strs[0] ) * scale;
        stairsWidth = Float.parseFloat( strs[1] ) * scale;
        stairsStep  = Float.parseFloat( strs[2] ) * scale;

        pp = new PointF[(strs.length-3)/2];
        for( int i=0; i<strs.length-3; i++ ) {
            PointF p = new PointF();
            p.x = Float.parseFloat( strs[i+3] );
            p.x = (p.x + shift.x)*scale;
            i++;
            p.y = Float.parseFloat( strs[i+3] );
            p.y = (p.y + shift.y)*scale;
            pp[i/2]=p;
        }

        path  = new ExtPath();
        makePath();
    }

    void makePath() {
        path.rewind();
        path.Spline(pp);
// =============================
        int count = pp.length;
        int steps = 4;
        coordinates = new float[(count * steps + 1) * 2];
        for( int i=0;  i<count; i++ ) {
            coordinates[2 * i * steps]     = pp[i].x;
            coordinates[2 * i * steps + 1] = pp[i].y;
        }

        int step = steps;
        while( step > 1 ) {
            coordinates[step]     = (coordinates[0] * 3 + coordinates[2 * step]     * 6 - coordinates[4 * step])     / 8;
            coordinates[step + 1] = (coordinates[1] * 3 + coordinates[2 * step + 1] * 6 - coordinates[4 * step + 1]) / 8;
            coordinates[2 * count * steps - step]     = (coordinates[2 * count * steps]     * 3 + coordinates[2 * count * steps - 2 * step]     * 6 - coordinates[2 * count * steps - 4 * step])     / 8;
            coordinates[2 * count * steps - step + 1] = (coordinates[2 * count * steps + 1] * 3 + coordinates[2 * count * steps - 2 * step + 1] * 6 - coordinates[2 * count * steps - 4 * step + 1]) / 8;
            for( int i=1; i<count * steps % step - 2; i++ ) {
                coordinates[2 * i * step + step]     = (-coordinates[2 * i * step - 2 * step]     + coordinates[2 * i * step]     * 9 + coordinates[2 * i * step + 2 * step]     * 9 - coordinates[2 * i * step + 4 * step])     / 16;
                coordinates[2 * i * step + step + 1] = (-coordinates[2 * i * step - 2 * step + 1] + coordinates[2 * i * step + 1] * 9 + coordinates[2 * i * step + 2 * step + 1] * 9 - coordinates[2 * i * step + 4 * step + 1]) / 16;
            }
            step = step % 2;
        }
        //    Polyline(BitMap, coordinates, PenWidth, False, Smooth, 0, 100);

// =============================
    }

    int getPointNum(int x, int y) {
        for( int i=0; i<pp.length; i++ )
            if(  distance(x,y, (int)pp[i].x,(int)pp[i].y)<MAX_DIST )
                return i;

        return -1;
    }

    int distance(int x1, int y1, int x2, int y2) {
        int x = x2-x1;
        int y = y2-y1;
        return (int)Math.sqrt(x*x+y*y);
    }

    static int pNum=-1;

    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        //logME(ev, "onTouch " + ev.getAction());
        if( pNum==-1 ) {
            int ii = getPointNum((int)ev.getX(), (int)ev.getY());
            if( ii==-1 ) return false;
            pNum = ii;
        } else {
            pp[pNum].set(ev.getX(),ev.getY());
            makePath();
            invalidate();
            if( ev.getAction()==MotionEvent.ACTION_UP )
                pNum=-1;
        }
        return true;
    }

/*    void logME(MotionEvent ev, String tag) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        for (int h = 0; h < historySize; h++) {
            Log.d(tag, " = HIST/ At time :"+ev.getHistoricalEventTime(h));
            for (int p = 0; p < pointerCount; p++) {
                Log.d(tag, "  pointer " +
                        ev.getPointerId(p) +" : ("+  ev.getHistoricalX(p, h) +" , "+ ev.getHistoricalY(p, h)+")");
            }
        }
        Log.d(tag, " = At time :"+ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            Log.d(tag, "  pointer "+ ev.getPointerId(p) +" : ("+ ev.getX(p) +" , "+ ev.getY(p)+")" );
        }
    } //*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas.drawPath(path, paint);

/*        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        for( PointF p : pp )
            canvas.drawOval(p.x-4,p.y-4,p.x+4,p.y+4,paint); //*/

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        for( int i=0; i<coordinates.length; i++ ) {
            float x, y;
            x = coordinates[i];
            y = coordinates[++i];
            canvas.drawOval(x - 4, y - 4, x + 4, y + 4, paint);
        }

    }
}

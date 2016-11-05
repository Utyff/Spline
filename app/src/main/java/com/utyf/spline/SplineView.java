package com.utyf.spline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class SplineView extends View implements View.OnTouchListener {

    Paint paint;
    Path path, path2;
    PointF [] pp;
    final static int MAX_DIST = 30;

    public SplineView(Context context) {
        super(context);
        init();
    }

    public SplineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setOnTouchListener(this);
        paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        pp = new PointF[4];
        pp[0] = new PointF(50,50);
        pp[1] = new PointF(700,50);
        pp[2] = new PointF(100,600);
        pp[3] = new PointF(400,600);
        path = new Path();
        path2 = new Path();
        makePath();
    }

    void makePath() {
        path.rewind();
        path.moveTo(pp[0].x, pp[0].y);
        path.cubicTo(pp[1].x, pp[1].y, pp[2].x, pp[2].y, pp[3].x, pp[3].y);

        path2.rewind();
        path2.moveTo(pp[0].x, pp[0].y);
        path2.lineTo(pp[1].x, pp[1].y);
        path2.lineTo(pp[2].x, pp[2].y);
        path2.lineTo(pp[3].x, pp[3].y);
    }

    int getPointNum(int x, int y) {
        for( int i=0; i<pp.length; i++ ) {
            if(  distance(x,y, (int)pp[i].x,(int)pp[i].y)<MAX_DIST )
                return i;
        }
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
        logME(ev, "onTouch " + ev.getAction());

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

    void logME(MotionEvent ev, String tag) {
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas.drawPath(path, paint);

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2);
        canvas.drawPath(path2, paint);
    }
}

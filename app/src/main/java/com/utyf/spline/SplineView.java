package com.utyf.spline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class SplineView extends View implements View.OnTouchListener {

    Paint paint;
    ExtPath path;
    PointF [] pp;
    final static int MAX_DIST = 30;
    PointF shift = new PointF(-300, -730);
    float  scale = 10;

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

        //String str="921,987, 871,987, 820,993, 779,1001, 750,987, 750,958, 774,941, 780,916, 768,893, 740,888, 697,894, 667,905, 645,907, 630,900, 621,881, 623,860, 645,841, 683,827, 703,809, 715,785, 718,746, 702,708, 680,679, 641,647, 611,630, 578,620, 554,621, 534,626, 514,636, 494,651, 466,683, 448,711, 419,753, 398,779, 371,802, 341,819, 303,821, 274,792, 275,759, 289,741, 322,713, 355,685, 369,663, 377,634, 371,610, 353,597, 307,599, 271,601, 249,595, 233,569, 236,535, 233,518, 215,505, 190,499, 172,484, 158,451, 150,410, 136,383, 109,362, 84,358, 57,358, 17,361";
        String str="380,773, 391,794, 392,818, 383,841, 363,859, 336,866";
        str = str.replace(" ", "");
        String[] strs = str.split(",");
        pp = new PointF[strs.length/2];
        for( int i=0; i<strs.length; i++) {
            PointF p = new PointF();
            p.x = Integer.parseInt( strs[i] );
            p.x = (p.x + shift.x)*scale;
            p.y = Integer.parseInt( strs[++i] );
            p.y = (p.y + shift.y)*scale;
            pp[i/2]=p;
        }

        path  = new ExtPath();
        makePath();
    }

    void makePath() {
        path.rewind();
        path.Spline(pp);
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

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        for( PointF p : pp )
            canvas.drawOval(p.x-4,p.y-4,p.x+4,p.y+4,paint);

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        for( PointF p : path.firstControlPoints )
            canvas.drawOval(p.x-4,p.y-4,p.x+4,p.y+4,paint);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        for( PointF p : path.secondControlPoints )
            canvas.drawOval(p.x-4,p.y-4,p.x+4,p.y+4,paint); //*/
    }
}

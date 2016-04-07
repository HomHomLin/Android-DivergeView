package lib.homhomlib.view.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Linhh on 16/3/28.
 */
public class DivergeView extends View{

    private Bitmap mBitmap;
    private final Random mRandom = new Random();

    private ArrayList<DivergeInfo> mDivergeInfos;
    private boolean mIsDiverge = false;

    private PointF mPtStart;
    private PointF mPtEnd;

    private Paint mPaint;
    private float mDuration = 0.005F;

    private long mAddDuration = 50;

    private long mLastTime = 0;

    public DivergeView(Context context) {
        this(context, null);
    }

    public DivergeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DivergeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //不需要支持wrap_content

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        invalidate();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mBitmap == null){
                return;
            }
            if(mPtStart == null) {
                mPtStart = new PointF(getMeasuredWidth() / 2, getMeasuredHeight() - mBitmap.getHeight());
            }
            invalidate();
        }
    };

    public void start(PointF startPoint){
        setStartPoint(startPoint);
        start();
    }

    public boolean isRunning(){
        return mIsDiverge;
    }

    public void start(){
        mIsDiverge = true;
        if(mDivergeInfos == null){
            mDivergeInfos = new ArrayList<>();
        }
        this.post(mRunnable);
    }

    public void stop(){
        this.removeCallbacks(mRunnable);
        if(mDivergeInfos != null){
            mDivergeInfos.clear();
        }
        mIsDiverge = false;
    }

    public void release(){
        stop();
        mPtEnd = null;
        mPtStart = null;
        mBitmap = null;
        mDivergeInfos = null;
    }

    public void setStartPoint(PointF point){
        mPtStart = point;
    }

    public void setEndPoint(PointF point){
        mPtEnd = point;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    private DivergeInfo createDivergeNode(){
        PointF endPoint = mPtEnd;
        if(endPoint == null){
            endPoint = new PointF(mRandom.nextInt(getMeasuredWidth()),0);
        }
        return new DivergeInfo(
                mPtStart.x,
                mPtStart.y,
                getBreakPointF(2),
                getBreakPointF(1),
                endPoint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap != null && mIsDiverge){
            if(System.currentTimeMillis() - mLastTime >= mAddDuration){
                mDivergeInfos.add(createDivergeNode());
                mLastTime = System.currentTimeMillis();
            }
            for(int i = 0 ; i < mDivergeInfos.size(); i ++){
                DivergeInfo divergeInfo = mDivergeInfos.get(i);
                if(divergeInfo.mY <=  divergeInfo.mEndPoint.y ){
                    mDivergeInfos.remove(i);
                    i--;
                    continue;
                }
                canvas.drawBitmap(mBitmap, divergeInfo.mX, divergeInfo.mY, mPaint);

                float timeLeft = 1.0F - divergeInfo.mDuration;

                divergeInfo.mDuration += mDuration;

                PointF point = new PointF();

                float time1 = timeLeft * timeLeft * timeLeft;
                float time2 = 3 * timeLeft * timeLeft * divergeInfo.mDuration;
                float time3 = 3 * timeLeft * divergeInfo.mDuration * divergeInfo.mDuration;
                float time4 = divergeInfo.mDuration * divergeInfo.mDuration * divergeInfo.mDuration;
                point.x = time1 * (mPtStart.x)
                        + time2 * (divergeInfo.mBreakPoint1.x)
                        + time3 * (divergeInfo.mBreakPoint2.x)
                        + time4 * (divergeInfo.mEndPoint.x);

                mDivergeInfos.get(i).mX = point.x;

                point.y = time1 * (mPtStart.y)
                        + time2 * (divergeInfo.mBreakPoint1.y)
                        + time3 * (divergeInfo.mBreakPoint2.y)
                        + time4 * (divergeInfo.mEndPoint.y);

                divergeInfo.mY = point.y;
            }
            invalidate();
        }
    }

    private PointF getBreakPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt(getMeasuredWidth());
        pointF.y = mRandom.nextInt(getMeasuredHeight() - 100)/scale;
        return pointF;
    }

    public class DivergeInfo {
        public float mDuration;
        public PointF mBreakPoint1;
        public PointF mBreakPoint2;
        public PointF mEndPoint;
        public float mX;
        public float mY;
        public DivergeInfo(float x, float y, PointF breakPoint1, PointF breakPoint2, PointF endPoint){
            mDuration = 0.0f;
            mEndPoint = endPoint;
            mX = x;
            mY = y;
            mBreakPoint1 = breakPoint1;
            mBreakPoint2 = breakPoint2;
        }
    }

}

package lib.homhomlib.view2;

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

    private final Random mRandom = new Random();

    private ArrayList<DivergeInfo> mDivergeInfos;
    private boolean mIsDiverge = false;

    private PointF mPtStart;
    private PointF mPtEnd;

    private Paint mPaint;

    private static final float mDuration = 0.010F;
    private static final int mDefaultHeight = 100;
//    private static final int mDefaultWidth = 100;
//    private static final int mAlphaOffset = 50;

    private DivergeViewProvider mDivergeViewProvider;

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

    public interface DivergeViewProvider{
        public Bitmap getBitmap(Object obj);
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

    public void setDivergeViewProvider(DivergeViewProvider divergeViewProvider){
        mDivergeViewProvider = divergeViewProvider;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public void start(PointF startPoint){
        setStartPoint(startPoint);
        start();
    }

    public PointF getStartPoint(){
        return mPtStart;
    }

    public boolean isRunning(){
        return mIsDiverge;
    }

    public void setDiverges(Object... objs){
        if(mDivergeInfos == null){
            mDivergeInfos = new ArrayList<>();
        }
        for(Object obj : objs){
            mDivergeInfos.add(createDivergeNode(obj));
        }
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
        if(mIsDiverge) {
            release();
        }
    }

    private DivergeInfo createDivergeNode(Object type){
        PointF endPoint = mPtEnd;
        if(endPoint == null){
            endPoint = new PointF(mRandom.nextInt(getMeasuredWidth()),0);
        }
//        int height = mDivergeViewProvider == null ? mDefaultHeight : mDivergeViewProvider.getBitmap(type).getHeight();
        if(mPtStart == null) {
            mPtStart = new PointF(getMeasuredWidth() / 2, getMeasuredHeight() - mDefaultHeight);//默认起始高度
        }
        return new DivergeInfo(
                mPtStart.x,
                mPtStart.y,
                getBreakPointF(2, 3),
                endPoint,
                type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mDivergeViewProvider == null){
            return;
        }
        if(mDivergeInfos == null){
            return;
        }
        if(mIsDiverge){
            for(int i = 0 ; i < mDivergeInfos.size(); i ++){
                DivergeInfo divergeInfo = mDivergeInfos.get(i);
                if(divergeInfo.mY <=  divergeInfo.mEndPoint.y ){
                    mDivergeInfos.remove(i);
                    i--;
                    continue;
                }
                mPaint.setAlpha((int)(255 * divergeInfo.mY / mPtStart.y));
                canvas.drawBitmap(mDivergeViewProvider.getBitmap(divergeInfo.mType), divergeInfo.mX, divergeInfo.mY, mPaint);

                float timeLeft = 1.0F - divergeInfo.mDuration;

                divergeInfo.mDuration += mDuration;

                float x, y;

                //二次贝塞尔
                float time1 = timeLeft * timeLeft;
                float time2 = 2 * timeLeft * divergeInfo.mDuration;
                float time3 = divergeInfo.mDuration * divergeInfo.mDuration;
                x = time1 * (mPtStart.x)
                        + time2 * (divergeInfo.mBreakPoint.x)
                        + time3 * (divergeInfo.mEndPoint.x);

                divergeInfo.mX = x;

                y = time1 * (mPtStart.y)
                        + time2 * (divergeInfo.mBreakPoint.y)
                        + time3 * (divergeInfo.mEndPoint.y);

                divergeInfo.mY = y;
            }
            this.post(mRunnable);
        }
    }

    private PointF getBreakPointF(int scale1, int scale2) {

        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt((getMeasuredWidth() - getPaddingRight() + getPaddingLeft()) / scale1) + getMeasuredWidth() / scale2;
        pointF.y = mRandom.nextInt((getMeasuredHeight() - getPaddingBottom() + getPaddingTop()) / scale1) + getMeasuredHeight() / scale2;
        return pointF;
    }

    public class DivergeInfo {
        public float mDuration;
        public PointF mBreakPoint;
        public PointF mEndPoint;
        public float mX;
        public float mY;
        public Object mType;
        public DivergeInfo(float x, float y, PointF breakPoint, PointF endPoint, Object type){
            mDuration = 0.0f;
            mEndPoint = endPoint;
            mX = x;
            mY = y;
            mBreakPoint = breakPoint;
            mType = type;
        }
    }

}

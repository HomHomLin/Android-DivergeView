package lib.homhomlib.view2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Linhh on 16/3/28.
 */
public class DivergeView extends View implements Runnable{

    public static final float mDuration = 0.010F;
    public static final int mDefaultHeight = 100;
    protected  static final long mQueenDuration = 200;

    protected final Random mRandom = new Random();

    protected ArrayList<DivergeInfo> mDivergeInfos;

    protected List<Object> mQueen;

    protected PointF mPtStart;
    protected PointF mPtEnd;

    protected ArrayList<DivergeInfo> mDeadPool = new ArrayList<>();

    private Paint mPaint;

//    private static final int mDefaultWidth = 100;
//    private static final int mAlphaOffset = 50;

    private DivergeViewProvider mDivergeViewProvider;

    private long mLastAddTime = 0;

    private Thread mThread;

    private boolean mRunning = true;

    private boolean mIsDrawing = false;

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


    /**
     * Loop
     */
    @Override
    public void run() {

        while(mRunning){

            if(mDivergeViewProvider == null){
                continue;
            }

            if(mQueen == null){
                continue;
            }

            if(mIsDrawing){
                //如果正在绘制，不要处理数据
                continue;
            }

            if(mDivergeInfos == null){
                continue;
            }

            dealQueen();

            if(mDivergeInfos.size() == 0){
                continue;
            }

            dealDiverge();

            mIsDrawing = true;

            postInvalidate();

        }

        //停止
        release();
    }

    private void dealDiverge(){
        for (int i = 0; i < mDivergeInfos.size(); i++) {
            DivergeInfo divergeInfo = mDivergeInfos.get(i);

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

            if (divergeInfo.mY <= divergeInfo.mEndPoint.y) {
                mDivergeInfos.remove(i);
                mDeadPool.add(divergeInfo);
                i--;
                continue;
            }
        }
    }

    private void dealQueen(){
        long now = System.currentTimeMillis();
        if(mQueen.size() > 0 && now - mLastAddTime > mQueenDuration){
            mLastAddTime = System.currentTimeMillis();
            DivergeInfo divergeInfo = null;
            if(mDeadPool.size() > 0){
                //死池里面有空闲的divergeNode
                divergeInfo = mDeadPool.get(0);
                mDeadPool.remove(0);
            }
            if(divergeInfo == null){
                divergeInfo = createDivergeNode(mQueen.get(0));
            }
            divergeInfo.reset();
            divergeInfo.mType = mQueen.get(0);
            mDivergeInfos.add(divergeInfo);
            mQueen.remove(0);
        }
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

    public PointF getStartPoint(){
        return mPtStart;
    }

    public boolean isRunning(){
        return mRunning;
    }

    public void startDiverges(Object obj){

        if(mDivergeInfos == null){
            mDivergeInfos = new ArrayList<>(30);
        }

        if(mQueen == null){
            mQueen = Collections.synchronizedList(new ArrayList<>(30));
        }

        mQueen.add(obj);
//        for(Object obj : objs) {
//            mQueen.add(obj);
//        }

        if(mThread == null) {
            mThread = new Thread(this);
            mThread.start();
        }
    }

    public void stop(){
        if(mDivergeInfos != null){
            mDivergeInfos.clear();
        }

        if(mQueen != null){
            mQueen.clear();
        }

        if(mDeadPool != null){
            mDeadPool.clear();
        }

    }

    public void release(){
        stop();
        mPtEnd = null;
        mPtStart = null;
        mDivergeInfos = null;
        mQueen = null;
        mDeadPool = null;
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
        mRunning = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mRunning && mDivergeViewProvider != null && mDivergeInfos != null){
            for(DivergeInfo divergeInfo : mDivergeInfos){
                mPaint.setAlpha((int)(255 * divergeInfo.mY / mPtStart.y));
                canvas.drawBitmap(mDivergeViewProvider.getBitmap(divergeInfo.mType),
                        divergeInfo.mX,
                        divergeInfo.mY,
                        mPaint);
            }
        }
        mIsDrawing = false;
    }


    private PointF getBreakPointF(int scale1, int scale2) {

        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt((getMeasuredWidth() - getPaddingRight() + getPaddingLeft()) / scale1) + getMeasuredWidth() / scale2;
        pointF.y = mRandom.nextInt((getMeasuredHeight() - getPaddingBottom() + getPaddingTop()) / scale1) + getMeasuredHeight() / scale2;
        return pointF;
    }

    protected DivergeInfo createDivergeNode(Object type){
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

    public class DivergeInfo {
        public float mDuration;
        public PointF mBreakPoint;
        public PointF mEndPoint;
        public float mX;
        public float mY;
        public Object mType;
        public float mStartX;
        public float mStartY;
        public DivergeInfo(float x, float y, PointF breakPoint, PointF endPoint, Object type){
            mDuration = 0.0f;
            mEndPoint = endPoint;
            mX = x;
            mY = y;
            mStartX = x;
            mStartY = y;
            mBreakPoint = breakPoint;
            mType = type;
        }

        public void reset(){
            mDuration = 0.0f;
            mX = mStartX;
            mY = mStartY;
        }
    }
}

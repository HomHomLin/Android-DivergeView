package lib.homhomlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

    private int mColor = Color.RED;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DivergeView(Context context) {
        this(context,null);
    }

    public DivergeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DivergeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mDivergeInfos = new ArrayList<>();
        mPaint.setColor(mColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        invalidate();
    }

    public void start(){
        mIsDiverge = true;
        invalidate();
    }

    public void stop(){
        mIsDiverge = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPtStart == null){
            mPtStart = new PointF(getMeasuredWidth() /2 ,getMeasuredHeight() - 100);
            mPtEnd = new PointF(0,0);//应该使用随机数
            DivergeInfo divergeInfo = new DivergeInfo(mPtStart.x,mPtStart.y,getBreakPointF(2),getBreakPointF(1));
            mDivergeInfos.add(divergeInfo);
        }

        if(mBitmap != null){
            for(int i = 0 ; i < mDivergeInfos.size(); i ++){
                canvas.drawBitmap(mBitmap,mDivergeInfos.get(i).mX,mDivergeInfos.get(i).mY,mPaint);
                mDivergeInfos.get(i).mDuration += 0.005f;
                float timeLeft = 1.0f - mDivergeInfos.get(i).mDuration;

                PointF point = new PointF();

                //PointF point0 = startValue;

                //PointF point3 = endValue;

                float time1 = timeLeft * timeLeft * timeLeft;
                float time2 = 3 * timeLeft * timeLeft * mDivergeInfos.get(i).mDuration;
                float time3 = 3 * timeLeft * mDivergeInfos.get(i).mDuration * mDivergeInfos.get(i).mDuration;
                float time4 = mDivergeInfos.get(i).mDuration * mDivergeInfos.get(i).mDuration * mDivergeInfos.get(i).mDuration;
                point.x = time1 * (mPtStart.x)
                        + time2 * (mDivergeInfos.get(i).mBreakPoint1.x)
                        + time3 * (mDivergeInfos.get(i).mBreakPoint2.x)
                        + time4 * (mPtEnd.x);

                mDivergeInfos.get(i).mX = point.x;

                point.y = time1 * (mPtStart.y)
                        + time2 * (mDivergeInfos.get(i).mBreakPoint1.y)
                        + time3 * (mDivergeInfos.get(i).mBreakPoint2.y)
                        + time4 * (mPtEnd.y);

                mDivergeInfos.get(i).mY = point.y;
            }

//            canvas.drawBitmap(mBitmap,i,0,mPaint);
//            i += 10;
            invalidate();
//            canvas.drawBitmap(mBitmap,0,0,mPaint);
//            canvas.drawBitmap(mBitmap,100,0,mPaint);
        }
//        canvas.drawBitmap();
//        int radius = Math.min(getWidth(), getWidth()) / 2;
//        canvas.drawCircle(getWidth() / 2, getWidth() / 2,
//                radius, mPaint);
    }

    private PointF getBreakPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt(getMeasuredWidth());
        pointF.y = mRandom.nextInt(getMeasuredHeight() - 100)/scale;
        return pointF;
    }
}

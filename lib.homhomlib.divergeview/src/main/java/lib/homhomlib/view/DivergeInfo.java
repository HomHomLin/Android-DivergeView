package lib.homhomlib.view;

import android.graphics.PointF;

/**
 * Created by Linhh on 16/3/28.
 */
public class DivergeInfo {
    public float mDuration;
    public PointF mBreakPoint1;
    public PointF mBreakPoint2;
    public float mX;
    public float mY;
    public DivergeInfo(float x, float y, PointF breakPoint1, PointF breakPoint2){
        mDuration = 0.0f;
        mX = x;
        mY = y;
        mBreakPoint1 = breakPoint1;
        mBreakPoint2 = breakPoint2;
    }
}

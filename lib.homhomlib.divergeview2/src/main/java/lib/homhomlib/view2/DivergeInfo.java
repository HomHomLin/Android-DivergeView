package lib.homhomlib.view2;

import android.graphics.PointF;

/**
 * Created by Linhh on 16/3/28.
 */
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

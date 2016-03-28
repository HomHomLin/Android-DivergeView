package lib.homhomlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Linhh on 16/3/28.
 */
public class DivergeView extends View{

    private Bitmap mBitmap;

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
        mPaint.setColor(mColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap != null){
            canvas.drawBitmap(mBitmap,0,0,mPaint);
            canvas.drawBitmap(mBitmap,100,0,mPaint);
        }
//        canvas.drawBitmap();
//        int radius = Math.min(getWidth(), getWidth()) / 2;
//        canvas.drawCircle(getWidth() / 2, getWidth() / 2,
//                radius, mPaint);
    }
}

package lib.homhomlib.view.demo;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import lib.homhomlib.view.DivergeView;

public class MainActivity extends AppCompatActivity {
    private DivergeView mDivergeView;
    private Button mBtnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnStart = (Button)findViewById(R.id.btnStart);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDivergeView.isRunning()){
                    mDivergeView.stop();
                }else{
                    mDivergeView.start();
                }
            }
        });
        mDivergeView = (DivergeView) findViewById(R.id.divergeView);
        mDivergeView.setBitmap(((BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap());
        mDivergeView.start();
    }
}

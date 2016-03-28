package lib.homhomlib.view.demo;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import lib.homhomlib.view.DivergeView;

public class MainActivity extends AppCompatActivity {
    DivergeView mDivergeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDivergeView = (DivergeView) findViewById(R.id.divergeView);
        mDivergeView.setBitmap(((BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap());
        mDivergeView.start();
    }
}

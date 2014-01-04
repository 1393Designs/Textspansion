package com.designs_1393.textspansion;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class QuickView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_view);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Double width = size.x * 0.8;
        Double height = size.y * 0.7;

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.clip_list_quick_view);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        params.height =  height.intValue();
        params.width = width.intValue();
        frameLayout.setLayoutParams(params);
    }

}

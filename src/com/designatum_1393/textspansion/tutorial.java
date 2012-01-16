package com.designatum_1393.textspansion;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.content.res.Resources;

public class tutorial extends Activity
{
    /** Called when the activity is first created. */

    private ImageView tutImage;
    private Button btnNext, btnBack;
    private TextView debug;
    private int counter = 0;
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

		tutImage = (ImageView) findViewById(R.id.tut_image);
		debug = (TextView) findViewById(R.id.debug);
		debug.setText("Image Bottom Padding: " + tutImage.getPaddingBottom());


		btnNext = (Button) findViewById(R.id.next_button);
		btnNext.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(counter < 6)
					counter++;
				debug.setText("Counter at " + counter);
				switch (counter){
					case 1:
						tutImage.setImageResource(R.drawable.tut_1);
						break;
					case 2:
						tutImage.setImageResource(R.drawable.tut_2);
						break;
					case 3:
						tutImage.setImageResource(R.drawable.tut_3);
						break;
					case 4:
						tutImage.setImageResource(R.drawable.tut_4);
						break;
					case 5:
						tutImage.setImageResource(R.drawable.tut_5);
						btnNext.setText("Finish");
						break;
					case 6:
						finish();
					default:
						break;
				}
				
			}
		});

		btnBack = (Button) findViewById(R.id.back_button);
		btnBack.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(counter > 0)
					counter--;
				debug.setText("Counter at " + counter);
				switch (counter){
					case 0:
						tutImage.setImageResource(R.drawable.tut_0);
						break;
					case 1:
						tutImage.setImageResource(R.drawable.tut_1);
						break;
					case 2:
						tutImage.setImageResource(R.drawable.tut_2);
						break;
					case 3:
						tutImage.setImageResource(R.drawable.tut_3);
						break;
					case 4:
						tutImage.setImageResource(R.drawable.tut_4);
						btnNext.setText("Next");
						break;
					default:
						break;
				}
				
			}
		});
	}

	public void skip(View view) {
		finish();
	}
}
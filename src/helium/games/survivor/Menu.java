package helium.games.survivor;

import helium.games.survivor.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class Menu extends Activity {

	private static final String TYPE_NEW = "TYPE_NEW";
	private static final String TYPE_CONT = "TYPE_CONT";
	private Button start;
	
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		start =  (Button)findViewById(R.id.button1);
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startLevel(TYPE_NEW);
				
			}
		});
		
	}

	public void startLevel( String type){
		
		Intent i = new Intent("helium.games.survivor.Level");
		i.putExtra("Type", type);
		startActivity(i);
		finish();
	}
	

}
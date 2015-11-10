package com.example.s188884_mappe3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;


public class StartupActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_startup);
        
      //Lager en thread til splash screen
        Thread logoTimer = new Thread()
        {
        	public void run()
        	{
        		try
        		{
        			sleep(3000);
        			Intent menyIntent = new Intent(StartupActivity.this,MainActivity.class);
        			startActivity(menyIntent);
        			overridePendingTransition(0,0);
        		} 
        		catch (InterruptedException e) 
        		{
					e.printStackTrace();
				}

        		finally
        		{
        			finish();
        		}
        	}
        };
        logoTimer.start();
    }
}

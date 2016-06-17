package com.allen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


    public class WelcomeActivity extends Activity {
        /** Called when the Activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            new Thread(){
            	public void run() {
            		try {sleep(2000); 
                    Intent Intent=new Intent(WelcomeActivity.this,ViewActivity.class);
                    //注销这个Activity              
                    finish();
                    startActivity(Intent);
                }catch (Exception e) {  
            	e.printStackTrace();  
            	 }  
                }          
            }.start(); 
        }
    }

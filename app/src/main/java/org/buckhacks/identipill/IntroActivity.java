package org.buckhacks.identipill;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }


    public void goToCamera (View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }


    public void goToSettings (View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}

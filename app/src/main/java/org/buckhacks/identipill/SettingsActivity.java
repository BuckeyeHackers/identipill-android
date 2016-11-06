package org.buckhacks.identipill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private EditText mName;
    private EditText mNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mName = (EditText) findViewById(R.id.nameText);
        mNumbers = (EditText) findViewById(R.id.numbersText);
    }

    public void goToCamera (View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("name", mName.getText().toString());
        intent.putExtra("numbers", mNumbers.getText().toString());
        startActivity(intent);
    }
}

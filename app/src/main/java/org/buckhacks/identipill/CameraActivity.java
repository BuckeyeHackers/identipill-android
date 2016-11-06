//package com.example.sairamkrishna.myapplication;
package org.buckhacks.identipill;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.buckhacks.identipill.R;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private GoogleApiClient client;
    private Button mPlayAudioButton;
    private TextView mPillNameTextView;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button mTakePicture = (Button) findViewById(R.id.take_photo);

        mPillNameTextView = (TextView) findViewById(R.id.pillName);
        mPlayAudioButton = (Button) findViewById(R.id.say_pill_name);

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else {
                    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissions, REQUEST_CAMERA);
                    }
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (thumbnail != null) {
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                }
                File destination = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                FileOutputStream fo;
                try {
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                AsyncHttpClient client = new AsyncHttpClient();

                File myFile = new File(destination.getAbsolutePath());
                RequestParams params = new RequestParams();

                try {
                    params.put("pill", myFile);
                } catch (FileNotFoundException e) {
                }

                ArrayList<String> numbers = new ArrayList<>();
                numbers.add("3307740777");
                params.put("numbers", numbers);
                params.put("name", "Grandma Unkrich");

                client.post("http://identipill.ngrok.io/api/identipill/", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        char[] processing = "Processing...".toCharArray();

                        mPillNameTextView.setText(processing, 0, processing.length);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        // called when response HTTP status is "200 OK"
                        String resp = new String(response);

                        Log.v(Integer.toString(statusCode), resp);

                        JsonObject jsonObject = new JsonParser().parse(resp).getAsJsonObject();

                        final String pillAudioURL = jsonObject.get("data").getAsJsonObject().get("pillAudio").getAsString();

                        mPlayAudioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Log.v("200", "http://identipill.ngrok.io/" + pillAudioURL);
                                    player.setDataSource("http://identipill.ngrok.io/" + pillAudioURL);
                                    player.prepare();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                player.start();
                            }
                        });

                        char[] pillName = jsonObject.get("data").getAsJsonObject().get("pillName").getAsString().toCharArray();

                        mPillNameTextView.setText(pillName, 0, pillName.length);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        char[] error = "There was an error... Try again!".toCharArray();

                        mPillNameTextView.setText(error, 0, error.length);
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }
                });
            }
        }
    }
}
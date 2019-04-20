package com.example.baddiemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    Bitmap bitty = null;
    byte[] byteArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View v) {
        EditText userField = findViewById(R.id.user);
        String user = userField.getText().toString();
        EditText passField = findViewById(R.id.pass);
        String password = passField.getText().toString();

        photoPage(v);
    }

    public void photoPage(View v) {
        setContentView(R.layout.photo_capture);
    }

    public void post(View v) {
        setContentView(R.layout.post_page);
        // TODO stuff that posts to insta/fb

    }

    public void camera(View v) {
        Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(x, 1);
    }

    protected void onActivityResult(int rc, int resc, Intent data) {
        ImageView iv = null;
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        iv = ((ImageView) findViewById(R.id.theView));
        iv.setBackgroundResource(0);
        iv.setImageBitmap(bm);

        bitty = ((BitmapDrawable) iv.getDrawable()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitty.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
    }


}

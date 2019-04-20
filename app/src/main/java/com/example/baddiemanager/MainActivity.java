package com.example.baddiemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

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

        setContentView(R.layout.photo_capture);
    }

    public void post(View v) {
        setContentView(R.layout.post_page);
        // TODO stuff that posts to insta/fb

    }

    public void camera() {

    }



}

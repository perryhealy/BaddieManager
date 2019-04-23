package com.example.baddiemanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    // REQUEST CODES:
        // 0: LOAD PHOTO
        // 1: TAKE PHOTO
        // 2: INSTAGRAM
        // 3: FACEBOOK

    Bitmap bitty = null;
    byte[] byteArray = null;
    private static final int WRITE_CODE = 1600;
    boolean havePermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void login(View v) {
//        EditText userField = findViewById(R.id.user);
//        String user = userField.getText().toString();
//        EditText passField = findViewById(R.id.pass);
//        String password = passField.getText().toString();

        photoPage(v);
    }

    public void photoPage(View v) {
        setContentView(R.layout.photo_capture);
    }

    public void post(View v) throws IOException {
        // TODO stuff that posts to insta/fb

        // STEP ZERO:  MAKE SURE WE GOT PERMISSION
        requestSinglePermission();

        if (havePermission) {

            // STEP ONE:  CONVERT THE BITMAP TO A URI
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitty, "Title", null);
            Uri bittyToUri = Uri.parse(path);

            int sticker = R.drawable.logo;
            Uri sticky = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(sticker) + '/' +
                    getResources().getResourceTypeName(sticker) + '/' +
                    getResources().getResourceEntryName(sticker) );

            // STEP TWO:  SEND ALL THE INFO VIA INTENTS
                // INSTAGRAM

            Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
            intent.setDataAndType(bittyToUri, getContentResolver().getType(bittyToUri));
            intent.putExtra("interactive_asset_uri", sticky);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, 2);


                // TODO;; WONT WORK w FACEBOOK
                    // it literally worked the first time and then stopped lmao wtf
            Intent fbintent = new Intent("com.facebook.stories.ADD_TO_STORY");
            fbintent.setDataAndType(bittyToUri, getContentResolver().getType(bittyToUri));
            fbintent.putExtra("interactive_asset_uri", sticky);
            fbintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (fbintent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(fbintent, 3);
            }


            // STEP THREE:  SHOW THE FINAL PAGE MEANING SUCCESS
            setContentView(R.layout.post_page);

        }

        // ALTERNATE METHOD THAT THREW ERRORS :(
        /*
        // CREATING A NEW TEMPORARY STORAGE FOR PHOTOS
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        tempDir.mkdir();

        // line below fails; cant find the directory even tho i just made it??
            // or is it that it cant find the file im making/???
        File tempFile = File.createTempFile("pic", ".jpg", tempDir);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitty.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] bitmapData = bytes.toByteArray();

        //write the bytes to the file
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        Uri bittyToUri = Uri.fromFile(tempFile);

        */
    }

    public void camera(View v) {
        Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(x, 1);
    }

    public void load(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 0);
    }

    protected void onActivityResult(int rc, int resc, Intent data) {

        if (rc == 1) {
            ImageView iv = null;
            iv = ((ImageView) findViewById(R.id.theView));
            iv.setBackgroundResource(0);
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bm);

            bitty = ((BitmapDrawable) iv.getDrawable()).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitty.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            return;
        }

        super.onActivityResult(rc, resc, data);

        if (rc == 0) {
            ImageView iv = null;
            iv = ((ImageView) findViewById(R.id.theView));
            iv.setBackgroundResource(0);
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                bitty = BitmapFactory.decodeStream(imageStream);
                iv.setImageBitmap(bitty);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //Toast.makeText("Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            //Toast.makeText("You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void requestSinglePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = checkSelfPermission(storagePermission);
        String[] permissions = new String[] { storagePermission };
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, WRITE_CODE);
        } else {
            havePermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WRITE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    havePermission = true;
                } else {
                    havePermission = false;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
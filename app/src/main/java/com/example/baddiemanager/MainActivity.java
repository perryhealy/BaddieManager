package com.example.baddiemanager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MainActivity extends AppCompatActivity {

    // REQUEST CODES:
        // 0: LOAD PHOTO
        // 1: TAKE PHOTO
        // 2: INSTAGRAM
        // 3: FACEBOOK
        // 4: VIDEO

    // TODO figure out how to send videos to both platforms
    Bitmap bitty = null;
    byte[] byteArray = null;
    private static final int WRITE_CODE = 1600;
    boolean havePermission = false;
    Uri bittyToUri = null;
    Uri sticky = null;
    public static int LOAD_PHOTO = 0;
    public static int TAKE_PHOTO = 1;
    public static int INSTAGRAM_STORY = 2;
    public static int FACEBOOK_STORY = 3;
    public static int VIDEO = 4;

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

            // use try catch ActivityNotFoundException when running an activity to the apps
        boolean igExists = doesPackageExist("com.instagram.android");
        boolean fbExists = doesPackageExist("com.facebook.katana");

        Log.v("IG", ""+igExists);
        Log.v("FB", ""+fbExists);

        if (!igExists || !fbExists) {
            setContentView(R.layout.failure_page);
        } else {
            photoPage(v);
        }
    }

    public void photoPage(View v) {
        setContentView(R.layout.photo_capture);
    }

    public void backToMain(View v) {
        setContentView(R.layout.activity_main);
    }

    public void igPost(View v) throws IOException {

        // STEP ZERO:  MAKE SURE WE GOT PERMISSION
        requestSinglePermission();

        if (havePermission) {

            // STEP ONE:  CONVERT THE BITMAP TO A URI
            if (bitty != null) {
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitty, "Title", null);
                bittyToUri = Uri.parse(path);
            }

            int sticker = R.drawable.logo;
            sticky = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(sticker) + '/' +
                    getResources().getResourceTypeName(sticker) + '/' +
                    getResources().getResourceEntryName(sticker) );

            // STEP TWO:  SEND ALL THE INFO VIA INTENTS
                // INSTAGRAM
            Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
            intent.setDataAndType(bittyToUri, getContentResolver().getType(bittyToUri));
            intent.putExtra("interactive_asset_uri", sticky);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivityForResult(intent, 2);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Please download Instagram!", Toast.LENGTH_SHORT).show();
            }
            // JUMPS TO ONACTIVITYRESULT, THEN RUNS FBPOST FOR FACEBOOK

        }


    }

    public void fbPost() {
        Intent fbintent = new Intent("com.facebook.stories.ADD_TO_STORY");
        fbintent.setDataAndType(bittyToUri, getContentResolver().getType(bittyToUri));
        fbintent.putExtra("interactive_asset_uri", sticky);
        fbintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (fbintent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivityForResult(fbintent, 3);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Please download Facebook!", Toast.LENGTH_SHORT).show();
            }
        }

        // STEP THREE:  SHOW THE FINAL PAGE MEANING SUCCESS
        setContentView(R.layout.post_page);

    }

    public void camera(View v) {
        Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(x, 1);
    }

    public void videoCamera(View v) {
        Intent x = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //bittyToUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        x.putExtra(MediaStore.EXTRA_OUTPUT, bittyToUri);
        startActivityForResult(x, VIDEO);

    }

    public void load(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 0);
    }

    public void loadVideo(View v) {
        // TODO make this shit do something
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

        } else if (rc == 2){
            fbPost();
        } else if (rc == VIDEO) {
            // TODO video saving stuff
           // bittyToUri = data.getData();
            Log.v("VIDEO_URI", ""+bittyToUri);

            ImageView iv = null;
            iv = ((ImageView) findViewById(R.id.theView));
            iv.setBackgroundResource(0);

            iv.setImageURI(bittyToUri);
            bitty = null;

            return;
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

    public boolean doesPackageExist(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }
}
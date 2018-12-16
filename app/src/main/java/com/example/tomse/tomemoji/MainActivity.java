package com.example.tomse.tomemoji;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button emojifyMeButton;
    private ImageView photoView;
    private FloatingActionButton closeFab;
    private FloatingActionButton saveFab;
    private FloatingActionButton shareFab;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    String mCurrentPhotoPath;

    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emojifyMeButton = findViewById(R.id.emojifyMe_button);
        photoView = findViewById(R.id.photo_view);
        closeFab = findViewById(R.id.closeFab);
        saveFab = findViewById(R.id.saveFab);
        shareFab = findViewById(R.id.shareFab);


    }

    /**
     * OnClick method for emojifyMe button. Launches Camera app.
     *
     * @param view The emojifyMe button
     */
    public void emojifyMe(View view) {

        // create file in internal storage to save the photo into.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imagesPath = new File(this.getFilesDir(), "images");
        if (!imagesPath.exists()) {
            imagesPath.mkdirs();
        }

        photoFile = new File(imagesPath, imageFileName);

        // Continue only if the File was successfully created
        if (photoFile != null) {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = photoFile.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Process the image and set it to the TextView

            photoView.setVisibility(View.VISIBLE);
            closeFab.show();
            saveFab.show();
            shareFab.show();
            emojifyMeButton.setVisibility(View.GONE);


            Bitmap photoBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            photoView.setImageBitmap(photoBitmap);


            // processAndSetImage();

        } else {

            // Otherwise, delete the temporary image file
            photoFile.delete();
        }
    }


//    private void processAndSetImage(){
//
//    }


    /**
     * onClick metod for closeMe button.
     *
     * @param view The closeMe button.
     */
    public void closeMe(View view) {
        photoFile.delete();

        closeFab.hide();
        saveFab.hide();
        shareFab.hide();
        photoView.setVisibility(View.INVISIBLE);
        emojifyMeButton.setVisibility(View.VISIBLE);


    }


    /**
     * @param view The saveMe button.
     */
    public void saveMe(View view) {

        //Check for external storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this,
                        "You have to allow this permission in order to use this feature",
                        Toast.LENGTH_LONG).show();


            }
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);

            // REQUEST_STORAGE_PERMISSION is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else{
        // Permission has already been granted
        saveToExternal();
    }


}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                    saveToExternal();
                    
                    
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "You are an asshole", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    private void saveToExternal() {

    }


    /**
     * @param view The shareMe butoon
     */
    public void shareMe(View view) {
    }
}

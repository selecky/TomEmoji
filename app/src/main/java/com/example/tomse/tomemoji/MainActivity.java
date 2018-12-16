package com.example.tomse.tomemoji;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
    String mCurrentPhotoPath;


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
        if (!imagesPath.exists()){
            imagesPath.mkdirs();
        }

        File photoFile = new File(imagesPath, imageFileName);

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
           // BitmapUtils.deleteImageFile(this, mTempPhotoPath);
            Toast.makeText(this, "nahovno", Toast.LENGTH_SHORT).show();
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
    }


    /**
     * @param view The saveMe button.
     */
    public void saveMe(View view) {
    }


    /**
     * @param view The shareMe butoon
     */
    public void shareMe(View view) {
    }
}

package com.example.tomse.tomemoji;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button emojifyMeButton;
    private ImageView photoView;
    private FloatingActionButton closeFab;
    private FloatingActionButton saveFab;
    private FloatingActionButton shareFab;

    private boolean isSmiling;
    private boolean isRightEyeOpen;
    private boolean isLeftEyeOpen;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    // index to save image with saveFab only once
    private int saveOnce;

    private String mCurrentPhotoPath;
    private String mCurrentPhotoPathExt;

    private File photoFile;
    private Uri photoURI;


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
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imagesPath = new File(this.getFilesDir(), "Tomimages");
        if (!imagesPath.exists()) {
            imagesPath.mkdirs();
        }

        photoFile = new File(imagesPath, imageFileName);

        // Continue only if the File was successfully created
        if (photoFile != null) {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = photoFile.getAbsolutePath();
            photoURI = FileProvider.getUriForFile(this,
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

            // index to save image with saveFab only once
            saveOnce = 1;


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

        } else {
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

        if (saveOnce == 1) {
            // create file in external storage to save the photo into.
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";

            File extPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/TomEmojif");
            if (!extPath.exists()) {
                extPath.mkdirs();
            }

            File photoFile2 = new File(extPath, imageFileName);

            // Continue only if the File was successfully created
            if (photoFile2 != null) {
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPhotoPathExt = photoFile2.getAbsolutePath();
                Bitmap photoBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

                try {
                    OutputStream fOut = new FileOutputStream(photoFile2);
                    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }


                //make the photo available to gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(photoFile2);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);


                // index to save image with saveFab only once
                saveOnce = 2;

                photoFile.delete();
                Toast.makeText(this, "Photo saved at " + mCurrentPhotoPathExt, Toast.LENGTH_LONG).show();


            }
        } else {
            Toast.makeText(this, "You already saved the image MotherFucker!", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    /**
     * @param view The shareMe butoon
     */
    public void shareMe(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        startActivity(shareIntent);
    }


    public void findFaces(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        for (FirebaseVisionFace face : faces) {

                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                isSmiling = face.getSmilingProbability() > 0.7;
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                isRightEyeOpen = face.getRightEyeOpenProbability() > 0.7;
                                            }
                                            if (face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                isLeftEyeOpen = face.getLeftEyeOpenProbability() > 0.7;
                                            }


                                            if (isSmiling && isLeftEyeOpen && isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.smile, null);

                                            } else if (isSmiling && isLeftEyeOpen && !isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.rightwink, null);

                                            } else if (isSmiling && !isLeftEyeOpen && isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.leftwink, null);

                                            } else if (isSmiling && !isLeftEyeOpen && !isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.closed_smile, null);


                                            } else if (!isSmiling && isLeftEyeOpen && isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.frown, null);

                                            } else if (!isSmiling && isLeftEyeOpen && !isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.rightwinkfrown, null);

                                            } else if (!isSmiling && !isLeftEyeOpen && isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.leftwinkfrown, null);

                                            } else if (!isSmiling && !isLeftEyeOpen && !isRightEyeOpen) {
                                                Drawable smajlik = ResourcesCompat.getDrawable(getResources(), R.drawable.closed_frown, null);

                                            }
                                        }
                                    }
                                }
                        )


                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });
    }

}

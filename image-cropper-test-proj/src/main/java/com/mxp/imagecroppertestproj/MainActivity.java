package com.mxp.imagecroppertestproj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mxp.profileimagecropper.ProfileImageCropper;
import com.mxp.profileimagecropper.ProfileImageCropperActivity;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final int PICK_IMAGE_FROM_GALLERY = 9000;
  private static final int PICA_ACITIVTY = 9001;
  ProfileImageCropper image = null;

  Button loadNew = null;
  Button cropImage = null;
  Button launchActivity = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    image = ((ProfileImageCropper) findViewById(R.id.profileImage));

    initCropImage();
    initDrawBorder();
    initLoadNewButton();

//    runAutomaticCrop();
  }

  private void runAutomaticCrop() {
    Handler handler = new Handler();
    Runnable r = new Runnable() {

      @Override
      public void run() {
        Bitmap bmp = image.crop();
        image.setEditMode(false);
        image.setImageBitmap(bmp);
//        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      }
    };

    handler.postDelayed(r, 5000);
  }

  private void initDrawBorder() {
    launchActivity = (Button) findViewById(R.id.activity);
    launchActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // image.setDrawBorder(!image.isDrawBorder());

        Intent intent = new Intent(getBaseContext(), ProfileImageCropperActivity.class);
        intent.putExtra("cropperBackground", Color.argb(150, 250, 190, 30));
        intent.putExtra("cropperBorder", Color.argb(255, 0, 0, 0));
        intent.putExtra("cropperBorderWidth", 5);
        intent.putExtra("cropperWidth", 350);
        intent.putExtra("cropperMinimumWidth", 200);
        intent.putExtra("handleBackground", Color.argb(255, 200, 45, 0));
        intent.putExtra("handleBorder", Color.argb(255, 0, 0, 0));
        intent.putExtra("handleBorderWidth", 5);
        intent.putExtra("handleWidth", 70);
        intent.putExtra("background", Color.argb(255, 0, 0, 0));
        intent.putExtra("controlBackground", Color.argb(255, 0, 0, 0));

        startActivityForResult(intent, PICA_ACITIVTY);
      }
    });
  }

  private void initCropImage() {
    cropImage = (Button) findViewById(R.id.cropImage);
    cropImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Bitmap bmp = ((ProfileImageCropper) findViewById(R.id.profileImage)).crop();
        image.setEditMode(false);
        image.setImageBitmap(bmp);
      }
    });
  }

  private void initLoadNewButton() {
    loadNew = (Button) findViewById(R.id.loadNew);
    loadNew.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_FROM_GALLERY);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
      handleFromGallery(data);
    } else if (requestCode == PICA_ACITIVTY && resultCode == RESULT_OK) {
      String fileName = data.getStringExtra("result");

      InputStream is = null;
      try {
        is = openFileInput(fileName);
        Drawable d = Drawable.createFromStream(is, fileName);
        image.setBackground(d);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

//      File filePath = getFileStreamPath(fileName);
//      Drawable d = Drawable.createFromPath(filePath.toString());
//      image.setBackground(d);
    }
  }

  private void handleFromGallery(Intent data) {
    if (data == null) {
      return;
    }

    try {
      InputStream inputStream = getBaseContext().getContentResolver().openInputStream(data.getData());
      // Bitmap bmp=BitmapFactory.decodeStream(inputStream);
      // Picasso.with(getBaseContext()).load(data.getData()).fit().centerCrop().into(image);
      Picasso.with(getBaseContext()).load(data.getData()).fit().centerInside().into(image);
      // image.setImageBitmap(bmp);
      image.setEditMode(true);

      // Log.d(TAG, data.getData().toString());
      // Log.d(TAG, "bmp: "+bmp.getWidth()+"x"+bmp.getHeight());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}

package com.mxp.imagecropper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mxp.profileimagecropper.ProfileImageCropper;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final int PICK_IMAGE = 9000;
  ProfileImageCropper image = null;

  Button loadNew = null;
  Button cropImage = null;
  Button reloadImage = null;
  Button drawBorder = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    image = ((ProfileImageCropper) findViewById(R.id.profileImage));

    initCropImage();
    initReload();
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
    drawBorder = (Button) findViewById(R.id.border);
    drawBorder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        image.setDrawBorder(!image.isDrawBorder());
      }
    });
  }

  private void initReload() {
    reloadImage = (Button) findViewById(R.id.reload);
    reloadImage.setOnClickListener(new View.OnClickListener() {
      @TargetApi(Build.VERSION_CODES.LOLLIPOP)
      @Override
      public void onClick(View view) {
        image.setImageDrawable(getResources().getDrawable(R.drawable.face4, null));
        image.setEditMode(true);
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
      if (data == null) {
        return;
      }

      try {
        InputStream inputStream = getBaseContext().getContentResolver().openInputStream(data.getData());
        // Bitmap bmp=BitmapFactory.decodeStream(inputStream);
//        Picasso.with(getBaseContext()).load(data.getData()).fit().centerCrop().into(image);
        Picasso.with(getBaseContext()).load(data.getData()).fit().centerInside().into(image);
//        image.setImageBitmap(bmp);
        image.setEditMode(true);

//        Log.d(TAG, data.getData().toString());
//        Log.d(TAG, "bmp: "+bmp.getWidth()+"x"+bmp.getHeight());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
    }
  }
}

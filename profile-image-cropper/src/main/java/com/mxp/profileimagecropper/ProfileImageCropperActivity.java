package com.mxp.profileimagecropper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileImageCropperActivity extends AppCompatActivity {
  private static final int PICK_IMAGE_FROM_GALLERY = 9000;
  ProfileImageCropper image = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_image_cropper);

    image = (ProfileImageCropper) findViewById(R.id.profileImage);

    applyIntent(getIntent());
    initLoadNewButton();
    initCropImage();
    initDoneButton();
  }

  @Override
  protected void onResume() {
    super.onResume();
    applyIntent(getIntent());
  }

  private void applyIntent(Intent intent) {
    int value = 0;
    String valueStr = "";

    value = intent.getIntExtra("cropperBackground", -1);
    if (value != -1) image.setCropperBackground(value);

    value = intent.getIntExtra("cropperBorder", -1);
    if (value != -1) image.setCropperBorder(value);

    value = intent.getIntExtra("cropperBorderWidth", -1);
    if (value != -1) image.setCropperBorderWidth(value);

    value = intent.getIntExtra("cropperWidth", -1);
    if (value != -1) image.setCropperWidth(value);

    value = intent.getIntExtra("cropperMinimumWidth", -1);
    if (value != -1) image.setCropperMinimumWidth(value);

    value = intent.getIntExtra("handleBackground", -1);
    if (value != -1) image.setHandleBackground(value);

    value = intent.getIntExtra("handleBorder", -1);
    if (value != -1) image.setHandleBorder(value);

    value = intent.getIntExtra("handleBorderWidth", -1);
    if (value != -1) image.setHandleBorderWidth(value);

    value = intent.getIntExtra("handleWidth", -1);
    if (value != -1) image.setHandleWidth(value);

    value = intent.getIntExtra("background", -1);
    if (value != -1) image.setBackgroundColor(value);

    value = intent.getIntExtra("controlBackground", -1);
    if (value != -1) ((LinearLayout) findViewById(R.id.layout_controls)).setBackgroundColor(value);
  }

  private void initLoadNewButton() {
    ((Button) findViewById(R.id.loadNew)).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        try {
          Intent intent = new Intent();
          intent.setType("image/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_FROM_GALLERY);
        }
        catch(Exception e){
          Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
            .setActionTextColor(getResources().getColor(android.R.color.primary_text_dark))
            .show();
        }
      }
    });
  }

  private void initCropImage() {
    ((Button) findViewById(R.id.cropImage)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          Bitmap bmp = ((ProfileImageCropper) findViewById(R.id.profileImage)).crop();
          image.setEditMode(false);
          image.setImageBitmap(bmp);
        }
        catch(Exception e){
          Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
            .setActionTextColor(getResources().getColor(android.R.color.primary_text_dark))
            .show();
        }
      }
    });
  }

  private void initDoneButton() {
    ((Button) findViewById(R.id.done)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent();
        try {

          // get bitmap
          Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

          // get compressed PNG image
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

          // get byte array
          byte[] b = bos.toByteArray();

          String tmpFilename = "tmp_cropped_image.png";

          FileOutputStream fileOutStream = openFileOutput(tmpFilename, MODE_PRIVATE);
          fileOutStream.write(b);  //b is byte array
          //otherwise this technique is useless
          fileOutStream.close();

          // return in intent
          intent.putExtra("result", tmpFilename);
          setResult(RESULT_OK, intent);
        } catch (Exception e) {
          intent.putExtra("exception", e.getMessage());
          setResult(RESULT_OK, intent);
        } finally {
          finish();
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
      if (data == null) {
        return;
      }

      try {
        InputStream inputStream = getBaseContext().getContentResolver().openInputStream(data.getData());
        Picasso.with(getBaseContext()).load(data.getData()).fit().centerInside().into(image);
        image.setEditMode(true);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }


}

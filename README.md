```
  Under construction, hard hats required at all times!
  
  If you're an adventurer: compile 'com.mxp.profileimagecropper:profile-image-cropper:0.0.2'  // See ya!
```
# profileimagecropper :sparkles:

1. [The What](#the-what)
2. [The Who](#the-who)
3. [The Hows](#the-hows)
  2. [How to Get](#how-to-get)
  1. [How to Use](#how-to-use)
    1. [How to Use the Widget](#how-to-use-the-widget)
    1. [How to Use the Activity](#how-to-use-the-activity)
4. [The Rest](#the-rest)

#The What
This is a simple android library that allows you to crop images. The main purpose is to crop profile images. The library doesn't focus on picture quality, resolution fidelity, or any other aspect of image quality preservation. This library focuses on the ease of allowing a developer to add code to their app so their users can select and crop an image to use as their profile image.

The library provides a widget, ProfileImageCropper, that you can use directly in your xml layout. You can configure the UI via xml attributes. You enable edit mode on the widget and once the user indicates they're ready, you call .crop() method which returns to you the modified image.

The library also provides a basic activity, ProfileImageCropperActivity, that you can use. The activity allows the user to select an image, crop an image, and click done. When the user clicks on done, the editted image is saved in a local file and the filename is returned to you.

The library only adds 32KB to your project side.

#The Who
This library is for you if:
  1. you need to allow your user to select an image as their profile image (or avatar), allow them to crop it, and to use the cropped image in your project.
  2. all you want is to add a gradle dep and move on.
  3. you are ok with modest customizations and don't require extensive control the widget UI.

#The Hows
##How to Get
To use this library, add the following compile line the dependencies section of your module's build.gradle file.

```
dependencies {
  ...
  compile 'com.mxp.profileimagecropper:profile-image-cropper:0.0.2'
  ...
}
```
##How to Use
You can either use the provided widget and build your own UI layout, or you can use the provided activity that does some of the work for you.

###How to Use the Widget
To add the widget in your existing activity, use the following code. The attributes shown allow you to customize the UI, but they are optional. If you leave them out, the library will use default values. Note, you can start with a default image loaded by using _android:src=_ element.

```
<com.mxp.profileimagecropper.ProfileImageCropper
    android:id="@+id/profileImage"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:cropperBackground="#75fcde00"
    app:cropperBorder="#ffffff"
    app:cropperBorderWidth="3dp"
    app:cropperWidth="200dp"
    app:cropperMinimumWidth="150dp"
    app:handleBackground="#df8507"
    app:handleBorder="#ffffff"
    app:handleBorderWidth="5dp"
    app:handleWidth="20dp"
    android:layout_weight="1"
    android:background="#ebcd45"
    android:scaleType="fitCenter"
    android:src="@drawable/modelstand"/>
```

This widget extends ImageView so you should be able to use all the ImageView proeprties.

####Loading directly
To load an image into the widget, use the following code. You can use any other method that loads a valid bitmap into an ImageView. Please note, DO NOT add an image as background using setBackground(), etc.

```
image.setImageDrawable(getResources().getDrawable(R.drawable.<your drawable>, null));
```

####Loading using picasso
```
// Valid File object as f
Picasso.with(getBaseContext()).load(f).fit().centerInside().into(image);
```

####Cropping the image

Add a button to your UI so the user can request a crop once they're done selecting the target area. In your button click, use the following code to drop the image.

**Note**: Please note at this time the .crop() method only returns a bitmap. It's possible that the returning bitmap is so large that it causes an out of memory error. A workable solution is being worked on.

```
Button cropButton = (Button) findViewById(R.id.cropImage);
cropButton.setOnClickListener(new View.OnClickListener() {
  @Override
  public void onClick(View view) {
    ProfileImageCropper image=(ProfileImageCropper)findViewById(R.id.profileImage);
    Bitmap bmp = ((ProfileImageCropper) findViewById(R.id.profileImage)).crop();
    image.setEditMode(false);
    image.setImageBitmap(bmp);
  }
});
```
###How to Use the Activity
To use the activity, use the following code. You can use _getBaseContext()_, _MainActivity.this_, or any other context. _getBaseContext()_ is the recommended approach. PICA_ACITIVTY is any integer value that you can use in onActivityResult().

```
Intent intent = new Intent(getBaseContext(), ProfileImageCropperActivity.class);
startActivityForResult(intent, PICA_ACITIVTY);
```

If you want to control the look and feel of the cropper, you can pass in values for the cropper.

```
Intent intent = new Intent(MainActivity.this, ProfileImageCropperActivity.class);
intent.putExtra("cropperBackground", Color.argb(100, 250, 190, 30));
intent.putExtra("cropperBorder", Color.argb(255, 223, 133, 07));
intent.putExtra("cropperBorderWidth", 5);
intent.putExtra("cropperWidth", 350);
intent.putExtra("cropperMinimumWidth", 200);
intent.putExtra("handleBackground", Color.argb(255, 223, 133, 07));
intent.putExtra("handleBorder", Color.argb(255, 223, 133, 07));
intent.putExtra("handleBorderWidth", 5);
intent.putExtra("handleWidth", 65);
intent.putExtra("background", Color.argb(255, 0, 0, 0));
intent.putExtra("controlBackground", Color.argb(255, 0, 0, 0));

startActivityForResult(intent, PICA_ACITIVTY);
```

####Receiving result from launched activity


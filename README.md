```
  Under construction, hard hats required at all times!
  
  If you're an adventurer: compile 'com.mxp.profileimagecropper:profile-image-cropper:0.0.2'  // See ya!
```
# profileimagecropper :sparkles:

1. [The What](#the-what)
2. [The Who](#the-who)
3. [The Hows](#the-hows)
  1. [How to Get](#how-to-get)
  2. [How to Use](#how-to-use)
4. [The Rest](#the-rest)

##The What
This is a simple android library that allows you to crop images. The main purpose is to crop profile images. The library doesn't focus on picture quality, resolution fidelity, or any other aspect of image quality preservation. This library focuses on the ease of allowing a developer to add code to their app so their users can select and crop an image to use as their profile image.

The library provides a widget, ProfileImageCropper, that you can use directly in your xml layout. You can configure the UI via xml attributes. You enable edit mode on the widget and once the user indicates they're ready, you call .crop() method which returns to you the modified image.

The library also provides a basic activity, ProfileImageCropperActivity, that you can use. The activity allows the user to select an image, crop an image, and click done. When the user clicks on done, the editted image is saved in a local file and the filename is returned to you.

The library only adds 32KB to your project side.

##The Who
This library is for you if you need to allow your user to select an image as their profile image (or avatar), allow them to crop it, and to use the cropped image in your project.

This library is for you if all you want is to add a gradle dep and move on.

This library is for you if you're ok with modest customizations and don't require extensive control the widget UI.

##The Hows
###How to Get
To use this library, add the following compile line the dependencies section of your module's build.gradle file.

```
dependencies {
  ...
  compile 'com.mxp.profileimagecropper:profile-image-cropper:0.0.2'
  ...
}
```
###How to Use
To add the widget in your existing activity, use the following code. The attributes shown allow you to customize the UI, but they are optional. If you leave them out, the library will use default values. Note, you can start with a default image loaded by using __android:src=__ element.

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


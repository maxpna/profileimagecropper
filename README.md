```
  Under construction, hard hats required at all times!
  
  If you're an adventurer: compile 'com.mxp.profileimagecropper:profile-image-cropper:0.0.2'  // See ya!
```
# profileimagecropper :sparkles:

1. [The What](#the-what)
2. [The Who](#the-who)
3. [The Hows](#the-hows)
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
###How To Get
To use this library, add the following (highlighted) dependency in module's build.gradle file. This 

```
dependencies {
  ...
  compile 'com.mxp.profileimagecropper:profile-image-cropper:0.0.2'
  ...
}
```

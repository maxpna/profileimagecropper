```
  Under construction, please use hard hats!
```
# profileimagecropper

1. [The What](#the-what)
2. [The Who](#thewho)
3. [The Hows](#thehows)
4. [The Rest](#therest)

##The What
This is a simple android library that allows you to crop images. The main purpose is to crop profile images. The library doesn't focus on picture quality, resolution fidelity, or any other aspect of image quality preservation. This library focuses on the ease of allowing a developer to add code to their app so their users can select and crop an image to use as their profile image.

The library provides a widget, ProfileImageCropper, that you can use directly in your xml layout. You can configure the UI via xml attributes. You enable edit mode on the widget and once the user indicates they're ready, you call .crop() method which returns to you the modified image.

The library also provides a basic activity, ProfileImageCropperActivity, that you can use. The activity allows the user to select an image, crop an image, and click done. When the user clicks on done, the editted image is saved in a local file and the filename is returned to you.

The library only add 32KB to your project side.
Ideal use is in applications where your user needs to select a profile image, or an avatar.

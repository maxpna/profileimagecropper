package com.mxp.profileimagecropper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by maxpower on 9/15/2016.
 */
public class ProfileImageCropper extends ImageView {
  // constants
  private static final String TAG = "ProfileImageSizer";
  private static final int INVALID_POINTER_ID = -1;
  private static final int DEFAULT_WIDTH = 350;  // pixel values
  private static final int DEFAULT_HANDLE_WIDTH = 50;  // pixel values
  private static final int DEFAULT_MIN_WIDTH = 200;  // pixel values

  // defaults
  private int cropperBackground = 0xFFB7264D;
  private int cropperBorder = 0x99B900B9;
  private int cropperBorderWidth = 5;
  private int cropperWidth = DEFAULT_WIDTH;  // pixel values, will be converted to dp when read from xml

  private int handleBackground = 0x99000000;
  private int handleBorder = 0x99000000;
  private int handleBorderWidth = 2;
  private int handleWidth = DEFAULT_HANDLE_WIDTH;  // pixel values, will be converted to dp when read from xml

  private int cropperMinimumWidth = DEFAULT_MIN_WIDTH;  // pixel values, will be converted to dp when read from xml

  // These are the bounds for the two rectangles.
  Rect cropperBounds = new Rect(0, 0, DEFAULT_WIDTH, DEFAULT_WIDTH);
  Rect handleBounds = new Rect(0, 0, DEFAULT_HANDLE_WIDTH, DEFAULT_HANDLE_WIDTH);

  // The ‘active pointer’ is the one currently moving our object. These values are for the cropper
  private int mActivePointerId = INVALID_POINTER_ID;
  private float mLastTouchX;
  private float mLastTouchY;
  private float mPosX;
  private float mPosY;
  private boolean inside = false;

  // These values are for the handle
  private boolean insideHandle = false;

  private boolean editMode = false;

  private boolean drawBorder = false;
  private Rect imageBorder;

  public ProfileImageCropper(Context context) {
    super(context);
    initDragListener();
  }

  private void initDragListener() {
  }

  public ProfileImageCropper(Context context, AttributeSet attrs) {
    super(context, attrs);
    initImage(attrs);
  }

  public ProfileImageCropper(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initImage(attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ProfileImageCropper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initImage(attrs);
  }

  private void initImage(AttributeSet attrs) {
    if (attrs == null) return;

    TypedArray a = getContext().getTheme().obtainStyledAttributes(
      attrs,
      R.styleable.ProfileImageCropper,
      0, 0);

    try {
      setCropperBackground(a.getColor(R.styleable.ProfileImageCropper_cropperBackground, 0xFFB7264D));
      setCropperBorder(a.getInteger(R.styleable.ProfileImageCropper_cropperBorder, 0x99B900B9));
      setCropperBorderWidth((int) a.getDimension(R.styleable.ProfileImageCropper_cropperBorderWidth, 5));
      setCropperWidth((int) a.getDimension(R.styleable.ProfileImageCropper_cropperWidth, DEFAULT_WIDTH));
      setCropperMinimumWidth((int) a.getDimension(R.styleable.ProfileImageCropper_cropperMinimumWidth, DEFAULT_MIN_WIDTH));

      if(cropperWidth<cropperMinimumWidth) cropperWidth=cropperMinimumWidth;

      setHandleBackground(a.getColor(R.styleable.ProfileImageCropper_handleBackground, 0x99000000));
      setHandleBorder(a.getInteger(R.styleable.ProfileImageCropper_handleBorder, 0x99000000));
      setHandleBorderWidth((int) a.getDimension(R.styleable.ProfileImageCropper_handleBorderWidth, 2));
      setHandleWidth((int) a.getDimension(R.styleable.ProfileImageCropper_handleWidth, DEFAULT_HANDLE_WIDTH));

      setEditMode(a.getBoolean(R.styleable.ProfileImageCropper_editMode, true));

      cropperBounds.right = cropperWidth;
      cropperBounds.bottom = cropperWidth;
    } finally {
      a.recycle();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    // store border

    int[] points = getBitmapPositionInsideImageView(this);
    imageBorder = new Rect(points[0], points[1], points[0] + points[2], points[1] + points[3]);

    if (!editMode) return;

    ShapeDrawable shape = new ShapeDrawable(new RectShape());
    shape.getPaint().setStyle(Paint.Style.FILL);
    shape.setBounds(cropperBounds);
    shape.getPaint().setColor(cropperBackground);
    shape.draw(canvas);

    // border
    shape.getPaint().setStyle(Paint.Style.STROKE);
    shape.getPaint().setColor(cropperBorder);
    shape.getPaint().setStrokeWidth(cropperBorderWidth);
    shape.draw(canvas);

    ShapeDrawable shape2 = new ShapeDrawable(new OvalShape());
    handleBounds.left = cropperBounds.right - (handleWidth / 2);
    handleBounds.right = handleBounds.left + handleWidth;
    handleBounds.top = cropperBounds.bottom - (handleWidth / 2);
    handleBounds.bottom = handleBounds.top + handleWidth;

    //    Log.d(TAG, "HandleRect: [" + handleBounds.left + ", " + handleBounds.top + "] - [" + handleBounds.right + "," + handleBounds.bottom + "]");

    shape2.setBounds(handleBounds);
    shape2.getPaint().setStyle(Paint.Style.FILL);
    shape2.getPaint().setColor(handleBackground);
    shape2.getPaint().setStrokeWidth(handleBorderWidth);
    shape2.draw(canvas);

    shape2.getPaint().setStyle(Paint.Style.STROKE);
    shape2.getPaint().setColor(handleBorder);
    shape2.getPaint().setStrokeWidth(handleBorderWidth);
    shape2.draw(canvas);

    if (drawBorder) {
      ShapeDrawable shape3 = new ShapeDrawable(new RectShape());
      shape3.setBounds(imageBorder);
      shape3.getPaint().setStyle(Paint.Style.STROKE);
      shape3.getPaint().setColor(Color.RED);
      shape3.getPaint().setStrokeWidth(15);
      shape3.draw(canvas);
    }
  }

  public Bitmap crop() {
    if (!editMode) throw new IllegalStateException("Not in edit mode");
    if(getDrawable()==null) throw new IllegalStateException("No drawable set!");

    // create a new rect with bounds and adjust bounds if they're outside the image
    Rect tmpr = new Rect(cropperBounds);

    // if rect is out of imageborder bounds then correct that.
    if(tmpr.left<imageBorder.left) tmpr.left=imageBorder.left;
    if(tmpr.right>imageBorder.right) tmpr.right=imageBorder.right;
    if(tmpr.top<imageBorder.top) tmpr.top=imageBorder.top;
    if(tmpr.bottom>imageBorder.bottom) tmpr.bottom=imageBorder.bottom;

    logRect(imageBorder, "imageBorder");
    logRect(tmpr, "cropperBounds");

    int renderedWidth=imageBorder.right-imageBorder.left;
    int renderedHeight=imageBorder.bottom-imageBorder.top;

    // translate the bounds into percentages so the start and end is described as a percentage from
    // left,top of the image as drawn on screen. uses image border which has real screen coordinates.
    float percX = ((float) tmpr.left - imageBorder.left) / ((float) imageBorder.right - imageBorder.left);
    float percY = ((float) tmpr.top - imageBorder.top) / ((float) imageBorder.bottom - imageBorder.top);
    float percW = ((float) tmpr.right-tmpr.left) / ((float) imageBorder.right - imageBorder.left);
    float percH = ((float) tmpr.bottom-tmpr.top) / ((float) imageBorder.bottom - imageBorder.top);

    Log.d(TAG, "percX: " + percX + "%, perxY: " + percY + "%, percW: " + percW + "%, perxH: " + percH+"%");

    // create a bitmap from source
    Bitmap bmp = ((BitmapDrawable) getDrawable()).getBitmap();
    final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
    bitmapOptions.inDensity = bmp.getDensity();
    bitmapOptions.inTargetDensity = 1;
    bmp.setDensity(Bitmap.DENSITY_NONE);

    // create a rect to translate screen points as defined in percentage into real coordinates into
    // the bitmap
    Rect finalCut = new Rect();
    finalCut.left = (int) (percX * bmp.getWidth());
    finalCut.top = (int) (percY * bmp.getHeight());
    finalCut.right = (int) (percW * bmp.getWidth());
    finalCut.bottom = (int) (percH * bmp.getHeight());

    Log.d(TAG, "image WxH: " + getWidth() + "x" + getHeight() + ", bmp WxH: " + bmp.getWidth() + "x" + bmp.getHeight());
    logRect(finalCut, "finalcut");

    Log.d(TAG, "percW: " + percW + " - percW/imageBorder.Width (" + percW + "*" + (imageBorder.right - imageBorder.left) + "): " + (percW * (imageBorder.right - imageBorder.left)));
    Log.d(TAG, "finalCut.width: " + (finalCut.right - finalCut.left) +
      " - finalCut.width/bmp.Width (" + (finalCut.right - finalCut.left) + "*" + (bmp.getWidth()) + "): " + ((finalCut.right - finalCut.left) * (bmp.getWidth())));

    Bitmap bmp2 = Bitmap.createBitmap(bmp, finalCut.left, finalCut.top, finalCut.right, finalCut.bottom);
    return bmp2;
  }

  // returns true if point is inside cropperBounds, otherwise false
  private boolean isTouchInside(Rect bounds, Point point) {
    return isTouchInside(bounds, point.x, point.y);
  }

  // returns true is point is inside cropperBounds, otherwise false
  private boolean isTouchInside(Rect bounds, int x, int y) {
    if (bounds.left < x && x < bounds.right
      && bounds.top < y && y < bounds.bottom)
      return true;
    else
      return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (editMode == false) return super.onTouchEvent(event);

    inside = isTouchInside(cropperBounds, (int) event.getX(), (int) event.getY());

    final int action = MotionEventCompat.getActionMasked(event);

    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        final int pointerIndex = MotionEventCompat.getActionIndex(event);
        final float x = MotionEventCompat.getX(event, pointerIndex);
        final float y = MotionEventCompat.getY(event, pointerIndex);

        // Remember where we started (for dragging)
        mLastTouchX = x;
        mLastTouchY = y;
        // Save the ID of this pointer (for dragging)
        mActivePointerId = MotionEventCompat.getPointerId(event, 0);

        insideHandle = isTouchInside(handleBounds, (int) event.getX(), (int) event.getY());
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        // Find the index of the active pointer and fetch its position
        final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);

        final float x = MotionEventCompat.getX(event, pointerIndex);
        final float y = MotionEventCompat.getY(event, pointerIndex);

        // Calculate the distance moved
        final float dx = x - mLastTouchX;
        final float dy = y - mLastTouchY;

        if (!insideHandle) {
          if ((mPosX + dx) >= 0 && ((mPosX + dx) <= getWidth() - cropperWidth)) mPosX += dx;
          if ((mPosY + dy) >= 0 && ((mPosY + dy) <= getHeight() - cropperWidth)) mPosY += dy;
        } else {  // touch is inside the handler area
          if (!(cropperWidth + dx < cropperMinimumWidth)) {
            cropperWidth += dx;
          }
        }

        cropperBounds.left = (int) mPosX;
        cropperBounds.top = (int) mPosY;
        cropperBounds.right = cropperBounds.left + cropperWidth;
        cropperBounds.bottom = cropperBounds.top + cropperWidth;

        invalidate();

        // Remember this touch position for the next move event
        mLastTouchX = x;
        mLastTouchY = y;

        break;
      }

      case MotionEvent.ACTION_UP: {
        mActivePointerId = INVALID_POINTER_ID;
        insideHandle = false;
        break;
      }

      case MotionEvent.ACTION_CANCEL: {
        mActivePointerId = INVALID_POINTER_ID;
        insideHandle = false;
        break;
      }

      case MotionEvent.ACTION_POINTER_UP: {

        final int pointerIndex = MotionEventCompat.getActionIndex(event);
        final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

        if (pointerId == mActivePointerId) {
          // This was our active pointer going up. Choose a new active pointer and adjust accordingly.
          final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
          mLastTouchX = MotionEventCompat.getX(event, newPointerIndex);
          mLastTouchY = MotionEventCompat.getY(event, newPointerIndex);
          mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
        }
        insideHandle = false;
        break;
      }
    }
    return true;
//    return super.onTouchEvent(event);
  }

  private int[] getBitmapPositionInsideImageView(ImageView imageView) {
    int[] ret = new int[4];

    if (imageView == null || imageView.getDrawable() == null)
      return ret;

    // Get image dimensions
    // Get image matrix values and place them in an array
    float[] f = new float[9];
    imageView.getImageMatrix().getValues(f);

    // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
    final float scaleX = f[Matrix.MSCALE_X];
    final float scaleY = f[Matrix.MSCALE_Y];

    // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
    final Drawable d = imageView.getDrawable();
    final int origW = d.getIntrinsicWidth();
    final int origH = d.getIntrinsicHeight();

    // Calculate the actual dimensions
    final int actW = Math.round(origW * scaleX);
    final int actH = Math.round(origH * scaleY);

    ret[2] = actW;
    ret[3] = actH;

    // Get image position
    // We assume that the image is centered into ImageView
    int imgViewW = imageView.getWidth();
    int imgViewH = imageView.getHeight();

    int top = (int) (imgViewH - actH) / 2;
    int left = (int) (imgViewW - actW) / 2;

    ret[0] = left;
    ret[1] = top;

    return ret;
  }

  private void logRect(Rect r, String name) {
    if (r != null)
      Log.d(TAG, "Rect (" + name + "): [" + r.left
        + ","
        + r.top + "] [" + r.right + "," + r.bottom + "]"
        + " "
        + "WxH: " + (r.right - r.left) + "x" + (r.bottom - r.top));
  }

  public int getCropperBackground() {
    return cropperBackground;
  }

  public void setCropperBackground(int cropperBackground) {
    this.cropperBackground = cropperBackground;
  }

  public int getCropperBorder() {
    return cropperBorder;
  }

  public void setCropperBorder(int cropperBorder) {
    this.cropperBorder = cropperBorder;
  }

  public int getCropperBorderWidth() {
    return cropperBorderWidth;
  }

  public void setCropperBorderWidth(int cropperBorderWidth) {
    this.cropperBorderWidth = cropperBorderWidth;
  }

  public int getHandleBackground() {
    return handleBackground;
  }

  public void setHandleBackground(int handleBackground) {
    this.handleBackground = handleBackground;
  }

  public int getHandleBorder() {
    return handleBorder;
  }

  public void setHandleBorder(int handleBorder) {
    this.handleBorder = handleBorder;
  }

  public int getHandleBorderWidth() {
    return handleBorderWidth;
  }

  public void setHandleBorderWidth(int handleBorderWidth) {
    this.handleBorderWidth = handleBorderWidth;
  }

  public int getCropperWidth() {
    return cropperWidth;
  }

  public void setCropperWidth(int cropperWidth) {
    this.cropperWidth = cropperWidth;
    cropperBounds.right = cropperBounds.left + cropperWidth;
    cropperBounds.bottom = cropperBounds.top + cropperWidth;
    invalidate();
  }

  public int getHandleWidth() {
    return handleWidth;
  }

  public void setHandleWidth(int handleWidth) {
    this.handleWidth = handleWidth;
  }

  public boolean isEditMode() {
    return editMode;
  }

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
    invalidate();
  }

  public boolean isDrawBorder() {
    return drawBorder;
  }

  /**
   * Draws a red border around image as debugging tool. Only visible in editmode=true
   * @param drawBorder - to draw border or not
   */
  public void setDrawBorder(boolean drawBorder) {
    this.drawBorder = drawBorder;
    invalidate();
  }

  /**
   * @return - Returns the minimum width the cropper square can have
   */
  public int getCropperMinimumWidth() {
    return cropperMinimumWidth;
  }

  /**
   * This is the minimum width the cropper square can have. Negative values will be converted to 200
   */
  public void setCropperMinimumWidth(int cropperMinimumWidth) {
    this.cropperMinimumWidth = cropperMinimumWidth;
  }

//  @Override
//  protected void onDraw(Canvas canvas) {
//    super.onDraw(canvas);
//
//    try {
//      Paint paint = new Paint();
//      paint.setStyle(Paint.Style.FILL);
//      paint.setColor(Color.BLACK);
//      paint.setTextSize(20);
//      canvas.drawText("cropperBounds: " + cropperBounds.left + "," + cropperBounds.top + " x " + cropperBounds.right + "," + cropperBounds.bottom, 0, 0, paint);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    Log.d(TAG, "Rect: [" + cropperBounds.left + (int) mPosX + ", " + cropperBounds.top + (int) mPosY + "] - [" + cropperBounds.right + (int) mPosX + "," + cropperBounds.bottom + (int) mPosY + "]");
//
//    ShapeDrawable shape = new ShapeDrawable(new RectShape());
//    shape.getPaint().setStyle(Paint.Style.FILL);
//    shape.setBounds(cropperBounds.left + (int) mPosX, cropperBounds.top + (int) mPosY, cropperBounds.right + (int) mPosX, cropperBounds.bottom + (int) mPosY);
////    if (inside)
//    shape.getPaint().setColor(cropperBackground);
////    else
////      shape.getPaint().setARGB(99, 0, 0, 255);
//    shape.draw(canvas);
//
//    // border
//    shape.getPaint().setStyle(Paint.Style.STROKE);
//    shape.getPaint().setColor(cropperBorder);
//    shape.getPaint().setStrokeWidth(cropperBorderWidth);
//    shape.draw(canvas);
//
//    ShapeDrawable shape2 = new ShapeDrawable(new RectShape());
//    int newX = (cropperBounds.right + (int) mPosX) - 25;
//    int newY = (cropperBounds.bottom + (int) mPosY) - 25;
//
//    handleBounds.left = newX;
//    handleBounds.right = handleBounds.left + 50;
//    handleBounds.top = newY;
//    handleBounds.bottom = handleBounds.top + 50;
//
//    Log.d(TAG, "HandleRect: [" + handleBounds.left + ", " + handleBounds.top + "] - [" + handleBounds.right + "," + handleBounds.bottom + "]");
//
//    shape2.setBounds(handleBounds);
//    shape.getPaint().setStyle(Paint.Style.FILL);
//    shape2.getPaint().setColor(handleBackground);
//    shape2.getPaint().setStrokeWidth(handleBorderWidth);
//    shape2.draw(canvas);
//
//    shape2.getPaint().setStyle(Paint.Style.STROKE);
//    shape2.getPaint().setColor(handleBorder);
//    shape2.getPaint().setStrokeWidth(handleBorderWidth);
//    shape2.draw(canvas);
//  }

}

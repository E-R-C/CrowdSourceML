package edu.hendrix.huynhem.seniorthesis.Imaging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.util.PriorityQueue;

/**
 * This can be changed into an abstract class because we may change the way we calculate the brief
 * descriptors.
 * Eric - 10/22/2017
 */

public class Image {
    private Bitmap bitmap;
    public Image(Bitmap b){
        bitmap = b;
    }
    public Image(String location, int maxDimension) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(location, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        // https://developer.android.com/topic/performance/graphics/load-bitmap.html
        // We have to do this or else it will overflow the memory of the device;
        if (height > maxDimension || width > maxDimension) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= maxDimension
                    || (halfWidth / inSampleSize) >= maxDimension) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(location, options);

    }

    
    public int getWidth(){
        return bitmap.getWidth();
    }
    public int getHeight(){
        return bitmap.getHeight();
    }

    public PriorityQueue<FASTFeature> getFastPoints(){
        return FAST.calculateFASTPoints(this);
    }


    // This could potentially be rewritten to test other comparison methods
    // It returns 0001 or 0000 which will be OR'd with some 0 bit in the descriptor.
    public int comparePixel(int color1, int color2){
//        if (color1 < 0 && color2 < 0 || color2 < 0 && color1 >= 0){
//            return 0;
//        } else if (color1 < 0 && color2 >= 0){
//            return 1;
//        }
//        byte score = 0;
//        if(Color.red(color1) > Color.red(color2)) score ++;
//        if(Color.blue(color1) > Color.blue(color2)) score++;
//        if(Color.green(color1) > Color.green(color2)) score++;

        return greyValue(color1) > greyValue(color2) ? 1 : 0;
    }
    private double greyValue(int color1){
        int r = Color.red(color1);
        int g = Color.green(color1);
        int b = Color.blue(color1);
        return (r + g + b)/3.0;
    }

    public int getValueAt(int x, int y) {
        if (x < bitmap.getWidth() && x > 0 && y > 0  && y < bitmap.getHeight()){
            return bitmap.getPixel(x,y);
        }
        return -1;
    }

    public int getIntensity(int x, int y){
        int color = getValueAt(x,y);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return (red + green + blue) / 3;
    }
    public Image scaleImage(float percent) {
        if (percent > 0){
            int newWidth = (int) Math.floor(bitmap.getWidth() * percent);
            int newHeight = (int) Math.floor(bitmap.getHeight() * percent);
            Log.d("IMAGE", "Resizing from " +  bitmap.getWidth() + "x" + bitmap.getHeight() + " to " + newWidth + "x" + newHeight );
            return new Image(Bitmap.createScaledBitmap(bitmap,newWidth,newHeight,false));
        }
        return this;
    }

    public Image rotateImage(float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap bp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        return new Image(bp);
    }




}

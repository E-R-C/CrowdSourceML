package edu.hendrix.huynhem.seniorthesis.Imaging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.PriorityQueue;

import edu.hendrix.huynhem.seniorthesis.Util.Duple;

/**
 * This can be changed into an abstract class because we may change the way we calculate the brief
 * descriptors.
 * Eric - 10/22/2017
 */

public class Image {
    private Bitmap bitmap;
    public Image(String  location){
        bitmap = BitmapFactory.decodeFile(location);
    }
    public Image(Bitmap b){
        bitmap = b;
    }
    public Image(String location, int maxDimension){
        bitmap = BitmapFactory.decodeFile(location);
        if (bitmap.getWidth() > bitmap.getHeight()){
            if (bitmap.getWidth() > maxDimension){
                float percent = (maxDimension * 1.0f) / (float) getWidth() ;
                Log.d("IMAGE", percent + "");
                bitmap = scaleImage(percent).bitmap;
            }
        } else {
            if (bitmap.getHeight() > maxDimension){
                float percent = (maxDimension * 1.0f) / (float) getHeight() ;
                bitmap = scaleImage(percent).bitmap;
            }
        }
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
    // first and second are the list of offsets from the center (each fastpoint) that we are comparing
    // if First > second, we store a 1, else we store a 0
    public BigInteger[] getBriefDescriptors(ArrayList<FASTFeature> fastPoints) {
        BigInteger[] result = new BigInteger[fastPoints.size()];
        for(int i = 0; i < fastPoints.size(); i++){
            BigInteger descriptor = BriefPatches.calculateDescriptor(this,fastPoints.get(i));
            result[i] = descriptor;
        }
        return result;
    }

    // This could potentially be rewritten to test other comparison methods
    // It returns 0001 or 0000 which will be OR'd with some 0 bit in the descriptor.
    public int comparePixel(int color1, int color2){
        if (color1 < 0 && color2 < 0 || color2 < 0 && color1 >= 0){
            return 0;
        } else if (color1 < 0 && color2 >= 0){
            return 1;
        }
        byte score = 0;
        if(Color.red(color1) > Color.red(color2)) score ++;
        if(Color.blue(color1) > Color.blue(color2)) score++;
        if(Color.green(color1) > Color.green(color2)) score++;
        return score > 1 ? 1 : 0;
    }

    public int getValueAt(int x, int y) {
        if (x < bitmap.getWidth() && x > 0 && y > 0  && y < bitmap.getHeight()){
            return bitmap.getPixel(x,y);
        }
        return -1;
    }
    public int getValueAt(Duple<Integer,Integer> xy){
        return getValueAt(xy.getOne(), xy.getTwo());
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

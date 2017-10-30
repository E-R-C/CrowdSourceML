package edu.hendrix.huynhem.seniorthesis.Imaging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

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
    Bitmap bitmap;
    public Image(String  location){
        bitmap = BitmapFactory.decodeFile(location);
    }
    public Image(Bitmap b){
        bitmap = b;
    }


    public PriorityQueue<FASTFeature> getFastPoints(){
        return FAST.calculateFASTPoints(this);
    }
    // first and second are the list of offsets from the center (each fastpoint) that we are comparing
    // if First > second, we store a 1, else we store a 0
    // How big is each desciptor? is each fast point stored as 256 bits?
    public BigInteger[] getBriefDescriptors(ArrayList<FASTFeature> fastPoints,
                                     ArrayList<Duple<Integer,Integer>> first,
                                     ArrayList<Duple<Integer,Integer>> second) {

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
        byte score = 0;
        if(Color.red(color1) > Color.red(color2)) score ++;
        if(Color.blue(color1) > Color.blue(color2)) score++;
        if(Color.green(color1) > Color.green(color2)) score++;
        return score > 1 ? 1 : 0;
    }

    public int getValueAt(int x, int y) {
        return bitmap.getPixel(x,y);
    }
    public int getValueAt(Duple<Integer,Integer> xy){
        return getValueAt(xy.getOne(), xy.getTwo());
    }

    public Image scaleImage(float percent) {
        int newWidth = (int) Math.floor(bitmap.getWidth() * percent);
        int newHeight = (int) Math.floor(bitmap.getHeight() * percent);
        return new Image(Bitmap.createScaledBitmap(bitmap,newWidth,newHeight,false));
    }


    public Image rotateImage(float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap bp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        return new Image(bp);
    }


}

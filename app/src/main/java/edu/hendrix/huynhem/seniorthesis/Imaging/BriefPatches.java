package edu.hendrix.huynhem.seniorthesis.Imaging;

import java.math.BigInteger;
import java.util.ArrayList;

import edu.hendrix.huynhem.seniorthesis.Util.Duple;

/**
 * This defines the Brief Patches that will be used throughout the entirety of the program
 */

public class BriefPatches {
    public static final int[] x1s = new int[]{6,-1,-2,2,-8,1,0,0,-3,3,-4,-9,-9,-6,6,5,-5,-10,-4,7,-6,6,-7,10,-8,0,3,-3,8,-4,9,-6,6,9,2,-9,4,-7,7,1,-8,0,7,3,-1,-9,9,6,-6,-9,-5,10,-2,4,-10,5,-1,-3,2,-7,7,5,-8,-2,2,-1,-6,7,7,-7,9,-5,5,-6,9,10,6,-4,7,-7,8,4,2,-10,-8,1,-8,3,-1,1,8,-2,-7,4,-10,-4,9,-5,5,5,-9,5,-10,-9,7,-6,-2,-1,2,2,-3,-7,7,3,2,-3,8,-1,4,-4,9,-6,5,-5,-1,-9,-2,7,-7,3,-4,0,0,1,-7,4,3,-3,-9,8,-4,-5,-6,6,-2,3,1,1,8,-6,7,-7,5,6,-6,-9,-5,-4,-5,8,-1,-3,-8,2,-2,1,-8,3,-3,-7,-4,7,10,4,-5,9,-10,4,7,-4,8,-1,3,0,-7,3,-1,-3,8,-2,5,4,-9,-10,9,-5,5,-1,10,-4,-7,7,-8,0,-8,4,-3,3,2,4,-4,4,6,8,-2,4,7,0,0,-8,-4,-10,10,-1,5,9,-6,-9,-10,9,4,5,-4,8,10,-2,-1,2,7,3,0,1,-8,-3,-2,1,6,-7,4,-10,9,5,6,5,-9,-10
    };
    public static final int[] y1s = new int[]{9,0,0,-4,-4,-5,7,10,-7,7,-8,-2,-1,-5,7,5,-5,-5,10,6,-10,10,-8,7,-7,4,2,-4,2,-3,3,-4,0,-9,-9,-5,-9,-5,5,-4,-10,-3,-5,1,-9,-9,9,-2,2,3,6,-6,-9,-7,9,-8,4,4,-8,-10,8,1,5,-3,1,-3,8,-2,-1,0,4,5,-5,7,-8,-10,-5,6,2,-4,-7,-2,-3,10,-3,-6,2,6,-1,10,6,-1,3,1,0,-7,7,0,-2,-1,-7,4,-8,5,1,-9,2,2,-2,-1,10,9,-9,5,7,-5,3,-5,6,-2,2,-3,7,-7,8,8,8,4,-6,-1,8,-1,2,3,4,8,0,-2,-10,0,2,10,-1,2,5,-3,-2,-1,10,9,-7,7,-6,-8,4,1,-1,7,9,-6,6,6,-2,-6,6,4,3,9,-9,2,-6,-4,-3,2,3,-6,-7,-4,0,4,-9,3,-9,6,8,4,-8,-6,4,-8,9,7,-6,-9,1,-8,6,9,6,9,-7,7,-8,3,10,9,-3,3,9,4,3,-5,3,-2,10,-10,10,-4,0,7,-10,5,-7,-10,-7,10,5,0,8,-2,-8,-10,0,-5,1,7,7,-5,9,-5,-10,-8,4,-10,-4,8,-9,1,3,6,5,-4,-6,2,7,-2
    };
    public static final int[] x2s = new int[]{-4,-7,6,8,4,-2,2,-8,1,0,0,-8,-3,-1,-2,8,5,-5,-10,4,9,-5,-9,10,-4,7,6,-10,-8,1,-1,2,8,6,-9,6,-6,-9,2,4,-7,1,-8,7,-4,-1,-9,5,-2,-5,-10,10,-4,-2,-3,2,7,0,0,-1,-8,-1,-2,8,-6,7,9,-6,6,-9,-10,10,-4,9,8,2,-1,-8,3,1,3,-2,-1,8,2,7,4,10,-4,9,-5,-6,5,9,-5,10,10,-6,8,2,2,-1,-2,0,-7,-3,2,-1,8,3,5,-4,-4,10,-2,4,6,-5,-9,-1,3,3,-7,7,-3,-6,5,-8,0,7,-7,-3,4,-9,9,3,8,4,-5,-6,6,-1,-2,1,1,0,-8,3,8,-6,7,-10,10,5,-5,-5,-9,10,-10,-4,-5,5,8,6,2,3,-8,-8,1,0,-8,3,2,1,8,-6,-7,-4,-3,10,9,5,6,-5,-9,10,-4,-7,6,-10,-2,-3,0,-7,7,3,-3,-8,5,4,10,-10,9,-10,6,5,-5,-5,-4,7,-8,-8,1,4,3,2,8,-6,4,8,-1,-3,3,1,-8,-7,-4,7,-1,9,-10,6,-6,2,4,8,-2,-3,-1,7,-7,-8,3,-1,1,2,6,-7,-2,-10,4,-9,5,-5,-9,-10
    };
    public static final int[] y2s = new int[]{5,-3,9,-8,-3,0,-4,-4,-5,7,10,1,-7,-7,-7,5,8,-10,3,0,0,-5,10,7,10,6,10,-5,-7,1,-6,6,2,-3,-3,0,-4,9,-9,-9,-5,-4,-10,-5,-9,-9,-9,-8,-9,6,9,-6,1,4,4,-8,8,-9,-6,-2,5,-3,-3,9,8,-2,4,7,-5,6,10,3,6,-8,-7,-3,1,-3,-7,5,6,-2,-1,6,2,-3,1,-4,-7,7,0,-8,-1,-5,-6,4,9,-9,-10,-1,-2,2,2,5,9,-5,7,-5,3,5,10,-2,-1,10,-5,6,1,-7,8,8,-2,-1,-6,4,0,-3,7,-9,-1,-6,4,-2,8,-10,8,0,0,-6,10,-1,2,5,5,-1,-2,-5,6,10,10,9,-7,4,-8,-6,4,-2,1,-9,-4,7,-1,0,-6,-8,-6,-8,-1,-2,-7,-8,3,9,3,9,7,10,2,-6,-9,-3,6,-3,5,-3,4,5,4,-1,8,-7,3,9,9,8,-10,4,-6,0,9,7,-2,2,1,-9,6,6,-8,-9,9,7,-8,10,2,9,3,9,1,0,-5,-1,10,2,-4,-3,7,6,-10,-8,-10,10,5,-7,5,10,-8,-5,7,5,7,9,-9,4,8,-4,8,0,-9,1,-4,6,3,-5,-4,-4,7,-2};
    public static ArrayList<Duple<Integer,Integer>> getFirst(){
       return get(x1s,y1s);
    }
    public static ArrayList<Duple<Integer,Integer>> getSecond(){
        return get(x2s,y2s);
    }
    private static ArrayList<Duple<Integer,Integer>> get(int[] xs, int[] ys){
        ArrayList<Duple<Integer,Integer>> result = new ArrayList<>(xs.length);
        for(int i = 0; i < xs.length; i++){
            result.add(new Duple<Integer,Integer>(xs[i],ys[i]));
        }
        return result;
    }

    public static BigInteger calculateDescriptor(Image image, FASTFeature fastPoint ){
        BigInteger descriptor = BigInteger.ZERO;
        int fx = fastPoint.X();
        int fy = fastPoint.Y();
        for(int j = 0; j < x1s.length; j++){
            descriptor = descriptor.shiftLeft(1);
            descriptor = descriptor.or(BigInteger.valueOf(
                    image.comparePixel(image.getValueAt(fx + x1s[j],fy + y1s[j]), image.getValueAt(fx + x2s[j],fy + y2s[j]))));
        }
        return descriptor;
    }
    // Due to performance issues hinted here: http://royontechnology.blogspot.com/2010/10/performance-of-bigintegertostringradix.html
    // I used this toHexStringFunction instead of the built in one.
    public static String bigIntToHexString(BigInteger bigInteger){
        byte[] input = bigInteger.toByteArray();
        char lookupArray[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] result = new char[input.length * 2];
        for(int i = 0; i < input.length; i++){
            result[2*i] = lookupArray[(input[i]>>4) & 0x0F];
            result[2*i+1] = lookupArray[(input[i] & 0x0F)];
            i++;
        }
        return String.valueOf(result);
    }
    public static String calcDescriptorString(Image image, FASTFeature fastFeature){
        BigInteger b = calculateDescriptor(image, fastFeature);
        return bigIntToHexString(b);
    }

}

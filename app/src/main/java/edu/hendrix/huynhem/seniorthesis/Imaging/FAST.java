package edu.hendrix.huynhem.seniorthesis.Imaging;

import java.util.PriorityQueue;

import edu.hendrix.huynhem.seniorthesis.Util.EnumHistogram;

/**
 *
 */

public class FAST {
    public static final int RADIUS = 3,
            N = 12,
            MIN_DIMENSION = RADIUS * 3,
            INTENSITY_THRESHOLD = 5;
    public static final int TOTAL_PATCHES = 256;
    public static final FASTFeature[] CIRCLE_POINTS =
            new FASTFeature[]{new FASTFeature(0, 3), new FASTFeature(1, 3), new FASTFeature(2, 2), new FASTFeature(3, 1),
                    new FASTFeature(3, 0), new FASTFeature(3, -1), new FASTFeature(2, -2), new FASTFeature(1, -3),
                    new FASTFeature(0, -3), new FASTFeature(-1, -3), new FASTFeature(-2, -2), new FASTFeature(-3, -1),
                    new FASTFeature(-3, 0), new FASTFeature(-3, 1), new FASTFeature(-2, 2), new FASTFeature(-1, 3)};
    public static PriorityQueue<FASTFeature> calculateFASTPoints(Image image){
        PriorityQueue<FASTFeature>result = new PriorityQueue<>();
        int centerX = image.getWidth() / 2;
        int centerY = image.getHeight() / 2;
        for (int x = RADIUS; x < image.getWidth() - RADIUS - 1; x++) {
            for (int y = RADIUS; y < image.getHeight() - RADIUS - 1; y++) {
                Thresh i0 = eval(image, x, y, 0);
                Thresh i8 = eval(image, x, y, 8);
                if (i0 != Thresh.WITHIN || i8 != Thresh.WITHIN) {
                    Thresh i4 = eval(image, x, y, 4);
                    Thresh i12 = eval(image, x, y, 12);
                    EnumHistogram<Thresh> counts = new EnumHistogram<>(Thresh.class);
                    counts.bump(i0);
                    counts.bump(i4);
                    counts.bump(i8);
                    counts.bump(i12);
                    boolean found = false;
                    if (counts.getCountFor(Thresh.ABOVE) >= 3) {
                        found = longestSequenceOf(getComparisons(image, x, y), Thresh.ABOVE) >= N;
                    } else if (counts.getCountFor(Thresh.BELOW) >= 3) {
                        found = longestSequenceOf(getComparisons(image, x, y), Thresh.BELOW) >= N;
                    }
                    if (found) {
                        result.add(new FASTFeature(x,y,calculateEuclideanDistance(centerX, centerY, x, y)));
                    }
                }
            }
        }
        return result;
    }


    static Thresh[] getComparisons(Image img, int x, int y) {
        Thresh[] result = new Thresh[CIRCLE_POINTS.length];
        for (int i = 0; i < CIRCLE_POINTS.length; i++) {
            result[i] = eval(img, x, y, i);
        }
        return result;
    }

    public static float calculateEuclideanDistance(int x1, int y1, int x2, int y2 ){
        return (float) Math.sqrt(Math.pow(x1 - x2,2) + Math.pow(y1 - y2, 2));
    }
    static Thresh eval(Image img, int x, int y, int circPt) {
        FASTFeature f = CIRCLE_POINTS[circPt];
        int diff = img.getIntensity(x + f.X(), y + f.Y()) - img.getIntensity(x, y);
        return diff > INTENSITY_THRESHOLD
                ? Thresh.ABOVE
                : (diff < -INTENSITY_THRESHOLD ? Thresh.BELOW : Thresh.WITHIN);
    }
    private static int longestSequenceOf(Thresh[] threshes, Thresh of) {
        int[] countFor = new int[threshes.length];
        for (int i = 0; i < threshes.length; i++) {
            int j = 0;
            while (j < threshes.length && threshes[(i+j) % threshes.length] == of) {
                j += 1;
            }
            countFor[i] = j;
        }
        int max = countFor[0];
        for (int i = 1; i < countFor.length; i++) {
            if (countFor[i] > max) {
                max = countFor[i];
            }
        }
        return max;
    }
    private enum Thresh {
        ABOVE, WITHIN, BELOW;
    }

}

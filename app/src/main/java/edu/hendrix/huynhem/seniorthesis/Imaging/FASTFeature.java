package edu.hendrix.huynhem.seniorthesis.Imaging;

import android.support.annotation.NonNull;

/**
 *
 */

public class FASTFeature implements Comparable<FASTFeature>{
    private int x, y;
    private float rank = 0;
    public FASTFeature(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public FASTFeature(int x, int y, float rank){
        this.x = x;
        this.y = y;
        this.rank = rank;
    }

    public FASTFeature add(FASTFeature other) {
        return new FASTFeature(this.x + other.x, this.y + other.y);
    }

    public int X() {return x;}
    public int Y() {return y;}

    @Override
    public boolean equals(Object other) {
        if (other instanceof FASTFeature) {
            FASTFeature that = (FASTFeature)other;
            return this.x == that.x && this.y == that.y;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }

    @Override
    public int hashCode() {return x * 10000 + y;}

    public static long euclideanDistanceSquared(FASTFeature f1, FASTFeature f2) {
        int xDiff = f2.x - f1.x;
        int yDiff = f2.y - f1.y;
        return xDiff*xDiff + yDiff*yDiff;
    }

    public static double angle(FASTFeature f1, FASTFeature f2) {
        return Math.atan2(f2.Y() - f1.Y(), f2.X() - f1.X());
    }

    public FASTFeature weightedCentroidWith(FASTFeature other, long thisCount, long otherCount) {
        long xNumer = x * thisCount + other.x * otherCount;
        long yNumer = y * thisCount + other.y * otherCount;
        long denom = thisCount + otherCount;
        return new FASTFeature((int)(xNumer/denom), (int)(yNumer/denom));
    }

    @Override
    public int compareTo(@NonNull FASTFeature fastFeature) {
        return Float.compare(this.rank, fastFeature.rank);
    }
}

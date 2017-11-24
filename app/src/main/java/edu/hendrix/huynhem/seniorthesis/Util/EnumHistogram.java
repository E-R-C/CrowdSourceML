package edu.hendrix.huynhem.seniorthesis.Util;

import java.util.HashMap;

/**
 * Enum Histogram used to keep track of Enum counts. Used in imaging/FAST.java
 */

public class EnumHistogram<T extends Enum<T>> {
    HashMap<T,Integer> counter;
    Integer max = 0;
    T maxT;
    public EnumHistogram(Class<T> EnumType){
        counter = new HashMap<>();
        for(T en : EnumType.getEnumConstants()){
            counter.put(en, 0);
        }
        maxT = EnumType.getEnumConstants()[0];
    }
    public void bump(T en){
        counter.put(en, counter.get(en) + 1);
        if (counter.get(en) > max){
            maxT = en;
            max = counter.get(en);
        }
    }
    public T getMax(){
        return maxT;
    }
    public int getMaxCount(){
        return max;
    }
    public int getCountFor(T en){
        return counter.get(en);
    }
}

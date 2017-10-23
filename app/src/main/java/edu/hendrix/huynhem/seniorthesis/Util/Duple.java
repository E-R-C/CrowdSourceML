package edu.hendrix.huynhem.seniorthesis.Util;

/**
 * Created by eric on 10/23/17.
 */

public class Duple <K,V> {
    K one;
    V two;
    public Duple (K k, V v){
        one = k;
        two = v;
    }

    public K getOne() {
        return one;
    }

    public V getTwo() {
        return two;
    }
}

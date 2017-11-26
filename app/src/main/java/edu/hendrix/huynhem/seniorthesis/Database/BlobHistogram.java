package edu.hendrix.huynhem.seniorthesis.Database;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 */

public class BlobHistogram implements Serializable {
    HashMap<String, Integer> labelHistogram; // Label and counts

    public BlobHistogram(){
        labelHistogram = new HashMap<>();
    }

    // Returns a new merged copy of the two histograms.
    public BlobHistogram mergeMakeNewCopy(BlobHistogram hist2){
        BlobHistogram result = new BlobHistogram();
        HashMap<String, Integer> map2 = hist2.getUnderlyingMap();
        for(String s: labelHistogram.keySet()){
            result.bumpBy(s,labelHistogram.get(s));
        }
        for(String s: map2.keySet()){
            result.bumpBy(s,map2.get(s));
        }
        return result;
    }

    public void bump(String label){
        if (!labelHistogram.containsKey(label)){
            labelHistogram.put(label,1);
        } else {
            labelHistogram.put(label, labelHistogram.get(label) + 1);
        }
    }
    private void bumpBy(String label, int value){
        if (!labelHistogram.containsKey(label)){
            labelHistogram.put(label,value);
        } else {
            labelHistogram.put(label, labelHistogram.get(label) + value);
        }
    }

    public String getMaxLabel(){
        int maxCount = 0;
        String result = "No Labels in Histogram";
        for(String s: labelHistogram.keySet()){
            if (labelHistogram.get(s) > maxCount){
                maxCount = labelHistogram.get(s);
                result = s;
            }
        }
        return result;
    }

    public int getCountFor(String label){
        return labelHistogram.get(label);
    }
    public HashMap<String, Integer> getUnderlyingMap(){
        return labelHistogram;
    }
}

package edu.hendrix.huynhem.seniorthesis.Util;

import java.util.HashMap;

/**
 * This class is meant to abstract out classification results.
 * Create a classification report after classifying a set and have the actual labels
 * This class will be able to generate the confusion matrix and hold statistics about the classification
 */

public class ClassificationReport {
    private int total, truePositive, trueNegative, falsePositive, falseNegative;
    private String positiveLabel;
    /**
     * @param actual is the true labels
     * @param  classification is the results of the classification
     * @param truePosiveLabel the label we use to compare true negatives and true positives
     */
    public ClassificationReport(HashMap<String, String> actual, HashMap<String, String> classification, String truePosiveLabel){
        positiveLabel = truePosiveLabel;
        total = classification.size();
        for(String key: classification.keySet()){
            if (actual.get(key).equals(classification.get(key))){
                if(classification.get(key).equals(truePosiveLabel)){
                    truePositive++;
                } else {
                    trueNegative++;
                }
            } else {
                if(classification.get(key).equals(truePosiveLabel)){
                    falsePositive++;
                } else {
                    falseNegative++;
                }
            }
        }
    }

    /*
     * The target output of this should look like this
     * n = total | Positive = positiveLabel
     * TN = 50   | FP = 10
     * FN = 5    | TP = 100
     *
     */
    public String toConfusionMatrix(){
        StringBuilder result = new StringBuilder();
        // "TN = " takes 5 digits.
        int stringLength = (int) Math.max(
                Math.max(
                    Math.max(Math.log10(falseNegative), Math.log10(trueNegative)),
                    Math.max(Math.log10(falsePositive), Math.log10(truePositive))
                ), 1) + 5;
        String[] prefixes = {"#n = ", "TN = ", "FN = "};
        int[] suffixes = {total, trueNegative, falseNegative};
        result.append(String.format("#n = %d|label = %s%-" + stringLength + "s", total, positiveLabel,""));
        result.append("\n");
        result.append(String.format("TN = %d|FP = %d%-" + stringLength + "s", trueNegative, falsePositive,""));
        result.append("\n");
        result.append(String.format("FN = %d|TP = %d%-" + stringLength + "s", falseNegative, truePositive,""));
        result.append("\n");

        return result.toString();
    }


    @Override
    public String toString() {
        return super.toString();
    }

    public int getTotal() {
        return total;
    }

    public int getTruePositive() {
        return truePositive;
    }

    public int getTrueNegative() {
        return trueNegative;
    }

    public int getFalsePositive() {
        return falsePositive;
    }

    public int getFalseNegative() {
        return falseNegative;
    }
}

package edu.hendrix.huynhem.seniorthesis.Models;

public interface ModelInterface {

    void train(String imageLocation, String label);

    String classify(String imageLocation);
    ModelInterface fromString();
}

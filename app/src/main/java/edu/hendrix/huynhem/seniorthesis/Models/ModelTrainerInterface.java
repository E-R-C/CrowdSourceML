package edu.hendrix.huynhem.seniorthesis.Models;

public interface ModelTrainerInterface {

    void train(String imageLocation, String label);
    ModelTrainerInterface fromString();
}

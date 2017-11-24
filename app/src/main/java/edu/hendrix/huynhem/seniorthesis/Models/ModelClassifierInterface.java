package edu.hendrix.huynhem.seniorthesis.Models;

public interface ModelClassifierInterface {

    String classify(String imageLocation);
    ModelClassifierInterface fromString();
}

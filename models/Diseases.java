package org.tensorflow.demo.models;

public class Diseases {

    private String imageURL, diseaseName, causativeAgent, diseaseSymptoms;

    public Diseases(String diseaseName, String causativeAgent, String imageURL, String diseaseSymptoms) {
        this.diseaseName = diseaseName;
        this.causativeAgent = causativeAgent;
        this.imageURL = imageURL;
        this.diseaseSymptoms = diseaseSymptoms;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getCausativeAgent() {
        return causativeAgent;
    }

    public String getDiseaseSymptoms() {
        return diseaseSymptoms;
    }
}

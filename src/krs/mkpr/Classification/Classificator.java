package krs.mkpr.Classification;

import krs.mkpr.Extraction.ArticleProperties;

import java.util.List;


public class Classificator {
    private static double AVERAGE_WORD_LENGTH = 7;
    private static double N_GRAM_MEASURE = 3;

    private Metric metric;
    private int k = 5;
    private float proportion = 0.5f;

    public void classificate(List<ArticleProperties> articleProperites) {

    }

    private double wordDistance(String a, String b) { // n-gram.
        return 0;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}

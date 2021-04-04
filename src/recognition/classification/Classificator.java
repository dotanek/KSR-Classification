package recognition.classification;

import recognition.classification.metrics.Metric;
import recognition.extraction.ArticleProperties;
import recognition.utilities.Pair;

import java.util.ArrayList;
import java.util.List;

public class Classificator {
    private Metric metric;
    private int k = 5;
    private double proportion = 0.5f;

    private List<ArticleProperties> trainingData;
    private List<ArticleProperties> testData;
    List<Pair<ArticleProperties,String>> dataClassified;

    public Classificator(Metric metric, int k, double proportion) {
        this.metric = metric;
        this.k = k;
        this.proportion = proportion;
    }

    public void loadAndPrepareData(List<ArticleProperties> data) {
        int totalSize = data.size();
        int trainingSize = (int)(totalSize * proportion);

        trainingData = data.subList(0,trainingSize);
        testData = data.subList(trainingSize,totalSize);
        dataClassified = new ArrayList<>();

        for (ArticleProperties datum : trainingData) {
            dataClassified.add(new Pair<>(datum,datum.getArticle().getCountry()));
        }
    }

    public List<Pair<ArticleProperties,String>> classificate() throws Exception {
        System.out.print("Classifying articles... ");

        for (ArticleProperties testDatum : testData) {
            List<Pair<String, Double>> closestNeighbours = new ArrayList<>();

            for (Pair<ArticleProperties,String> classifiedDatum : dataClassified) { // Loop for finding k closest neighbours.
                List<Object> v1 = testDatum.getProperties();
                List<Object> v2 = classifiedDatum.getFirst().getProperties();
                double distance = metric.vectorDistance(v1,v2);
                String className = classifiedDatum.getSecond();

                if (closestNeighbours.size() < k) {
                    closestNeighbours.add(new Pair<>(className,distance));
                } else {
                    for (Pair<String,Double> neighbour : closestNeighbours) {
                        if (neighbour.getSecond() > distance) {
                            neighbour.set(className,distance);
                            break;
                        }
                    }
                }
            }

            List<Pair<String, Integer>> foundClasses = new ArrayList<>();

            for (Pair<String, Double> neighbour : closestNeighbours) {
                boolean classFound = false;
                for (Pair<String, Integer> foundClass : foundClasses) {
                    if (foundClass.getFirst().equals(neighbour.getFirst())) {
                        foundClass.setSecond(foundClass.getSecond()+1);
                        classFound = true;
                        break;
                    }
                }
                if (!classFound) {
                    foundClasses.add(new Pair<>(neighbour.getFirst(),1));
                }
            }

            Pair<String,Integer> topClass = foundClasses.get(0);

            for (Pair<String,Integer> foundClass : foundClasses) {
                if (topClass.getSecond() < foundClass.getSecond()) {
                    topClass = foundClass;
                }
            }
            int i = 0;
            dataClassified.add(new Pair<>(testDatum,topClass.getFirst()));
        }

        System.out.println("Done.");
        return dataClassified;
    }

    public List<List<Integer>> generateConfusionMatrix() {

        return null;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}

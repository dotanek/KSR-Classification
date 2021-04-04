package view;

import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import recognition.Keywords;
import recognition.classification.Classificator;
import recognition.classification.ConfusionMatrix;
import recognition.classification.metrics.ChebyshevMetric;
import recognition.classification.metrics.EuclideanMetric;
import recognition.classification.metrics.ManhattanMetric;
import recognition.classification.metrics.Metric;
import recognition.extraction.ArticleProperties;
import recognition.extraction.Extractor;
import recognition.preparation.Article;
import recognition.preparation.ArticleLoader;
import recognition.preparation.Preparator;
import recognition.utilities.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Button buton_datapath;
    public Button buton_prepare;
    public Button buton_classify;

    public Label label_datapath;
    public Label label_k;
    public Label label_proportion;
    public Label label_prepare;
    public Label label_classify;
    public Label label_accuracy;

    public RadioButton label_property1;
    public RadioButton label_property2;
    public RadioButton label_property3;
    public RadioButton label_property4;
    public RadioButton label_property5;
    public RadioButton label_property6;
    public RadioButton label_property7;
    public RadioButton label_property8;
    public RadioButton label_property9;
    public RadioButton label_property10;

    public ComboBox combobox_metric;

    public GridPane grid_quality;
    public GridPane grid_confusion;

    DirectoryChooser chooser = new DirectoryChooser();

    // Classification

    private List<Article> articles;
    private List<ArticleProperties> articlesProperties;
    private List<Pair<ArticleProperties,String>> classifiedArticles;

    public void buttonDatapathClick() {
       label_datapath.setText(chooser.showDialog(null).getAbsolutePath());
    }

    public void buttonPrepareClick() {
        String dataPath = label_datapath.getText();

        label_prepare.setText("Przygotowywanie danych...");

        boolean[] propertyChoice = new boolean[10];
        propertyChoice[0] = label_property1.isSelected();
        propertyChoice[1] = label_property2.isSelected();
        propertyChoice[2] = label_property3.isSelected();
        propertyChoice[3] = label_property4.isSelected();
        propertyChoice[4] = label_property5.isSelected();
        propertyChoice[5] = label_property6.isSelected();
        propertyChoice[6] = label_property7.isSelected();
        propertyChoice[7] = label_property8.isSelected();
        propertyChoice[8] = label_property9.isSelected();
        propertyChoice[9] = label_property10.isSelected();

        Task<Boolean> task = new Task<Boolean>() {
            @Override public Boolean call() throws InterruptedException {

                Keywords keywords = new Keywords();
                Preparator preparator = new Preparator();
                Extractor extractor = new Extractor(keywords);

                try {
                    //articles = ArticleLoader.loadFromFile(dataPath + "\\documents\\reut2-000.sgm");
                    articles = ArticleLoader.loadFromFiles(dataPath + "\\documents");
                    keywords.loadFromFiles(dataPath+"\\keywords");
                    preparator.loadFromFile(dataPath+"\\stopwords\\default_english.txt");

                } catch(Exception e) {
                    showError("Ścieżka z danymi niepoprawna.");
                    return false;
                }

                preparator.prepare(articles);

                articlesProperties = extractor.extract(articles,propertyChoice);

                return true;
            }
        };

        task.setOnSucceeded(e -> {

            if(task.getValue()) {
                label_prepare.setText("Gotowe.");
            } else {
                label_prepare.setText("Wystąpił błąd.");
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void buttonClassifyClick() {
        label_classify.setText("Klasyfikowanie danych...");

        Task<Boolean> task = new Task<Boolean>() {
            @Override public Boolean call() throws InterruptedException {

                String kString = label_k.getText();
                int k = Integer.parseInt(kString.substring(0,kString.indexOf(".")));
                double proportion = Double.parseDouble(label_proportion.getText());

                Metric metric;

                switch (combobox_metric.getValue().toString()) {
                    case "Euklidesowa":
                        metric = new EuclideanMetric();
                        break;
                    case "Czebyszewa":
                        metric = new ChebyshevMetric();
                        break;
                    case "Uliczna":
                        metric = new ManhattanMetric();
                        break;
                    default:
                        return false;
                }

                Classificator classificator = new Classificator(metric,k,proportion);
                classificator.loadAndPrepareData(articlesProperties);

                try {
                    classifiedArticles =  classificator.classificate();
                } catch(Exception e) {
                    showError("nwm co jest lol");
                    return false;
                }

                return true;
            }
        };

        task.setOnSucceeded(e -> {

            if(task.getValue()) {
                ConfusionMatrix confusionMatrix = new ConfusionMatrix(classifiedArticles);

                List<String> classNames = confusionMatrix.getClassNames();

                double accuracy = confusionMatrix.getAccuracy();
                accuracy = Math.round(accuracy * 1000) / 1000.0;
                label_accuracy.setText(Double.toString(accuracy));

                grid_quality.getChildren().clear();
                grid_confusion.getChildren().clear();

                for (int i = 1; i < 7; i++) {
                    Label temp1 = new Label();
                    Label temp2 = new Label();
                    Label temp3 = new Label();
                    String shortenedName = shortName(classNames.get(i-1));
                    String expandedName = fullName(classNames.get(i-1));
                    temp1.setText(shortenedName);
                    temp2.setText(shortenedName);
                    temp3.setText(expandedName);
                    temp1.setPrefWidth(100);
                    temp2.setPrefWidth(100);
                    temp3.setPrefWidth(100);
                    temp1.setPrefHeight(100);
                    temp2.setPrefHeight(100);
                    temp3.setPrefHeight(100);
                    temp1.setAlignment(Pos.CENTER);
                    temp2.setAlignment(Pos.CENTER);
                    temp3.setAlignment(Pos.CENTER);
                    grid_confusion.add(temp1,i,0);
                    grid_confusion.add(temp2,0,i);
                    grid_quality.add(temp3,i,0);
                }

                Label precisionLabel = new Label();
                Label recallLabel = new Label();
                Label f1Label = new Label();
                precisionLabel.setText("Precyzja");
                recallLabel.setText("Czułość");
                f1Label.setText("F1");
                precisionLabel.setPrefWidth(100);
                recallLabel.setPrefWidth(100);
                f1Label.setPrefWidth(100);
                precisionLabel.setPrefHeight(100);
                recallLabel.setPrefHeight(100);
                f1Label.setPrefHeight(100);
                precisionLabel.setAlignment(Pos.CENTER);
                recallLabel.setAlignment(Pos.CENTER);
                f1Label.setAlignment(Pos.CENTER);
                grid_quality.add(precisionLabel,0,1);
                grid_quality.add(recallLabel,0,2);
                grid_quality.add(f1Label,0,3);

                for (int i = 1; i < 7; i++) {
                    String className = classNames.get(i-1);
                    double precision = confusionMatrix.getPrecision(className);
                    precision = Math.round(precision * 1000) / 1000.0;
                    double recall = confusionMatrix.getRecall(className);
                    recall = Math.round(recall * 1000) / 1000.0;
                    double f1 = confusionMatrix.getF1(className);
                    f1 = Math.round(f1 * 1000) / 1000.0;

                    Label temp1 = new Label();
                    Label temp2 = new Label();
                    Label temp3 = new Label();
                    temp1.setText(Double.toString(precision));
                    temp2.setText(Double.toString(recall));
                    temp3.setText(Double.toString(f1));
                    temp1.setPrefWidth(100);
                    temp2.setPrefWidth(100);
                    temp3.setPrefWidth(100);
                    temp1.setPrefHeight(100);
                    temp2.setPrefHeight(100);
                    temp3.setPrefHeight(100);
                    temp1.setAlignment(Pos.CENTER);
                    temp2.setAlignment(Pos.CENTER);
                    temp3.setAlignment(Pos.CENTER);
                    grid_quality.add(temp1,i,1);
                    grid_quality.add(temp2,i,2);
                    grid_quality.add(temp3,i,3);
                }


                for (int i = 1; i < 7; i++) {
                    for (int j = 1; j < 7; j++) {
                        Label temp = new Label();
                        int value = confusionMatrix.get(j-1,i-1);
                        temp.setText(Integer.toString(value));
                        temp.setPrefWidth(100);
                        temp.setPrefHeight(100);
                        temp.setAlignment(Pos.CENTER);
                        grid_confusion.add(temp,i,j);
                    }
                }

                label_classify.setText("Gotowe.");
            } else {
                label_classify.setText("Wystąpił błąd.");
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String shortName(String name) {
        switch (name) {
            case "usa": return "USA";
            case "canada": return  "KAN";
            case "france": return  "FRA";
            case "japan": return  "JAP";
            case "uk": return  "UK";
            case "west-germany": return  "NIE";
            default: return "WHAT";
        }
    }

    private String fullName(String name) {
        switch (name) {
            case "usa": return "USA";
            case "canada": return  "Kanada";
            case "france": return  "Francja";
            case "japan": return  "Japonia";
            case "uk": return  "UK";
            case "west-germany": return  "Z.Niemcy";
            default: return "WHAT";
        }
    }

    private void showError(String message) {
        System.out.println(message);
        /*Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();*/
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}

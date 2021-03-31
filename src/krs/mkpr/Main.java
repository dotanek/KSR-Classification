package krs.mkpr;

import krs.mkpr.Classification.*;
import krs.mkpr.Classification.Metrics.EuclideanMetric;
import krs.mkpr.Classification.Metrics.Metric;
import krs.mkpr.Extraction.ArticleProperties;
import krs.mkpr.Extraction.Extractor;
import krs.mkpr.Preparation.Article;
import krs.mkpr.Preparation.ArticleLoader;
import krs.mkpr.Preparation.Preparator;
import krs.mkpr.Utilities.Pair;

// import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        /*JOptionPane.showInputDialog("Wprowadź wartość k:","10");
        JOptionPane.showInputDialog("Wprowadź oddzielone przecinkami numery cech, które mają być wzięte pod uwagę w procesie klasyfikacji. \n" +
                "1. Liczba wszystkich słów pozostałych w tekście,\n" +
                "2. Słowo kluczowe, które wystąpiło w tekście najwięcej razy,\n" +
                "3. Słowo kluczowe, które wystąpiło w tytule jako pierwsze,\n" +
                "4. Częstotliwość występowania słowa opisanego w poprzednim punkcie, w pierwszych 20 procentach całego tekstu,\n" +
                "5. Nazwa kontynentu, która wystąpiła w tekście najwięcej razy,\n" +
                "6. Nazwa miejsca, która wystąpiła w tekście najwięcej razy,\n" +
                "7. Słowna nazwa waluty, która wystąpiła w tekście najwięcej razy,\n" +
                "8. Liczba unikatowych słów w tekście,\n" +
                "9. Nazwa miejscowości, w której artykuł został napisany,\n" +
                "10. Średnia liczba słów w zdaniu zawartym w tekście \n", "1,2,3,4,5,6,7,8,9,10");
        JOptionPane.showInputDialog("Wprowadź procent proporcji podziału zbioru na zbiór uczący i testowy:", "50");

        System.out.println("|" + "    P\\T    " + "|" + " usa       " + "|" + " france    " + "|" + " we-ger    " + "|");
        System.out.println("|" + " usa       " + "|" + " X         " + "|" + " X         " + "|" + " X         " + "|");
        System.out.println("|" + " france    " + "|" + " X         " + "|" + " X         " + "|" + " X         " + "|");
        System.out.println("|" + " we-ger    " + "|" + " X         " + "|" + " X         " + "|" + " X         " + "|");
        System.out.println();
        System.out.println("Accuracy: X.XXX");
        System.out.println();
        System.out.println("-- usa --");
        System.out.println("Precision: X.XXX");
        System.out.println("Recall: X.XXX");
        System.out.println("F1: X.XXX");
        System.out.println();
        System.out.println("-- france --");
        System.out.println("Precision: X.XXX");
        System.out.println("Recall: X.XXX");
        System.out.println("F1: X.XXX");
        System.out.println();
        System.out.println("-- we-ger --");
        System.out.println("Precision: X.XXX");
        System.out.println("Recall: X.XXX");
        System.out.println("F1: X.XXX");*/

        try {
            long startTime;
            long endTime;

            String slash = File.separator;
            String currentDir = System.getProperty("user.dir");
            String mainDirName = "KSR-Classification";
            String dataPath = currentDir;

            if (currentDir.contains(mainDirName + slash)) {
                dataPath += slash + ".." + slash + ".." + slash + "..";
            }
            dataPath += slash + "data";

            System.out.println(dataPath);

            startTime = System.nanoTime();
            System.out.print("Loading articles... ");
            List<Article> articles = ArticleLoader.loadFromFiles(dataPath+"\\documents");
            //List<Article> articles = ArticleLoader.loadFromFile(dataPath+"\\documents\\reut2-000.sgm"); //Uncomment for single file.
            //articles.addAll(ArticleLoader.loadFromFile(dataPath+"\\documents\\reut2-001.sgm")); //Uncomment for single file.
            endTime = System.nanoTime();
            System.out.println("execution time: "+((endTime - startTime) * 10e-10));

            startTime = System.nanoTime();
            System.out.print("Loading keywords... ");
            Keywords keywords = new Keywords();
            keywords.loadFromFiles(dataPath+"\\keywords");
            endTime = System.nanoTime();
            System.out.println("execution time: "+((endTime - startTime) * 10e-10));

            startTime = System.nanoTime();
            System.out.print("Loading stopwords... ");
            Preparator preparator = new Preparator();
            preparator.loadFromFile(dataPath+"\\stopwords\\default_english.txt");
            endTime = System.nanoTime();
            System.out.println("execution time: "+((endTime - startTime) * 10e-10));

            startTime = System.nanoTime();
            System.out.print("Preparing articles... ");
            preparator.prepare(articles);
            endTime = System.nanoTime();
            System.out.println("execution time: "+((endTime - startTime) * 10e-10));

            startTime = System.nanoTime();
            System.out.print("Extracting properties... ");
            Extractor extractor = new Extractor(keywords);
            List<ArticleProperties> articlesProperties = extractor.extract(articles);
            endTime = System.nanoTime();
            System.out.println("execution time: "+((endTime - startTime) * 10e-10));

            startTime = System.nanoTime();
            System.out.println("Performing classification... \n");
            Metric metric = new EuclideanMetric();
            Classificator classificator = new Classificator(metric,5,0.3);
            classificator.loadAndPrepareData(articlesProperties);
            List<Pair<ArticleProperties,String>> classifiedArticles = classificator.classificate();
            endTime = System.nanoTime();
            System.out.println("\nClassification execution time: "+((endTime - startTime) * 10e-10));

            ConfusionMatrix confusionMatrix = new ConfusionMatrix(classifiedArticles);
            System.out.println(confusionMatrix.toString());

            System.out.println("Accuracy: " + confusionMatrix.getAccuracy() + "\n");

            for (String name : confusionMatrix.getClassNames()) {
                System.out.println("Country: " + name);
                double precision = Math.round(confusionMatrix.getPrecision(name) * 1000.0) / 1000.0;
                double recall = Math.round(confusionMatrix.getRecall(name) * 1000.0) / 1000.0;
                double f1 = Math.round(confusionMatrix.getF1(name) * 1000.0) / 1000.0;
                System.out.println(" Precision: " + precision);
                System.out.println(" Recall: " + recall);
                System.out.println(" F1: " + f1);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

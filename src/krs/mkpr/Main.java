package krs.mkpr;

import krs.mkpr.Extraction.ArticleProperties;
import krs.mkpr.Extraction.Extractor;
import krs.mkpr.Preparation.Article;
import krs.mkpr.Preparation.ArticleLoader;
import krs.mkpr.Preparation.Preparator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {

            long startTime;
            long endTime;

            String dataPath = System.getProperty("user.dir") + "\\..\\..\\..\\data"; // Modify the string so that it points to the correct directory.

            startTime = System.nanoTime();
            System.out.print("Loading articles... ");
            List<Article> articles = ArticleLoader.loadFromFile(dataPath+"\\documents");
            //List<Article> articles = ArticleLoader.loadFromFile(dataPath+"\\documents\\reut2-000.sgm"); Uncomment for single file.
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
            System.out.println("execution time: "+((endTime - startTime) * 10e-10) + "\n");

            for (ArticleProperties ap : articlesProperties) {
                System.out.println(ap.getProperties());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

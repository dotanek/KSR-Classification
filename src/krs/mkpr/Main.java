package krs.mkpr;

import krs.mkpr.Extraction.Article;
import krs.mkpr.Extraction.ArticleLoader;
import krs.mkpr.Preparation.Preparator;

import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        try {
            String dataPath = System.getProperty("user.dir") + "\\..\\..\\..\\data"; // Modify the string so that it points to the correct directory.
            List<Article> articles = ArticleLoader.loadFromFiles(dataPath+"\\documents");

            for(Article article : articles) {
                article.text = article.text.toLowerCase(Locale.ROOT);
            }

            Keywords keywords = new Keywords();
            keywords.loadFromFiles(dataPath+"\\keywords");

            Preparator preparator = new Preparator();
            preparator.loadFromFile(dataPath+"\\stopwords\\default_english.txt");

            Article test = articles.get(0);

            preparator.prepare(test);

            /*long startTime = System.nanoTime(); // Execution time
            long endTime = System.nanoTime();
            System.out.println("Execution time: "+((endTime - startTime) * 10e-9));*/

            //System.out.print(StringUtils.countMatches(test.text,test_wrd)); // Counting

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

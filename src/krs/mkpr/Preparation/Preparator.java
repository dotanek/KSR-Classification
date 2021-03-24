package krs.mkpr.Preparation;

import krs.mkpr.Extraction.Article;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Preparator {
    public List<String> stopList;

    public Preparator() {}

    public void loadFromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        stopList = new ArrayList<String>();

        while (scanner.hasNextLine()) {
            stopList.add(scanner.nextLine());
        }

        stopList.add("reuter3"); // Weird word at the end of each text.
    }

    public void prepare(Article article) {
        clean(article);
        filter(article);
        stem(article);
    }

    private void filter(Article article) {
        String[] splitText = article.text.split(" ");
        article.text = "";
        for (String word : splitText) {
            if (!stopList.contains(word)) {
                article.text += word + " ";
            }
        }
        article.text.trim();
    }

    private void stem(Article article) {
        String[] splitText = article.text.split(" ");
        article.text = "";

        PorterStemmer stemmer = new PorterStemmer();

        for (String word : splitText) {
            stemmer.setCurrent(word);
            stemmer.stem();
            article.text += stemmer.getCurrent() + " ";
        }
        article.text.trim();
    }

    private void clean(Article article) {
        article.text = article.text.replaceAll("[^A-Za-z ]", ""); // Removing all special characters.
        article.text = article.text.replaceAll("( )+", " "); // Replacing triple spaces with single spaces.
    }
}


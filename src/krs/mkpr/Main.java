package krs.mkpr;

import krs.mkpr.Extraction.ArticleLoader;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.print(ArticleLoader.loadFromFile(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

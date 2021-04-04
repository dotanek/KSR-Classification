package recognition.extraction;

import recognition.Keywords;
import recognition.preparation.Article;
import recognition.utilities.Pair;

import java.util.*;

public class Extractor {
    private Keywords keywords;

    public Extractor(Keywords keywords) {
        this.keywords = keywords;
    }

    public List<ArticleProperties> extract(List<Article> articles) {
        List<ArticleProperties> articleProperties = new ArrayList<>();
        for (Article article : articles) {
            articleProperties.add(extract(article));
        }
        return articleProperties;
    }

    public List<ArticleProperties> extract(List<Article> articles, boolean[] propertyChoice) {
        System.out.print("Extracting articles... ");

        List<ArticleProperties> articleProperties = new ArrayList<>();
        for (Article article : articles) {
            articleProperties.add(extract(article,propertyChoice));
        }

        System.out.println("Done.");
        return articleProperties;
    }

    public ArticleProperties extract(Article article) {
        List<Object> properties = new ArrayList<Object>();

        addKeywordOccurrenceProperties(article, properties);
        addTextLengthProperties(article, properties);
        addDatelineProperties(article, properties);

        return new ArticleProperties(article, properties);
    }

    public ArticleProperties extract(Article article, boolean[] propertyChoice) {
        List<Object> properties = new ArrayList<Object>();

        addKeywordOccurrenceProperties(article, properties);
        addTextLengthProperties(article, properties);
        addDatelineProperties(article, properties);

        List<Object> filteredProperties = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if (propertyChoice[i]) {
                filteredProperties.add(properties.get(i));
            }
        }

        return new ArticleProperties(article, filteredProperties);
    }

    private void addDatelineProperties(Article article, List<Object> properties) {
        properties.add(article.getDateline().replace(" ","-")); // 9. Name of place the article was written in.
    }

    private void addTextLengthProperties(Article article, List<Object> properties) {
        String text = article.getText();

        int wordsAmount = text.split(" ").length;
        int sentencesAmount = text.split("\\.").length;
        int uniqieWordsAmount = new HashSet<>(Arrays.asList(text.split(" "))).size();

        properties.add(wordsAmount); // 1. Amount of words in the text.
        properties.add(uniqieWordsAmount); // 8. Amount of unique words in text.
        properties.add(wordsAmount/sentencesAmount); // 10. Average amount of words in the text sentence.
    }

    private void addKeywordOccurrenceProperties(Article article, List<Object> properties) {

        // Finding the first keyword in the title.
        String firstKeyword = firstKeyword(article.getTitle(),keywords.getAll());
        String[] splitText = article.getText().split("\\s");

        double firstFrequency = 0d;
        if (firstKeyword != "none"){
            int wordsAmount = splitText.length;
            int chunkWordsAmount = splitText.length / 5; // 20% rounded down.
            int firstOccurrences = countOccurrences(Arrays.copyOfRange(splitText,0,chunkWordsAmount), firstKeyword); // Within first 20% of text.
            firstFrequency = firstOccurrences / chunkWordsAmount;
        }

        properties.add(firstKeyword); // 3. First keyword that occurred in the title.
        properties.add(firstFrequency); // 4. Frequency of the first keyword in the first 20% of the text.

        // Most common keywords per category.
        Pair<String,Integer> tGeneral = topKeyword(article.getText(), keywords.general);
        Pair<String,Integer> tCountry = topKeyword(article.getText(), keywords.countries);
        Pair<String,Integer> tContinent = topKeyword(article.getText(), keywords.continents);
        Pair<String,Integer> tCurrency =  topKeyword(article.getText(), keywords.currencies);

        properties.add(tContinent.getFirst()); // 5. Most common continent keyword.
        properties.add(tCountry.getFirst()); // 6. Most common country keyword.
        properties.add(tCurrency.getFirst()); // 7. Most common currency keyword.

        // Merging into list to find the most common keyword overall.
        List<Pair<String,Integer>> allTops = new ArrayList<>();
        allTops.add(tGeneral);
        allTops.add(tCountry);
        allTops.add(tContinent);
        allTops.add(tCurrency);

        Pair<String,Integer> tOverall = new Pair<>("none",0);

        for (Pair<String,Integer> top : allTops) {
            if (tOverall.getSecond() < top.getSecond()) {
                tOverall = top;
            }
        }

        properties.add(tOverall.getFirst()); // 2. Most common overall keyword.
    }

    private String firstKeyword(String text, List<String> keywords) {
        String[] splitText = text.split("\\s");
        Pair<String,Integer> first = new Pair<>("none",Integer.MAX_VALUE);
        for (String keyword : keywords) {
            String[] keyparts = keyword.split("-");
            for (int i = 0; i < splitText.length; i++) {
                if (i + keyparts.length < splitText.length) {
                    String[] textparts = Arrays.copyOfRange(splitText, i, i+keyparts.length);
                    if(checkMatch(textparts, keyparts)) {
                        if (i < first.getSecond()) {
                            first.set(keyword,i);
                        }
                        break;
                    }
                }
            }
        }
        return first.getFirst();
    }

    private int countOccurrences(String[] splitText, String keyword) {
        int occurrences = 0;
        String[] keyparts = keyword.split("-");
        for (int i = 0; i < splitText.length; i++) {
            if (i + keyparts.length < splitText.length) {
                String[] textparts = Arrays.copyOfRange(splitText, i, i+keyparts.length);
                if(checkMatch(textparts, keyparts)) {
                    occurrences++;
                }
            }
        }
        return occurrences;
    }

    private Pair<String,Integer> topKeyword(String text, List<String> keywords) { // Searching for keywords by comparing them to individual words (won't work for 2 word keywords)
        String[] splitText = text.split("\\s");
        Pair<String,Integer> top = new Pair<>("none",0);
        for (String keyword : keywords) {
            int current = countOccurrences(splitText, keyword);
            if (current > top.getSecond()) {
                top.set(keyword,current);
            }
        }
        return top;
    }

    private boolean checkMatch(String[] textparts, String[] keyparts) {
        for (int i = 0; i < textparts.length; i++) {
            if (!textparts[i].equals(keyparts[i])) {
                return false;
            }
        }
        return  true;
    }
}

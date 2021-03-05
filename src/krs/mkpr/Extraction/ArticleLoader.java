package krs.mkpr.Extraction;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleLoader {

    private static final Pattern TITLE_REGEX = Pattern.compile("<TITLE>(.+?)</TITLE>", Pattern.DOTALL);
    private static final Pattern TEXT_REGEX = Pattern.compile("<BODY>(.+?)</BODY>", Pattern.DOTALL);

    private static List<String> getTagValues(final String str, Pattern REGEX) { // Finds strings between regex expressions.
        final List<String> tagValues = new ArrayList<String>();
        final Matcher matcher = REGEX.matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group(1)); // group(1) gets rid of the tag name.
        }
        return tagValues;
    }

    public static List<Article> loadFromFile(String fileName) throws FileNotFoundException {
        List<Article> articles = new ArrayList<Article>();

        Scanner scanner = new Scanner(new File("../../../data/reut2-000.sgm")); // Test filename.

        String articleString;
        String line;

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains("<REUTERS")) { // Putting every line containing tags in single string. (Assumes the newlines are properly added in the file)
                articleString = line;
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    articleString += line;
                    if (line.contains("</REUTERS>")) {
                        try {
                            articles.add(parseArticleString(articleString));
                        } catch (Exception e) {
                            continue; // Means that article was invalid, we simply do not add it.
                        }
                        break;
                    }
                }
            }
        }
        scanner.close();

        return articles;
    }

    private static Article parseArticleString(String articleString) throws Exception {
        String title;
        String text;

        List<String> titleLookup = getTagValues(articleString, TITLE_REGEX);
        List<String> textLookup = getTagValues(articleString, TEXT_REGEX);

        if (titleLookup.isEmpty() || textLookup.isEmpty()) {
            throw new Exception("Invalid article.");
        } else {
            title = titleLookup.get(0);
            text = textLookup.get(0);
        }
        return new Article(title,text);
    }
}

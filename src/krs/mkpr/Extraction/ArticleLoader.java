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
    private static final Pattern DATELINE_REGEX = Pattern.compile("<DATELINE>(.+?)</DATELINE>", Pattern.DOTALL);
    private static final Pattern PLACES_REGEX = Pattern.compile("<PLACES>(.+?)</PLACES>", Pattern.DOTALL);
    private static final Pattern D_REGEX = Pattern.compile("<D>(.+?)</D>", Pattern.DOTALL);

    private static final List<String> TARGET_PLACES = Arrays.asList(
            new String[]{"west-germany", "usa", "france", "uk", "canada", "japan"}
    );

    private static List<String> getTagValues(final String str, Pattern REGEX) { // Finds strings between regex expressions.
        final List<String> tagValues = new ArrayList<String>();
        final Matcher matcher = REGEX.matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group(1)); // group(1) gets rid of the tag name.
        }
        return tagValues;
    }

    public static List<Article> loadFromFiles(String dirPath) throws FileNotFoundException {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null) {
            List<Article> articles = new ArrayList<Article>();

            for (File file : files) {
                articles.addAll(loadFromFile(file.getAbsolutePath()));
            }

            return articles;
        } else {
          throw new FileNotFoundException("Could not find the directory with given path.");
        }
    }

    public static List<Article> loadFromFile(String filePath) throws FileNotFoundException {
        List<Article> articles = new ArrayList<Article>();

        Scanner scanner = new Scanner(new File(filePath));

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
                        } catch (Exception e) {} // Means that article was invalid, we simply do not add it.
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
        String dateline;
        String place;

        List<String> titleLookup = getTagValues(articleString, TITLE_REGEX);
        List<String> textLookup = getTagValues(articleString, TEXT_REGEX);
        List<String> datelineLookup = getTagValues(articleString, DATELINE_REGEX);
        List<String> placesLookup = getTagValues(articleString, PLACES_REGEX);
        List<String> placeLookup = getTagValues(placesLookup.get(0), D_REGEX); // Countries in places tag are surrounded by <D> tag which we need to strip.

        if (!checkLookups(titleLookup,textLookup,datelineLookup,placeLookup)) {
            throw new Exception("Invalid article.");
        } else {
            title = titleLookup.get(0);
            text = textLookup.get(0);
            dateline = datelineLookup.get(0);
            place = placeLookup.toString();
        }
        return new Article(title,text,dateline,place);
    }

    private static boolean checkLookups(List<String> title, List<String> text, List<String> dateline, List<String> place) {
        if (title.isEmpty()) return false;
        if (text.isEmpty()) return false;
        if (dateline.isEmpty()) return false;
        if (place.size() != 1) return false; // We only consider articles with just one place.
        if (!TARGET_PLACES.contains(place.get(0))) return false;
        return true;
    }
}

package recognition.preparation;

import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Preparator {
    private List<String> stopList;
    private final PorterStemmer stemmer = new PorterStemmer();

    public Preparator() {}

    public void loadFromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        stopList = new ArrayList<>();

        while (scanner.hasNextLine()) {
            stopList.add(scanner.nextLine());
        }

        stopList.add("reuter"); // Weird word at the end of each text.
    }

    public void prepare(Article article) {
        List<String> title = Arrays.asList(clean(article.getTitle()).split("\\s"));
        title = filter(title);
        title = stem(title);

        List<String> text = Arrays.asList(clean(article.getText()).split("\\s"));
        text = filter(text);
        text = stem(text);

        String datelineString = article.getDateline().split("\\,")[0]; // Getting part of string before comma.
        List<String> dateline = Arrays.asList(clean(datelineString).split("\\s"));
        dateline = filter(dateline);
        dateline = stem(dateline);

        article.setTitle(String.join(" ", title));
        article.setText(String.join(" ", text));
        article.setDateline(String.join(" ", dateline));
    }

    public void prepare(List<Article> articles) {
        System.out.print("Preparing articles... ");

        for (Article article : articles) {
            prepare(article);
        }

        System.out.println("Done.");
    }

    private List<String> filter(List<String> text) {
        List<String> filteredText = new ArrayList<>();
        for (String word : text) {
            if (!stopList.contains(word)) {
                filteredText.add(word);
            }
        }
        return filteredText;
    }

    private List<String> stem(List<String> text) {
        List<String> stemmedText = new ArrayList<>();
        for (String word : text) {
            stemmer.setCurrent(word);
            stemmer.stem();
            stemmedText.add(stemmer.getCurrent());
        }
        return stemmedText;
    }

    private String clean(String text) {
        return text
            .toLowerCase(Locale.ROOT) // Converting to lower case.
            .replaceAll("\\d+[,.]\\d+", " ") // Removing all numbers with dots or commas.
            .replaceAll("u\\.s\\.", "us") // Special case - removing dots from U.S.
            .replaceAll("u\\.k\\.", "uk") // Special case - removing dots from U.K.
            .replaceAll("[.]", " .") // Separating dots from the words.
            .replaceAll("[^A-Za-z .]", " ") // Removing all special characters and numbers.
            .replaceAll("( )+", " ") // Replacing triple spaces with single spaces.
            .trim(); // Removing extra whitespaces at the ends.
    }
}


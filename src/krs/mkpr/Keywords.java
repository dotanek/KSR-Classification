package krs.mkpr;

import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Keywords {
    public List<String> general;
    public List<String> countries;
    public List<String> currencies;
    public List<String> continents;
    private final PorterStemmer stemmer = new PorterStemmer();

    public List<String> getAll() {
        List<String> all = new ArrayList<>();
        all.addAll(general);
        all.addAll(countries);
        all.addAll(continents);
        all.addAll(currencies);
        return all;
    }

    public void loadFromFiles(String dirPath) throws FileNotFoundException {
        this.general = loadFromFile(dirPath+"\\general.txt");
        this.countries = loadFromFile(dirPath+"\\countries.txt");
        this.continents = loadFromFile(dirPath+"\\continents.txt");
        this.currencies = loadFromFile(dirPath+"\\currencies.txt");
    }

    private List<String> loadFromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        List<String> keywords = new ArrayList<>();

        while (scanner.hasNextLine()) {
            keywords.add(prepare(scanner.nextLine()));
        }
        return keywords;
    }

    private String prepare(String keyword) {
        List<String> stemmedKeyparts =  new ArrayList<>();
        for (String part : keyword.toLowerCase(Locale.ROOT).split("-")) {
            stemmer.setCurrent(part); // Stemming keywords.
            stemmer.stem();
            stemmedKeyparts.add(stemmer.getCurrent());
        }
        return String.join("-",stemmedKeyparts);
    }
}

package krs.mkpr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Keywords {
    public List<String> general;
    public List<String> countries;
    public List<String> currencies;
    public List<String> continents;

    public Keywords() {

    }

    public void loadFromFiles(String dirPath) throws FileNotFoundException {
        this.general = loadFromFile(dirPath+"\\general.txt");
        this.countries = loadFromFile(dirPath+"\\countries.txt");
        this.continents = loadFromFile(dirPath+"\\continents.txt");
    }

    private List<String> loadFromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        List<String> keywords = new ArrayList<String>();

        while (scanner.hasNextLine()) {
            keywords.add(scanner.nextLine());
        }

        return keywords;
    }
}

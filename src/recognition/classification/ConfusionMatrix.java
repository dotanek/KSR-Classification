package recognition.classification;

import recognition.extraction.ArticleProperties;
import recognition.utilities.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConfusionMatrix {
    private List<String> classNames;
    private int[][] matrix;

    public ConfusionMatrix(List<Pair<ArticleProperties,String>> dataClassified) {
        classNames = new ArrayList<>();
        for (Pair<ArticleProperties,String> datum : dataClassified) { // Getting all the class names.
            String className = datum.getFirst().getArticle().getCountry();
            if (!classNames.contains(className)) {
                classNames.add(className);
            }
        }

        matrix = new int[classNames.size()][classNames.size()];

        for (Pair<ArticleProperties,String> datum : dataClassified) { // Creating matrix.
            String predictedClass = datum.getSecond();
            String trueClass = datum.getFirst().getArticle().getCountry();
            int predictedIndex = classNames.indexOf(predictedClass);
            int trueIndex = classNames.indexOf(trueClass);

            matrix[trueIndex][predictedIndex]++;
        }
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public int get(int row, int col) {
        return matrix[row][col];
    }

    public int get(String trueClass, String predictedClass) {
        int row = classNames.indexOf(trueClass);
        int col = classNames.indexOf(predictedClass);

        if (row == -1 || col == -1) {
            return -1;
        }

        return matrix[row][col];
    }

    public double getAccuracy() {
        int correct = 0;
        int population = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                population += matrix[i][j];
                if (i == j) {
                    correct += matrix[i][j];
                }
            }
        }

        return correct/(double)population;
    }

    public double getPrecision(String className) {
        int classIndex = classNames.indexOf(className);
        if (classIndex == -1) {
            return -1.0;
        }

        int correct = matrix[classIndex][classIndex];
        int population = 0;

        for (int i = 0; i < matrix.length; i++) {
            population += matrix[i][classIndex];
        }

        if (population == 0) {
            return 0.0;
        }

        return correct/(double)population;
    }

    public double getRecall(String className) {
        int classIndex = classNames.indexOf(className);
        if (classIndex == -1) {
            return -1.0;
        }

        int correct = matrix[classIndex][classIndex];
        int population = 0;

        for (int i = 0; i < matrix.length; i++) {
            population += matrix[classIndex][i];
        }

        if (population == 0) {
            return 0.0;
        }

        return correct/(double)population;
    }

    public double getF1(String className) {
        int classIndex = classNames.indexOf(className);
        if (classIndex == -1) {
            return -1.0;
        }

        double precision = getPrecision(className);
        double recall = getRecall(className);

        return 2 * precision * recall / (precision + recall);
    }

    @Override
    public String toString() {
        int cellLength = 15;
        String str = "";
        String cell = "";

        while (cell.length() < cellLength) {
            cell += " ";
        }
        str += cell;

        for (String name : classNames) {
            cell = name;
            while (cell.length() < cellLength) {
                cell += " ";
            }
            str += cell;
        }

        str += "\n";

        int i = 0;
        for (int[] row : matrix) {
            cell = classNames.get(i);
            while (cell.length() < cellLength) {
                cell += " ";
            }
            str += cell;

            for (int col : row) {
                cell = Integer.toString(col);
                while (cell.length() < cellLength) {
                    cell += " ";
                }
                str += cell;
            }
            str += "\n";
            i++;
        }

        return str;
    }
}

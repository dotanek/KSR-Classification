package recognition.classification.metrics;

import java.util.List;

public abstract class Metric {
    private static int N_GRAM_MEASURE = 2;

    public abstract double vectorDistance(List<Object> v1, List<Object> v2) throws Exception;

    protected boolean checkVectorCompatibility(List<Object> v1, List<Object> v2) {
        if (v1.size() != v2.size()) {
            return false;
        }
        for (int i = 0; i < v1.size(); i++) {
            if (v1.get(i).getClass() != v2.get(i).getClass()) {
                return false;
            }
        }
        return true;
    }

    protected double propertyDistance(Object o1, Object o2) throws Exception {
        Class c = o1.getClass();

        if (c == String.class) {
            return 10.0 * propertyDistance((String)o1,(String)o2);
        } else if (c == Integer.class) {
            return propertyDistance((int)o1,(int)o2);
        } else if (c == Double.class) {
            return propertyDistance((double)o1,(double)o2);
        } else {
            throw new Exception("Unknown property type exception.");
        }
    }

    protected double propertyDistance(double d1, double d2) {
        return d1 - d2;
    }

    protected double propertyDistance(String s1, String s2) { // n-gram. (Case sensitive.)
        if (s1.length() < N_GRAM_MEASURE || s2.length() < N_GRAM_MEASURE) {
            return 1.0;
        }

        if (s1.equals("none") || s2.equals("none")) {
            return 1.0;
        }

        if (s1.equals(s2)) {
            return 0.0;
        }

        if (s1.length() > s2.length()) { // Swapping so s2 is longer.
            String temp = s1;
            s1 = s2;
            s2 = temp;
        }

        int N = s1.length() - N_GRAM_MEASURE + 1;
        int maxN = s2.length() - N_GRAM_MEASURE + 1;
        int matches = 0;

        for (int i = 0; i < N; i++) {
            String sub = s1.substring(i,i + N_GRAM_MEASURE);
            if (s2.contains(sub)) {
                matches++;
            }
        }

        return 1.0 - (matches / (double)maxN); // Multiplying times 10 so that the range is 0 - 10.
    }
}

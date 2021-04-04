package recognition.classification.metrics;

import java.util.List;

public class ManhattanMetric extends Metric {

    @Override
    public double vectorDistance(List<Object> v1, List<Object> v2) throws Exception {
        if (!checkVectorCompatibility(v1,v2)) {
            throw new Exception("Incompatible vectors.");
        }

        double distance = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            double temp = propertyDistance(v1.get(i),v2.get(i));
            distance += Math.abs(temp);
        }

        return distance;
    }
}

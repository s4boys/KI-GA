package mlulsp.solvers.ga;

import java.util.Comparator;

public class IndividualComparator implements Comparator<Individual> {
    @Override
    public int compare(Individual a, Individual b) {
        return Double.compare(b.getFitness(),a.getFitness());
    }
}

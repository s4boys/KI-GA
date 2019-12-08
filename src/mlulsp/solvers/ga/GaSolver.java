package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;

import java.util.Arrays;


public class GaSolver implements Solver {
    private final int anzahlLoesungen;

    /*
     * hier k�nnen Parameter des GA angegeben werden z.B. PopulationsGroesse,
     * IterationenAnzahl
     */

    public GaSolver(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    // instance ist eine Instanz des Entscheidungsproblems
    public ProductionSchedule solve(Instance instance) {
        Individual.firstLastPeriodsBerechnen(instance); // muss uns glaube ich nicht interessieren
        Individual.mutationsWahrscheinlichkeit(); // wahrscheinlichkeit für mutationen global

        int anz = 10;
        Individual best;

        Population population = new Population(anz, instance);

//        Individual[] parents = new Individual[anz];


        // erzeugen der Ursprungspopulation
        population.initialize();
//        for (int i = 0; i < anz; i++) {
//            children[i] = new Individual(instance);
//            parents[i] = new Individual(instance);
//            children[i].initRandom();
//            parents[i].initRandom();
//            children[i].decoding(instance);
//            parents[i].decoding(instance);
//            children[i].evaluate();
//            parents[i].evaluate();
//        }

        // Standard Vergleichswert erschaffen
        best = new Individual(instance);
        best.initRandom();
        best.decoding(instance);
        best.evaluate();
        System.out.println("Aufgabe " + instance.getName());
        System.out.println("Startwert = " + best.getFitness());

        // fortpflanzen alle mit alle? Oder selektion der besten wahrscheinlicher oder ganz random

        // v1 one pop of 20 for 10 children + 10 parents, children replace parents
        // v2 one pop of 20 for 10 children + 10 parents, new 10 replace worst? Best? middle?

//        Individual[] population = new Individual[anz * 2];

        // for Schleife für alle Lösungen
        for (int i = 1; i < anzahlLoesungen / anz; i++) {
            Individual[] children = new Individual[anz];
//            System.arraycopy(parents, 0, population, 0, parents.length);
//            System.arraycopy(children, 0, population, parents.length, children.length);
            population.rankRouletteSelection();
//            parents = children.clone();

//            Individual[] roulette_pop = Individual.getRouletteDistribution(population);
//            Individual[] roulette_pop = Individual.getRouletteDistribution(parents);
            for (int j = 0; j < population.getSize(); j++) {
                Individual parent_a = population.getEntity();
                Individual parent_b = population.getEntity();
                children[j] = parent_a.mate(parent_b);
//				children[j] = new Individual(instance);
//				children[j].crossover(parent_a,parent_b);
                children[j].mutate();
                children[j].decoding(instance);
                children[j].evaluate();
            }

            population.setNewPopulation(children);

            Arrays.sort(children, new IndividualComparator());
            if (children[children.length - 1].getFitness() < best.getFitness()) {
                best = children[children.length - 1];
                System.out.println("Neuer bester Wert = " + best.getFitness());
            }
            System.out.println(i);
        }

        best.ausgabe(instance);
        return best.getPhaenotype();

    }
}

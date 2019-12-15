package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;

import java.util.Arrays;


public class GaSolver implements Solver {
    private final int anzahlLoesungen;

    /*
     * hier können Parameter des GA angegeben werden z.B. PopulationsGroesse,
     * IterationenAnzahl
     */

    public GaSolver(int anzahlLoesungen) {
        this.anzahlLoesungen = anzahlLoesungen;
    }

    // instance ist eine Instanz des Entscheidungsproblems
    public ProductionSchedule solve(Instance instance) {
        int anz = 10;
        Individual best;

        Population population = new Population(anz, instance);
        population.firstLastPeriodsBerechnen();
        population.mutationsWahrscheinlichkeit();

        // erzeugen der Ursprungspopulation
        population.initialize();

        // Standard Vergleichswert erschaffen
        best = new Individual(instance);
        best.initRandom();
        best.decoding(instance);
        best.evaluate();
        System.out.println("Aufgabe " + instance.getName());
        System.out.println("Startwert = " + (1/best.getFitness()));

        population.setBestIndividual(best);

        // fortpflanzen alle mit alle? Oder selektion der besten wahrscheinlicher oder ganz random

        // v1 one pop of 20 for 10 children + 10 parents, children replace parents
        // v2 one pop of 20 for 10 children + 10 parents, new 10 replace worst? Best? middle?

        // for Schleife für alle Lösungen

        int iterations = anzahlLoesungen / anz;
        int step_one = (int)(iterations * 0.25);
        int step_two = (int)(iterations * 0.5);
        int step_three = (int)(iterations * 0.9);

        double pMut = population.getMutationProbability();

        population.setMutationProbability(pMut);

        for (int i = 1; i < iterations; i++) {
            if(iterations == step_one){
                population.setMutationProbability(pMut);
            }
            if(iterations ==step_two){
                population.setMutationProbability(pMut);
            }
            if(iterations == step_three){
                population.setMutationProbability(10*pMut);
            }

            Individual[] children = new Individual[anz];
            population.rankRouletteSelection();

            for (int j = 0; j < population.getSize(); j++) {
                Individual parent_a = population.getEntity();
                Individual parent_b = population.getEntity();
                children[j] = parent_a.mate(parent_b);
                children[j].mutate(population.getMutationProbability());
                children[j].decoding(instance);
                children[j].evaluate();
            }

            population.setNewPopulation(children);
            for (Individual child : children){
                if (child.getFitness() > best.getFitness()) {
//                    System.out.println("Neuer bester Wert = " + (1/best.getFitness()));
                    best = child;
                }
            }
            population.setBestIndividual(best);
        }

        best.ausgabe(instance);
        return best.getPhaenotype();

    }
}

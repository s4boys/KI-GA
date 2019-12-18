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
        Individual finalBest,tempBest;

        Population population = new Population(anz, instance);
        population.firstLastPeriodsBerechnen();
        population.mutationsWahrscheinlichkeit();

        // erzeugen der Ursprungspopulation
        population.initialize();

        // Standard Vergleichswert erschaffen
        finalBest = new Individual(instance);
		finalBest.initRandom();
		finalBest.decoding(instance);
		finalBest.evaluate();
        tempBest = new Individual(instance);
        tempBest.initRandom();
		tempBest.decoding(instance);
		tempBest.evaluate();

        System.out.println("Aufgabe " + instance.getName());
        System.out.println("Startwert = " + finalBest.getFitness());

        population.setBestIndividual(finalBest);

		int iterations = anzahlLoesungen / anz;
		int step_one = (int)(iterations * 0.05);
		int step_two = (int)(iterations * 0.1);
		int step_three = (int)(iterations * 0.7);
		int step_four = (int)(iterations * 0.75);

        double pMut = population.getMutationProbability();

		population.setMutationProbability(20*pMut);

		for (int i = 1; i < iterations; i++) {
			if(iterations == step_one){
				population.setMutationProbability(pMut);
			}
			if(iterations ==step_two){
				population.setMutationProbability(pMut);
			}
			if(iterations == step_three){
				population.setMutationProbability(20*pMut);
			}
			if(iterations == step_four){
				population.setMutationProbability(pMut);
			}

            Individual[] children = new Individual[anz];
            population.rankRouletteSelection();

            for (int j = 0; j < population.getSize(); j++) {
                Individual parent_a = population.getEntity();
                Individual parent_b = population.getEntity();
                children[j] = parent_a.mate(parent_b,instance);
                children[j].mutate(population.getMutationProbability());
                children[j].decoding(instance);
                children[j].evaluate();
            }

			population.setNewPopulation(children);
			for (Individual child : children){
				if (child.getFitness() < tempBest.getFitness()) {
					tempBest = child;
					finalBest = child.getGenotypeClone(instance);
				}
			}
			population.setBestIndividual(tempBest);
		}
		finalBest.decoding(instance);
		finalBest.evaluate();
		System.out.println("best fitness:" + finalBest.getFitness());
		finalBest.ausgabe(instance);
		return finalBest.getPhaenotype();

	}
}

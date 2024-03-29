package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;
import mlulsp.solvers.ga.Population;

public class GaSolver implements Solver {
	private final int anzahlLoesungen;

	/*
	 * hier k�nnen Parameter des GA angegeben werden z.B. PopulationsGroesse,
	 * IterationenAnzahl
	 */

	public GaSolver(int anzahlLoesungen) {
		this.anzahlLoesungen = anzahlLoesungen;
	}

	public ProductionSchedule solve(Instance instance) {

		int anz = 2;
		Individual elter, child;
		Individual[] pop = new Individual[anz];
		
		Population population = new Population(anz, instance);
		population.firstLastPeriodsBerechnen();
		population.mutationsWahrscheinlichkeit();
		population.populate();
		
		for (int i = 0; i < anzahlLoesungen/(population.getPopSize()*2); i++) {
			//population.onlyRouletteCrossover();
//			System.out.println(population.getMutationProbability());
			population.rouletteCrossover();
			//population.rankRouletteSelection();
			population.bestHalfSelection();
			//population.bestSelection();
//			System.out.println(population.toString());
		}
		
		population.sortPopulation();
		
		Individual best = population.getAt(population.getPopSize()-1);
		best.ausgabe(instance);
		return best.getPhaenotype();
		
		
//		for(int i=0;i<anz;i++){
//			pop[i] = new Individual(instance);
//			pop[i].initRandom();
//			pop[i].decoding(instance);
//			pop[i].evaluate();
//			
//		}
		
		
//		elter = new Individual(instance);
//		elter.initRandom();
//		elter.decoding(instance);
//		elter.evaluate();
//
//		
//		for (int i = 1; i < anzahlLoesungen; i++) {
//			
//			child = new Individual(instance);
//			child.reproduce(elter);
//			child.flipMutate();
//			child.decoding(instance);
//			child.evaluate();
//				if (child.getFitness() < elter.getFitness()) {
//					System.out.println(i + " " + elter.getFitness());
//					elter = child;
//				} 
//
//
//		}
//
//		elter.ausgabe(instance);
//		return elter.getPhaenotype();

	}
}

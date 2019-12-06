package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.solvers.Solver;


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
		Individual.firstLastPeriodsBerechnen(instance);
		Individual.mutationsWahrscheinlichkeit();

		int anz = 10;
		Individual elter, child;
		Individual[] pop = new Individual[anz];
		
		for(int i=0;i<anz;i++){
			pop[i] = new Individual(instance);
			pop[i].initRandom();
			pop[i].decoding(instance);
			pop[i].evaluate();
			
		}
		
		
		elter = new Individual(instance);
		elter.initRandom();
		elter.decoding(instance);
		elter.evaluate();

		
		for (int i = 1; i < anzahlLoesungen; i++) {
			
			child = new Individual(instance);
			child.reproduce(elter);
			child.mutate();
			child.decoding(instance);
			child.evaluate();
			if (child.getFitness() < elter.getFitness()) {
				if (child.getFitness() < elter.getFitness()) {
					//System.out.println(i + " " + elter.getFitness());
				}				
				elter = child;
			}
		}

		elter.ausgabe(instance);
		return elter.getPhaenotype();

	}
}

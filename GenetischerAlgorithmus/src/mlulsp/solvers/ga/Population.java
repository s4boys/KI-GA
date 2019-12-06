package mlulsp.solvers.ga;

import mlulsp.solvers.ga.Individual;
import mlulsp.domain.Instance;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;



public class Population {

	private int popSize;
	private Instance instance;
	private Individual[] population;
	
	// Constructor
	public Population(int popSize, Instance instance) {
		super();
		this.popSize = popSize;
		this.instance = instance;
		this.population = new Individual[popSize];
	}
	
	//  Initial population with random Individuals
	public void populate() {
		for (int i = 0; i < population.length; i++) {
			this.population[i] = new Individual(this.instance);
			this.population[i].initRandom();
			this.population[i].decoding(this.instance);
			this.population[i].evaluate();
		}
	}
	

	
	
	public void rouletteSelection() {	
		Arrays.sort(this.population, new IndividualComparator());
		int gaus = (int) (Math.pow(this.population.length, 2) + this.population.length) / 2;
		
		
		System.out.println(getRandomIntRange(0, gaus));
	
	}
	
	
	// Return Individual at Index
	public Individual getAt(int index) {
		return this.population[index];
	}
	
	
	// Getter and Setter
	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Individual[] getPopulation() {
		return population;
	}

	public void setPopulation(Individual[] population) {
		this.population = population;
	}

	// Helper Methods
	
	private static int getRandomIntRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	private static double getRandomDoubleRange(double min, double max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return (double) r;
	}
	
	// Comperator for Population sorting
	private class IndividualComparator implements Comparator<Individual> {
	    @Override
	    public int compare(Individual a, Individual b) {
	        return (int) (a.getFitness() - b.getFitness());
	    }
	}
	
	
	
}

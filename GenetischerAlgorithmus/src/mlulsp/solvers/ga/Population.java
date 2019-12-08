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
	
	// roulette selection with parents. Rule: no double entries;
	public void rouletteSelection() {
		
        Individual[] roulette = createRoulette();
		Population newGeneration = new Population(this.popSize/2, this.instance);
		
		
		// vlt mit while ersetzen
		for (int i = 0; i < newGeneration.popSize; i++) {
			Individual chosen = roulette[getRandomIntRange(0, roulette.length-1)];
			if(!Arrays.asList(newGeneration.population).contains(chosen)) {
				newGeneration.setAt(i, chosen);
			} else {
				i--;
			}
		}
		


	
	}
	
	// completely random selection. Rule: no double entries; Mainly for testing
	public void randomSelection() {
		Population newGeneration = new Population(this.popSize/2, this.instance);
		for (int i = 0; i < newGeneration.popSize; i++) {
			Individual chosen = this.population[getRandomIntRange(0, this.popSize-1)];
			if(!Arrays.asList(newGeneration.population).contains(chosen)) {
				newGeneration.setAt(i, chosen);
			} else {
				i--;
			}
		}
	}
	// only select the best half of each generation
	public void bestHalfSelection() {
		this.sortPopulation();
		Population newGeneration = new Population(this.popSize/2, this.instance);
		for (int i = 0; i < popSize/2; i++) {
			newGeneration.setAt(i, this.getAt(i+popSize/2));
		}
		 this.popSize = newGeneration.popSize;
		 this.population = newGeneration.population;
	}
	
	// only select the best Individual of each generation
	public void bestSelection() {
		this.sortPopulation();
		Population newGeneration = new Population(this.popSize/2, this.instance);
		for (int i = 0; i < newGeneration.popSize; i++) {
			newGeneration.setAt(i, this.getAt(this.popSize-1));
		}
		 this.popSize = newGeneration.popSize;
		 this.population = newGeneration.population;
	}
	
	// random crossover. Rule: no crossover with itself
	public void randomCrossover(){
		Individual[] newGeneration = new Individual[this.popSize*2];
		
		for (int i = 0; i < newGeneration.length/2; i++) {
			newGeneration[i] = this.getAt(i);
		}
		
		// vlt mit while ersetzen
		for (int i = newGeneration.length/2; i < newGeneration.length; i++) {
			int indexp1 = getRandomIntRange(0, this.popSize-1);
			int indexp2 = getRandomIntRange(0, this.popSize-1);
			Individual parent1 = this.getAt(indexp1);
			Individual parent2 = this.getAt(indexp2);
			
			if(parent1 != parent2) {
				Individual child = new Individual(this.instance);
				child.crossover(parent1, parent2);
				child.mutate();
				child.decoding(instance);
				child.evaluate();
				newGeneration[i] = child;
			} else {
				i--;
			}

		}
		
		this.popSize = newGeneration.length;
		this.population = newGeneration;
		
	};
	
	// roulette crossover. Rule: no crossover with itself
	public void rouletteCrossover(){
		
		Individual[] roulette = createRoulette();
		Individual[] newGeneration = new Individual[this.popSize*2];
		
		for (int i = 0; i < newGeneration.length/2; i++) {
			newGeneration[i] = this.getAt(i);
		}
		
		// vlt mit while ersetzen
		for (int i = newGeneration.length/2; i < newGeneration.length; i++) {
			int indexp1 = getRandomIntRange(0, roulette.length-1);
			int indexp2 = getRandomIntRange(0, roulette.length-1);
			Individual parent1 = roulette[indexp1];
			Individual parent2 = roulette[indexp2];
			if(parent1 != parent2) {
				Individual child = new Individual(this.instance);
				child.crossover(parent1, parent2);
				child.mutate();
				child.decoding(instance);
				child.evaluate();
				newGeneration[i] = child;
			} else {
				i--;
			}

		}
		
		this.popSize = newGeneration.length;
		this.population = newGeneration;
		
	};
	
	
	

	
	// Getter and Setter
	public Individual getAt(int index) {
		return this.population[index];
	}

	public void setAt(int index, Individual i) {
		this.population[index] = i;
	}
	
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
	
	public double getTotalFitness() {
		double totalFitness = 0;
		for (Individual individual : this.population) {
			totalFitness += individual.getFitness();
		}
		return totalFitness;
	}
	
	public double getMeanFitness() {
		return getTotalFitness()/this.popSize;
	}

	// Helper Methods
	@Override
	public String toString() {
		return Arrays.toString(population);
	}

	public void sortPopulation() {
		Arrays.sort(this.population, new IndividualComparator());
	}
	
	// create roulette out of this.population
	private Individual[] createRoulette() {
		Arrays.sort(this.population, new IndividualComparator());
		int gaus = (int) (Math.pow(this.population.length, 2) + this.population.length) / 2;
        Individual[] roulette = new Individual[gaus];
        int k = 0;
        
        for (int i = 0; i < this.popSize; i++) {
            for(int j = 0; j <= i; j++){
                roulette[k] = this.getAt(i);
                k++;
            }
        }
        
        return roulette;
	}
	private static int getRandomIntRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return (r.nextInt((max - min) + 1) + min );
	}
	

	
	// Comparator for Population sorting
	private class IndividualComparator implements Comparator<Individual> {
	    @Override
	    public int compare(Individual a, Individual b) {
	        return (int) (a.getFitness() - b.getFitness());
	    }
	}
	
	
	
}
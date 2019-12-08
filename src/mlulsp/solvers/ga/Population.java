package mlulsp.solvers.ga;

import mlulsp.domain.Instance;

import java.util.Random;

public class Population {
    Random rGenerator = new Random();

    Individual[] entities;
    Individual[] selectedEntities;
    Instance instance;

    public int getSize(){
        return entities.length;
    }

    Population(int size, Instance inst) {
        this.entities = new Individual[size];
        this.instance = inst;
        this.selectedEntities = new Individual[size];
    }

    public void initialize() {
        for (int i = 0; i < entities.length; i++) {
            this.entities[i] = new Individual(instance);
            this.entities[i].initRandom();
            this.entities[i].decoding(instance);
            this.entities[i].evaluate();
        }
    }

    public Individual getEntity(){
        return selectedEntities[(int)(rGenerator.nextDouble()*selectedEntities.length)];
    }

    public void setNewPopulation(Individual[] children){
        this.entities = children;
    }

    public void rouletteSelection() {
        // Roulette selection via share of complete fitness
        // This selection calculates the share of each fitness from the complete fitness and creates an array of them
        // The random number matches one instance (the first one the selector is smaller as) and this instance is selected
        double[] shares = new double[this.entities.length];
        int complete_fitness = 0;
        for (int i = 0; i < this.entities.length; i++) {
            complete_fitness += this.entities[i].getFitness();
        }

        double share = 0.;
        for (int i = 0; i < this.entities.length; i++) {
            share += this.entities[i].getFitness() / complete_fitness; // percentage of this fitness share
            shares[i] = share;
        }

        for (int i=0; i< this.selectedEntities.length; i++){
            double selector = rGenerator.nextDouble();
            for (int j = 0; j < shares.length; j++){
                if (selector < shares[j]){
                    this.selectedEntities[i] = this.entities[i];
                }
            }

        }
    }

    public void rankRouletteSelection() {
        // Rank selection
        // This selection orders the entities by fitness and distributes them according to their rank.
        // Very good and very bad fitnesses are not as drastic as with the regular roulette.
        // But it seems worse when all fitnesses are almost the same.
        int complete_fitness = (int) (Math.pow(this.entities.length, 2) + this.entities.length) / 2;

        Individual[] roulette_pop = new Individual[complete_fitness];
        for (int i = 0, k = 0; i < this.entities.length; i++) {
            for (int j = 0; j < i + 1 && k < complete_fitness; j++, k++) {
                roulette_pop[k] = this.entities[i];
            }
        }
        for (int i = 0; i < this.selectedEntities.length; i++) {
            int selector = (int) (rGenerator.nextDouble() * roulette_pop.length);
            this.selectedEntities[i] = roulette_pop[selector];
        }
    }
}


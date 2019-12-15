package mlulsp.solvers.ga;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;

import java.util.Arrays;
import java.util.Random;

public class Population {
    int firstPeriodforItems[];
    int lastPeriodforItems[];
    double pMut;
    Random rGenerator = new Random();

    Individual[] entities;
    Individual[] selectedEntities;
    Instance instance;

    public int getSize(){
        return entities.length;
    }

    public void firstLastPeriodsBerechnen() {
        ProductionSchedule dummySolution = new ProductionSchedule(instance.getItemCount(), instance.getPeriodCount());
        dummySolution.justInTime();
        instance.decodeMatrix(dummySolution);
        dummySolution.bereinigen();

        firstPeriodforItems = new int[instance.getItemCount()];
        lastPeriodforItems = new int[instance.getItemCount()];

        for (int i = 0; i < instance.getItemCount(); i++) {
            boolean first = false;
            for (int p = 0; p < instance.getPeriodCount(); p++) {
                if (dummySolution.demand[i][p] != 0) {
                    if (!first) {
                        first = true;
                        firstPeriodforItems[i] = p;
                    }
                    lastPeriodforItems[i] = p;
                }
            }
        }
    }

    public double getMutationProbability() {
        return pMut;
    }

    public void setMutationProbability(double newMutProp) {
        pMut = newMutProp;
    }

    public void mutationsWahrscheinlichkeit() {
        int anzahlPerioden = 0;
        for (int i = 0; i < firstPeriodforItems.length; i++) {
            anzahlPerioden += lastPeriodforItems[i] - firstPeriodforItems[i] + 1;
        }
        pMut = 1. / anzahlPerioden;
//		pMut = 0.005;
        System.out.println("Mutationswahrscheinlichkeit : " + pMut);
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
        double complete_fitness = 0;
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
                    this.selectedEntities[i] = this.entities[j];
                    break;
                }
            }

        }
    }

    public void mixedSelection(){
        // regular rank roulette but the top 40% and lowest 10% are always kept

        // regular rank roulette
        rankRouletteSelection();

        Arrays.sort(this.entities, new IndividualComparator());

        // top 60%
        int separator = (int)(this.entities.length * 0.6);
        int position = 0;
        System.arraycopy(this.entities,separator,selectedEntities, 0,selectedEntities.length-separator);
        position += selectedEntities.length-separator;

//        // worst 10%
//        separator = (int)(this.entities.length * 0.1);
//        // from entities 0 into selectedEntities
//        System.arraycopy(this.entities,0,selectedEntities,position,separator);
    }

    public void rankRouletteSelection() {
        // Rank selection
        // This selection orders the entities by fitness and distributes them according to their rank.
        // Very good and very bad fitnesses are not as drastic as with the regular roulette.
        // But it seems worse when all fitnesses are almost the same.

        // Gauß
        int complete_fitness = (int) (Math.pow(this.entities.length, 2) + this.entities.length) / 2;

        Arrays.sort(this.entities,new IndividualComparator());

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
        // Replaces the worst of the new entities with the old best 10% / Elitism
        int position = (int)(this.entities.length * 0.9);
        Arrays.sort(this.selectedEntities,new IndividualComparator());
        System.arraycopy(this.entities,position,this.selectedEntities,0,this.entities.length-position);

    }
}


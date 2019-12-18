package mlulsp.solvers.ga;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;


import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;

public class Individual {
    Random rGenerator = new Random();

    private int[][] genotype;
    private ProductionSchedule phaenotype;
    private double fitness;

    public ProductionSchedule getPhaenotype() {
        return phaenotype;
    }

    Individual(Instance inst) {
        genotype = new int[inst.getItemCount()][inst.getPeriodCount()];
    }

    public void initRandom() {
        for (int i = 0; i < genotype.length; i++) {
            for (int j = 0; j < genotype[i].length; j++) {
                if (rGenerator.nextDouble() < 0.5) genotype[i][j] = 0;
                else genotype[i][j] = 1;
            }
        }
    }

    public void decoding(Instance instance) {
        phaenotype = new ProductionSchedule(genotype, instance);
    }

    public void evaluate() {
        fitness = phaenotype.getCostSum();
    }

    public double getFitness() {
        return fitness;
    }

    public void mutateShiftRight(double pMut) {
        double erwartungswert = genotype.length * genotype[0].length * pMut;
        double pMutShift = 1 / erwartungswert; // mutation happens as often as as the other mutations (I hope)

        for (int i = 0; i < genotype.length; i++) {
            if (rGenerator.nextDouble() < pMutShift) {
                int last_value = genotype[i][genotype[i].length - 1];
                int temp = 0;
                for (int j = 0; j < genotype[i].length - 1; j++) {
                    temp = genotype[i][j];
                    genotype[i][j] = last_value;
                    last_value = temp;
                }
                genotype[i][genotype[i].length - 1] = last_value;
            }
        }
    }

    public void mutate(double pMut) {
        mutateSwap(0.80 * pMut);
    }

    public void mutateShiftLeft(double pMut) {
        double erwartungswert = genotype.length * genotype[0].length * pMut;
        double pMutShift = 1 / erwartungswert; // mutation happens as often as as the other mutations (I hope)

        for (int i = 0; i < genotype.length; i++) {
            if (rGenerator.nextDouble() < pMutShift) {
                int last_value = genotype[i][0];
                int temp = 0;
                for (int j = genotype[i].length - 1; j > 1; j--) {
                    temp = genotype[i][j];
                    genotype[i][j] = last_value;
                    last_value = temp;
                }
                genotype[i][0] = last_value;
            }
        }
    }

    public void mutateFlip(double pMut) {
        for (int i = 0; i < genotype.length; i++) {
            //for(int j=firstPeriodforItems[i];j<=lastPeriodforItems[i];j++){
            for (int j = 0; j < genotype[i].length; j++) {
                if (rGenerator.nextDouble() < pMut) {
                    if (genotype[i][j] == 1) genotype[i][j] = 0;
                    else genotype[i][j] = 1;
                    return;
                }
            }
        }
    }

    public void mutateSwap(double pMut) {
        for (int i = 0; i < this.genotype.length; i++) {
            for (int j = 0; j < this.genotype[i].length; j++) {
                if (rGenerator.nextDouble() < pMut) {
                    int temp = this.genotype[i][j];
                    int new_column = (int) (rGenerator.nextDouble() * this.genotype.length);
                    int new_row = (int) (rGenerator.nextDouble() * this.genotype[new_column].length);
                    this.genotype[i][j] = this.genotype[new_column][new_row];
                    this.genotype[new_column][new_row] = temp;
                }
            }
        }
    }

    public Individual getGenotypeClone(Instance instance) {
        Individual clone = new Individual(instance);
        for (int i = 0; i < this.genotype.length; i++) {
            System.arraycopy(this.genotype[i], 0, clone.genotype[i], 0, this.genotype[i].length);
        }
        return clone;
    }

    public Individual mate(Individual other, Instance instance) {
        int cross = (int) (Math.random() * genotype.length);

        Individual child = new Individual(instance);

        for (int i = 0; i < cross; i++) {
            System.arraycopy(other.genotype[i], 0, child.genotype[i], 0, other.genotype[i].length);
        }
        for (int i = cross; i < genotype.length; i++) {
            System.arraycopy(this.genotype[i], 0, child.genotype[i], 0, this.genotype[i].length);
        }
        return child;
    }

    public void ausgabe(Instance instance) {
        try {
            String ausgabeName = instance.getName();
            PrintWriter pu = new PrintWriter(new FileWriter(ausgabeName + ".sol"));
            pu.println(instance.getName());
            pu.println("Fitness (total costs): " + fitness);
            pu.println("Genotype: ");

            for (int i = 0; i < genotype.length; i++) {
                for (int j = 0; j < genotype[i].length; j++) {
                    pu.print(genotype[i][j]);
                }
                pu.println();
            }
            pu.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

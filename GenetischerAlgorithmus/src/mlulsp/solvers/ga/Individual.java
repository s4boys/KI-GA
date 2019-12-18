package mlulsp.solvers.ga;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;

public class Individual {
//	static int firstPeriodforItems[];
//	static int lastPeriodforItems[];
	static double pMut;

	// necessary for parallelization
	Random rGenerator = new Random();

	private int[][] genotype;
	private ProductionSchedule phaenotype;
	private double fitness;	
	
	public ProductionSchedule getPhaenotype(){
		return phaenotype;
	}
	
//	public void firstLastPeriodsBerechnen(Instance inst){
//		ProductionSchedule dummySolution = new ProductionSchedule(inst.getItemCount(), inst.getPeriodCount());
//		dummySolution.justInTime();
//    	inst.decodeMatrix(dummySolution);
//    	dummySolution.bereinigen();
//    	
//    	firstPeriodforItems = new int[inst.getItemCount()];
//    	lastPeriodforItems  = new int[inst.getItemCount()];
//    	
//    	for(int i=0;i<inst.getItemCount();i++){
//    		boolean first = false;
//    		for(int p=0;p<inst.getPeriodCount();p++){
//    			if(dummySolution.demand[i][p] != 0){
//    				if(!first){
//    					first=true;
//    					firstPeriodforItems[i] = p;
//    				}
//    				lastPeriodforItems[i] = p;
//    			}
//    		}
//    	}		
//	}
//	
//	public static void mutationsWahrscheinlichkeit(){
//		int anzahlPerioden  = 0;
//		for(int i=0;i<firstPeriodforItems.length;i++){
//    		anzahlPerioden +=  lastPeriodforItems[i]-firstPeriodforItems[i]+1;
//		}
////		pMut = (1./anzahlPerioden);
////		pMut = 0.0005;
//		pMut = 0.005;
////		System.out.println("Mutationswahrscheinlichkeit : " + pMut);
//	}

	Individual(Instance inst) {
		genotype = new int[inst.getItemCount()][inst.getPeriodCount()];
	}
	
	public void initRandom() {
		for (int i = 0; i < genotype.length; i++) {
			for (int j = 0; j < genotype[i].length; j++) {
				if(rGenerator.nextDouble()<0.5)	genotype[i][j] = 0;
				else 					genotype[i][j] = 1;
			}
		}
	}
	
//	public void initJustInTime() {
////		for (int i = 0; i < genotype.length; i++) {
////			for (int j = 0; j < genotype[i].length; j++) {
////				genotype[i][j] = 1;
////			}
////		}
//		for (int i = 0; i < genotype.length; i++) {
//			for (int j = firstPeriodforItems[i]; j <= lastPeriodforItems[i]; j++) {
//				genotype[i][j] = 1;
//			}
//		}		
//	}
	

	public void decoding(Instance instance){
		phaenotype = new ProductionSchedule(genotype, instance);		
	}
	
	public void ausgabe(int[] firstPeriodforItems, int[] lastPeriodforItems){
		System.out.println("Genotype");
		for(int i=0;i<genotype.length;i++){
			for(int j=0;j<genotype[i].length;j++){
				System.out.print(genotype[i][j]);
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Phaenotype");
		for(int i=0;i<phaenotype.genome.length;i++){
			for(int j=0;j<phaenotype.genome[i].length;j++){
				System.out.print(phaenotype.genome[i][j]);
			}
			System.out.print(" " + firstPeriodforItems[i] + " " + lastPeriodforItems[i]);
			System.out.println();
		}
		System.out.println();
	}
	
	public void evaluate(){
		fitness = phaenotype.getCostSum();
	}
	public double getFitness(){
		return fitness;
	}
	
	public void reproduce(Individual elter){
		for(int i=0;i<elter.genotype.length;i++){
			for(int j=0;j<elter.genotype[i].length;j++){
				this.genotype[i][j] = elter.genotype[i][j];
			}
		}
	}
	
	// change all 1 to 0 and all 0 to 1
	public void mutate(double pMut){
		
		for(int i=0;i<genotype.length;i++){
			//for(int j=firstPeriodforItems[i];j<=lastPeriodforItems[i];j++){
			for(int j=0;j<genotype[i].length;j++){
				if(rGenerator.nextDouble() < pMut){
					if(genotype[i][j] == 1)genotype[i][j] = 0;
					else                   genotype[i][j] = 1;
				}
			}
		}
	}
	
	// flip one random bit in one genotype array
	public void flipMutate(double pMut) {
		for(int i=0;i<genotype.length;i++){
			if(rGenerator.nextDouble() < pMut){
				int flipBit = getRandomIntRange(0, genotype[i].length-1);
				if(genotype[i][flipBit] == 1)genotype[i][flipBit] = 0;
				else                   genotype[i][flipBit] = 1;
			}
		}
	}
	
	// flip whole genotype array
	
	public void fullFlipMutate(double pMut) {
		for(int i=0;i<genotype.length;i++){
			if(rGenerator.nextDouble() < pMut){
				for (int j : genotype[i]) {
					if(j == 1)j = 0;
					else j = 1;
				}

			}
		}
	}
	// swap to right neighbouring elements
	public void swapMutate(double pMut){
		for(int i=0;i<genotype.length;i++){
			//for(int j=firstPeriodforItems[i];j<=lastPeriodforItems[i];j++){
			for(int j=0;j<genotype[i].length;j++){
				if(rGenerator.nextDouble() < pMut){
					if(j != genotype[i].length-1) {
						int tmp = genotype[i][j];
						genotype[i][j] = genotype[i][j+1];
						genotype[i][j+1] = tmp;
						//Collections.swap(Arrays.asList(genotype[i]), j, j+1);
					} else {
						int tmp = genotype[i][j];
						genotype[i][j] = genotype[i][j-1];
						genotype[i][j-1] = tmp;
						//Collections.swap(Arrays.asList(genotype[i]), j, j-1);
					}
				}
			}
		}
	}
	
	
	public void randSwapMutate(double pMut){
		for(int i=0;i<genotype.length;i++){
			//for(int j=firstPeriodforItems[i];j<=lastPeriodforItems[i];j++){
			for(int j=0;j<genotype[i].length;j++){
				if(rGenerator.nextDouble() < pMut){
					int position = getRandomIntRange(0, genotype[i].length-1);
					if(j != position) {
						int tmp = genotype[i][j];
						genotype[i][j] = genotype[i][position];
						genotype[i][position] = tmp;
					} else {
						j--;
					}
				}
			}
		}
	}
	
	// shift whole array once to the right
	public void rightShiftMutate(double pMut) {
		for(int i=0;i<genotype.length;i++){
			if(rGenerator.nextDouble() < pMut){
				shiftRight(genotype[i], 1);
			}
		}
	}
	
	// shift whole array once to the left
	public void leftShiftMutate(double pMut) {
		for(int i=0;i<genotype.length;i++){
			if(rGenerator.nextDouble() < pMut){
				shiftLeft(genotype[i], 1);
			}
		}
	}
	
	public void crossover(Individual mama, Individual papa){
		int crossProb = 95;//getRandomIntRange(65, 85);
		double prob = Math.random()*100;
		int cross = (int)(rGenerator.nextDouble()*genotype.length);
		
		if(prob <= crossProb) {
			for(int i=0;i<cross;i++){
				for(int j=0;j<genotype[i].length;j++){
					this.genotype[i][j] = mama.genotype[i][j];
				}
			}
			for(int i=cross;i<genotype.length;i++){
				for(int j=0;j<genotype[i].length;j++){
					this.genotype[i][j] = papa.genotype[i][j];
				}
			}
		} else {
			for (int i = 0; i < mama.genotype.length; i++) {
				for (int j = 0; j < mama.genotype[i].length; j++) {
					this.genotype[i][j] = mama.genotype[i][j];
				}
			}
		}


	}
	
	
	public void ausgabe(Instance instance){
		try{			
			String ausgabeName = instance.getName();
			PrintWriter pu = new PrintWriter(new FileWriter(ausgabeName + ".sol"));
			pu.println(instance.getName());
			pu.println("Fitness (total costs): " + fitness);
			pu.println("Genotype: ");
			
			for(int i=0;i<genotype.length;i++){
				for(int j=0;j<genotype[i].length;j++){
					pu.print(genotype[i][j]);
				}
				pu.println();
			}
			pu.close();	
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	
	public void shiftRight(int[] array, int amount) {
		for (int j = 0; j < amount; j++) {
			int a = array[array.length - 1];
			int i;
			for (i = array.length - 1; i > 0; i--)
				array[i] = array[i - 1];
			array[i] = a;
		}
	}
	public void shiftLeft(int[] array, int amount) {
		for (int j = 0; j < amount; j++) {
			int a = array[0];
			int i;
			for (i = 0; i < array.length - 1; i++)
				array[i] = array[i + 1];
			array[i] = a;
		}
	}
	@Override
	public String toString() {
		return "Individual [ fitness="
				+ fitness + "]";
	}
	
	private int getRandomIntRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return (r.nextInt((max - min) + 1) + min );
	}
	
}

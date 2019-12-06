package mlulsp.solvers.ga;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;

public class Individual {
	static int firstPeriodforItems[];
	static int lastPeriodforItems[];
	static double pMut;
	
	private int[][] genotype;
	private ProductionSchedule phaenotype;
	private double fitness;	
	
	public ProductionSchedule getPhaenotype(){
		return phaenotype;
	}
	
	public static void firstLastPeriodsBerechnen(Instance inst){
		ProductionSchedule dummySolution = new ProductionSchedule(inst.getItemCount(), inst.getPeriodCount());
		dummySolution.justInTime();
    	inst.decodeMatrix(dummySolution);
    	dummySolution.bereinigen();
    	
    	firstPeriodforItems = new int[inst.getItemCount()];
    	lastPeriodforItems  = new int[inst.getItemCount()];
    	
    	for(int i=0;i<inst.getItemCount();i++){
    		boolean first = false;
    		for(int p=0;p<inst.getPeriodCount();p++){
    			if(dummySolution.demand[i][p] != 0){
    				if(!first){
    					first=true;
    					firstPeriodforItems[i] = p;
    				}
    				lastPeriodforItems[i] = p;
    			}
    		}
    	}		
	}
	
	public static void mutationsWahrscheinlichkeit(){
		int anzahlPerioden  = 0;
		for(int i=0;i<firstPeriodforItems.length;i++){
    		anzahlPerioden +=  lastPeriodforItems[i]-firstPeriodforItems[i]+1;
		}
		pMut = 1./anzahlPerioden;
		//pMut = 0.0005;
		//System.out.println("Mutationswahrscheinlichkeit : " + pMut);
	}

	Individual(Instance inst) {
		genotype = new int[inst.getItemCount()][inst.getPeriodCount()];
	}
	
	public void initRandom() {
		for (int i = 0; i < genotype.length; i++) {
			for (int j = 0; j < genotype[i].length; j++) {
				if(Math.random()<0.5)	genotype[i][j] = 0;
				else 					genotype[i][j] = 1;
			}
		}
	}
	
	public void initJustInTime() {
//		for (int i = 0; i < genotype.length; i++) {
//			for (int j = 0; j < genotype[i].length; j++) {
//				genotype[i][j] = 1;
//			}
//		}
		for (int i = 0; i < genotype.length; i++) {
			for (int j = firstPeriodforItems[i]; j <= lastPeriodforItems[i]; j++) {
				genotype[i][j] = 1;
			}
		}		
	}
	

	public void decoding(Instance instance){
		phaenotype = new ProductionSchedule(genotype, instance);		
	}
	
	public void ausgabe(){
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
	
	public void mutate(){
		
		for(int i=0;i<genotype.length;i++){
			//for(int j=firstPeriodforItems[i];j<=lastPeriodforItems[i];j++){
			for(int j=0;j<genotype[i].length;j++){
				if(Math.random() < pMut){
					if(genotype[i][j] == 1)genotype[i][j] = 0;
					else                   genotype[i][j] = 1;
				}
			}
		}
	}
	
	public void crossover(Individual mama, Individual papa){
		int cross = (int)(Math.random()*genotype.length);
		
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

	@Override
	public String toString() {
		return "Individual [ fitness="
				+ fitness + "]";
	}
	
	
}
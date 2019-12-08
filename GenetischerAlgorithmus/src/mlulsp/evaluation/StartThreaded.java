package mlulsp.evaluation;

import java.io.File;
import java.util.ArrayList;


import mlulsp.domain.Instance;
import mlulsp.domain.ProductionSchedule;
import mlulsp.helper.SharedData;
import mlulsp.io.DirectoryWalker;
import mlulsp.io.InstanceReader;
import mlulsp.solvers.ga.GaSolver;
import mlulsp.io.ReadBestResults;

import mlulsp.helper.SharedData;

public class StartThreaded {

    public static void main(String[] args) {
        new StartThreaded().exec();
    }


    public void exec() {
        final int anzahlLoesungenProInstanz = 400000;
        // class1: anzahlLoesungenProInstanz =  50000
        // class2: anzahlLoesungenProInstanz = 200000
        // class3: anzahlLoesungenProInstanz = 400000

        int anzahlOptimal = 0;
        int anzahlInstanzen = 0;
//        double gapOptimal = 0.; // exists in thread scope
        double avgGapOptimal = 0.;
        double optimalValue = 0.; // exists in thread scope
		final double precision = 0.001; // final for inner class

        String insPath = "MLULSPinstances/Phase1";
        //String insPath         = "instances";
        final String solFile = "solutions.txt";
        DirectoryWalker walker = new DirectoryWalker(new File(insPath), false);

        ArrayList<Thread> threads = new ArrayList<Thread>();

		SharedData sdata = new SharedData(anzahlInstanzen,avgGapOptimal,anzahlOptimal);

        class GaTask implements Runnable {
            File insFile;
			double optimalValue = 0.;
			double gapOptimal = 0.;
			SharedData sdata;

            GaTask(File insFile,SharedData sdata) {
                this.insFile = insFile;
                this.sdata = sdata;
            }

            @Override
            public void run() {
                InstanceReader inReader = new InstanceReader(insFile);
                Instance i = inReader.parse();
                optimalValue = ReadBestResults.getBestSolution(new File(solFile), i.getName());

                GaSolver solver = new GaSolver(anzahlLoesungenProInstanz);

                long start = System.currentTimeMillis();
                ProductionSchedule solution = solver.solve(i);
                long time = System.currentTimeMillis() - start;

                if (optimalValue > 0.) {
                    gapOptimal = 100 * ((solution.getCostSum() - optimalValue) / optimalValue);
                    if (Math.abs(gapOptimal) < precision) sdata.incrementAnzahlOptimal();
                    sdata.addToAvgGapOptimal(gapOptimal);
                    sdata.incrementAnzahlInstanzen();
                    System.out.print("Instance " + i.getName() + " solved in " + time + " ms");
                    System.out.print(" ZF = " + solution.getCostSum() + " OPT = " + optimalValue);
                    System.out.print(" Abweichung: " + gapOptimal + " %");
                    System.out.println();
                } else {
                    System.out.print("Kein Literaturwert vorhanden fuer: " + i.getName());
                    System.out.print(" solved in " + time + " ms");
                    System.out.print(" ZF = " + solution.getCostSum());
                }
            }
        }
        long start = System.currentTimeMillis();
        for (File insFile : walker) {
        	Thread t = new Thread(new GaTask(insFile,sdata));
        	threads.add(t);
        	t.start();
        }
        for (Thread t : threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
        long time = System.currentTimeMillis() - start;

        System.out.println();
        if (sdata.getAnzahlInstanzen() != 0) {
            System.out.println("----> Anzahl Instanzen mit Vergleichswerten: " + sdata.getAnzahlInstanzen());
            System.out.println("----> Anzahl optimal gelöster Instanzen: " + sdata.getAnzahlOptimal() + "\t (Precision: " + precision + ")");
            System.out.println("----> Durchschnittliche prozentuale Abweichung: " + (sdata.getAvgGapOptimal() / sdata.getAnzahlInstanzen()));
            System.out.println("Insgesamte Dauer = " + time + "ms");
        }
    }
}

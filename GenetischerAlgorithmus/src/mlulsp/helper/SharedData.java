package mlulsp.helper;

public class SharedData {
    double anzahlOptimal;
    double anzahlInstanzen;
    double avgGapOptimal;

    public double getAnzahlOptimal() {
        return anzahlOptimal;
    }

    public double getAnzahlInstanzen() {
        return anzahlInstanzen;
    }

    public double getAvgGapOptimal() {
        return avgGapOptimal;
    }

    public SharedData(double anzahlInstanzen, double avgGapOptimal, double anzahlOptimal){
        this.anzahlInstanzen = anzahlInstanzen;
        this.avgGapOptimal = avgGapOptimal;
        this.anzahlOptimal = anzahlOptimal;
    }

    public void incrementAnzahlInstanzen(){
        synchronized (this){
            this.anzahlInstanzen++;
        }
    }
    public void addToAvgGapOptimal(double a){
        synchronized (this){
            this.avgGapOptimal += a;
        }
    }
    public void incrementAnzahlOptimal(){
        synchronized (this){
            this.anzahlOptimal++;
        }
    }
}
package utn.proy2k18.vantrack.mainFunctionality;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Company {

    private String bussinessName;
    private int cuit;
    private ArrayList<Double> calification;

    public Company() {
    }

    public Company(String bussinessName, int cuit) {
        this.bussinessName = bussinessName;
        this.cuit = cuit;
        this.calification = new ArrayList<>();
    }

    public String getBussinessName() { return this.bussinessName; }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public int getCuit() { return this.cuit; }

    public void setCuit(int cuit) {
        this.cuit = cuit;
    }

    public ArrayList<Double> getCalification() {
        return calification;
    }

    public void setCalification(ArrayList<Double> calification) {
        this.calification = calification;
    }

    public void addCalification(double calif) {
        this.calification.add(calif);
    }

    @Exclude
    public double getCalificationAvg() {
        double calif_avg = 0.0;

        if (!this.calification.isEmpty()) {
            for (double calif : this.calification) {
                calif_avg += calif;
            }
            calif_avg /= this.calification.size();
        }

        return calif_avg;
    }
}

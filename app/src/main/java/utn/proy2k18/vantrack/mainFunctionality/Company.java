package utn.proy2k18.vantrack.mainFunctionality;

import java.util.ArrayList;

public class Company {

    private String bussinessName;
    private int cuit;
    private ArrayList<Double> calification;

    public Company(String bussinessName, int cuit) {
        this.bussinessName = bussinessName;
        this.cuit = cuit;
        this.calification = new ArrayList<>();
    }

    public String getCompanyName() { return this.bussinessName; }

    public int getCuit() { return this.cuit; }

    public void addCalification(double calif) {
        this.calification.add(calif);
    }

    public double getCalification() {
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

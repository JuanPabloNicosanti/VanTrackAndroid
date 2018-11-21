package utn.proy2k18.vantrack.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Company implements Serializable {

    @JsonProperty("company_id")
    private int companyId;
    @JsonProperty("business_name")
    private String bussinessName;
    @JsonProperty("cuit")
    private String cuit;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("rating")
    private float calification;

    private Company() { }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getBussinessName() { return this.bussinessName; }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public String getCuit() { return this.cuit; }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public float getCalification() {
        return calification;
    }

    public void setCalification(float calification) {
        this.calification = calification;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
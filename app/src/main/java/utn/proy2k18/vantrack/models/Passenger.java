package utn.proy2k18.vantrack.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.UUID;

public class Passenger implements Parcelable {

    private UUID uuid;
    private String id;
    private String name;
    private String surname;

    public Passenger(String name, String surname){
        this.name = name;
        this.surname = surname;
    }

    public String getName() { return this.name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() { return this.surname; }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNameAndSurname() {
        return name + " " + surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public static ArrayList<Passenger> createList(){
        ArrayList<Passenger> passengers = new ArrayList<>();
        passengers.add(new Passenger("Juan","DAmbrosio"));
        passengers.add(new Passenger("Martin","Battaglino"));
        passengers.add(new Passenger("Juan","Nicosanti"));
        passengers.add(new Passenger("Santiago","Franco Goyeneche"));
        return passengers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(surname);
    }

    protected Passenger(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
    }

    public static final Creator<Passenger> CREATOR = new Creator<Passenger>() {
        @Override
        public Passenger createFromParcel(Parcel in) {
            return new Passenger(in);
        }

        @Override
        public Passenger[] newArray(int size) {
            return new Passenger[size];
        }
    };
}

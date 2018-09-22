package utn.proy2k18.vantrack.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Passenger {

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

    public static List<Passenger> createList(){
        List<Passenger> passengers = new ArrayList<>();
        passengers.add(new Passenger("Juan","DAmbrosio"));
        passengers.add(new Passenger("Martin","Battaglino"));
        passengers.add(new Passenger("Juan","Nicosanti"));
        passengers.add(new Passenger("Santiago","Franco Goyeneche"));
        return passengers;
    }
}

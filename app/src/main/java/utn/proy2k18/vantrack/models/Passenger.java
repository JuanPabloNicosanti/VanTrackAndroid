package utn.proy2k18.vantrack.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Passenger implements Parcelable {

    private int userId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private boolean banned;

    public Passenger() { }

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

    public int getId() {
        return userId;
    }

    public void setId(int userId) {
        this.userId = userId;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeByte((byte) (banned ? 1 : 0));
    }

    protected Passenger(Parcel in) {
        userId = in.readInt();
        name = in.readString();
        surname = in.readString();
        email = in.readString();
        password = in.readString();
        banned = in.readByte() != 0;
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

package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utn.proy2k18.vantrack.Test.TestTrips;
import utn.proy2k18.vantrack.search.Trip;


public class TripsViewModel extends ViewModel {
    private final List<Trip> totalTrips = (new TestTrips().getTestTrips());
    private List<Trip> baseFilteredTrips;
    private List<Trip> filteredTripsByCompany;
    private List<Trip> filteredTripsByTime;

    public void init(){

    }

    public void filterBaseTrips(String argTripOrigin, String argTripDest, String argTripDate) {
        baseFilteredTrips = new ArrayList<>();

        for(Trip trip : totalTrips){
            if (trip.getDestination().equals(argTripDest) &&
                    trip.getOrigin().equals(argTripOrigin) &&
                    trip.getCalendarDate().equals(argTripDate)) {
                baseFilteredTrips.add(trip);
            }
        }
        filteredTripsByTime = baseFilteredTrips;
        filteredTripsByCompany = baseFilteredTrips;
    }

    public List<Trip> getBaseFilteredTrips() {
        return baseFilteredTrips;
    }

    public List<Trip> getFilteredTrips() {
        return intersection(filteredTripsByCompany, filteredTripsByTime);
    }

    public Trip getFilteredTripAtPosition(int position) {
        return getFilteredTrips().get(position);
    }

    private <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public void filterTripsByCompany(String companyName) {
        if (companyName != null) {
            filteredTripsByCompany = new ArrayList<>();

            for (Trip trip : baseFilteredTrips) {
                if (trip.getCompanyName().equals(companyName)) {
                    filteredTripsByCompany.add(trip);
                }
            }
        } else {
            filteredTripsByCompany = baseFilteredTrips;
        }
    }

    public void filterTripsByTime(int minValue, int maxValue) {
        if (maxValue < this.getTripsMaxTime() || minValue > this.getTripsMinTime()) {
            filteredTripsByTime = new ArrayList<>();

            for(Trip trip : baseFilteredTrips){
                if (trip.getTimeHour() >= minValue && trip.getTimeHour() <= maxValue) {
                    filteredTripsByTime.add(trip);
                }
            }
        } else {
            filteredTripsByTime = baseFilteredTrips;
        }
    }

    public int getTripsMaxTime() {
        int maxValue = 0;
        for(Trip trip : baseFilteredTrips) {
            if(trip.getTimeHour() > maxValue) {
                maxValue = trip.getTimeHour();
            }
        }
        return maxValue;
    }

    public int getTripsMinTime() {
        int minValue = 24;
        for(Trip trip : baseFilteredTrips) {
            if(trip.getTimeHour() < minValue) {
                minValue = trip.getTimeHour();
            }
        }
        return minValue;
    }

    public void sortTripsByPrice() {
        Collections.sort(filteredTripsByCompany, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return (int)(t1.getPrice() - t2.getPrice());
            }
        });
        Collections.sort(filteredTripsByTime, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return (int)(t1.getPrice() - t2.getPrice());
            }
        });
    }

    public void sortTripsByCompanyName() {
        Collections.sort(filteredTripsByCompany, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return Double.compare(t2.getCompanyCalification(),
                        t1.getCompanyCalification());
            }
        });
        Collections.sort(filteredTripsByTime, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return Double.compare(t2.getCompanyCalification(),
                        t1.getCompanyCalification());
            }
        });
    }
}

package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mainFunctionality.search.SearchResults;
import utn.proy2k18.vantrack.exceptions.NoReturnTripsException;
import utn.proy2k18.vantrack.exceptions.NoTripsException;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class TripsViewModel extends ViewModel {

    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_GET = "GET";
    private SearchResults totalTrips = null;
    private List<String> stopsDescriptions;
    private String argTripOriginHopOnStop;
    private String argTripDestinationHopOnStop;
    private String argTripHopOnStop;
    private List<Trip> activeTrips;
    private List<Trip> filteredTrips;
    private HashMap<String, String> searchedParams;
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");

    public List<String> getAllStops() {
        if (stopsDescriptions == null) {
            String url = queryBuilder.getAllStopsDescriptions();
            stopsDescriptions = backendMapper.mapListFromBackend(String.class, url, HTTP_GET);
        }
        return stopsDescriptions;
    }

    public List<Trip> getTrips(boolean returnSearch) {
        activeTrips = returnSearch ? totalTrips.getInboundTrips() : totalTrips.getOutboundTrips();
        argTripHopOnStop = returnSearch ? argTripDestinationHopOnStop : argTripOriginHopOnStop;
        filteredTrips = activeTrips;
        return activeTrips;
    }

    public List<Trip> getTrips(String companyName, Integer minValue, Integer maxValue,
                               String sortOption) {
        filteredTrips = filterTripsByCompany(activeTrips, companyName);
        filteredTrips = filterTripsByTime(filteredTrips, minValue, maxValue);
        sortTripsBySpinnerOption(sortOption);
        return filteredTrips;
    }

    public void fetchTrips(String origin, String destination, String goingDate, String returnDate) {
        argTripOriginHopOnStop = origin;
        argTripDestinationHopOnStop = destination;

        HashMap<String, String> newSearchParams = new HashMap<>();
        newSearchParams.put("origin", formatStop(origin));
        newSearchParams.put("destination", formatStop(destination));
        newSearchParams.put("going_date", formatDate(goingDate));
        if (returnDate != null) {
            newSearchParams.put("return_date", formatDate(returnDate));
        }

        if (!newSearchParams.equals(searchedParams)) {
            searchedParams = newSearchParams;
            String url = queryBuilder.getTripsQuery(newSearchParams);
            totalTrips = backendMapper.mapObjectFromBackend(SearchResults.class, url, HTTP_GET);
            checkTotalTrips();
        }
    }

    private void checkTotalTrips() {
        if (totalTrips.getOutboundTrips() == null) {
            throw new NoTripsException();
        } else {
            if (!hasReturnTrips()) {
                throw new NoReturnTripsException();
            }
        }
    }

    public boolean isReturnSearch() {
        return activeTrips.equals(totalTrips.getInboundTrips());
    }

    public boolean hasReturnTrips() {
        return totalTrips.getInboundTrips() != null;
    }

    private String formatStop(String stop) {
        return stop.replaceAll(" ", "+");
    }

    private String formatDate(String strDate) {
        LocalDate date = dtf.parseLocalDate(strDate);
        return date.toString().replaceAll("-", "");
    }

    public String getArgTripHopOnStop() {
        return argTripHopOnStop;
    }

    public Trip getFilteredTripAtPosition(int position) {
        return filteredTrips.get(position);
    }

    public List<Trip> getFilteredTrips() {
        return filteredTrips;
    }

    public List<String> getTripsCompanies() {
        List<String> companiesNames = new ArrayList<>();
        companiesNames.add("Todas");
        for(Trip trip: activeTrips) {
            if (!companiesNames.contains(trip.getCompanyName())) {
                companiesNames.add(trip.getCompanyName());
            }
        }
        return companiesNames;
    }

    private List<Trip> filterTripsByCompany(List<Trip> trips, String companyName) {
        List<Trip> filteredTripsByCompany = new ArrayList<>();
        if (!companyName.equalsIgnoreCase("todas")) {
            for (Trip trip : trips) {
                if (trip.getCompanyName().equalsIgnoreCase(companyName)) {
                    filteredTripsByCompany.add(trip);
                }
            }
        } else {
            filteredTripsByCompany = trips;
        }
        return filteredTripsByCompany;
    }

    private List<Trip> filterTripsByTime(List<Trip> trips, int minValue, int maxValue) {
        List<Trip> filteredTripsByTime = new ArrayList<>();
        if (maxValue < this.getTripsMaxTime(trips) || minValue > this.getTripsMinTime(trips)) {
            for(Trip trip : trips){
                if (trip.getTimeHour() >= minValue && trip.getTimeHour() <= maxValue) {
                    filteredTripsByTime.add(trip);
                }
            }
        } else {
            filteredTripsByTime = trips;
        }
        return filteredTripsByTime;
    }

    public int getTripsMaxTime(List<Trip> trips) {
        int maxValue = 0;
        for(Trip trip : trips) {
            if(trip.getTimeHour() > maxValue) {
                maxValue = trip.getTimeHour();
            }
        }
        return maxValue;
    }

    public int getTripsMinTime(List<Trip> trips) {
        int minValue = 24;
        for(Trip trip : trips) {
            if(trip.getTimeHour() < minValue) {
                minValue = trip.getTimeHour();
            }
        }
        return minValue;
    }

    public void sortTripsBySpinnerOption(String spinnerOption) {
        switch (spinnerOption) {
            case "Precio":
                sortTripsByPrice();
                break;
            case "Calificacion de la empresa":
                sortTripsByCompanyCalification();
                break;
            case "Duracion":
                sortTripsByDuration();
                break;
            case "Hora de salida":
                sortTripsByDepartureTime();
                break;
        }
    }

    private void sortTripsByDuration() {
        Collections.sort(filteredTrips, new Comparator<Trip>() {
            @Override
            public int compare(Trip trip1, Trip trip2) {
                return getTripDurationBetweenStops(trip1) - getTripDurationBetweenStops(trip2);
            }
        });
    }

    private Integer getTripDurationBetweenStops(Trip trip) {
        LocalTime originTime = getTripStopTimeByDescription(trip, argTripOriginHopOnStop);
        LocalTime destTime = getTripStopTimeByDescription(trip, argTripDestinationHopOnStop);
        return Minutes.minutesBetween(originTime, destTime).getMinutes();
    }

    private void sortTripsByDepartureTime() {
        Collections.sort(filteredTrips, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return getTripStopTimeByDescription(t1, argTripOriginHopOnStop).compareTo(
                        getTripStopTimeByDescription(t2, argTripOriginHopOnStop));
            }
        });
    }

    private LocalTime getTripStopTimeByDescription(Trip trip, String tripStopDescription) {
        return trip.getTripStopByDescription(tripStopDescription).getHour();
    }

    private void sortTripsByPrice() {
        Collections.sort(filteredTrips, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return (int)(t1.getPrice() - t2.getPrice());
            }
        });
    }

    private void sortTripsByCompanyCalification() {
        Collections.sort(filteredTrips, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return Double.compare(t2.getCompanyCalification(),
                        t1.getCompanyCalification());
            }
        });
    }
}

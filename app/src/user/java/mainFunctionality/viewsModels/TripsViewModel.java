package mainFunctionality.viewsModels;

import androidx.lifecycle.ViewModel;

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

    private static QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_GET = "GET";
    private static TripsViewModel viewModel;

    private SearchResults totalTrips = null;
    private List<String> stopsDescriptions;
    private List<Trip> activeTrips;
    private List<Trip> filteredTrips;
    private HashMap<String, String> searchedParams;
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    private DateTimeFormatter dtfBack = DateTimeFormat.forPattern("yyyyMMdd");


    public static TripsViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new TripsViewModel();
        }
        return viewModel;
    }

    public HashMap<String, String> getSearchedParams() {
        return searchedParams;
    }

    public String getSearchedOrigin() {
        return this.formatStopBack(this.searchedParams.get("origin"));
    }

    public String getSearchedDestination() {
        return this.formatStopBack(this.searchedParams.get("destination"));
    }

    public String getSearchedGoingDate() {
        return this.formatDateBack(this.searchedParams.get("going_date"));
    }

    public String getSearchedReturnDate() {
        if (this.searchedParams.containsKey("return_date")) {
            return this.formatDateBack(this.searchedParams.get("return_date"));
        }
        return null;
    }

    public List<String> getAllStops() {
        if (stopsDescriptions == null) {
            String url = queryBuilder.getAllStopsDescriptions();
            stopsDescriptions = backendMapper.mapListFromBackend(String.class, url, HTTP_GET);
        }
        return stopsDescriptions;
    }

    public List<Trip> getTrips(boolean returnSearch) {
        activeTrips = returnSearch ? totalTrips.getInboundTrips() : totalTrips.getOutboundTrips();
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
        searchedParams = new HashMap<>();
        searchedParams.put("origin", formatStop(origin));
        searchedParams.put("destination", formatStop(destination));
        searchedParams.put("going_date", formatDate(goingDate));
        if (returnDate != null) {
            searchedParams.put("return_date", formatDate(returnDate));
        }

        String url = queryBuilder.getTripsQuery(searchedParams);
        totalTrips = backendMapper.mapObjectFromBackend(SearchResults.class, url, HTTP_GET);
        checkTotalTrips(searchedParams);
    }

    private void checkTotalTrips(HashMap<String, String> newSearchParams) {
        if (totalTrips.getOutboundTrips() == null) {
            throw new NoTripsException();
        } else {
            if (!hasReturnTrips() && newSearchParams.containsKey("return_date")) {
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

    private String formatStopBack(String stop) {
        return stop.replaceAll("\\+", " ");
    }

    private String formatDate(String strDate) {
        LocalDate date = dtf.parseLocalDate(strDate);
        return date.toString().replaceAll("-", "");
    }

    private String formatDateBack(String strDate) {
        LocalDate date = dtfBack.parseLocalDate(strDate);
        return date.toString(dtf);
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
        LocalTime originTime = getTripStopTimeByDescription(trip, this.getSearchedOrigin());
        LocalTime destTime = getTripStopTimeByDescription(trip, this.getSearchedDestination());
        return Minutes.minutesBetween(originTime, destTime).getMinutes();
    }

    private void sortTripsByDepartureTime() {
        Collections.sort(filteredTrips, new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                LocalTime t1Time = getTripStopTimeByDescription(t1, getSearchedOrigin());
                LocalTime t2Time = getTripStopTimeByDescription(t2, getSearchedOrigin());
                return t1Time.compareTo(t2Time);
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

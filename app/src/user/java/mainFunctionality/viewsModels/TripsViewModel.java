package mainFunctionality.viewsModels;

import android.arch.lifecycle.ViewModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mainFunctionality.search.SearchResults;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.VanTrackApplication;
import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;


public class TripsViewModel extends ViewModel {

    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_GET = "GET";
    private SearchResults totalTrips = null;
    private String argTripOriginHopOnStop;
    private String argTripDestinationHopOnStop;
    private List<Trip> activeTrips;
    private List<Trip> filteredTripsByCompany;
    private List<Trip> filteredTripsByTime;
    private HashMap<String, String> searchedParams;
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    private DecimalFormat df = new DecimalFormat("00");

    public List<Trip> getTrips(String origin, String destination, String goingDate,
                               String returnDate, boolean isReturnSearch) {
        if (!isReturnSearch) {
            setArgTripOriginHopOnStop(origin);
            setArgTripDestinationHopOnStop(destination);
            HashMap<String, String> newSearchParams = createSearchParams(origin, destination,
                    goingDate, returnDate);
            if (!newSearchParams.equals(searchedParams)) {
                searchedParams = newSearchParams;
                String url = queryBuilder.getTripsQuery(newSearchParams);
                totalTrips = getTripsFromBack(url);
            }
        }

        if (totalTrips.getInboundTrips() != null || totalTrips.getOutboundTrips() != null) {
            activeTrips = isReturnSearch ? totalTrips.getInboundTrips() : totalTrips.getOutboundTrips();
        } else {
            activeTrips = new ArrayList<>();
        }

        filteredTripsByCompany = activeTrips;
        filteredTripsByTime = activeTrips;
        return activeTrips;
    }

    private HashMap<String, String> createSearchParams(String origin, String destination,
                                                       String goingDate, String returnDate) {
        HashMap<String, String> newSearchParams = new HashMap<>();
        newSearchParams.put("origin", origin.replace(" ", "+"));
        newSearchParams.put("destination", destination.replace(" ", "+"));
        newSearchParams.put("going_date", formatDate(goingDate));

        if (!returnDate.equals(VanTrackApplication.getContext().getString(R.string.no_return_date))) {
            newSearchParams.put("return_date", formatDate(returnDate));
        }
        return newSearchParams;
    }

    private SearchResults getTripsFromBack(String url){
        final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
        try{
            String result = HTTP_CONNECTOR.execute(url, HTTP_GET).get();
            TypeReference searchResultsType = new TypeReference<SearchResults>(){};
            return objectMapper.readValue(result, searchResultsType);
        } catch (ExecutionException ee){
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return new SearchResults();
    }

    private String formatDate(String strDate) {
        LocalDate date = dtf.parseLocalDate(strDate);
        return date.toString();
    }

    public void init(){ }

    public String getArgTripOriginHopOnStop() {
        return argTripOriginHopOnStop;
    }

    public void setArgTripOriginHopOnStop(String argTripOriginHopOnStop) {
        this.argTripOriginHopOnStop = argTripOriginHopOnStop;
    }

    public String getArgTripDestinationHopOnStop() {
        return argTripDestinationHopOnStop;
    }

    public void setArgTripDestinationHopOnStop(String argTripDestinationHopOnStop) {
        this.argTripDestinationHopOnStop = argTripDestinationHopOnStop;
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

            for (Trip trip : activeTrips) {
                if (trip.getCompanyName().equals(companyName)) {
                    filteredTripsByCompany.add(trip);
                }
            }
        } else {
            filteredTripsByCompany = activeTrips;
        }
    }

    public void filterTripsByTime(int minValue, int maxValue) {
        if (maxValue < this.getTripsMaxTime() || minValue > this.getTripsMinTime()) {
            filteredTripsByTime = new ArrayList<>();

            for(Trip trip : activeTrips){
                if (trip.getTimeHour() >= minValue && trip.getTimeHour() <= maxValue) {
                    filteredTripsByTime.add(trip);
                }
            }
        } else {
            filteredTripsByTime = activeTrips;
        }
    }

    public int getTripsMaxTime() {
        int maxValue = 0;
        for(Trip trip : getFilteredTrips()) {
            if(trip.getTimeHour() > maxValue) {
                maxValue = trip.getTimeHour();
            }
        }
        return maxValue;
    }

    public int getTripsMinTime() {
        int minValue = 24;
        for(Trip trip : getFilteredTrips()) {
            if(trip.getTimeHour() < minValue) {
                minValue = trip.getTimeHour();
            }
        }
        return minValue;
    }

    public void sortTripsByPrice() {
        Collections.sort(getFilteredTrips(), new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return (int)(t1.getPrice() - t2.getPrice());
            }
        });
    }

    public void sortTripsByCompanyName() {
        Collections.sort(getFilteredTrips(), new Comparator<Trip>() {
            @Override
            public int compare(final Trip t1, final Trip t2) {
                return Double.compare(t2.getCompanyCalification(),
                        t1.getCompanyCalification());
            }
        });
    }
}

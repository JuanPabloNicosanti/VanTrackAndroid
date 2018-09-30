package mainFunctionality.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;

public class SearchResults {

    @JsonProperty("outbound_trips")
    private List<Trip> outboundTrips;
    @JsonProperty("inbound_trips")
    private List<Trip> inboundTrips;

    public SearchResults() { }

    public List<Trip> getOutboundTrips() {
        return outboundTrips;
    }

    public void setOutboundTrips(List<Trip> outboundTrips) {
        this.outboundTrips = outboundTrips;
    }

    public List<Trip> getInboundTrips() {
        return inboundTrips;
    }

    public void setInboundTrips(List<Trip> inboundTrips) {
        this.inboundTrips = inboundTrips;
    }
}

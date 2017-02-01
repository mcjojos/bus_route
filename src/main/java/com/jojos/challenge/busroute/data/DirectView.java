package com.jojos.challenge.busroute.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * The return JSON object of the controller
 *
 * @author karanikasg@gmail.com.
 */
public class DirectView {

    private final int departure;
    private final int arrival;
    private final boolean directBusRoute;

    @JsonCreator
    public DirectView(int departure, int arrival, boolean directBusRoute) {
        this.departure = departure;
        this.arrival = arrival;
        this.directBusRoute = directBusRoute;
    }

    @JsonGetter("dep_sid")
    public int getDeparture() {
        return departure;
    }

    @JsonGetter("arr_sid")
    public int getArrival() {
        return arrival;
    }

    @JsonGetter("direct_bus_route")
    public boolean isDirectBusRoute() {
        return directBusRoute;
    }
}

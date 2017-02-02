package com.jojos.challenge.busroute.service;

import java.util.LinkedHashSet;

/**
 * An interface to mark the means of storing the route information.
 *
 * We could use some kind of directed acyclic graph structure, but for the time being we
 * only need to know the order of the station ids for a particular route and that's exactly
 * the reason we are using a LinkedHashSet
 *
 * @author karanikasg@gmail.com.
 */
public interface RouteStore {

    /**
     * Store a route identified by its route id and an ordered set of station ids.
     *
     * @param routeId, uniquely identify the route
     * @param stationIds the ordered set of station ids
     */
    default void storeRoute(int routeId, LinkedHashSet<Integer> stationIds) {}

    /**
     * The total number of routes that have been stored in the data store
     * @return the total number of stored routes
     */
    default int getTotalNumberOfRoutes() {
        return 0;
    }

    /**
     * The total number of distinct stations that are found stored in the data store
     * @return the total number of unique stations
     */
    default int getTotalNumberOfStations() {
        return 0;
    }

    /**
     * If a direct connection between the two stations exists then return true.
     * If it doesn't then return false
     * @param stationId1 the first station
     * @param stationId2 station number two
     * @return true or false depending on whether or not a direct connection between two stations exists
     */
    default boolean isDirectConnectionExistBetween(int stationId1, int stationId2) {
        return false;
    }


}

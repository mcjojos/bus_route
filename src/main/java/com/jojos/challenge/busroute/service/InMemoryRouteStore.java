package com.jojos.challenge.busroute.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.jojos.challenge.busroute.util.RouteUtils.addToContainedSet;

/**
 * Implementation of {@link RouteStore} that holds all of the route information in memory
 * Optimized for fast resolution of the following answer:
 * Does a direct connection exist between two stations at any given route?
 *
 * @author karanikasg@gmail.com.
 */
public class InMemoryRouteStore implements RouteStore {

    // eah entry contains a routeId as the key with an ordered set of stations (order matters)
    private final ConcurrentMap<Integer, LinkedHashSet<Integer>> routesToStations = new ConcurrentHashMap<>();

    // orthogonal equivalent after transforming the previous collection. Here each key is the station with a set of routeIds
    private final ConcurrentMap<Integer, Set<Integer>> stationsToRoutes = new ConcurrentHashMap<>();

    @Override
    public void storeRoute(int routeId, LinkedHashSet<Integer> stationIds) {
        stationIds.forEach(stationId -> addToContainedSet(stationsToRoutes, stationId, routeId));
        routesToStations.put(routeId, stationIds);
    }

    @Override
    public int getTotalNumberOfRoutes() {
        return routesToStations.size();
    }

    @Override
    public int getTotalNumberOfStations() {
        return stationsToRoutes.size();
    }

    @Override
    public boolean isDirectConnectionExistBetween(int departure, int arrival) {
        Set<Integer> routeIds1 = stationsToRoutes.get(departure);
        Set<Integer> routeIds2 = stationsToRoutes.get(arrival);
        if (routeIds1 == null || routeIds2 == null) {
            // at least one of the provided stations does not exist in any of the routes
            return false;
        }

        Set<Integer> intersection = new HashSet<>(routeIds1);
        intersection.retainAll(routeIds2);

        // go and chase the order from 1 -> 2 in the ordered collection
        for (Integer routeId : intersection) {
            LinkedHashSet<Integer> stations = routesToStations.get(routeId);
            if (connectionFromAToBExistsIn(stations, departure, arrival)) {
                return true;
            }
        }

        return false;
    }

    private boolean connectionFromAToBExistsIn(LinkedHashSet<Integer> stations, int departure, int arrival) {
        for (int station : stations) {
            if (station == departure) {
                return true;
            }
            if (station == arrival) {
                return false;
            }
        }
        return false;
    }
}

package com.jojos.challenge.busroute.util;

import com.jojos.challenge.busroute.service.InMemoryRouteStore;
import com.jojos.challenge.busroute.service.RouteStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author karanikasg@gmail.com.
 */
public class RouteUtils {

    private static final Logger log = LoggerFactory.getLogger(RouteUtils.class);


    public static final int MAX_BUS_ROUTES = 100_000;
    public static final int MAX_STATIONS = 1_000_000;
    public static final int MAX_STATIONS_PER_ROUTE = 1_000;

    /**
     * Loads the file and returns a supplier of a stream of strings representing the lines in the file.
     * Perhaps not the most elegant solution but the reason for that is that we want to reuse the Stream in
     * later consecutive validations.
     *
     * Should an {@link IOException} occur or the file does not exist in the path or is not a regular file
     * a {@link Supplier} of an empty {@link Stream} shall be returned.
     *
     * @param filePath the path under which the file shall be loaded from
     * @return a supplier of stream
     */
    public static Supplier<Stream<String>> loadFile(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return () -> {
                try {
                    return Files.lines(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Stream.empty();
            };
        }
        return Stream::empty;
    }

    public static RouteStore loadRoutesFromFileAndValidate(String filePath) {
        Supplier<Stream<String>> stream = RouteUtils.loadFile(filePath);
        return loadRoutesFromStreamAndValidate(stream);
    }

    /**
     * For the time being these are the following validations:
     * 1. the first line must contain a number representing the number of N routes contained in the file
     * 2. Assume 100,000 as upper limit for the number of bus routes
     * 3. The remaining lines must be exactly N (except empty ones)
     * 4. For each bus route there will be one line containing a space separated list of integers. This list contains at least three integers
     * 5. On a route line the first integer represents the bus route id. The bus route id must be unique among all other bus route ids in the input
     * 6. A station id can never occur twice within the same bus route
     * 7. Assume 1,000 as upper limit for the number of stations of one bus route
     * 8. 1,000,000 as upper limit for the number of stations
     *
     */
    static RouteStore loadRoutesFromStreamAndValidate(Supplier<Stream<String>> stream) {
        String firstElement = stream.get().findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid first element"));

        // rule 1
        int routeCount = Integer.parseInt(firstElement);

        // rule 2
        throwIf(routeCount > MAX_BUS_ROUTES, String.format("Number of bus routes %d larger than expected %d", routeCount, MAX_BUS_ROUTES));

        RouteStore routeStore = new InMemoryRouteStore();
        log.info("Start to load file into memory");

        stream.get().
                filter(s -> !s.isEmpty()).
                skip(1).
                parallel().
                map(s -> s.split("\\s+")).
                forEach(strings -> {
                    // rule 4
                    throwIf(strings.length < 3, "Each bus route must have at least 3 integers");

                    int routeId = Integer.parseInt(strings[0]);
                    LinkedHashSet<Integer> stationIds = Arrays.stream(strings).
                            skip(1).
                            map(Integer::parseInt).
                            collect(Collectors.toCollection(LinkedHashSet::new));
                    // rule 6
                    throwIf(stationIds.size() != strings.length - 1, "Duplicate stations per route found");
                    // rule 7
                    throwIf(stationIds.size() > MAX_STATIONS_PER_ROUTE, "Upper limit for the number of stations in one bus route exceeded");
                    routeStore.storeRoute(routeId, stationIds);
                });

        // rule 3 & 5
        throwIf(routeStore.getTotalNumberOfRoutes() != routeCount, "Route ids are not unique among all other bus routes ids in the input OR number of lines differs");
        // rule 8
        throwIf(routeStore.getTotalNumberOfStations() > MAX_STATIONS, "Upper limit for the total number of stations on all routes exceeded");

        return routeStore;
    }

    private static void throwIf(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <K, V> boolean addToContainedSet(ConcurrentMap<K, Set<V>> map, K mapKey, V value) {
        Set<V> existingSet = map.get(mapKey);
        if (existingSet == null) {
            Set<V> newSet = Collections.newSetFromMap(new ConcurrentHashMap<V, Boolean>());
            existingSet = map.putIfAbsent(mapKey, newSet);
            if (existingSet == null) {
                existingSet = newSet;
            }
        }
        return existingSet.add(value);
    }

}

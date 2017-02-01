package com.jojos.challenge.busroute.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jojos.challenge.busroute.util.RouteUtils.MAX_BUS_ROUTES;
import static com.jojos.challenge.busroute.util.RouteUtils.MAX_STATIONS;
import static com.jojos.challenge.busroute.util.RouteUtils.MAX_STATIONS_PER_ROUTE;

/**
 * @author karanikasg@gmail.com.
 */
public class ImMemoryRouteStoreTest {

    private final Random random = new Random();

    @Test
    public void testAddSomeRoutes() {
        int some = 1_000;
        RouteStore routeStore = randomStoreOfSize(some);
        Assert.assertEquals(some, routeStore.getTotalNumberOfRoutes());
    }

    @Test
    public void testAddAFewRoutes() {
        int aFew = MAX_BUS_ROUTES / 4;
        RouteStore routeStore = randomStoreOfSize(aFew);
        Assert.assertEquals(aFew, routeStore.getTotalNumberOfRoutes());
    }

//    @Test
    public void testAddMaxAllowedRoutes() {
        // might take some time
        RouteStore routeStore = randomStoreOfSize(MAX_BUS_ROUTES);
        Assert.assertEquals(MAX_BUS_ROUTES, routeStore.getTotalNumberOfRoutes());
    }

    @Test
    public void testDirectRoute() {
        int start = 0;
        int end = 140;
        RouteStore routeStore = routesStoreBetween(start, end, MAX_BUS_ROUTES / 100);

        Assert.assertTrue(routeStore.isDirectConnectionExistBetween(start, end - 1));
        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(start, end));
        Assert.assertTrue(routeStore.isDirectConnectionExistBetween(start + 50, end - 50));

        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(end-1, start));
        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(end, start));
        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(end - 50, start + 50));

    }

    @Test
    public void testDirectRouteForBiggerRange() {
        int start = 500_000;
        int end = 501_000;
        RouteStore routeStore = routesStoreBetween(start, end, MAX_BUS_ROUTES / 10);

        Assert.assertTrue(routeStore.isDirectConnectionExistBetween(start, end - 1));
        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(start, end));
        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(start, start - 1));
        Assert.assertTrue(routeStore.isDirectConnectionExistBetween(start + 100, end - 100));

        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(end-1, start));
        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(end, start));
        Assert.assertFalse(routeStore.isDirectConnectionExistBetween(end - 50, start + 50));

    }


    private RouteStore routesStoreBetween(int start, int end, int size) {
        RouteStore routeStore = new InMemoryRouteStore();
        IntStream routeIds = IntStream.range(0, size);
        routeIds.forEach(routeId -> {
            IntStream stations = IntStream.range(start, end);
            LinkedHashSet<Integer> stationIds = stations.mapToObj(value -> value).collect(Collectors.toCollection(LinkedHashSet::new));
            routeStore.storeRoute(routeId, stationIds);
        });
        return routeStore;
    }


    private RouteStore randomStoreOfSize(int routesSize) {
        RouteStore routeStore = new InMemoryRouteStore();
        IntStream routeIds = ThreadLocalRandom.current().ints(0, MAX_BUS_ROUTES).distinct().limit(routesSize);
        routeIds.forEach(routeId -> {
            IntStream stations = ThreadLocalRandom.current().ints(0, MAX_STATIONS).distinct().limit(random.nextInt(MAX_STATIONS_PER_ROUTE));
            LinkedHashSet<Integer> stationIds = stations.mapToObj(value -> value).collect(Collectors.toCollection(LinkedHashSet::new));
            routeStore.storeRoute(routeId, stationIds);
        });
        return routeStore;
    }
}

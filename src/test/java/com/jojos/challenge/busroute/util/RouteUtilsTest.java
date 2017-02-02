package com.jojos.challenge.busroute.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class that tests all the possible rule violetions
 * @author karanikasg@gmail.com.
 */
@RunWith(PowerMockRunner.class)
public class RouteUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(RouteUtilsTest.class);

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testInvalidEmptyFirstLine() throws Exception {
        thrown.expect(NumberFormatException.class);
        thrown.expectMessage("For input string: \"\"");
        Supplier<Stream<String>> stream = getStreamFor("");
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    @Test
    public void testInvalidNumberFirstLine() throws Exception {
        thrown.expect( NumberFormatException.class );
        Supplier<Stream<String>> stream = getStreamFor("not a number");
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    @Test
    public void testLargerThanExpectedNumberOfRoutes() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Number of bus routes 100001 larger than expected 100000");
        Supplier<Stream<String>> stream = getStreamFor("100001");
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    @Test
    public void testInvalidNumberOfLines() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("number of lines differs");
        Supplier<Stream<String>> stream = getStreamFor("5", "1 3 2", "2 3 2", "3 3 2", "4 3 2");
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    @Test
    public void testAtLeastThreeIntegersPerRoute() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Each bus route must have at least 3 integers");
        Supplier<Stream<String>> stream = getStreamFor("4", "0 1 2", "2 1 2", "3 1 2", "0 2");
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }


    @Test
    public void testNotUniqueRoutes() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Route ids are not unique among all other bus routes ids in the input");
        Supplier<Stream<String>> stream = getStreamFor("4", "0 1 2", "2 1 2", "3 1 2", "0 1 2");
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    @Test
    public void testNotUniqueStations() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate stations per route found");
        Supplier<Stream<String>> stream = getStreamFor("5", "0 1 2", "2 1 2", "3 1 2", "4 1 2", "5 1 2 1");
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    @Test
    public void testInvalidUpperLimitOfStationsPerRoute() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Upper limit for the number of stations in one bus route exceeded");
        IntStream intStream = IntStream.range(0, RouteUtils.MAX_STATIONS_PER_ROUTE + 2);
        String stationIdsStr = intStream.boxed().map(String::valueOf).collect(Collectors.joining(" "));
        Supplier<Stream<String>> stream = getStreamFor("1", stationIdsStr);
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    @Test
    public void testInvalidUpperLimitOfTotalStations() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Upper limit for the total number of stations on all routes exceeded");
        int routes = 1001;
        int stationsPerRoute = 1000;
        String[] rows = produceRoutesXByY(stationsPerRoute, routes);
        Supplier<Stream<String>> stream = getStreamFor(rows);
        RouteUtils.loadRoutesFromStreamAndValidate(stream);
    }

    private String[] produceRoutesXByY(int stations, int routes) {
        String[] rows = new String[routes + 1];
        rows[0] = String.valueOf(routes);
        int stationId = 0;
        for (int i = 1; i <= routes; i++) {
            IntStream intStream = IntStream.range(stationId, stationId + stations);
            String stationIdsStr = i + " " + intStream.boxed().map(String::valueOf).collect(Collectors.joining(" "));
            rows[i] = stationIdsStr;
            stationId += stations;
        }

        return rows;
    }

    // commenting this time consuming test
//    @Test
    public void loadBigRouteFile() throws IOException {
        String fileName = "examples/small_route";
        try {
            produceBigFile(fileName);

            Instant start = Instant.now();

            Supplier<Stream<String>> supplier = RouteUtils.loadFile(fileName);
            RouteUtils.loadRoutesFromStreamAndValidate(supplier);

            Instant end = Instant.now();
            log.info("Validating big file took {} ", Duration.between(start, end).toString());
        } finally {
            deleteBigFile(fileName);
        }

    }

    // here we are producing the biggest file possible according to the specs (= 100K max routes and 1K max stations)
    private static void produceBigFile(String fileName) throws IOException {
        File fout = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(fout); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {

            int numberOfRoutes = RouteUtils.MAX_BUS_ROUTES;

            IntStream routeIdStream = IntStream.range(0, numberOfRoutes);

            IntStream intStream = IntStream.range(0, RouteUtils.MAX_STATIONS_PER_ROUTE);
            String something = intStream.boxed().map(String::valueOf).collect(Collectors.joining(" "));

            bw.write(String.valueOf(numberOfRoutes));
            bw.newLine();

            routeIdStream.boxed().forEach(integer -> {
                try {
                    bw.write(String.valueOf(integer) + " ");
                    bw.write(something);
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void deleteBigFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }



    private Supplier<Stream<String>> getStreamFor(String... testData) {
        return () -> Arrays.stream(testData);
    }

}

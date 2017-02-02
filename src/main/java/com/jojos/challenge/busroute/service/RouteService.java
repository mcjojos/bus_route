package com.jojos.challenge.busroute.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.jojos.challenge.busroute.util.RouteUtils.loadFile;
import static com.jojos.challenge.busroute.util.RouteUtils.loadRoutesFromStreamAndValidate;

/**
 * A component that will complete (i.e. the {@link #run(ApplicationArguments) method will be called}
 * just before the {@link org.springframework.boot.SpringApplication#run(Object, String...)} completes.
 *
 * The class will try to parse the route table information that is stored in a file which
 * is given as the first command line argument.
 *
 * @author karanikasg@gmail.com.
 */
@Service
public class RouteService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RouteService.class);

    private RouteStore routeStore;

    @Override
    public void run(ApplicationArguments args) throws IOException {

        List<String> nonOptionArgs = args.getNonOptionArgs();

        if (nonOptionArgs == null || nonOptionArgs.isEmpty()) {
            log.error("Houston we have a problem, no input bus route data file defined... Returning always false");
            routeStore = new RouteStore() {};
        } else {
            String filePath = nonOptionArgs.get(0);
            log.info("Loading route map from {}", filePath);

            routeStore = loadRoutesFromFileAndValidate(filePath);
        }
    }

    public RouteStore loadRoutesFromFileAndValidate(String filePath) {
        Supplier<Stream<String>> stream = loadFile(filePath);
        return loadRoutesFromStreamAndValidate(stream);
    }

    /**
     * Returns the answer to the question:
     * Is there any route in the bus route data from point A to point B?
     *
     * @param departure start bus station
     * @param arrival ebd bus station
     * @return true or false depending on whether the route exists
     */
    public boolean isThereConnectionBetween(int departure, int arrival) {
        return routeStore.isDirectConnectionExistBetween(departure, arrival);
    }

}

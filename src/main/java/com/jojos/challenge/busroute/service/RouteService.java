package com.jojos.challenge.busroute.service;

import com.jojos.challenge.busroute.util.RouteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
            throw new IllegalArgumentException("No input bus route data file defined... exiting");
        }
        String filePath = nonOptionArgs.get(0);

        log.info("Loading route map from {}", filePath);

        routeStore = RouteUtils.loadRoutesFromFileAndValidate(filePath);

    }

    public boolean isThereConnectionBetween(int departure, int arrival) {
        return routeStore.isDirectConnectionExistBetween(departure, arrival);
    }

}

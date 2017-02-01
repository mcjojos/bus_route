package com.jojos.challenge.busroute.service;

import com.jojos.challenge.busroute.util.RouteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * A component that will complete (i.e. the {@link #run(ApplicationArguments) method will be called}
 * just before the {@link org.springframework.boot.SpringApplication#run(Object, String...)} completes.
 *
 * The class will try to parse the route table information that is stored in a file which
 * is given as the first command line argument. If the command line argument is omitted it will default
 * to the property defined in application.properties.
 *
 * @author karanikasg@gmail.com.
 */
@Service
public class RouteService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RouteService.class);

    // although we could use ApplicationArguments's methods to extract the file given in the command line
    // we are using the properties style to get a seamless default, defined at application.properties
    @Value("${file}")
    private String filePath;

    private RouteStore routeStore;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        log.info("Loading route map from {}", filePath);

        routeStore = RouteUtils.loadRoutesFromFileAndValidate(filePath);

    }

    public boolean isThereConnectionBetween(int departure, int arrival) {
        return routeStore.isDirectConnectionExistBetween(departure, arrival);
    }

}

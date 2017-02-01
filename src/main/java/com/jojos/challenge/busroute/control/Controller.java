package com.jojos.challenge.busroute.control;

import com.jojos.challenge.busroute.data.DirectView;
import com.jojos.challenge.busroute.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The main (and only) controller
 *
 * @author karanikasg@gmail.com.
 */
@RestController
@RequestMapping("/api/direct")
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private final RouteService routeService;

    @Autowired
    public Controller(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public DirectView isDirect(@RequestParam("dep_sid") int departure, @RequestParam("arr_sid") int arrival) {
        boolean exist = routeService.isThereConnectionBetween(departure, arrival);
        log.info(String.format("Connection between %d and %d %sexists", departure, arrival, !exist ? "doesn't " : ""));
        return new DirectView(departure, arrival, exist);
    }
}

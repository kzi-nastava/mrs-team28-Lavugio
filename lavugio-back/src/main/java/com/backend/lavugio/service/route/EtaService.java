package com.backend.lavugio.service.route;
import com.backend.lavugio.model.route.RouteTimeEstimation;

import java.io.IOException;

public interface EtaService {
    RouteTimeEstimation calculateEta(double startLon, double startLat,
                                     double endLon, double endLat)
            throws IOException, InterruptedException;
}

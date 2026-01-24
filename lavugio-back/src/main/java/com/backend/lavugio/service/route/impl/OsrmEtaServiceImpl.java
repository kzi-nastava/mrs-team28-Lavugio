package com.backend.lavugio.service.route.impl;

import com.backend.lavugio.model.route.RouteTimeEstimation;
import com.backend.lavugio.service.route.EtaService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class OsrmEtaServiceImpl implements EtaService {

    private final String osrmBaseUrl;
    private final HttpClient httpClient;

    public OsrmEtaServiceImpl() {
        this("http://router.project-osrm.org");
    }

    public OsrmEtaServiceImpl(String osrmBaseUrl) {
        this.osrmBaseUrl = osrmBaseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public RouteTimeEstimation calculateEta(double startLon, double startLat,
                                            double endLon, double endLat)
            throws IOException, InterruptedException {

        String url = String.format("%s/route/v1/driving/%f,%f;%f,%f?overview=false",
                osrmBaseUrl, startLon, startLat, endLon, endLat);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("OSRM API returned status: " + response.statusCode());
        }

        return parseResponse(response.body());
    }

    private RouteTimeEstimation parseResponse(String responseBody) throws IOException {
        Pattern codePattern = Pattern.compile("\"code\"\\s*:\\s*\"([^\"]+)\"");
        Matcher codeMatcher = codePattern.matcher(responseBody);

        if (!codeMatcher.find() || !"Ok".equals(codeMatcher.group(1))) {
            throw new IOException("OSRM API error or no route found");
        }
        Pattern durationPattern = Pattern.compile("\"duration\"\\s*:\\s*([0-9.]+)");
        Matcher durationMatcher = durationPattern.matcher(responseBody);

        if (!durationMatcher.find()) {
            throw new IOException("Could not find duration in response");
        }
        double durationSeconds = Double.parseDouble(durationMatcher.group(1));

        Pattern distancePattern = Pattern.compile("\"distance\"\\s*:\\s*([0-9.]+)");
        Matcher distanceMatcher = distancePattern.matcher(responseBody);

        if (!distanceMatcher.find()) {
            throw new IOException("Could not find distance in response");
        }
        double distanceMeters = Double.parseDouble(distanceMatcher.group(1));

        return new RouteTimeEstimation(durationSeconds*1.5, distanceMeters);
    }
}
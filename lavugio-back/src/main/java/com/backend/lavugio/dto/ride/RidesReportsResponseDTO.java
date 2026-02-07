package com.backend.lavugio.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RidesReportsResponseDTO {
        private List<ChartData> charts;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ChartData {
            private String title;
            private String xAxisLabel;
            private String yAxisLabel;
            private List<String> labels;
            private List<Double> data;
            private double sum;
            private double average;

        }
}

/*{
        "charts": [
        {
        "title": "Rides Per Day",
        "xAxisLabel": "Date",
        "yAxisLabel": "Rides",
        "labels": ["2026-02-01", "2026-02-02", "2026-02-03"],
        "data": [12, 9, 15],
        "sum": 36,
        "average": 12
        },
        {
        "title": "Mileage Covered Per Day",
        "xAxisLabel": "Date",
        "yAxisLabel": "Mileage (km)",
        "labels": ["2026-02-01", "2026-02-02", "2026-02-03"],
        "data": [78.5, 64.2, 92.1],
        "sum": 234.8,
        "average": 78.27
        },
        {
        "title": "Daily Financial Report",
        "xAxisLabel": "Date",
        "yAxisLabel": "Revenue (RSD)",
        "labels": ["2026-02-01", "2026-02-02", "2026-02-03"],
        "data": [4200, 3650, 4980],
        "sum": 12830,
        "average": 4276.67
        }
        ]
}*/
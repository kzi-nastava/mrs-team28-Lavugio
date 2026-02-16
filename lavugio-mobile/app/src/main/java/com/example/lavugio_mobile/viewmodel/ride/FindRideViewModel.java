package com.example.lavugio_mobile.viewmodel.ride;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.data.model.route.Coordinates;
import com.example.lavugio_mobile.data.model.route.FavoriteRoute;
import com.example.lavugio_mobile.data.model.route.RideDestination;
import com.example.lavugio_mobile.data.model.utils.ResultState;
import com.example.lavugio_mobile.models.RidePriceEstimateDTO;
import com.example.lavugio_mobile.models.RideRequestDTO;
import com.example.lavugio_mobile.repository.ride.FindRideRepository;
import com.example.lavugio_mobile.services.utils.GeocodingHelper;

import java.util.List;

public class FindRideViewModel extends ViewModel {
        private final FindRideRepository repository;

        public FindRideViewModel() {
            repository = new FindRideRepository();
        }

        public FindRideRepository getRepository() {
            return repository;
        }

        public LiveData<Object> findRide(RideRequestDTO requestDTO) {
            return repository.findRide(requestDTO);
        }

        public LiveData<Double> estimatePrice(RidePriceEstimateDTO requestDTO) {
            return repository.estimatePrice(requestDTO);
        }

    public LiveData<ResultState> createFavoriteRoute(
            String name,
            List<GeocodingHelper.GeocodingResult> selectedDestinations) {

        RideDestination[] rideDestinations =
                new RideDestination[selectedDestinations.size()];

        for (int i = 0; i < selectedDestinations.size(); i++) {
            GeocodingHelper.GeocodingResult dest = selectedDestinations.get(i);

            rideDestinations[i] = new RideDestination(
                    dest.getDisplayName(),
                    dest.getStreet(),
                    dest.getHouseNumber(),
                    dest.getCity(),
                    dest.getCountry(),
                    new Coordinates(dest.getLongitude(), dest.getLatitude())
            );
        }

        FavoriteRoute request = new FavoriteRoute();
        request.setName(name);
        request.setDestinations(rideDestinations);

        return repository.createFavoriteRoute(request);
    }


    public LiveData<FavoriteRoute[]> getFavoriteRoutes() {
            return repository.getFavoriteRoutes();
        }
}

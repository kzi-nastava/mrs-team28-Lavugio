package com.example.lavugio_mobile.viewmodel.ride;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.data.model.route.FavoriteRoute;
import com.example.lavugio_mobile.models.RidePriceEstimateDTO;
import com.example.lavugio_mobile.models.RideRequestDTO;
import com.example.lavugio_mobile.repository.ride.FindRideRepository;

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

        public LiveData<Object> createFavoriteRoute(FavoriteRoute request) {
            return repository.createFavoriteRoute(request);
        }

        public LiveData<FavoriteRoute[]> getFavoriteRoutes() {
            return repository.getFavoriteRoutes();
        }
}

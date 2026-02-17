package com.example.lavugio_mobile.viewmodel.reports;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.data.model.RidesReportsAdminFiltersDTO;
import com.example.lavugio_mobile.data.model.reports.RidesReportsAdminFilterEnum;
import com.example.lavugio_mobile.data.model.reports.RidesReportsDateRangeDTO;
import com.example.lavugio_mobile.data.model.reports.RidesReportsResponseDTO;
import com.example.lavugio_mobile.repository.reports.RidesReportsRepository;
import com.example.lavugio_mobile.services.auth.AuthService;

public class RidesReportsViewModel extends ViewModel {

    private final RidesReportsRepository repository;
    private final MutableLiveData<RidesReportsResponseDTO> reportData = new MutableLiveData<>();

    public RidesReportsViewModel() {
        repository = new RidesReportsRepository();
    }

    public LiveData<RidesReportsResponseDTO> getReportData() {
        return reportData;
    }

    public void generateReport(String startDate, String endDate, String filterType, String userEmail) {

        AuthService authService = AuthService.getInstance();

        LiveData<RidesReportsResponseDTO> source;

        if (authService.isAdmin()) {
            RidesReportsAdminFilterEnum filterEnum =
                    RidesReportsAdminFilterEnum.valueOf(filterType);

            RidesReportsAdminFiltersDTO filters =
                    new RidesReportsAdminFiltersDTO(startDate, endDate, userEmail, filterEnum);

            source = repository.getAdministratorReport(filters);

        } else if (authService.isDriver()) {
            source = repository.getDriverReport(
                    new RidesReportsDateRangeDTO(startDate, endDate)
            );

        } else {
            source = repository.getRegularUserReport(
                    new RidesReportsDateRangeDTO(startDate, endDate)
            );
        }

        source.observeForever(report -> reportData.setValue(report));
    }
}

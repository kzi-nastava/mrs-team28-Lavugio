package com.backend.lavugio.model.ride;

import com.backend.lavugio.model.user.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ride_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideReport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long reportId;

    @ManyToOne()
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(nullable = false)
    private String reportMessage;

    @ManyToOne()
    @JoinColumn(name = "account_id", nullable = false)
    private Account reporter;
}

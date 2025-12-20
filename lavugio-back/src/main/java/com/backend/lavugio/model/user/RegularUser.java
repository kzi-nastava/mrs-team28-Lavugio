package com.backend.lavugio.model.user;

import com.backend.lavugio.model.ride.Ride;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "regular_users")
public class RegularUser extends BlockableAccount {

    @ManyToMany(mappedBy = "passangers")
    private Set<Ride> rides = new HashSet<>();
}

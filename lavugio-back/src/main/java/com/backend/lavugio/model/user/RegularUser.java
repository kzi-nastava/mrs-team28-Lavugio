package com.backend.lavugio.model.user;

import com.backend.lavugio.model.ride.Ride;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "regular_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegularUser extends BlockableAccount {

    @ManyToMany(mappedBy = "passengers")
    @JsonIgnore
    private Set<Ride> rides = new HashSet<>();

    @Column
    private boolean canOrder;
}

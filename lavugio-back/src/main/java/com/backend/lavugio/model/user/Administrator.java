package com.backend.lavugio.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="administrators")
public class Administrator extends Account {

}

package com.backend.lavugio.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="administrators")
@Getter
@Setter
@NoArgsConstructor
public class Administrator extends Account {

}

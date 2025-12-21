package com.backend.lavugio.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blockable_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockableAccount extends Account {
	@Column
	private boolean blocked;
	@Column
	private String blockReason;
}

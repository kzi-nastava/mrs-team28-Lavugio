package com.backend.lavugio.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "blockable_accounts")
public class BlockableAccount extends Account {
	@Column
	private boolean isBlocked;
	@Column
	private String blockReason;
}

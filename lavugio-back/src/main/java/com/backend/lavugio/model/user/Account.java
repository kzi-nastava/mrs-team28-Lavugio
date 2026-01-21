package com.backend.lavugio.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;
	@Column
	private String lastName;
	@Column(unique = true)
	private String email;
	@Column
	private String password;
	@Column(name = "profile_photo_path")
	private String profilePhotoPath;
	@Column
	private String phoneNumber;
	@Column
	private String address;
	@Column(columnDefinition = "boolean default false")
	private boolean emailVerified = false;
}

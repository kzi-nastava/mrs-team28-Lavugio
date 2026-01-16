package com.backend.lavugio.model.route;

import com.backend.lavugio.model.user.RegularUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "favorite_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRoute {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private RegularUser user;

}

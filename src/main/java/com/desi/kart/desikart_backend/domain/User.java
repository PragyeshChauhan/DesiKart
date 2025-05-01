package com.desi.kart.desikart_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "User Name is required")
	@Column(nullable = false)
	@Unique
	private String username;

	@NotBlank(message = "Email is required")
	@Column(nullable = false)
	private String email;

	@NotBlank(message = "Phone is required")
	@Column(nullable = false)
	private String phone;

	@NotBlank(message = "Phone is required")
	@Column(nullable = false)
	private String phoneCountryCode;

	@NotBlank(message = "Password is required")
	@Column(nullable = false)
	private String password;
	
	private boolean isActive;
	
	private String deviceToken;

	private String provider ;

	private boolean  resetPasswordAfterLogin ;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name ="User_Roles",
			joinColumns = @JoinColumn(name= "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")

	)
	private Set<BaseRoles> roles;

	private boolean isVerified;
}

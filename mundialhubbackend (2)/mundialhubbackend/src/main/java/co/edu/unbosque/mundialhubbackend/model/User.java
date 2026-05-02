package co.edu.unbosque.mundialhubbackend.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "user")
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String username;

	@Column(unique = true)
	private String email;

	private String password;

	private String verificationCode;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	private LocalDateTime createdAt;

	// Dentro de la clase User.java

	@ManyToMany
	@JoinTable(name = "user_favorite_teams", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
	private List<Team> favoriteTeams;

	@ManyToMany
	@JoinTable(name = "user_favorite_stadiums", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "stadium_id"))
	private List<Stadium> favoriteStadiums;

	// IMPORTANTE: Genera sus respectivos Getters y Setters
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	public User() {
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.role = Role.USER;
	}

	public User(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public User(String email, String password, Role role) {
		super();
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public User(String name, String username, String email, String password) {
		super();
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public User(String name, String username, String email, String password, Role role, UserStatus status) {
		super();
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.status = status;
	}

	public enum Role {
		USER, ADMIN, CONTENT, SUPPORT, COMPLIANCE
	}

	public enum UserStatus {
		ACTIVE, SUSPENDED, PENDING_VERIFICATION
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public List<Team> getFavoriteTeams() {
		return favoriteTeams;
	}

	public void setFavoriteTeams(List<Team> favoriteTeams) {
		this.favoriteTeams = favoriteTeams;
	}

	public List<Stadium> getFavoriteStadiums() {
		return favoriteStadiums;
	}

	public void setFavoriteStadiums(List<Stadium> favoriteStadiums) {
		this.favoriteStadiums = favoriteStadiums;
	}

}
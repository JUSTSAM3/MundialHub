package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.unbosque.mundialhubbackend.model.User.Role;
import co.edu.unbosque.mundialhubbackend.model.User.UserStatus;

public class UserDTO {

	private Long id;

	private String name;

	private String username;

	private String email;

	private String password;

	private String verificationCode;

	private Role role;

	private UserStatus status;

	private LocalDateTime createdAt;

	private List<TeamDTO> favoriteTeams;
	private List<StadiumDTO> favoriteStadiums;

	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	public UserDTO() {
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.role = Role.USER;
	}

	public UserDTO(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	public UserDTO(String username, String password, Role role) {
		this();
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public UserDTO(String name, String username, String email, String password) {
		this();
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public UserDTO(String name, String username, String email, String password, Role rol) {
		this();
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = rol;
	}

	public UserDTO(String name, String username, String email, String password, Role role, UserStatus status) {
		super();
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.status = status;
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

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
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

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<TeamDTO> getFavoriteTeams() {
		return favoriteTeams;
	}

	public void setFavoriteTeams(List<TeamDTO> favoriteTeams) {
		this.favoriteTeams = favoriteTeams;
	}

	public List<StadiumDTO> getFavoriteStadiums() {
		return favoriteStadiums;
	}

	public void setFavoriteStadiums(List<StadiumDTO> favoriteStadiums) {
		this.favoriteStadiums = favoriteStadiums;
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", name=" + name + ", username=" + username + ", email=" + email + ", password="
				+ password + ", verificationCode=" + verificationCode + ", role=" + role + ", status=" + status
				+ ", createdAt=" + createdAt + ", accountNonExpired=" + accountNonExpired + ", accountNonLocked="
				+ accountNonLocked + ", credentialsNonExpired=" + credentialsNonExpired + ", enabled=" + enabled + "]";
	}

}
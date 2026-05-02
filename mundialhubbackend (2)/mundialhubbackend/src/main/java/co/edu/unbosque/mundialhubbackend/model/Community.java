package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "community")
public class Community {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunityMember> members;

	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunityInvite> invites;

	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Poll> polls;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<CommunityMember> getMembers() {
		return members;
	}

	public void setMembers(List<CommunityMember> members) {
		this.members = members;
	}

	public List<CommunityInvite> getInvites() {
		return invites;
	}

	public void setInvites(List<CommunityInvite> invites) {
		this.invites = invites;
	}

	public List<Poll> getPolls() {
		return polls;
	}

	public void setPolls(List<Poll> polls) {
		this.polls = polls;
	}

}
package lads.lads_website.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String permissionLevel;

    @Transient
    private LocalDate addDate;

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<PlayerCard> playerCards = new ArrayList<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<ResourceTracking> resourceTrackings = new ArrayList<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<PlayerBounty> playerBounties = new ArrayList<>();

    public Player() {
    }

    public Player(Long id, String username, String email, String password, String permissionLevel, LocalDate addDate, List<PlayerCard> playerCards, List<ResourceTracking> resourceTrackings, List<PlayerBounty> playerBounties) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.permissionLevel = permissionLevel;
        this.addDate = addDate;
        this.playerCards = playerCards;
        this.resourceTrackings = resourceTrackings;
        this.playerBounties = playerBounties;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public LocalDate getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDate addDate) {
        this.addDate = addDate;
    }

    public List<PlayerCard> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<PlayerCard> playerCards) {
        this.playerCards = playerCards;
    }

    public List<ResourceTracking> getResourceTrackings() {
        return resourceTrackings;
    }

    public void setResourceTrackings(List<ResourceTracking> resourceTrackings) {
        this.resourceTrackings = resourceTrackings;
    }

    public List<PlayerBounty> getPlayerBounties() {
        return playerBounties;
    }

    public void setPlayerBounties(List<PlayerBounty> playerBounties) {
        this.playerBounties = playerBounties;
    }
}

package lads.lads_website.domain;

import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String permissionLevel;
    private LocalDate addDate;
    private Collection<? extends GrantedAuthority> authorities;
    private List<PlayerCard> playerCards = new ArrayList<>();
    private List<ResourceTracking> resourceTrackings = new ArrayList<>();

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> grantedAuthorities,
                             String email, String permissionLevel, LocalDate addDate, Long id, List<PlayerCard> playerCards, List<ResourceTracking> resourceTrackings) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.permissionLevel = permissionLevel;
        this.addDate = addDate;
        this.authorities = grantedAuthorities;
        this.id = id;
        this.playerCards = playerCards;
        this.resourceTrackings = resourceTrackings;
    }


    @Override
    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public List<PlayerCard> getPlayerCards() {
        return playerCards;
    }

    public List<ResourceTracking> getResourceTrackings() {
        return resourceTrackings;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public LocalDate getAddDate() {
        return addDate;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

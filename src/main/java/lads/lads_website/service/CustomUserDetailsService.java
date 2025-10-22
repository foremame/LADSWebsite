package lads.lads_website.service;

import lads.lads_website.domain.CustomUserDetails;
import lads.lads_website.domain.Player;
import lads.lads_website.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private PlayerRepository playerRepository;

    @Autowired
    public CustomUserDetailsService(PlayerRepository playerRepository) {
        super();
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Player> foundPlayer = playerRepository.findByUsername(username);
        if (foundPlayer.isEmpty()) {
            throw new UsernameNotFoundException("Username " + username + " does not exist");
        } else {
            Player player = foundPlayer.get();
            return new CustomUserDetails(player.getUsername(), player.getPassword(), authorities(player.getPermissionLevel()), player.getEmail(), player.getPermissionLevel(), player.getAddDate(), player.getId(), player.getPlayerCards(), player.getResourceTrackings());
        }
    }

    public Collection<? extends GrantedAuthority> authorities(String permissionLevel) {
        return Arrays.asList(new SimpleGrantedAuthority(permissionLevel.toUpperCase()));
    }
}

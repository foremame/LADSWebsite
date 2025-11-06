package lads.lads_website.service;

import lads.lads_website.domain.Player;
import lads.lads_website.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public Player save(Player player) throws IllegalStateException{
        validateNewPlayer(player);
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        return playerRepository.save(player);
    }

    private void validateNewPlayer(Player player) throws IllegalStateException{
        playerRepository.findByUsername(player.getUsername())
                .ifPresent(m -> {
                    throw new IllegalStateException("The username " + m.getUsername() + " already exists.");
                });
    }
}

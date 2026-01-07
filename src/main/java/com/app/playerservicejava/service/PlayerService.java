package com.app.playerservicejava.service;

import com.app.playerservicejava.exception.PlayerNotFoundException;
import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepository;

    public Players getPlayers() {
        Players players = new Players();
        playerRepository.findAll()
                .forEach(players.getPlayers()::add);
        return players;
    }

    public Page<Player> getPlayers(Pageable pageable) {
        return playerRepository.findAll(pageable);
    }

    public Page<Player> searchByLastName(String fragment, Pageable pageable) {
        return playerRepository.findByLastNameContainingIgnoreCase(fragment, pageable);
    }

    public Page<Player> filterByBirthYear(String year, Pageable pageable) {
        return playerRepository.findByBirthYear(year, pageable);
    }

    public Optional<Player> getPlayerById(String playerId) {
        Optional<Player> player = null;

        /* simulated network delay */
        try {
            player = playerRepository.findById(playerId);
            Thread.sleep((long)(Math.random() * 2000));
        } catch (Exception e) {
            LOGGER.error("message=Exception in getPlayerById; exception={}", e.toString());
            return Optional.empty();
        }
        return player;
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player replacePlayer(String id, Player replacement) {
        return playerRepository.findById(id).map(existing -> {
            // replace all fields; set id to path id
            replacement.setPlayerId(id);
            return playerRepository.save(replacement);
        }).orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
    }

    public Optional<Player> patchPlayer(String id, Player patch) {
        return playerRepository.findById(id).map(existing -> {
            if (patch.getBirthYear() != null) existing.setBirthYear(patch.getBirthYear());
            if (patch.getBirthMonth() != null) existing.setBirthMonth(patch.getBirthMonth());
            if (patch.getBirthDay() != null) existing.setBirthDay(patch.getBirthDay());
            if (patch.getBirthCountry() != null) existing.setBirthCountry(patch.getBirthCountry());
            if (patch.getBirthState() != null) existing.setBirthState(patch.getBirthState());
            if (patch.getBirthCity() != null) existing.setBirthCity(patch.getBirthCity());
            if (patch.getDeathYear() != null) existing.setDeathYear(patch.getDeathYear());
            if (patch.getDeathMonth() != null) existing.setDeathMonth(patch.getDeathMonth());
            if (patch.getDeathDay() != null) existing.setDeathDay(patch.getDeathDay());
            if (patch.getDeathCountry() != null) existing.setDeathCountry(patch.getDeathCountry());
            if (patch.getDeathState() != null) existing.setDeathState(patch.getDeathState());
            if (patch.getDeathCity() != null) existing.setDeathCity(patch.getDeathCity());
            if (patch.getFirstName() != null) existing.setFirstName(patch.getFirstName());
            if (patch.getLastName() != null) existing.setLastName(patch.getLastName());
            if (patch.getGivenName() != null) existing.setGivenName(patch.getGivenName());
            if (patch.getWeight() != null) existing.setWeight(patch.getWeight());
            if (patch.getHeight() != null) existing.setHeight(patch.getHeight());
            if (patch.getBats() != null) existing.setBats(patch.getBats());
            if (patch.getThrowStats() != null) existing.setThrowStats(patch.getThrowStats());
            if (patch.getDebut() != null) existing.setDebut(patch.getDebut());
            if (patch.getFinalGame() != null) existing.setFinalGame(patch.getFinalGame());
            if (patch.getRetroId() != null) existing.setRetroId(patch.getRetroId());
            if (patch.getBbrefId() != null) existing.setBbrefId(patch.getBbrefId());
            return playerRepository.save(existing);
        });
    }

    public boolean deletePlayer(String id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}


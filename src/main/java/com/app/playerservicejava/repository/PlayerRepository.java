package com.app.playerservicejava.repository;
import com.app.playerservicejava.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, String> {
    Optional<Player> findByPlayerId(String playerId);

    List<Player> findByLastNameIgnoreCase(String lastName);
    List<Player> findByFirstNameIgnoreCase(String firstName);
    List<Player> findByLastNameContainingIgnoreCase(String fragment);

    Optional<Player> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    List<Player> findByBirthYear(String birthYear);
    List<Player> findByBirthCountryIgnoreCase(String birthCountry);

    List<Player> findByBats(String bats);
    List<Player> findByThrowStats(String throwStats);

    Optional<Player> findByRetroId(String retroId);
    Optional<Player> findByBbrefId(String bbrefId);

    Page<Player> findByLastNameContainingIgnoreCase(String fragment, Pageable pageable);
    Page<Player> findByBirthYear(String birthYear, Pageable pageable);
}



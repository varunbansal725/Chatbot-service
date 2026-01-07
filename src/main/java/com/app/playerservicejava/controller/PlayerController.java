package com.app.playerservicejava.controller;

import com.app.playerservicejava.exception.PlayerNotFoundException;
import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.service.PlayerService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "v1/players", produces = { MediaType.APPLICATION_JSON_VALUE })
@Validated
public class PlayerController {
    @Resource
    private PlayerService playerService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Players> getPlayers() {
        Players players = playerService.getPlayers();
        return ok(players);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Player>> searchPlayers(
            @RequestParam(required = false) 
            @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters") 
            String lastName,
            @RequestParam(required = false) 
            @Pattern(regexp = "\\d{4}", message = "Birth year must be a 4-digit number") 
            String birthYear,
            Pageable pageable) {
        Page<Player> page;
        if (lastName != null) {
            page = playerService.searchByLastName(lastName, pageable);
        } else if (birthYear != null) {
            page = playerService.filterByBirthYear(birthYear, pageable);
        } else {
            page = playerService.getPlayers(pageable);
        }
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") @NotBlank @Size(min = 3, max = 32) String id) {
        Optional<Player> player = playerService.getPlayerById(id);

        if (player.isPresent()) {
            return new ResponseEntity<>(player.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@RequestBody @Valid Player player) {
        Player created = playerService.createPlayer(player);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> replacePlayer(@PathVariable("id") @NotBlank @Size(min = 3, max = 32) String id,
                                                @RequestBody @Valid Player replacement) {
        try {
            Player updated = playerService.replacePlayer(id, replacement);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (PlayerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> patchPlayer(@PathVariable("id") @NotBlank @Size(min = 3, max = 32) String id,
                                              @RequestBody Player patch) {
        return playerService.patchPlayer(id, patch)
                .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") @NotBlank @Size(min = 3, max = 32) String id) {
        boolean deleted = playerService.deletePlayer(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


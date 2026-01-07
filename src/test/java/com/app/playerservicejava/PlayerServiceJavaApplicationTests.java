package com.app.playerservicejava;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.repository.PlayerRepository;
import com.app.playerservicejava.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceJavaApplicationTests {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;
    
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player();
        testPlayer.setPlayerId("1");
        testPlayer.setFirstName("Test");
        testPlayer.setLastName("Player");
    }

    @Test
    void contextLoads() {
        assertNotNull(playerService, "PlayerService should be initialized");
        assertNotNull(playerRepository, "PlayerRepository should be mocked");
    }

    @Test
    void getPlayerById_shouldReturnPlayer_whenPlayerExists() {
        // Arrange
        when(playerRepository.findById("1")).thenReturn(Optional.of(testPlayer));
        
        // Act
        Player found = playerService.getPlayerById("1").orElseThrow();
        
        // Assert
        assertEquals("1", found.getPlayerId());
        assertEquals("Test", found.getFirstName());
        verify(playerRepository, times(1)).findById("1");
    }
    
    @Test
    void getPlayerById_shouldReturnEmptyOptional_whenPlayerNotFound() {
        // Arrange
        when(playerRepository.findById("999")).thenReturn(Optional.empty());
        
        // Act
        Optional<Player> result = playerService.getPlayerById("999");
        
        // Assert
        assertFalse(result.isPresent(), "Optional should be empty when player not found");
        verify(playerRepository, times(1)).findById("999");
    }
    
    @Test
    void getPlayers_shouldReturnEmptyList_whenNoPlayersExist() {
        // Arrange
        when(playerRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        Players result = playerService.getPlayers();
        
        // Assert
        assertTrue(result.getPlayers().isEmpty(), "Should return empty players list when no players exist");
        verify(playerRepository, times(1)).findAll();
    }
    
    @Test
    void getPlayers_shouldReturnAllPlayers() {
        // Arrange
        List<Player> players = Arrays.asList(
            createTestPlayer("1", "John", "Doe"),
            createTestPlayer("2", "Jane", "Smith")
        );
        when(playerRepository.findAll()).thenReturn(players);
        
        // Act
        Players result = playerService.getPlayers();
        
        // Assert
        assertEquals(2, result.getPlayers().size(), "Should return all players");
        assertTrue(result.getPlayers().containsAll(players), "Should contain all test players");
        verify(playerRepository, times(1)).findAll();
    }
    
    @Test
    void getPlayers_withPagination_shouldReturnPageOfPlayers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Player> players = Arrays.asList(
            createTestPlayer("1", "John", "Doe"),
            createTestPlayer("2", "Jane", "Smith")
        );
        Page<Player> playerPage = new PageImpl<>(players, pageable, 2);
        when(playerRepository.findAll(pageable)).thenReturn(playerPage);
        
        // Act
        Page<Player> result = playerService.getPlayers(pageable);
        
        // Assert
        assertEquals(2, result.getTotalElements(), "Should return page with 2 players");
        assertEquals(1, result.getTotalPages(), "Should have 1 page total");
        assertTrue(result.getContent().containsAll(players), "Should contain all test players");
        verify(playerRepository, times(1)).findAll(pageable);
    }
    
    @Test
    void getPlayers_withPagination_shouldReturnEmptyPage_whenNoPlayers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Player> emptyPage = Page.empty(pageable);
        when(playerRepository.findAll(pageable)).thenReturn(emptyPage);
        
        // Act
        Page<Player> result = playerService.getPlayers(pageable);
        
        // Assert
        assertTrue(result.isEmpty(), "Should return empty page when no players exist");
        verify(playerRepository, times(1)).findAll(pageable);
    }
    
    private Player createTestPlayer(String id, String firstName, String lastName) {
        Player player = new Player();
        player.setPlayerId(id);
        player.setFirstName(firstName);
        player.setLastName(lastName);
        return player;
    }
}
package com.tennis.domain.port;

import com.tennis.domain.model.GameState;
import java.util.List;
import java.util.Optional;

public interface GameRepository {
    void save(GameState gameState);
    Optional<GameState> findById(String gameId);
    List<GameState> findAll();
    List<GameState> findFinishedGames();
    long countTotalGames();
    long countFinishedGames();
    long countGamesByWinner(String winner);
}

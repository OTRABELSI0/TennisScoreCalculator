package com.tennis.infrastructure.adapter;

import com.tennis.domain.model.GameState;
import com.tennis.domain.port.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    @Override
    public void save(GameState gameState) {
        games.put(gameState.gameId(), gameState);
    }

    @Override
    public Optional<GameState> findById(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    @Override
    public List<GameState> findAll() {
        return List.copyOf(games.values());
    }

    @Override
    public List<GameState> findFinishedGames() {
        return games.values().stream()
                .filter(GameState::isFinished)
                .toList();
    }

    @Override
    public long countTotalGames() {
        return games.values().stream()
                .map(GameState::gameId)
                .distinct()
                .count();
    }

    @Override
    public long countFinishedGames() {
        return games.values().stream()
                .filter(GameState::isFinished)
                .map(GameState::gameId)
                .distinct()
                .count();
    }

    @Override
    public long countGamesByWinner(String winner) {
        return games.values().stream()
                .filter(GameState::isFinished)
                .filter(game -> winner.equals(game.winner() != null ? game.winner().name() : null))
                .map(GameState::gameId)
                .distinct()
                .count();
    }
}
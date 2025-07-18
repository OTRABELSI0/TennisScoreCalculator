package com.tennis.domain.port;

import com.tennis.domain.model.GameState;
import com.tennis.domain.model.Player;

public interface GameEventPublisher {
    void publishPointScored(GameState gameState, Player player);
    void publishGameFinished(GameState gameState);
}
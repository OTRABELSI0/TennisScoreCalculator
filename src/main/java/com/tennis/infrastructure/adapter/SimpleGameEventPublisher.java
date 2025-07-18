package com.tennis.infrastructure.adapter;

import com.tennis.domain.model.GameState;
import com.tennis.domain.model.Player;
import com.tennis.domain.port.GameEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!kafka")
public class SimpleGameEventPublisher implements GameEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SimpleGameEventPublisher.class);

    @Override
    public void publishPointScored(GameState gameState, Player player) {
        logger.info("Point scored - Game: {}, Player: {}, Score: {}",
                gameState.gameId(), player, gameState.displayScore());
    }

    @Override
    public void publishGameFinished(GameState gameState) {
        logger.info("Game finished - Game: {}, Winner: {}",
                gameState.gameId(), gameState.winner());
    }
}

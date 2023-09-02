package club.crestmc.uhc.game.state;

import club.crestmc.uhc.scoreboard.ScoreboardType;

public enum GameState {

    WAITING() {
        @Override
        public ScoreboardType getScoreboard() {
            return ScoreboardType.WAITING;
        }
    },
    SCATTERING {
        @Override
        public ScoreboardType getScoreboard() {
            return ScoreboardType.SCATTERING_AWAITING;
        }
    },
    PLAYING {
        @Override
        public ScoreboardType getScoreboard() {
            return ScoreboardType.PLAYING;
        }
    },
    ENDING {
        @Override
        public ScoreboardType getScoreboard() {
            return ScoreboardType.ENDING;
        }
    };

    public abstract ScoreboardType getScoreboard();

}

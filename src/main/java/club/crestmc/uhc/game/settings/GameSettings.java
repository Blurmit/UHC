package club.crestmc.uhc.game.settings;

import java.util.Arrays;

public enum GameSettings {

    START_TIMER() {
        @Override
        public Object getValue() {
            return 60;
        }
    },
    START_TIMER_ALERT_INTERVALS() {
        @Override
        public Object getValue() {
            return Arrays.asList(60, 30, 15, 10, 5, 4, 3, 2, 1);
        }
    },
    FINAL_HEAL_TIMER() {
        @Override
        public Object getValue() {
            return 600;
        }
    },
    FINAL_HEAL_ALERT_INTERVALS() {
        @Override
        public Object getValue() {
            return Arrays.asList(600, 300, 60, 30, 15, 10, 5, 4, 3, 2, 1);
        }
    },
    PVP_ENABLE_TIMER() {
        @Override
        public Object getValue() {
            return 600;
        }
    },
    PVP_ENABLE_ALERT_INTERVALS() {
        @Override
        public Object getValue() {
            return Arrays.asList(600, 300, 60, 30, 15, 10, 5, 4, 3, 2, 1);
        }
    };

    public abstract Object getValue();

    public <T> T getAs(Class<T> clazz) {
        if (!clazz.isAssignableFrom(getValue().getClass())) {
            throw new IllegalArgumentException(clazz + " does not extend " + getValue().getClass());
        }

        return clazz.cast(getValue());
    }

}

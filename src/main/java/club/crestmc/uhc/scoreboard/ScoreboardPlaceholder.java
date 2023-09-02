package club.crestmc.uhc.scoreboard;

import java.util.function.Function;

public class ScoreboardPlaceholder {

    public static <R> void addHook(String placeholder, Function<String, ? extends R> mapper) {
        mapper.apply(placeholder);
    }

}

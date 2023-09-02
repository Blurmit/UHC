package club.crestmc.uhc.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TimeUtil {

    public static long getCurrentTimeSeconds() {
        return Instant.ofEpochMilli(System.currentTimeMillis()).getEpochSecond();
    }

    /**
     * Converts unix time stamps into text, readable by humans
     * @param epochSeconds The time stamp with the date to convert
     * @return The formatted text containing the remaining time until the specified date
     */
    public static String getHowLongUntil(long epochSeconds) {
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
        Duration duration = Duration.between(dateTime, now).abs();

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        String result = "";

       if (days > 0) {
            result += days + " day" + (days > 1 ? "s" : "") + " ";
        } else if (hours > 0) {
            result += hours + " hour" + (hours > 1 ? "s" : "") + " ";
        } else if (minutes > 0) {
            result += minutes + " minute" + (minutes > 1 ? "s" : "") + " ";
        } else if (seconds > 0) {
            result += seconds + " second" + (seconds > 1 ? "s" : "") + " ";
        }

        if (result.equals("")) {
            result = "now";
        }

        return result.trim();
    }

    /**
     * Converts unix time stamps into text which is used to determined how long ago the time stamp occurred
     * @param epochSeconds The time stamp with the date to convert
     * @return The formatted text containing the time that has passed since the provided date
     */
    public static String getHowLongSince(long epochSeconds) {
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
        Duration duration = Duration.between(dateTime, now).abs();

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(String.format("%02d", days)).append(":");
        }
        if (hours > 0) {
            result.append(String.format("%02d", hours)).append(":");
        }
        if (minutes > 0) {
            result.append(String.format("%02d", minutes)).append(":");
        } else {
            result.append("00:");
        }
        if (seconds > 0) {
            result.append(String.format("%02d", seconds));
        } else {
            result.append("00");
        }

        return result.toString();
    }

    /**
     * Returns how many seconds until a certain time stamp
     * @param epochSeconds The time stamp with the date to convert
     * @return An integer depicting how long is left until the specified time stamp
     */
    public static long getSecondsUntil(long epochSeconds) {
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
        Duration duration = Duration.between(dateTime, now).abs();

        return duration.getSeconds();
    }

    private enum Units {

        SECONDS() {
            @Override
            public String[] getUnits() {
                return new String[] { "s", "sec", "secs", "second", "seconds" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.SECONDS;
            }
        },
        MINUTES {
            @Override
            public String[] getUnits() {
                return new String[] { "m", "min", "mins", "minute", "minutes" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.MINUTES;
            }
        },
        HOURS {
            @Override
            public String[] getUnits() {
                return new String[] { "h", "hour", "hours" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.HOURS;
            }
        },
        DAYS {
            @Override
            public String[] getUnits() {
                return new String[] { "d", "day", "days" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.DAYS;
            }
        },
        WEEKS {
            @Override
            public String[] getUnits() {
                return new String[] { "w", "week", "weeks" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.WEEKS;
            }
        },
        MONTHS {
            @Override
            public String[] getUnits() {
                return new String[] { "mo", "month", "months" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.MONTHS;
            }
        },
        YEARS {
            @Override
            public String[] getUnits() {
                return new String[] { "y", "year", "years" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.YEARS;
            }
        };

        private static final Map<String, TimeUtil.Units> units;

        static {
            units = new HashMap<>();

            for (Units unit : values()) {
                for (String unitString : unit.getUnits()) {
                    units.put(unitString, unit);
                }
            }
        }

        public abstract String[] getUnits();
        public abstract ChronoUnit getTimeUnit();

        public static Set<String> getAllUnits() {
            return new HashSet<>(units.keySet());
        }

        public static TimeUtil.Units getByName(String name) {
            return units.entrySet().stream()
                    .filter(unitEntry -> unitEntry.getKey().equals(name))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }

    }

}

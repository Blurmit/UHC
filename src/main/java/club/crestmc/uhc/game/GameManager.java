package club.crestmc.uhc.game;

import club.crestmc.uhc.UHC;
import club.crestmc.uhc.game.settings.GameSettings;
import club.crestmc.uhc.game.state.GameState;
import club.crestmc.uhc.scoreboard.ScoreboardPlaceholder;
import club.crestmc.uhc.scoreboard.ScoreboardType;
import club.crestmc.uhc.user.User;
import club.crestmc.uhc.util.LocationUtil;
import club.crestmc.uhc.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class GameManager {

    private final UHC plugin;

    private final Set<User> players;
    private final Set<User> spectators;

    private GameState state;
    private boolean started;
    private int gameTimer;

    private BukkitTask startCountdownTask;
    private BukkitTask scatterTask;
    private BukkitTask gameTimerTask;
    private BukkitTask finalHealTask;
    private BukkitTask pvpEnableTask;

    public GameManager(UHC plugin) {
        this.plugin = plugin;

        this.players = new HashSet<>();
        this.spectators = new HashSet<>();

        this.state = GameState.WAITING;
        this.started = false;
        this.gameTimer = 0;
    }

    public void startPregame() {
        if (startCountdownTask != null || scatterTask != null) {
            return;
        }

        state = GameState.SCATTERING;
        ScoreboardPlaceholder.addHook("GAME_TIME", hook -> getGameTime());

        for (User user : plugin.getLobbyManager().getPlayers()) {
            user.setScoreboardType(ScoreboardType.SCATTERING_AWAITING);
            user.setInvisible(true);
        }

        int startTimer = GameSettings.START_TIMER.getAs(Integer.class);
        AtomicInteger interval = new AtomicInteger(startTimer);
        List<Integer> alertIntervals = GameSettings.START_TIMER_ALERT_INTERVALS.getAs(List.class);

        startCountdownTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int currentInterval = interval.getAndDecrement();
            String startsInText = TimeUtil.getHowLongUntil(TimeUtil.getCurrentTimeSeconds() + currentInterval + 1);

            if (currentInterval < 1) {
                startGame();
                startCountdownTask.cancel();
                return;
            }

            if (!alertIntervals.contains(currentInterval)) {
                return;
            }

            plugin.getServer().broadcastMessage(ChatColor.AQUA + "The game will begin in " + ChatColor.DARK_AQUA + startsInText + ChatColor.AQUA + ".");
        }, 0L, 20L);


        Set<User> awaitingScatter = plugin.getLobbyManager().getPlayers();
        int playersPerSecond = (int) Math.ceil(awaitingScatter.size() / (float) startTimer);

        scatterTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (awaitingScatter.size() < 1 && interval.get() > 10) {
                interval.set(10);

                String startsInText = TimeUtil.getHowLongUntil(TimeUtil.getCurrentTimeSeconds() + (interval.get() + 1));
                plugin.getServer().broadcastMessage(ChatColor.AQUA + "All players are in! Reducing start countdown to " + ChatColor.DARK_AQUA + startsInText + ChatColor.AQUA + ".");

                scatterTask.cancel();
                return;
            }

            for (int i = 0; i < playersPerSecond; i++) {
                User nextAvailable = plugin.getLobbyManager().getNextAvailable();
                join(nextAvailable);
            }
        }, 0, 20L);
    }

    public void startGame() {
        plugin.getServer().broadcastMessage(ChatColor.AQUA + "The game has commenced!");
        state = GameState.PLAYING;
        started = true;

        for (User user : players) {
            user.setScoreboardType(ScoreboardType.PLAYING);
            user.setFrozen(false);
            user.setInvisible(false);
        }

        int finalHealTimer = GameSettings.FINAL_HEAL_TIMER.getAs(Integer.class);
        AtomicInteger interval = new AtomicInteger(finalHealTimer);
        List<Integer> alertIntervals = GameSettings.FINAL_HEAL_ALERT_INTERVALS.getAs(List.class);

        finalHealTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            String healsAtText = TimeUtil.getHowLongUntil(TimeUtil.getCurrentTimeSeconds() + (interval.get() + 1));
            int currentInterval = interval.getAndDecrement();

            if (currentInterval < 1) {
                finalHeal();
                finalHealTask.cancel();
                return;
            }

            if (!alertIntervals.contains(currentInterval)) {
                return;
            }

            plugin.getServer().broadcastMessage(ChatColor.AQUA + "Final heal will occur in " + ChatColor.DARK_AQUA + healsAtText + ChatColor.AQUA + ".");
        }, 0L, 20L);

        gameTimerTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            gameTimer++;
        }, 20L, 20L);
    }

    public void finalHeal() {
        plugin.getServer().broadcastMessage(ChatColor.AQUA + "Final heal has commenced!");

        for (User user : players) {
            user.heal();
        }

        int pvpEnableTimer = GameSettings.PVP_ENABLE_TIMER.getAs(Integer.class);
        AtomicInteger interval = new AtomicInteger(pvpEnableTimer);
        List<Integer> alertIntervals = GameSettings.PVP_ENABLE_ALERT_INTERVALS.getAs(List.class);

        pvpEnableTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            String enablesInText = TimeUtil.getHowLongUntil(TimeUtil.getCurrentTimeSeconds() + (interval.get() + 1));
            int currentInterval = interval.getAndDecrement();

            if (currentInterval < 1) {
                enablePvP();
                pvpEnableTask.cancel();
                return;
            }

            if (!alertIntervals.contains(currentInterval)) {
                return;
            }

            plugin.getServer().broadcastMessage(ChatColor.AQUA + "PvP will enable in " + ChatColor.DARK_AQUA + enablesInText + ChatColor.AQUA + ".");
        }, 0L, 20L);
    }

    public void enablePvP() {
        plugin.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "PvP has been enabled!");

        for (User user : players) {
            user.setCanPvP(true);
        }
    }

    public void join(User user) {
        user.setGame(this);
        user.setScoreboardType(ScoreboardType.SCATTERING_AWAITING);

        scatter(user).whenComplete((completed, error) -> {
            user.setScoreboardType(ScoreboardType.SCATTERING_COMPLETE);
            user.clear();
            user.setFrozen(true);

            plugin.getServer().getScheduler().runTask(plugin, () -> user.getPlayer().setGameMode(GameMode.SURVIVAL));

            players.add(user);
            plugin.getLobbyManager().getPlayers().remove(user);
        });
    }

    public void leave(User user) {
        players.remove(user);
        plugin.getLobbyManager().send(user);
        user.setFrozen(false);
    }

    public CompletableFuture<Boolean> scatter(User user) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            World world = user.getWorld();
            Location center = world.getWorldBorder().getCenter();
            double size = world.getWorldBorder().getSize() / 2;
            double centerX = center.getX();
            double centerZ = center.getZ();

            if (size > Short.MAX_VALUE) {
                size = Short.MAX_VALUE;
            }

            Location min = new Location(world, centerX - size, 0, centerZ + size);
            Location max = new Location(world, centerX + size, 200, centerZ - size);
            Location location = LocationUtil.chooseRandomLocation(min, max);
            location.getWorld().getHighestBlockYAt(location);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                user.teleport(location);
                future.complete(true);
            });
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to scatter " + user.getName(), e);
            future.complete(false);
        }

        return future;
    }

    public String getGameTime() {
        if (gameTimer == 0) {
            return "00:00";
        }

        return TimeUtil.getHowLongSince(TimeUtil.getCurrentTimeSeconds() - gameTimer);
    }

    public GameState getGameState() {
        return state;
    }

    public boolean isStarted() {
        return started;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public Set<User> getSpectators() {
        return spectators;
    }

}

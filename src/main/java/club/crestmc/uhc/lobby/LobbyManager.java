package club.crestmc.uhc.lobby;

import club.crestmc.uhc.UHC;
import club.crestmc.uhc.scoreboard.ScoreboardType;
import club.crestmc.uhc.user.User;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class LobbyManager {

    private final UHC plugin;
    private final LobbyListener listener;

    private final Set<User> players;

    public LobbyManager(UHC plugin) {
        this.plugin = plugin;
        this.listener = new LobbyListener(plugin, this);
        this.players = new HashSet<>();
    }

    public void send(User user) {
        players.add(user);

        plugin.getGameManager().getPlayers().remove(user);
        plugin.getGameManager().getSpectators().remove(user);

        user.getPlayer().setGameMode(GameMode.ADVENTURE);
        user.setScoreboardType(ScoreboardType.WAITING);
        user.setGame(null);
    }

    public User getNextAvailable() {
        return players.stream().findFirst().orElse(null);
    }

    public Set<User> getPlayers() {
        return players;
    }

}

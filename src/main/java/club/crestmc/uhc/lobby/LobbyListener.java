package club.crestmc.uhc.lobby;

import club.crestmc.uhc.UHC;
import club.crestmc.uhc.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class LobbyListener implements Listener {

    private final UHC plugin;
    private final LobbyManager lobbyManager;

    public LobbyListener(UHC plugin, LobbyManager lobbyManager) {
        this.plugin = plugin;
        this.lobbyManager = lobbyManager;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());
        lobbyManager.send(user);

        event.setJoinMessage("");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getGameManager().isStarted()) {
            return;
        }

        if (event.getPlayer().hasPermission("uhc.lobby.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getGameManager().isStarted()) {
            return;
        }

        if (event.getPlayer().hasPermission("uhc.lobby.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (plugin.getGameManager().isStarted()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (plugin.getGameManager().isStarted()) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(event.getFormat().replace("{HOST}", ""));
    }

}

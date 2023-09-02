package club.crestmc.uhc.game;

import club.crestmc.uhc.UHC;
import org.bukkit.event.Listener;

public class GameListener implements Listener {

    private final UHC plugin;
    private final GameManager gameManager;

    public GameListener(UHC plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}

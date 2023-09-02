package club.crestmc.uhc;

import club.crestmc.uhc.commands.CommandManager;
import club.crestmc.uhc.game.GameManager;
import club.crestmc.uhc.lobby.LobbyManager;
import club.crestmc.uhc.scenario.ScenarioManager;
import club.crestmc.uhc.user.UserManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class UHC extends JavaPlugin {

    @Getter
    private CommandManager commandManager;
    @Getter
    private UserManager userManager;
    @Getter
    private ScenarioManager scenarioManager;
    @Getter
    private GameManager gameManager;
    @Getter
    private LobbyManager lobbyManager;

    @Override
    public void onEnable() {
        getLogger().info("Loading configuration...");
        saveDefaultConfig();

        getLogger().info("Loading scenarios...");
        scenarioManager = new ScenarioManager(this);

        getLogger().info("Registering commands...");
        commandManager = new CommandManager(this);
        commandManager.registerCommands();

        getLogger().info("Loading user manager...");
        userManager = new UserManager(this);

        getLogger().info("Loading lobby manager...");
        lobbyManager = new LobbyManager(this);

        getLogger().info("Loading game manager...");
        gameManager = new GameManager(this);

        getLogger().info(getName() + " version " + getDescription().getVersion() + " by " + String.join(", ", getDescription().getAuthors()) + " has been successfully loaded and enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Unregistering commands...");
        commandManager.unregisterCommands();
        commandManager = null;

        getLogger().info("Unregistering scoreboard");
        userManager.getUsers().forEach(user -> user.getScoreboard().clear());

        getLogger().info("Destroying user manager instance...");
        userManager = null;

        getLogger().info(getName() + " version " + getDescription().getVersion() + " by " + String.join(", ", getDescription().getAuthors()) + " has been successfully shutdown.");
    }

}

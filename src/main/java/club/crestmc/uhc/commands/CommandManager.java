package club.crestmc.uhc.commands;

import club.crestmc.uhc.UHC;
import club.crestmc.uhc.util.ReflectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CommandManager implements Listener {

    private final UHC plugin;

    private final Map<UUID, Long> commandCooldowns;
    private SimpleCommandMap commandMap;
    private Map<String, Command> knownCommandsMap;

    public CommandManager(UHC plugin) {
        this.plugin = plugin;
        this.commandCooldowns = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        load();
    }

    public void registerCommands() {
        ReflectionUtil.consume("club.crestmc.uhc.commands.defined", getClass().getClassLoader(), CommandBase.class, this::register, true);
    }

    public void unregisterCommands() {
        ReflectionUtil.consume("club.crestmc.uhc.commands.defined", getClass().getClassLoader(), CommandBase.class, this::unregister, true);
    }

    public void register(CommandBase command) {
        // Remove command if it's already registered by another plugin
        knownCommandsMap.remove(command.getName().toLowerCase());
        command.getAliases().forEach(alias -> knownCommandsMap.remove(alias.toLowerCase()));

        // Register command in the command map
        commandMap.register(command.getName(), plugin.getName().toLowerCase(), command);

        // Remove fallback prefix command
        knownCommandsMap.remove(plugin.getName().toLowerCase() + ":" + command.getName());
        command.getAliases().forEach(alias -> knownCommandsMap.remove(plugin.getName().toLowerCase() + ":" + alias.toLowerCase()));
    }

    public void unregister(CommandBase command) {
        // Remove command if it's already registered by another plugin
        knownCommandsMap.remove(command.getName().toLowerCase());
        command.getAliases().forEach(alias -> knownCommandsMap.remove(alias.toLowerCase()));
    }

    @SuppressWarnings("unchecked")
    private void load() {
        // Check if the server's plugin manager is SimplePluginManager
        if (!(plugin.getServer().getPluginManager() instanceof SimplePluginManager)) {
            plugin.getLogger().severe("Could not load commands: SimplePluginManager not found!");
            return;
        }

        try {
            // Get the commandmap field
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(plugin.getServer().getPluginManager());

            // Get the direct hashmap of known commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommandsMap = (Map<String, Command>) knownCommandsField.get(this.commandMap);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load commands: " + e.getMessage(), e);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.split("/").length == 0) {
            return;
        }

        String commandLabel = message.split("/")[1].split(" ")[0];
        Command command = commandMap.getCommand(commandLabel);

        if (!(command instanceof CommandBase)) {
            return;
        }

        if (message.split(" ").length < 2) {
            return;
        }

        CommandBase commandBase = (CommandBase) commandMap.getCommand(commandLabel);
        long cooldown = commandBase.getCooldown();

        if (cooldown == 0) {
            return;
        }

        if (commandCooldowns.containsKey(event.getPlayer().getUniqueId())) {
            long secondsLeft = ((commandCooldowns.get(event.getPlayer().getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);

            if (secondsLeft > 0) {
                player.sendMessage(ChatColor.RED + "You are on command cooldown for " + secondsLeft + " more seconds.");
                event.setCancelled(true);
                return;
            }

            commandCooldowns.remove(player.getUniqueId());
        }

        commandCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
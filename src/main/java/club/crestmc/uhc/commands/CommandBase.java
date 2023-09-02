package club.crestmc.uhc.commands;

import club.crestmc.uhc.UHC;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommandBase extends BukkitCommand {

    protected final UHC plugin;
    private long cooldown;

    public CommandBase(String name) {
        super(name);

        this.plugin = JavaPlugin.getPlugin(UHC.class);
        this.cooldown = 0;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        dispatch(sender, commandLabel, args);
        return true;
    }

    /**
     * Executes the command
     *
     * @param sender Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args All arguments passed to the command, split via ' '
     */
    public abstract void dispatch(CommandSender sender, String commandLabel, String[] args);

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldown() {
        return cooldown;
    }

}
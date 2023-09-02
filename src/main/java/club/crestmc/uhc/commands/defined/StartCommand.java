package club.crestmc.uhc.commands.defined;

import club.crestmc.uhc.commands.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class StartCommand extends CommandBase {

    public StartCommand() {
        super("start");
        setAliases(Arrays.asList("startgame", "play", "forcestart", "fs"));
        setDescription("Command used to force start the game");
        setPermission("uhc.commands.forcestart");
        setUsage("/start");
    }

    @Override
    public void dispatch(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage UHC games.");
            return;
        }

        plugin.getGameManager().startPregame();
    }

}

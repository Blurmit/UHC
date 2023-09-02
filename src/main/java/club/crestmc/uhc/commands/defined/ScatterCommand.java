package club.crestmc.uhc.commands.defined;

import club.crestmc.uhc.commands.CommandBase;
import club.crestmc.uhc.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ScatterCommand extends CommandBase {

    public ScatterCommand() {
        super("scatter");
        setAliases(Collections.singletonList("scatterplayer"));
        setDescription("Scatters a player within the worldborder");
        setPermission("uhc.commands.scatter");
        setUsage("/scatter <player>");
    }

    @Override
    public void dispatch(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage UHC games.");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments! Please use one of the following: \n  - /scatter <player>");
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUser(player);
        plugin.getGameManager().scatter(user);
    }

}

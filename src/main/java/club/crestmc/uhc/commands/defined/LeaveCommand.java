package club.crestmc.uhc.commands.defined;

import club.crestmc.uhc.commands.CommandBase;
import club.crestmc.uhc.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class LeaveCommand extends CommandBase {

    public LeaveCommand() {
        super("leave");
        setAliases(Arrays.asList("exit"));
        setDescription("Command used to leave the game");
        setPermission("uhc.commands.leave");
        setUsage("/leave");
    }

    @Override
    public void dispatch(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage UHC games.");
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUser(player);
        plugin.getGameManager().leave(user);
    }

}

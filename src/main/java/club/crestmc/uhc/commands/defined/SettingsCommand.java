package club.crestmc.uhc.commands.defined;

import club.crestmc.uhc.commands.CommandBase;
import club.crestmc.uhc.scoreboard.ScoreboardType;
import club.crestmc.uhc.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SettingsCommand extends CommandBase {

    public SettingsCommand() {
        super("settings");
        setAliases(Arrays.asList("setting"));
        setDescription("Command used to edit the settings of the game");
        setPermission("uhc.commands.settings");
        setUsage("/settings");
    }

    @Override
    public void dispatch(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can interact with UHC games.");
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUser(player);

        if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("edit")) {
                return;
            }

            if (!args[1].equalsIgnoreCase("scoreboard")) {
                return;
            }

            switch (args[2].toLowerCase()) {
                case "waiting":
                    user.setScoreboardType(ScoreboardType.WAITING);
                    break;
                case "scattering_awaiting":
                    user.setScoreboardType(ScoreboardType.SCATTERING_AWAITING);
                    break;
                case "scattering_complete":
                    user.setScoreboardType(ScoreboardType.SCATTERING_COMPLETE);
                    break;
                case "playing":
                    user.setScoreboardType(ScoreboardType.PLAYING);
                    break;
                case "host":
                    user.setScoreboardType(ScoreboardType.HOST);
                    break;
                case "freeze":
                    user.setFrozen(user.getPlayer().isFlying());
                    break;
                case "ending":
                    user.setScoreboardType(ScoreboardType.ENDING);
                    break;
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("edit", "disable", "enable", "toggle", "test");
        }

        if (args.length == 2) {
            return Arrays.asList("scoreboard", "border", "max_players", "start_timer", "host", "restart_timer", "scatter");
        }

        return Arrays.asList("waiting", "scattering_awaiting", "scattering_complete", "playing", "host", "ending");
    }

}

package club.crestmc.uhc.commands.defined;

import club.crestmc.uhc.commands.CommandBase;
import club.crestmc.uhc.scenario.Scenario;
import club.crestmc.uhc.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class DisableScenario extends CommandBase {

    public DisableScenario() {
        super("disablescenario");
        setAliases(Collections.singletonList("endscenario"));
        setDescription("Disables a scenario");
        setPermission("uhc.commands.disablescenario");
        setUsage("/disablescenario <scenario>");
    }

    @Override
    public void dispatch(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can manage UHC games.");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments! Please use one of the following: \n  - /disablescenario <scenario>");
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUser(player);
        Scenario scenario = plugin.getScenarioManager().getScenario(args[0]);

        if (scenario == null) {
            sender.sendMessage(ChatColor.RED + "That scenario does not exist! Valid scenarios include: " + plugin.getScenarioManager().getScenarios());
            return;
        }

        sender.sendMessage(ChatColor.RED + "Disabling scenario " + scenario.getName() + "...");
        plugin.getScenarioManager().disable(scenario);
    }

}

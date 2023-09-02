package club.crestmc.uhc.user;

import club.crestmc.uhc.UHC;
import club.crestmc.uhc.scoreboard.ScoreboardPlaceholder;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class UserListener implements Listener {

    private final UHC plugin;
    private final UserManager userManager;

    public UserListener(UHC plugin, UserManager userManager) {
        this.plugin = plugin;
        this.userManager = userManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        User user = userManager.getUser(event.getPlayer());
        user.clear();

        plugin.getLogger().info(user.getName() + " logged in.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        User user = userManager.getUser(event.getPlayer());
        userManager.deleteUser(user);

        plugin.getLogger().info(user.getName() + " logged out.");
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        net.minecraft.server.v1_8_R3.Entity passenger = ((CraftEntity) event.getVehicle()).getHandle().passenger;
        Vehicle vehicle = event.getVehicle();

        if (!(passenger instanceof EntityPlayer)) {
            return;
        }

        if (!(vehicle instanceof UserFrozenEntity)) {
            return;
        }

        Player player = (Player) passenger.getBukkitEntity();
        User user = userManager.getUser(player);
        event.setCancelled(user.isFrozen());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = userManager.getUser(player);
        event.setCancelled(user.isFrozen());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        User user = userManager.getUser(player);
        event.setCancelled(user.isFrozen());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity hurt = event.getEntity();

        if (!(attacker instanceof Player)) {
            return;
        }

        if (!(hurt instanceof Player)) {
            return;
        }

        Player player = (Player) attacker;
        User user = userManager.getUser(player);
        event.setCancelled(!user.getCanPvP());
    }

}

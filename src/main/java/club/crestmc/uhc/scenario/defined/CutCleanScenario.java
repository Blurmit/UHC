package club.crestmc.uhc.scenario.defined;

import club.crestmc.uhc.scenario.Scenario;
import club.crestmc.uhc.user.User;
import net.minecraft.server.v1_8_R3.BlockOre;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class CutCleanScenario extends Scenario {

    public CutCleanScenario() {
        setName("CutClean");
        setDescription("All ores are automatically smelted!");
        setAuthor("Blurmit");
        setIcon(Material.IRON_INGOT);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUserManager().getUser(player);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (user.isHost()) {
            return;
        }

        if (user.isSpectating()) {
            return;
        }

        Block block = event.getBlock();
        Material material = block.getType();
        ItemStack item = getItemFromOre(material);

        if (item == null) {
            return;
        }

        World world = block.getWorld();
        Location location = block.getLocation();

        event.setCancelled(true);
        block.setType(Material.AIR);

        Map<Enchantment, Integer> enchantments = player.getItemInHand().getEnchantments();
        Integer level = enchantments.get(Enchantment.LOOT_BONUS_BLOCKS);
        if (level == null) {
            return;
        }

        int dropCount = new BlockOre().getDropCount(level, new Random());
        item.setAmount(dropCount);
        world.dropItem(location, item);
    }

    private ItemStack getItemFromOre(Material ore) {
        String name = ore.name();

        if (!name.contains("_ORE")) {
            return null;
        }

        name = name.split("_")[0] + "_INGOT";
        Material material = Material.getMaterial(name);
        if (material == null) {
            return null;
        }

        return new ItemStack(material);
    }

}

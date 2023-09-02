package club.crestmc.uhc.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.concurrent.ThreadLocalRandom;

public class LocationUtil {

    public static Location chooseRandomLocation(Location min, Location max) {
        Location range = new Location(min.getWorld(), Math.abs(max.getX() - min.getX()), ThreadLocalRandom.current().nextInt(0, 256), Math.abs(max.getZ() - min.getZ()));
        Location randomLocation = new Location(min.getWorld(), (Math.random() * range.getX()) + (Math.min(min.getX(), max.getX())), range.getY(), (Math.random() * range.getZ()) + (Math.min(min.getZ(), max.getZ())));

        Block block = randomLocation.getBlock();

        while (block.getType() != Material.AIR && block.getRelative(0, 1, 0).getType() != Material.AIR) {
            randomLocation.setY(randomLocation.getY() + 1);
            block = randomLocation.getBlock();
        }

        while (block.getRelative(0, -1, 0).getType() == Material.AIR) {
            randomLocation.setY(randomLocation.getY() - 1);
            block = randomLocation.getBlock();
        }

        if (block.getRelative(0, -1, 0).getType() != Material.LAVA &&
                block.getRelative(0, -1, 0).getType() != Material.CACTUS &&
                block.getRelative(0, -1, 0).getType() != Material.WATER &&
                block.getType() != Material.LAVA &&
                block.getType() != Material.CACTUS &&
                block.getType() != Material.WATER &&
                block.getRelative(0, 1, 0).getType() != Material.LAVA &&
                block.getRelative(0, 1, 0).getType() != Material.WATER &&
                block.getRelative(0, 1, 0).getType() != Material.CACTUS) {
            randomLocation.setY(randomLocation.getY() + 1);
            return randomLocation;
        }

        return chooseRandomLocation(min, max);
    }

}

package club.crestmc.uhc.user;

import net.minecraft.server.v1_8_R3.EntityBat;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftBat;
import org.bukkit.entity.Vehicle;

public class UserFrozenEntity extends CraftBat implements Vehicle {

    public UserFrozenEntity(CraftServer server, EntityBat entity) {
        super(server, entity);
    }

}

package club.crestmc.uhc.user;

import club.crestmc.uhc.UHC;
import club.crestmc.uhc.game.GameManager;
import club.crestmc.uhc.game.team.GameTeam;
import club.crestmc.uhc.scoreboard.ScoreboardType;
import club.crestmc.uhc.scoreboard.UHCScoreboard;
import club.crestmc.uhc.util.ChatUtil;
import club.crestmc.uhc.util.ReflectionUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class User {

    private final UHC plugin;

    private final Player player;
    private final UHCScoreboard scoreboard;

    private GameManager game;
    private GameTeam team;

    private boolean canMove = true;
    private boolean hidden = false;
    private boolean frozen = false;
    private Entity frozenEntity;
    private boolean invisible = false;
    private boolean pvp = false;

    public User(Player player) {
        this.plugin = JavaPlugin.getPlugin(UHC.class);

        this.player = player;
        this.scoreboard = new UHCScoreboard(plugin.getGameManager().getGameState().getScoreboard()).show(player);
    }

    public String getName() {
        return player.getName();
    }

    public String getDisplayName() {
        if (team == null) {
            return getName();
        }

        return getName(); // TODO: Team color
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public void setGame(GameManager game) {
        this.game = game;
    }

    public GameManager getGame() {
        return game;
    }

    public World getWorld() {
        return player.getWorld();
    }

    public Location getLocation() {
        return player.getLocation();
    }

    public Inventory getInventory() {
        return player.getInventory();
    }

    public void clear() {
        getInventory().clear();
        getPlayer().getActivePotionEffects().forEach(effect -> getPlayer().removePotionEffect(effect.getType()));
        getPlayer().setExp(0);
        getPlayer().setLevel(0);
        getPlayer().setFoodLevel(20);
        getPlayer().setHealth(getPlayer().getMaxHealth());
        setScoreboardType(plugin.getGameManager().getGameState().getScoreboard());
    }

    public void heal() {
        getPlayer().setHealth(getPlayer().getMaxHealth());
    }

    public void teleport(Location location) {
        player.teleport(location);
    }

    public void setCanPvP(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean getCanPvP() {
        return pvp;
    }

    public UHCScoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboardType(ScoreboardType type) {
        scoreboard.setType(type);
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public void sendTitle(String title) {
        sendTitle(title, null);
    }

    public void sendTitle(String title, String subtitle) {
        ChatUtil.sendTitle(player, title, subtitle, 0, 60, 0);
    }

    public void sendActionBar(String message) {
        ChatUtil.sendActionBar(player, message);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean isInGame() {
        return game != null;
    }

    public void setHost(boolean states) {

    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        EntityPlayer playerHandle = ((CraftPlayer) player).getHandle();
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) player.getWorld()).getHandle();

        if (frozen) {
            Location location = player.getLocation();

            EntityBat entity = new EntityBat(world);
            entity.setSize(0, 0);
            entity.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
            entity.ai = false;
            entity.passenger = playerHandle;
            playerHandle.vehicle = entity;

            Vehicle bukkitEntity = new UserFrozenEntity((CraftServer) plugin.getServer(), entity);
            ReflectionUtil.setField(entity, "net.minecraft.server.v1_8_R3.Entity", "bukkitEntity", bukkitEntity);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                entity.setInvisible(true);

                playerHandle.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(entity));
                playerHandle.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, playerHandle, entity));
            }, 2);

            frozenEntity = entity;
        } else {
            playerHandle.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(frozenEntity.getId()));

            frozenEntity.getBukkitEntity().remove();
            frozenEntity = null;
        }

        this.frozen = frozen;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        EntityTracker tracker = ((WorldServer) handle.world).tracker;

        if (invisible) {
            plugin.getServer().getOnlinePlayers().stream()
                    .filter(onlinePlayer -> !onlinePlayer.equals(player))
                    .map(onlinePlayer -> ((CraftPlayer) player).getHandle())
                    .forEach(otherPlayer -> {
                        EntityTrackerEntry entry = tracker.trackedEntities.get(otherPlayer.getId());

                        if (entry != null) {
                            entry.clear(handle);
                        }
                    });
        } else {
            plugin.getServer().getOnlinePlayers().stream()
                    .filter(onlinePlayer -> !onlinePlayer.equals(player))
                    .map(onlinePlayer -> ((CraftPlayer) player).getHandle())
                    .forEach(otherPlayer -> {
                        EntityTrackerEntry entry = tracker.trackedEntities.get(otherPlayer.getId());

                        if (entry != null && !entry.trackedPlayers.contains(handle)) {
                            entry.updatePlayer(handle);
                        }
                    });
        }

        this.invisible = invisible;
    }

    public boolean isHost() {
        return false;
    }

    public void setSpectating() {

    }

    public boolean isSpectating() {
        return false;
    }

    public Player getPlayer() {
        return player;
    }

}

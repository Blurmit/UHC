package club.crestmc.uhc.user;

import club.crestmc.uhc.UHC;
import com.google.gson.stream.JsonReader;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private final UHC plugin;
    private final Set<User> users;

    public UserManager(UHC plugin) {
        this.plugin = plugin;
        this.users = new HashSet<>();

        plugin.getServer().getPluginManager().registerEvents(new UserListener(plugin, this), plugin);
    }

    public User createUser(Player player) {
        User user = new User(player);
        users.add(user);

        return user;
    }

    public void deleteUser(User user) {
        users.remove(user);
    }

    public User getUser(Player player) {
        return users.stream()
                .filter(user -> user.getUUID().equals(player.getUniqueId()))
                .findFirst()
                .orElseGet(() -> createUser(player));
    }

    public Set<User> getUsers() {
        return users;
    }

}

package club.crestmc.uhc.scenario;

import club.crestmc.uhc.UHC;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Scenario implements Listener {

    protected final UHC plugin;

    private String name;
    private String description;
    private String author;
    private Material icon;
    private boolean enabled = false;

    protected Scenario() {
        this.plugin = JavaPlugin.getPlugin(UHC.class);
        this.name = "Unset Scenario Name";
        this.description = "Unset Scenario Description";
        this.icon = Material.PAPER;
    }

    protected void onLoad() {}

    protected void onEnable() {}

    protected void onDisable() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public Material getIcon() {
        return icon;
    }

    public void setEnabled(boolean enabled) {
        try {
            if (enabled && !isEnabled()) {
                onEnable();
                plugin.getServer().getPluginManager().registerEvents(this, plugin);
            } else if (!enabled && isEnabled()) {
                onDisable();
                HandlerList.unregisterAll(this);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not enable scenario " + getName() + " because " + e.getCause().getMessage() + ". Contact the scenario author (" + getAuthor() + ") for more.");
        }

        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }


}

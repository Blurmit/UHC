package club.crestmc.uhc.scoreboard;

import club.crestmc.uhc.UHC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public enum ScoreboardType {

    WAITING() {
        @Override
        public String getTitle() {
            return CONFIG.getString("Waiting-Title");
        }

        @Override
        public List<String> getEntries() {
            return CONFIG.getStringList("Waiting-Entries");
        }
    },
    SCATTERING_AWAITING() {
        @Override
        public String getTitle() {
            return CONFIG.getString("Scattering-Awaiting-Title");
        }

        @Override
        public List<String> getEntries() {
            return CONFIG.getStringList("Scattering-Awaiting-Entries");
        }
    },
    SCATTERING_COMPLETE() {
        @Override
        public String getTitle() {
            return CONFIG.getString("Scattering-Complete-Title");
        }

        @Override
        public List<String> getEntries() {
            return CONFIG.getStringList("Scattering-Complete-Entries");
        }
    },
    PLAYING() {
        @Override
        public String getTitle() {
            return CONFIG.getString("Playing-Title");
        }

        @Override
        public List<String> getEntries() {
            return CONFIG.getStringList("Playing-Entries");
        }
    },
    HOST() {
        @Override
        public String getTitle() {
            return CONFIG.getString("Host-Title");
        }

        @Override
        public List<String> getEntries() {
            return CONFIG.getStringList("Host-Entries");
        }
    },
    ENDING() {
        @Override
        public String getTitle() {
            return CONFIG.getString("Ending-Title");
        }

        @Override
        public List<String> getEntries() {
            return CONFIG.getStringList("Ending-Entries");
        }
    };

    public abstract String getTitle();
    public abstract List<String> getEntries();

    private static final UHC PLUGIN = JavaPlugin.getPlugin(UHC.class);
    private static final ConfigurationSection CONFIG = PLUGIN.getConfig().getConfigurationSection("Scoreboards");

}

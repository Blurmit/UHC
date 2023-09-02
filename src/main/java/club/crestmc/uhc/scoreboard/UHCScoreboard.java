package club.crestmc.uhc.scoreboard;

import club.crestmc.uhc.UHC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UHCScoreboard {

    private final UHC plugin;

    private final Scoreboard scoreboard;
    private final Objective objective;

    private String title;
    private ScoreboardType type;

    private List<ScoreboardEntry> entries;

    public UHCScoreboard(ScoreboardType type) {
        this.plugin = JavaPlugin.getPlugin(UHC.class);
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();

        this.objective = scoreboard.registerNewObjective("UHC-Display", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        setType(type);
        handleUpdates();
    }

    public UHCScoreboard setTitle(String title) {
        this.title = title;
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));

        return this;
    }

    public UHCScoreboard setType(ScoreboardType type) {
        this.type = type;

        setTitle(type.getTitle());
        load();

        return this;
    }

    public UHCScoreboard update(ScoreboardEntry entry) {
        String text = getFormattedText(entry.getText());
        Team team = entry.getTeam();
        String id = ChatColor.values()[entry.getID()].toString() + ChatColor.RESET;

        String prefix = text.substring(0, Math.min(text.length(), 16));
        String suffix = "";

        if (text.length() > 32) {
            text = text.substring(0, 32);
        }

        if (text.length() > 16) {
            prefix = text.substring(0, 16);
            String originalLastColor = ChatColor.getLastColors(prefix).equals("") ? ChatColor.RESET + "" : ChatColor.getLastColors(prefix);
            suffix = id + originalLastColor + text.substring(16, Math.min(32, text.length()));

            boolean splitPrefix = prefix.endsWith("§");
            if (splitPrefix) {
                prefix = text.substring(0, 15);
                suffix = text.substring(15, Math.min(32, text.length()));
            }

            String lastColors = ChatColor.getLastColors(prefix);
            suffix = lastColors + suffix;
        }

        if (team.getPrefix().equals(prefix) && team.getSuffix().equals(suffix)) {
            return this;
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        team.addEntry(id);
        entry.setPlayerEntry(id);

        return this;
    }

    public UHCScoreboard show(Player player) {
        player.setScoreboard(scoreboard);

        return this;
    }

    public UHCScoreboard hide(Player player) {
        player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());

        return this;
    }

    public void load() {
        int id = 0;
        List<ScoreboardEntry> newEntries = type.getEntries().stream()
                .map(entry -> new ScoreboardEntry(ChatColor.translateAlternateColorCodes('&', entry)))
                .collect(Collectors.toList());
        Collections.reverse(newEntries);

        if (entries != null) {
            while (entries.size() > newEntries.size()) {
                removeEntry(entries.size() - 1);
            }
        }

        for (ScoreboardEntry entry : newEntries) {
            entry.setID(++id);

            String teamName = "SB-Team-" + id;
            Team team = scoreboard.getTeam(teamName);

            if (team != null) {
                entry.setTeam(team);
                update(entry);
                continue;
            }

            team = scoreboard.registerNewTeam(teamName);
            entry.setTeam(team);
            update(entry);

            objective.getScore(entry.getPlayerEntry()).setScore(id);
        }

        this.entries = newEntries;
    }

    public void clear() {
        if (entries == null) {
            return;
        }

        IntStream.range(0, entries.size()).forEach(i -> removeEntry(0));
    }

    public UHCScoreboard addEntry(ScoreboardEntry entry) {
        if (entries.size() > 15) {
            throw new IllegalArgumentException("Index out of bounds. Expected 0-15, got " + entries.size());
        }

        entries.add(entry);

        return this;
    }

    public UHCScoreboard removeEntry(int index) {
        if (index < 0 || index >= entries.size()) {
            throw new IllegalArgumentException("Index out of bounds. Expected 0-" + entries.size() + ", got " + index);
        }

        ScoreboardEntry entry = entries.remove(index);
        Team team = entry.getTeam();

        if (team != null) {
            team.getEntries().forEach(scoreboard::resetScores);
            team.unregister();
        }

        return this;
    }

    public ScoreboardEntry getEntry(int index) {
        return entries.get(index);
    }

    private String getFormattedText(String text) {
        text = text.replaceAll("(§[a-f0-9]) *", "$1");
        text = text.replaceAll("(§[a-f0-9])(§[a-f0-9])+", "$2");

        return text;
    }

    public List<ScoreboardEntry> getEntries() {
        return entries;
    }

    public String getTitle() {
        return this.title;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }

}

package club.crestmc.uhc.game.team;

import org.bukkit.ChatColor;

public enum TeamColor {

    TEAM(ChatColor.GREEN),
    ENEMY(ChatColor.RED),
    SPECTATING(ChatColor.GRAY);

    private final ChatColor color;

    TeamColor(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getName() {
        String name = name().toLowerCase();
        char firstLetter = Character.toUpperCase(name.charAt(0));
        name = firstLetter + name.substring(1);

        return name;
    }

}

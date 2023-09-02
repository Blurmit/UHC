package club.crestmc.uhc.util;

import club.crestmc.uhc.UHC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;

public class ChatUtil {

    private static final UHC plugin = JavaPlugin.getPlugin(UHC.class);
    private static boolean isLegacyServer = false;

    static {
        String version = ReflectionUtil.getServerPackageVersion();

        if (version.equalsIgnoreCase("v1_8_R1") || version.startsWith("v1_7_")) {
            isLegacyServer = true;
        }
    }

    public static void sendActionBar(Player player, String message, long duration) {
        sendActionBar(player, message);

        if (duration >= 0) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> sendActionBar(player, ""), duration + 1L);
        }

        if (duration < 40) {
            return;
        }

        while (duration > 40) {
            duration -= 40;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> sendActionBar(player, message), duration);
        }
    }

    public static void sendActionBar(Player player, String message) {
        try {
            Object packet;
            Class<?> packetPlayOutChatClass = ReflectionUtil.getNMSClass("PacketPlayOutChat");

            if (isLegacyServer) {
                Class<?> chatSerializerClass = ReflectionUtil.getNMSClass("ChatSerializer");
                Class<?> iChatBaseComponentClass = ReflectionUtil.getNMSClass("IChatBaseComponent");

                Method methodThree = chatSerializerClass.getDeclaredMethod("a", String.class);
                Object chatBaseComponent = iChatBaseComponentClass.cast(methodThree.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));

                packet = ReflectionUtil.construct(packetPlayOutChatClass, chatBaseComponent, (byte) 2);
            } else {
                Class<?> chatComponentTextClass = ReflectionUtil.getNMSClass("ChatComponentText");

                try {
                    Class<?> chatMessageTypeClass = ReflectionUtil.getNMSClass("ChatMessageType");
                    Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();

                    Object chatMessageType = Arrays.stream(chatMessageTypes)
                            .filter(type -> type.toString().equals("GAME_INFO"))
                            .findFirst()
                            .orElse(null);

                    Object chatComponentText = ReflectionUtil.construct(chatComponentTextClass, message);
                    packet = ReflectionUtil.construct(packetPlayOutChatClass, chatComponentText, chatMessageType);
                } catch (ClassNotFoundException e) {
                    Object chatComponentText = ReflectionUtil.construct(chatComponentTextClass, message);
                    packet = ReflectionUtil.construct(packetPlayOutChatClass, chatComponentText, (byte) 2);
                }
            }

            ReflectionUtil.sendPacket(player, packet);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Something went wrong whilst attempting to send actionbar packet to " + player.getName() + "!", e);
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Class<?> packetPlayOutTitleClass = ReflectionUtil.getNMSClass("PacketPlayOutTitle");
            Class<?> iChatBaseComponentClass = ReflectionUtil.getNMSClass("IChatBaseComponent");

            Object packetPlayOutTitle;
            Object chatTitle;
            Object chatSubtitle;
            Object titlePacket;
            Object subtitlePacket;

            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);

                packetPlayOutTitle = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TIMES").get(null);
                chatTitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
                titlePacket = ReflectionUtil.construct(packetPlayOutTitleClass, packetPlayOutTitle, chatTitle, fadeIn, stay, fadeOut);
                ReflectionUtil.sendPacket(player, titlePacket);

                packetPlayOutTitle = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TITLE").get(null);
                chatTitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                titlePacket = ReflectionUtil.construct(packetPlayOutTitleClass, packetPlayOutTitle, chatTitle);
                ReflectionUtil.sendPacket(player, titlePacket);
            }

            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

                packetPlayOutTitle = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TIMES").get(null);
                chatSubtitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitlePacket = ReflectionUtil.construct(packetPlayOutTitleClass, packetPlayOutTitle, chatSubtitle, fadeIn, stay, fadeOut);
                ReflectionUtil.sendPacket(player, subtitlePacket);

                packetPlayOutTitle = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitlePacket = ReflectionUtil.construct(packetPlayOutTitleClass, packetPlayOutTitle, chatSubtitle, fadeIn, stay, fadeOut);
                ReflectionUtil.sendPacket(player, subtitlePacket);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Something went wrong whilst attempting to send title packet to " + player.getName() + "!", e);
        }
    }

}

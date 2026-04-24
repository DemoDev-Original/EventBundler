package com.eventbundler.commands;

import com.eventbundler.EventBundler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventBundlerCommand implements CommandExecutor, TabCompleter {
    
    private final EventBundler plugin;
    
    public EventBundlerCommand(EventBundler plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("eventbundler.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "EventBundler reloaded successfully!");
                return true;
                
            case "list":
                if (!sender.hasPermission("eventbundler.list")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                listEvents(sender, args);
                return true;
                
            case "info":
                if (!sender.hasPermission("eventbundler.list")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                showInfo(sender);
                return true;
                
            case "toggle":
                if (!sender.hasPermission("eventbundler.toggle")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                toggleEvent(sender, args);
                return true;
                
            case "stats":
                if (!sender.hasPermission("eventbundler.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                showStatistics(sender);
                return true;
                
            case "reset":
                if (!sender.hasPermission("eventbundler.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                plugin.getEventManager().resetStatistics();
                sender.sendMessage(ChatColor.GREEN + "Event statistics reset!");
                return true;
                
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== EventBundler Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/eb reload" + ChatColor.GRAY + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/eb list [category]" + ChatColor.GRAY + " - List events");
        sender.sendMessage(ChatColor.YELLOW + "/eb info" + ChatColor.GRAY + " - Show plugin information");
        sender.sendMessage(ChatColor.YELLOW + "/eb toggle <event> [on|off]" + ChatColor.GRAY + " - Toggle event");
        sender.sendMessage(ChatColor.YELLOW + "/eb stats" + ChatColor.GRAY + " - Show event statistics");
        sender.sendMessage(ChatColor.YELLOW + "/eb reset" + ChatColor.GRAY + " - Reset statistics");
    }
    
    private void listEvents(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.GOLD + "=== Event Categories ===");
            plugin.getEventManager().getEnabledCategories().forEach(cat -> 
                sender.sendMessage(ChatColor.YELLOW + "- " + cat)
            );
            sender.sendMessage(ChatColor.GRAY + "Use /eb list <category> to see events");
        } else {
            String category = args[1].toLowerCase();
            sender.sendMessage(ChatColor.GOLD + "=== " + category + " Events ===");
            plugin.getEventManager().getEnabledEvents().stream()
                    .filter(e -> e.contains(category))
                    .forEach(event -> sender.sendMessage(ChatColor.YELLOW + "- " + event));
        }
    }
    
    private void showInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== EventBundler Info ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Categories: " + ChatColor.WHITE + plugin.getEventManager().getEnabledCategoriesCount());
        sender.sendMessage(ChatColor.YELLOW + "Events: " + ChatColor.WHITE + plugin.getEventManager().getEnabledEventsCount() + "/110");
        sender.sendMessage(ChatColor.YELLOW + "API: " + ChatColor.WHITE + plugin.getConfigManager().getApiAccess());
        sender.sendMessage(ChatColor.YELLOW + "Debug: " + ChatColor.WHITE + plugin.getConfigManager().isDebugEnabled());
    }
    
    private void toggleEvent(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /eb toggle <event> [on|off]");
            return;
        }
        
        String eventName = args[1].toLowerCase();
        boolean enable = args.length == 2 || args[2].equalsIgnoreCase("on");
        
        plugin.getEventManager().toggleEvent(eventName, enable);
        sender.sendMessage(ChatColor.GREEN + "Event " + eventName + " " + (enable ? "enabled" : "disabled"));
    }
    
    private void showStatistics(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Top 10 Fired Events ===");
        plugin.getEventManager().getEventStatistics().entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .forEach(entry -> sender.sendMessage(
                        ChatColor.YELLOW + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue()
                ));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "list", "info", "toggle", "stats", "reset").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            return new ArrayList<>(plugin.getEventManager().getEnabledCategories());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            return new ArrayList<>(plugin.getEventManager().getEnabledEvents());
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("toggle")) {
            return Arrays.asList("on", "off");
        }
        
        return new ArrayList<>();
    }
}
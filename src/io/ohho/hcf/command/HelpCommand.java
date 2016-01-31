package io.ohho.hcf.command;

import com.parapvp.util.BukkitUtils;

import io.ohho.hcf.ConfigurationService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class HelpCommand
  implements CommandExecutor, TabCompleter
{
  ChatColor MAIN_COLOR = ChatColor.BLUE;
  ChatColor SECONDARY_COLOR = ChatColor.AQUA;
  ChatColor EXTRA_COLOR = ChatColor.YELLOW;
  ChatColor VALUE_COLOR = ChatColor.GRAY;
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    sender.sendMessage(this.VALUE_COLOR + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    sender.sendMessage(this.MAIN_COLOR + " Warzone Radius: " + this.VALUE_COLOR + 1000);
    sender.sendMessage(this.MAIN_COLOR + " Border Size: ");
    for (World.Environment environment : World.Environment.values()) {
      sender.sendMessage(this.SECONDARY_COLOR + "  " + WordUtils.capitalizeFully(environment.name().replace('_', ' ')) + ": " + this.VALUE_COLOR + ConfigurationService.BORDER_SIZES.getOrDefault(environment, Integer.valueOf(0)));
    }
    sender.sendMessage(this.MAIN_COLOR + " End Portal Locations: ");
    sender.sendMessage(this.SECONDARY_COLOR + "  Location: " + this.VALUE_COLOR + "Overworld, -1000 | -1000");
    sender.sendMessage(this.SECONDARY_COLOR + "  Location: " + this.VALUE_COLOR + "Overworld, 1000 | 1000");
    sender.sendMessage(this.SECONDARY_COLOR + "  Location: " + this.VALUE_COLOR + "Overworld, -1000 | 1000");
    sender.sendMessage(this.SECONDARY_COLOR + "  Location: " + this.VALUE_COLOR + "Overworld, 1000 | -1000");
    sender.sendMessage(this.MAIN_COLOR + " Teamspeak: " + this.VALUE_COLOR + "ts.HCPots.net");
    sender.sendMessage(this.MAIN_COLOR + " Server rules: " + this.VALUE_COLOR + "/rules");
    sender.sendMessage(this.MAIN_COLOR + " subreddit: " + this.VALUE_COLOR + "https://www.reddit.com/r/CapPvP/");
    sender.sendMessage("");
    sender.sendMessage(this.EXTRA_COLOR + " If you require additional assistance contact a staff member.");
    sender.sendMessage(this.VALUE_COLOR + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
}

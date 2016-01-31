package io.ohho.hcf.command;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.oCore.faction.FactionManager;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class LocationCommand
  implements CommandExecutor, TabCompleter
{
  private final HCF plugin;
  
  public LocationCommand(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    Player target;
    Player target1;
    if ((args.length >= 1) && (sender.hasPermission(command.getPermission() + ".others")))
    {
      target1 = Bukkit.getPlayer(args[0]);
    }
    else
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " [playerName]");
        return true;
      }
      target1 = (Player)sender;
    }
    if ((target1 == null) || (((sender instanceof Player)) && (!((Player)sender).canSee(target1))))
    {
      sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
      return true;
    }
    Location location = target1.getLocation();
    Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
    sender.sendMessage(ChatColor.YELLOW + target1.getName() + " is in the territory of " + factionAt.getDisplayName(sender) + ChatColor.YELLOW + '(' + (factionAt.isSafezone() ? ChatColor.GREEN + "Non-Deathban" : new StringBuilder().append(ChatColor.RED).append("Deathban").toString()) + ChatColor.YELLOW + ')');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return (args.length == 1) && (sender.hasPermission(command.getPermission() + ".others")) ? null : Collections.emptyList();
  }
}

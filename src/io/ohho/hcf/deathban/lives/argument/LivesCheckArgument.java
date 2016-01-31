package io.ohho.hcf.deathban.lives.argument;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.deathban.DeathbanManager;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LivesCheckArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public LivesCheckArgument(HCF plugin)
  {
    super("check", "Check how much lives a player has");
    this.plugin = plugin;
    this.permission = ("hcf.command.lives.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " [playerName]";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    OfflinePlayer target;
    OfflinePlayer target1;
    if (args.length > 1)
    {
      target1 = Bukkit.getOfflinePlayer(args[1]);
    }
    else
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        return true;
      }
      target1 = (OfflinePlayer)sender;
    }
    if ((!target1.hasPlayedBefore()) && (!target1.isOnline()))
    {
      sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
      return true;
    }
    int targetLives = this.plugin.getDeathbanManager().getLives(target1.getUniqueId());
    sender.sendMessage(ChatColor.YELLOW + target1.getName() + ChatColor.YELLOW + " has " + ChatColor.LIGHT_PURPLE + targetLives + ChatColor.YELLOW + ' ' + (targetLives == 1 ? "life" : "lives") + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 2 ? null : Collections.emptyList();
  }
}

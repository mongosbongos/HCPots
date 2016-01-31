package io.ohho.hcf.command;

import io.ohho.hcf.HCF;
import io.ohho.hcf.timer.PlayerTimer;
import io.ohho.hcf.timer.TimerManager;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class GoppleCommand
  implements CommandExecutor, TabCompleter
{
  private final HCF plugin;
  
  public GoppleCommand(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    Player player = (Player)sender;
    PlayerTimer timer = this.plugin.getTimerManager().notchAppleTimer;
    long remaining = timer.getRemaining(player);
    if (remaining <= 0L)
    {
      sender.sendMessage(ChatColor.RED + "Your " + timer.getDisplayName() + ChatColor.RED + " timer is no longer active.");
      return true;
    }
    sender.sendMessage(ChatColor.YELLOW + "Your " + timer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
}

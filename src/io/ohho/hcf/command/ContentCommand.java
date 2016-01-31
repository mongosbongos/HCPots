package io.ohho.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContentCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args)
  {
    Player player = (Player)cs;
    if (args.length != 1)
    {
      player.sendMessage(ChatColor.RED + "Usage: /" + s + " [link]");
      return true;
    }
    if (((args[0].contains("twitch.tv")) && (args[0].contains(".tv"))) || ((args[0].contains("youtube")) && (args[0].contains(".com")))) {
      Bukkit.broadcastMessage(ChatColor.GRAY + "{" + ChatColor.GREEN + "Alert" + ChatColor.GRAY + "]" + ChatColor.GREEN + args[0]);
    }
    return false;
  }
}

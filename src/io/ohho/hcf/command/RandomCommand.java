package io.ohho.hcf.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.ohho.hcf.HCF;

public class RandomCommand
  implements CommandExecutor
{
  private final HCF plugin;
  
  public RandomCommand(HCF plugin)
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
    List<Player> players = new ArrayList();
    for (Player players1 : Bukkit.getOnlinePlayers()) {
      players.add(players1);
    }
    Collections.shuffle(players);
    Random random = new Random();
    Integer randoms = Integer.valueOf(random.nextInt(Bukkit.getOnlinePlayers().length) + 1);
    Player p = (Player)players.get(randoms.intValue());
    if ((player.canSee(p)) && (player.hasPermission(command.getPermission() + ".teleport")))
    {
      player.teleport(p);
      player.sendMessage(ChatColor.YELLOW + "You've teleported to " + p.getName());
    }
    else if (player.canSee(p))
    {
      player.sendMessage(ChatColor.YELLOW + "You've found " + p.getName());
    }
    else
    {
      player.sendMessage(ChatColor.RED + "Player not found");
    }
    return true;
  }
}

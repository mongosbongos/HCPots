package io.ohho.hcf.command;

import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.ohho.hcf.listener.DeathListener;

public class RefundCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args)
  {
    String Usage = ChatColor.RED + "/" + s + " <playerName> <reason>";
    if (!(cs instanceof Player))
    {
      cs.sendMessage(ChatColor.RED + "You must be a player");
      return true;
    }
    Player p = (Player)cs;
    if (args.length < 2)
    {
      cs.sendMessage(Usage);
      return true;
    }
    if (Bukkit.getPlayer(args[0]) == null)
    {
      p.sendMessage(ChatColor.RED + "Player must be online");
      return true;
    }
    Player target = Bukkit.getPlayer(args[0]);
    if (DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId()))
    {
      target.getInventory().setContents((ItemStack[])DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
      target.getInventory().setArmorContents((ItemStack[])DeathListener.PlayerArmorContents.get(target.getUniqueId()));
      String reason = StringUtils.join(args, ' ', 2, args.length);
      Command.broadcastCommandMessage(p, ChatColor.YELLOW + "Refunded " + target.getName() + "'s items for: " + reason);
      DeathListener.PlayerArmorContents.remove(target.getUniqueId());
      DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
      return true;
    }
    p.sendMessage(ChatColor.RED + "Player was already refunded items");
    
    return false;
  }
}

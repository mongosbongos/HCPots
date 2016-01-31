package io.ohho.hcf.economy;

import com.google.common.primitives.Ints;

import io.ohho.hcf.HCF;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PayCommand
  implements CommandExecutor, TabCompleter
{
  private final HCF plugin;
  
  public PayCommand(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName> <amount>");
      return true;
    }
    Integer amount = Ints.tryParse(args[1]);
    if (amount == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
      return true;
    }
    if (amount.intValue() <= 0)
    {
      sender.sendMessage(ChatColor.RED + "You must send money in positive quantities.");
      return true;
    }
    Player senderPlayer = (Player)sender;
    int senderBalance = senderPlayer != null ? this.plugin.getEconomyManager().getBalance(senderPlayer.getUniqueId()) : 1024;
    if (senderBalance < amount.intValue())
    {
      sender.sendMessage(ChatColor.RED + "You tried to pay " + '$' + amount + ", but you only have " + '$' + senderBalance + " in your bank account.");
      return true;
    }
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
    if (sender.equals(target))
    {
      sender.sendMessage(ChatColor.RED + "You cannot send money to yourself.");
      return true;
    }
    Player targetPlayer = target.getPlayer();
    if ((!target.hasPlayedBefore()) && (targetPlayer == null))
    {
      sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
      return true;
    }
    if (targetPlayer == null) {
      return false;
    }
    if (senderPlayer != null) {
      this.plugin.getEconomyManager().subtractBalance(senderPlayer.getUniqueId(), amount.intValue());
    }
    this.plugin.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount.intValue());
    targetPlayer.sendMessage(ChatColor.YELLOW + sender.getName() + " has sent you " + ChatColor.GOLD + '$' + amount + ChatColor.YELLOW + '.');
    sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GREEN + '$' + amount + ChatColor.YELLOW + " to " + target.getName() + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 1 ? null : Collections.emptyList();
  }
}

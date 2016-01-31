package io.ohho.hcf.economy;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.parapvp.base.BaseConstants;
import com.parapvp.util.BukkitUtils;
import com.parapvp.util.JavaUtils;

import io.ohho.hcf.HCF;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand
  implements CommandExecutor
{
  private static final int MAX_ENTRIES = 10;
  private static final ImmutableList<String> COMPLETIONS_SECOND = ImmutableList.of("add", "set", "take");
  private final HCF plugin;
  
  public EconomyCommand(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    boolean hasStaffPermission = sender.hasPermission(command.getPermission() + ".staff");
    OfflinePlayer target;
    OfflinePlayer target1;
    if ((args.length > 0) && (hasStaffPermission))
    {
      target1 = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
    }
    else
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
        return true;
      }
      target1 = (OfflinePlayer)sender;
    }
    if ((!target1.hasPlayedBefore()) && (!target1.isOnline()))
    {
      sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
      return true;
    }
    UUID uuid = target1.getUniqueId();
    int balance = this.plugin.getEconomyManager().getBalance(uuid);
    if ((args.length < 2) || (!hasStaffPermission))
    {
      sender.sendMessage(ChatColor.YELLOW + (sender.equals(target1) ? "Your balance" : new StringBuilder().append("Balance of ").append(target1.getName()).toString()) + " is " + ChatColor.GREEN + '$' + balance + ChatColor.GOLD + '.');
      return true;
    }
    if ((args[1].equalsIgnoreCase("give")) || (args[1].equalsIgnoreCase("add")))
    {
      if (args.length < 3)
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target1.getName() + ' ' + args[1] + " <amount>");
        return true;
      }
      Integer amount = Ints.tryParse(args[2]);
      if (amount == null)
      {
        sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
        return true;
      }
      int newBalance = this.plugin.getEconomyManager().addBalance(uuid, amount.intValue());
      sender.sendMessage(new String[] { ChatColor.YELLOW + "Added " + '$' + JavaUtils.format(amount) + " to balance of " + target1.getName() + '.', ChatColor.YELLOW + "Balance of " + target1.getName() + " is now " + '$' + newBalance + '.' });
      return true;
    }
    if ((args[1].equalsIgnoreCase("take")) || (args[1].equalsIgnoreCase("negate")) || (args[1].equalsIgnoreCase("minus")) || (args[1].equalsIgnoreCase("subtract")))
    {
      if (args.length < 3)
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target1.getName() + ' ' + args[1] + " <amount>");
        return true;
      }
      Integer amount = Ints.tryParse(args[2]);
      if (amount == null)
      {
        sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
        return true;
      }
      int newBalance = this.plugin.getEconomyManager().subtractBalance(uuid, amount.intValue());
      sender.sendMessage(new String[] { ChatColor.YELLOW + "Taken " + '$' + JavaUtils.format(amount) + " from balance of " + target1.getName() + '.', ChatColor.YELLOW + "Balance of " + target1.getName() + " is now " + '$' + newBalance + '.' });
      return true;
    }
    if (!args[1].equalsIgnoreCase("set"))
    {
      sender.sendMessage(ChatColor.GOLD + (sender.equals(target1) ? "Your balance" : new StringBuilder().append("Balance of ").append(target1.getName()).toString()) + " is " + ChatColor.WHITE + '$' + balance + ChatColor.GOLD + '.');
      return true;
    }
    if (args.length < 3)
    {
      sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target1.getName() + ' ' + args[1] + " <amount>");
      return true;
    }
    Integer amount = Ints.tryParse(args[2]);
    if (amount == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
      return true;
    }
    int newBalance = this.plugin.getEconomyManager().setBalance(uuid, amount.intValue());
    sender.sendMessage(ChatColor.YELLOW + "Set balance of " + target1.getName() + " to " + '$' + JavaUtils.format(Integer.valueOf(newBalance)) + '.');
    return true;
  }
}

package io.ohho.hcf.faction.argument.staff;

import com.google.common.primitives.Doubles;
import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.oCore.faction.FactionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionSetDtrArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public FactionSetDtrArgument(HCF plugin)
  {
    super("setdtr", "Sets the DTR of a faction.");
    this.plugin = plugin;
    this.permission = ("hcf.command.faction.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <playerName|factionName> <newDtr>";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 3)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    Double newDTR = Doubles.tryParse(args[2]);
    if (newDTR == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
      return true;
    }
    if (args[1].equalsIgnoreCase("all"))
    {
      for (Faction faction : this.plugin.getFactionManager().getFactions()) {
        if ((faction instanceof PlayerFaction)) {
          ((PlayerFaction)faction).setDeathsUntilRaidable(newDTR.doubleValue());
        }
      }
      Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR of all factions to " + newDTR + '.');
      return true;
    }
    Faction faction2 = this.plugin.getFactionManager().getContainingFaction(args[1]);
    if (faction2 == null)
    {
      sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
      return true;
    }
    if (!(faction2 instanceof PlayerFaction))
    {
      sender.sendMessage(ChatColor.RED + "You can only set DTR of player factions.");
      return true;
    }
    PlayerFaction playerFaction = (PlayerFaction)faction2;
    double previousDtr = playerFaction.getDeathsUntilRaidable();
    newDTR = Double.valueOf(playerFaction.setDeathsUntilRaidable(newDTR.doubleValue()));
    Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR of " + faction2.getName() + " from " + previousDtr + " to " + newDTR + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    if ((args.length != 2) || (!(sender instanceof Player))) {
      return Collections.emptyList();
    }
    if (args[1].isEmpty()) {
      return null;
    }
    Player player = (Player)sender;
    List<String> results = new ArrayList(this.plugin.getFactionManager().getFactionNameMap().keySet());
    for (Player target : Bukkit.getOnlinePlayers()) {
      if ((player.canSee(target)) && (!results.contains(target.getName()))) {
        results.add(target.getName());
      }
    }
    return results;
  }
}

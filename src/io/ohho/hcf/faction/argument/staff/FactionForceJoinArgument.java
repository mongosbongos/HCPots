package io.ohho.hcf.faction.argument.staff;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.struct.ChatChannel;
import io.ohho.hcf.faction.struct.Role;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.oCore.faction.FactionManager;
import io.ohho.oCore.faction.FactionMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionForceJoinArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public FactionForceJoinArgument(HCF plugin)
  {
    super("forcejoin", "Forcefully join a faction.");
    this.plugin = plugin;
    this.permission = ("hcf.command.faction.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <factionName>";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "Only players can join factions.");
      return true;
    }
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    Player player = (Player)sender;
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
    if (playerFaction != null)
    {
      sender.sendMessage(ChatColor.RED + "You are already in a faction.");
      return true;
    }
    Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
    if (faction == null)
    {
      sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
      return true;
    }
    if (!(faction instanceof PlayerFaction))
    {
      sender.sendMessage(ChatColor.RED + "You can only join player factions.");
      return true;
    }
    playerFaction = (PlayerFaction)faction;
    if (playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER), true)) {
      playerFaction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + sender.getName() + " has forcefully joined the faction.");
    }
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

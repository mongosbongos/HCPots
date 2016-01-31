package io.ohho.hcf.faction.argument.staff;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.struct.Role;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.oCore.faction.FactionManager;
import io.ohho.oCore.faction.FactionMember;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionForceLeaderArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public FactionForceLeaderArgument(HCF plugin)
  {
    super("forceleader", "Forces the leader of a faction.");
    this.plugin = plugin;
    this.permission = ("hcf.command.faction.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <playerName>";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    PlayerFaction playerFaction = this.plugin.getFactionManager().getContainingPlayerFaction(args[1]);
    if (playerFaction == null)
    {
      sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
      return true;
    }
    FactionMember factionMember = playerFaction.getMember(args[1]);
    if (factionMember == null)
    {
      sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
      return true;
    }
    if (factionMember.getRole() == Role.LEADER)
    {
      sender.sendMessage(ChatColor.RED + factionMember.getName() + " is already the leader of " + playerFaction.getDisplayName(sender) + ChatColor.RED + '.');
      return true;
    }
    FactionMember leader = playerFaction.getLeader();
    String oldLeaderName = leader == null ? "none" : leader.getName();
    String newLeaderName = factionMember.getName();
    if (leader != null) {
      leader.setRole(Role.CAPTAIN);
    }
    factionMember.setRole(Role.LEADER);
    playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " has forcefully set the leader to " + newLeaderName + '.');
    sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Leader of " + playerFaction.getName() + "was forcefully set from " + oldLeaderName + " to " + newLeaderName + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 2 ? null : Collections.emptyList();
  }
}

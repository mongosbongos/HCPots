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

public class FactionForcePromoteArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public FactionForcePromoteArgument(HCF plugin)
  {
    super("forcepromote", "Forces the promotion status of a player.");
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
    if (factionMember.getRole() != Role.MEMBER)
    {
      sender.sendMessage(ChatColor.RED + factionMember.getName() + " is already a " + factionMember.getRole().getName() + '.');
      return true;
    }
    factionMember.setRole(Role.CAPTAIN);
    playerFaction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + sender.getName() + " has been forcefully assigned as a captain.");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 2 ? null : Collections.emptyList();
  }
}

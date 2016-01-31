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

public class FactionForceKickArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public FactionForceKickArgument(HCF plugin)
  {
    super("forcekick", "Forcefully kick a player from their faction.");
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
      sender.sendMessage(ChatColor.RED + "You cannot forcefully kick faction leaders.");
      return true;
    }
    if (playerFaction.setMember(factionMember.getUniqueId(), null, true)) {
      playerFaction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + factionMember.getName() + " has been forcefully kicked by " + sender.getName() + '.');
    }
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 2 ? null : Collections.emptyList();
  }
}

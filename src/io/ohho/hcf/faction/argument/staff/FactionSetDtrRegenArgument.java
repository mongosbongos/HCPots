package io.ohho.hcf.faction.argument.staff;

import com.parapvp.util.JavaUtils;
import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.oCore.faction.FactionManager;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionSetDtrRegenArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public FactionSetDtrRegenArgument(HCF plugin)
  {
    super("setdtrregen", "Sets the DTR cooldown of a faction.", new String[] { "setdtrregeneration" });
    this.plugin = plugin;
    this.permission = ("hcf.command.faction.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <playerName|factionName> <newRegen>";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 3)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    long newRegen = JavaUtils.parse(args[2]);
    if (newRegen == -1L)
    {
      sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
      return true;
    }
    if (newRegen > FactionManager.MAX_DTR_REGEN_MILLIS)
    {
      sender.sendMessage(ChatColor.RED + "Cannot set factions DTR regen above " + FactionManager.MAX_DTR_REGEN_WORDS + ".");
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
      sender.sendMessage(ChatColor.RED + "This type of faction does not use DTR.");
      return true;
    }
    PlayerFaction playerFaction = (PlayerFaction)faction;
    long previousRegenRemaining = playerFaction.getRemainingRegenerationTime();
    playerFaction.setRemainingRegenerationTime(newRegen);
    Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set DTR regen of " + faction.getName() + " from " + DurationFormatUtils.formatDurationWords(previousRegenRemaining, true, true) + " to " + DurationFormatUtils.formatDurationWords(newRegen, true, true) + '.');
    return true;
  }
}

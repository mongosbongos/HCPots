package io.ohho.hcf.eventgame.argument;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.EventType;
import io.ohho.hcf.eventgame.faction.ConquestFaction;
import io.ohho.hcf.eventgame.faction.KothFaction;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.oCore.faction.FactionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventCreateArgument
  extends CommandArgument
{
  private final com.exodon.hcf.HCF plugin;
  
  public EventCreateArgument(com.exodon.hcf.HCF plugin2)
  {
    super("create", "Defines a new event", new String[] { "make", "define" });
    this.plugin = plugin2;
    this.permission = ("hcf.command.event.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <eventName> <Conquest|KOTH|Siege>";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 3)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    com.exodon.hcf.faction.type.Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
    if (faction != null)
    {
      sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[1] + '.');
      return true;
    }
    String upperCase = args[2].toUpperCase();
    switch (upperCase)
    {
    case "CONQUEST": 
      faction = new ConquestFaction(args[1]);
      break;
    case "KOTH": 
      faction = new KothFaction(args[1]);
      break;
    default: 
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    this.plugin.getFactionManager().createFaction(faction, sender);
    sender.sendMessage(ChatColor.YELLOW + "Created event faction " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + " with type " + WordUtils.capitalizeFully(args[2]) + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length != 3) {
      return Collections.emptyList();
    }
    EventType[] eventTypes = EventType.values();
    List<String> results = new ArrayList(eventTypes.length);
    for (EventType eventType : eventTypes) {
      results.add(eventType.name());
    }
    return results;
  }
}

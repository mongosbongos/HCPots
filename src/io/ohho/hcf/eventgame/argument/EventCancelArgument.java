package io.ohho.hcf.eventgame.argument;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.EventTimer;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.timer.TimerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventCancelArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public EventCancelArgument(HCF plugin)
  {
    super("cancel", "Cancels a running event", new String[] { "stop", "end" });
    this.plugin = plugin;
    this.permission = ("hcf.command.event.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName();
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
    Faction eventFaction = eventTimer.getEventFaction();
    if (!eventTimer.clearCooldown())
    {
      sender.sendMessage(ChatColor.RED + "There is not a running event.");
      return true;
    }
    Bukkit.broadcastMessage(sender.getName() + ChatColor.YELLOW + " has cancelled " + (eventFaction == null ? "the active event" : new StringBuilder().append(eventFaction.getName()).append(ChatColor.YELLOW).toString()) + ".");
    return true;
  }
}

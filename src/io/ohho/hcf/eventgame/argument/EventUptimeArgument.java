package io.ohho.hcf.eventgame.argument;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.DateTimeFormats;
import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.EventTimer;
import io.ohho.hcf.eventgame.faction.EventFaction;
import io.ohho.hcf.timer.TimerManager;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventUptimeArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public EventUptimeArgument(HCF plugin)
  {
    super("uptime", "Check the uptime of an event");
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
    if (eventTimer.getRemaining() <= 0L)
    {
      sender.sendMessage(ChatColor.RED + "There is not a running event.");
      return true;
    }
    EventFaction eventFaction = eventTimer.getEventFaction();
    sender.sendMessage(ChatColor.YELLOW + "Up-time of " + eventTimer.getName() + " timer" + (eventFaction == null ? "" : new StringBuilder().append(": ").append(ChatColor.BLUE).append('(').append(eventFaction.getDisplayName(sender)).append(ChatColor.BLUE).append(')').toString()) + ChatColor.YELLOW + " is " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(eventTimer.getUptime(), true, true) + ChatColor.YELLOW + ", started at " + ChatColor.GOLD + DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + ChatColor.YELLOW + '.');
    return true;
  }
}

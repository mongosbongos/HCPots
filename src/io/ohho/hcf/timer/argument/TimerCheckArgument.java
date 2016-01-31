package io.ohho.hcf.timer.argument;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.UUIDFetcher;
import io.ohho.hcf.timer.PlayerTimer;
import io.ohho.hcf.timer.Timer;
import io.ohho.hcf.timer.TimerManager;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerCheckArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public TimerCheckArgument(HCF plugin)
  {
    super("check", "Check remaining timer time");
    this.plugin = plugin;
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <timerName> <playerName>";
  }
  
  public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args)
  {
    if (args.length < 3)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    PlayerTimer temporaryTimer = null;
    for (Timer timer : this.plugin.getTimerManager().getTimers()) {
      if (((timer instanceof PlayerTimer)) && (timer.getName().equalsIgnoreCase(args[1])))
      {
        temporaryTimer = (PlayerTimer)timer;
        break;
      }
    }
    if (temporaryTimer == null)
    {
      sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
      return true;
    }
    final PlayerTimer playerTimer = temporaryTimer;
    new BukkitRunnable()
    {
      public void run()
      {
        try
        {
          uuid = UUIDFetcher.getUUIDOf(args[2]);
        }
        catch (Exception ex)
        {
          UUID uuid;
          sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found."); return;
        }
        UUID uuid;
        long remaining = playerTimer.getRemaining(uuid);
        sender.sendMessage(ChatColor.YELLOW + args[2] + " has timer " + playerTimer.getName() + " for another " + DurationFormatUtils.formatDurationWords(remaining, true, true));
      }
    }
    
      .runTaskAsynchronously(this.plugin);
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 2 ? null : Collections.emptyList();
  }
}

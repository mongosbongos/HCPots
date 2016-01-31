package io.ohho.hcf.command;

import io.ohho.hcf.HCF;
import io.ohho.hcf.timer.TimerManager;
import io.ohho.hcf.timer.type.SOTWTimer;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SOTWCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args)
  {
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("start"))
      {
        HCF.getPlugin().getTimerManager().sotw.setRemaining(TimeUnit.MINUTES.toMillis(30L), true);
        HCF.getPlugin().getTimerManager().sotw.setPaused(false);
        cs.sendMessage(ChatColor.YELLOW + "SOTW started.");
      }
      else if (args[0].equalsIgnoreCase("end"))
      {
        HCF.getPlugin().getTimerManager().sotw.setRemaining(TimeUnit.MINUTES.toMillis(30L), true);
        HCF.getPlugin().getTimerManager().sotw.setPaused(true);
        cs.sendMessage(ChatColor.YELLOW + "SOTW stopped.");
      }
      else if (args[0].equalsIgnoreCase("pause"))
      {
        HCF.getPlugin().getTimerManager().sotw.setPaused(true);
        cs.sendMessage(ChatColor.YELLOW + "SOTW paused.");
      }
      else
      {
        cs.sendMessage(ChatColor.RED + "I only know end and start.");
      }
    }
    return false;
  }
}

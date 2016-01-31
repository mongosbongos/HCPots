package io.ohho.hcf.timer.type;

import io.ohho.hcf.HCF;
import io.ohho.hcf.timer.GlobalTimer;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SOTWTimer
  extends GlobalTimer
{
  public SOTWTimer()
  {
    super("SOTW", TimeUnit.MINUTES.toMillis(30L));
  }
  
  public void run()
  {
    if (getRemaining() % 30L == 0L) {
      Bukkit.broadcastMessage(ChatColor.YELLOW + "SOTW will start in " + ChatColor.RED + HCF.getRemaining(getRemaining(), true));
    }
  }
  
  public String getScoreboardPrefix()
  {
    return ChatColor.YELLOW.toString() + ChatColor.BOLD;
  }
}

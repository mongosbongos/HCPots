package io.ohho.hcf.eventgame.tracker;

import com.exodon.hcf.eventgame.tracker.EventTracker;

import io.ohho.hcf.DateTimeFormats;
import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.CaptureZone;
import io.ohho.hcf.eventgame.EventTimer;
import io.ohho.hcf.eventgame.EventType;
import io.ohho.hcf.eventgame.faction.EventFaction;
import io.ohho.hcf.eventgame.faction.KothFaction;
import io.ohho.hcf.timer.TimerManager;

import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Deprecated
public class KothTracker
  implements EventTracker
{
  private final HCF plugin;
  private static final long MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);
  public static final long DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);
  
  public KothTracker(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public EventType getEventType()
  {
    return EventType.KOTH;
  }
  
  public void tick(EventTimer eventTimer, EventFaction eventFaction)
  {
    CaptureZone captureZone = ((KothFaction)eventFaction).getCaptureZone();
    long remainingMillis = captureZone.getRemainingCaptureMillis();
    if (remainingMillis <= 0L)
    {
      this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
      eventTimer.clearCooldown();
      return;
    }
    if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
      return;
    }
    int remainingSeconds = (int)(remainingMillis / 1000L);
    if ((remainingSeconds > 0) && (remainingSeconds % 30 == 0)) {
      Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.GOLD + "Someone is controlling " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
    }
  }
  
  public void onContest(EventFaction eventFaction, EventTimer eventTimer)
  {
    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.GOLD + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
  }
  
  public boolean onControlTake(Player player, CaptureZone captureZone)
  {
    player.sendMessage(ChatColor.GOLD + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
    return true;
  }
  
  public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction)
  {
    player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
    long remainingMillis = captureZone.getRemainingCaptureMillis();
    if ((remainingMillis > 0L) && (captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE)) {
      Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
    }
    return true;
  }
  
  public void stopTiming() {}
}

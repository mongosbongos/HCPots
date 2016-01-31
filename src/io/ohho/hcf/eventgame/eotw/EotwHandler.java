package io.ohho.hcf.eventgame.eotw;

import com.exodon.hcf.eventgame.eotw.EotwHandler;
import com.exodon.hcf.eventgame.eotw.EotwRunnable;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.ClaimableFaction;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.listener.BorderListener;
import io.ohho.oCore.faction.FactionManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EotwHandler
{
  public static final int BORDER_DECREASE_MINIMUM = 1000;
  public static final int BORDER_DECREASE_AMOUNT = 200;
  public static final long BORDER_DECREASE_TIME_MILLIS = TimeUnit.MINUTES.toMillis(5L);
  public static final int BORDER_DECREASE_TIME_SECONDS = (int)(BORDER_DECREASE_TIME_MILLIS / 1000L);
  public static final String BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS, true, true);
  public static final String BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS / 2L, true, true);
  public static final long EOTW_WARMUP_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
  public static final int EOTW_WARMUP_WAIT_SECONDS = (int)(EOTW_WARMUP_WAIT_MILLIS / 1000L);
  private static final long EOTW_CAPPABLE_WAIT = TimeUnit.MINUTES.toMillis(45L);
  private final HCF plugin;
  private EotwRunnable runnable;
  
  public EotwHandler(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public EotwRunnable getRunnable()
  {
    return this.runnable;
  }
  
  public boolean isEndOfTheWorld()
  {
    return isEndOfTheWorld(true);
  }
  
  public void setEndOfTheWorld(boolean yes)
  {
    if (yes == isEndOfTheWorld(false)) {
      return;
    }
    if (yes)
    {
      (this.runnable = new EotwRunnable(((Integer)ConfigurationService.BORDER_SIZES.get(World.Environment.NORMAL)).intValue())).runTaskTimer(this.plugin, 1L, 100L);
    }
    else if (this.runnable != null)
    {
      this.runnable.cancel();
      this.runnable = null;
    }
  }
  
  public boolean isEndOfTheWorld(boolean ignoreWarmup)
  {
    return (this.runnable != null) && ((!ignoreWarmup) || (this.runnable.getElapsedMilliseconds() > 0L));
  }
  
  public static final class EotwRunnable
    extends BukkitRunnable
  {
    private static final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER, 200, 0);
    private final Set<Player> outsideBorder;
    private boolean hasInformedStarted;
    private long startStamp;
    private int borderSize;
    
    public EotwRunnable(int borderSize)
    {
      this.outsideBorder = new HashSet();
      this.hasInformedStarted = false;
      this.borderSize = borderSize;
      this.startStamp = (System.currentTimeMillis() + EotwHandler.EOTW_WARMUP_WAIT_MILLIS);
    }
    
    public void handleDisconnect(Player player)
    {
      this.outsideBorder.remove(player);
    }
    
    public long getTimeUntilStarting()
    {
      long difference = System.currentTimeMillis() - this.startStamp;
      return difference > 0L ? 0L : Math.abs(difference);
    }
    
    public long getTimeUntilCappable()
    {
      return EotwHandler.EOTW_CAPPABLE_WAIT - getElapsedMilliseconds();
    }
    
    public long getElapsedMilliseconds()
    {
      return System.currentTimeMillis() - this.startStamp;
    }
    
    public void run()
    {
      long elapsedMillis = getElapsedMilliseconds();
      int elapsedSeconds = (int)Math.round(elapsedMillis / 1000.0D);
      if ((!this.hasInformedStarted) && (elapsedSeconds >= 0))
      {
        for (Faction faction : HCF.getPlugin().getFactionManager().getFactions()) {
          if ((faction instanceof ClaimableFaction))
          {
            ClaimableFaction claimableFaction = (ClaimableFaction)faction;
            claimableFaction.removeClaims(claimableFaction.getClaims(), Bukkit.getConsoleSender());
          }
        }
        this.hasInformedStarted = true;
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "EndOfTheWorld" + ChatColor.DARK_AQUA + " has began. Border will decrease by " + 200 + " blocks every " + EotwHandler.BORDER_DECREASE_TIME_WORDS + " until at " + 1000 + " blocks.");
        return;
      }
      if ((elapsedMillis < 0L) && (elapsedMillis >= -EotwHandler.EOTW_WARMUP_WAIT_MILLIS))
      {
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "EndOfTheWorld" + ChatColor.DARK_AQUA + " is starting in " + HCF.getRemaining(Math.abs(elapsedMillis), true, false) + '.');
        return;
      }
      Object iterator = this.outsideBorder.iterator();
      while (((Iterator)iterator).hasNext())
      {
        Player player = (Player)((Iterator)iterator).next();
        if (BorderListener.isWithinBorder(player.getLocation()))
        {
          ((Iterator)iterator).remove();
        }
        else
        {
          player.sendMessage(ChatColor.RED + "You are currently outside of the border during EOTW, so you were withered.");
          player.addPotionEffect(WITHER, true);
        }
      }
      int newBorderSize = this.borderSize - 200;
      if (elapsedSeconds % EotwHandler.BORDER_DECREASE_TIME_SECONDS == 0)
      {
        Map<World.Environment, Integer> border_SIZES = ConfigurationService.BORDER_SIZES;
        World.Environment normal = World.Environment.NORMAL;
        int borderSize = newBorderSize;
        this.borderSize = borderSize;
        border_SIZES.put(normal, Integer.valueOf(borderSize));
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Border has been decreased to " + ChatColor.YELLOW + newBorderSize + ChatColor.DARK_AQUA + " blocks.");
        for (Player player2 : Bukkit.getOnlinePlayers()) {
          if (!BorderListener.isWithinBorder(player2.getLocation())) {
            this.outsideBorder.add(player2);
          }
        }
      }
      else if (elapsedSeconds % (EotwHandler.BORDER_DECREASE_TIME_SECONDS / 2) == 0)
      {
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Border decreasing to " + ChatColor.YELLOW + newBorderSize + ChatColor.DARK_AQUA + " blocks in " + ChatColor.YELLOW + EotwHandler.BORDER_DECREASE_TIME_ALERT_WORDS + ChatColor.DARK_AQUA + '.');
      }
    }
  }
}

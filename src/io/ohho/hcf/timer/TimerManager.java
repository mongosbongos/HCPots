package io.ohho.hcf.timer;

import com.exodon.hcf.timer.Timer;
import com.parapvp.util.Config;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.EventTimer;
import io.ohho.hcf.timer.type.ArcherTimer;
import io.ohho.hcf.timer.type.EnderPearlTimer;
import io.ohho.hcf.timer.type.LogoutTimer;
import io.ohho.hcf.timer.type.NotchAppleTimer;
import io.ohho.hcf.timer.type.PvpClassWarmupTimer;
import io.ohho.hcf.timer.type.PvpProtectionTimer;
import io.ohho.hcf.timer.type.SOTWTimer;
import io.ohho.hcf.timer.type.SpawnTagTimer;
import io.ohho.hcf.timer.type.StuckTimer;
import io.ohho.hcf.timer.type.TeleportTimer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TimerManager
  implements Listener
{
  public final LogoutTimer logoutTimer;
  public final EnderPearlTimer enderPearlTimer;
  public final NotchAppleTimer notchAppleTimer;
  public final PvpProtectionTimer pvpProtectionTimer;
  public final PvpClassWarmupTimer pvpClassWarmupTimer;
  public final StuckTimer stuckTimer;
  public final SpawnTagTimer spawnTagTimer;
  public final SOTWTimer sotw;
  public final TeleportTimer teleportTimer;
  public final EventTimer eventTimer;
  public final ArcherTimer archerTimer;
  private final Set<Timer> timers;
  private final JavaPlugin plugin;
  private Config config;
  
  public TimerManager(HCF plugin)
  {
    this.timers = new HashSet();
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    registerTimer(this.sotw = new SOTWTimer());
    registerTimer(this.archerTimer = new ArcherTimer(plugin));
    registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
    registerTimer(this.logoutTimer = new LogoutTimer());
    registerTimer(this.notchAppleTimer = new NotchAppleTimer(plugin));
    registerTimer(this.stuckTimer = new StuckTimer());
    registerTimer(this.pvpProtectionTimer = new PvpProtectionTimer(plugin));
    registerTimer(this.spawnTagTimer = new SpawnTagTimer(plugin));
    registerTimer(this.teleportTimer = new TeleportTimer(plugin));
    registerTimer(this.eventTimer = new EventTimer(plugin));
    registerTimer(this.pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin));
    reloadTimerData();
  }
  
  public Collection<Timer> getTimers()
  {
    return this.timers;
  }
  
  public void registerTimer(Timer timer)
  {
    this.timers.add(timer);
    if ((timer instanceof Listener)) {
      this.plugin.getServer().getPluginManager().registerEvents((Listener)timer, this.plugin);
    }
  }
  
  public void unregisterTimer(Timer timer)
  {
    this.timers.remove(timer);
  }
  
  public void reloadTimerData()
  {
    this.config = new Config(this.plugin, "timers");
    for (Timer timer : this.timers) {
      timer.load(this.config);
    }
  }
  
  public void saveTimerData()
  {
    for (Timer timer : this.timers) {
      timer.onDisable(this.config);
    }
    this.config.save();
  }
}

package io.ohho.hcf.timer;

import com.exodon.hcf.timer.Timer;
import com.exodon.hcf.timer.TimerRunnable;
import com.google.common.base.Optional;
import com.parapvp.util.Config;

import io.ohho.hcf.timer.event.TimerClearEvent;
import io.ohho.hcf.timer.event.TimerExpireEvent;
import io.ohho.hcf.timer.event.TimerExtendEvent;
import io.ohho.hcf.timer.event.TimerPauseEvent;
import io.ohho.hcf.timer.event.TimerStartEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javafx.util.Callback;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

public abstract class PlayerTimer
  extends Timer
{
  private static final String COOLDOWN_PATH = "timer-cooldowns";
  private static final String PAUSE_PATH = "timer-pauses";
  protected final boolean persistable;
  protected final Map<UUID, TimerRunnable> cooldowns;
  
  public PlayerTimer(String name, long defaultCooldown)
  {
    this(name, defaultCooldown, true);
  }
  
  public PlayerTimer(String name, long defaultCooldown, boolean persistable)
  {
    super(name, defaultCooldown);
    this.cooldowns = new ConcurrentHashMap();
    this.persistable = persistable;
  }
  
  public void onExpire(UUID userUUID) {}
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onTimerExpireLoadReduce(TimerExpireEvent event)
  {
    if (event.getTimer().equals(this))
    {
      Optional<UUID> optionalUserUUID = event.getUserUUID();
      if (optionalUserUUID.isPresent())
      {
        UUID userUUID = (UUID)optionalUserUUID.get();
        onExpire(userUUID);
        clearCooldown(userUUID);
      }
    }
  }
  
  public void clearCooldown(Player player)
  {
    clearCooldown(player.getUniqueId());
  }
  
  public TimerRunnable clearCooldown(UUID playerUUID)
  {
    TimerRunnable runnable = (TimerRunnable)this.cooldowns.remove(playerUUID);
    if (runnable != null)
    {
      runnable.cancel();
      Bukkit.getPluginManager().callEvent(new TimerClearEvent(playerUUID, this));
      return runnable;
    }
    return null;
  }
  
  public void clearCooldowns()
  {
    for (UUID uuid : this.cooldowns.keySet()) {
      clearCooldown(uuid);
    }
  }
  
  public boolean isPaused(Player player)
  {
    return isPaused(player.getUniqueId());
  }
  
  public boolean isPaused(UUID playerUUID)
  {
    TimerRunnable runnable = (TimerRunnable)this.cooldowns.get(playerUUID);
    return (runnable != null) && (runnable.isPaused());
  }
  
  public void setPaused(@Nullable Player player, UUID playerUUID, boolean paused)
  {
    TimerRunnable runnable = (TimerRunnable)this.cooldowns.get(playerUUID);
    if ((runnable != null) && (runnable.isPaused() != paused))
    {
      TimerPauseEvent event = new TimerPauseEvent(playerUUID, this, paused);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
        runnable.setPaused(paused);
      }
    }
  }
  
  public long getRemaining(Player player)
  {
    return getRemaining(player.getUniqueId());
  }
  
  public long getRemaining(UUID playerUUID)
  {
    TimerRunnable runnable = (TimerRunnable)this.cooldowns.get(playerUUID);
    return runnable == null ? 0L : runnable.getRemaining();
  }
  
  public boolean setCooldown(@Nullable Player player, UUID playerUUID)
  {
    return setCooldown(player, playerUUID, this.defaultCooldown, false);
  }
  
  public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite)
  {
    return setCooldown(player, playerUUID, duration, overwrite, null);
  }
  
  public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite, @Nullable Callback callback)
  {
    TimerRunnable runnable;
    TimerRunnable runnable;
    if (duration <= 0L) {
      runnable = clearCooldown(playerUUID);
    } else {
      runnable = (TimerRunnable)this.cooldowns.get(playerUUID);
    }
    if (runnable != null)
    {
      long remaining = runnable.getRemaining();
      if ((!overwrite) && (remaining > 0L) && (duration > remaining)) {
        return false;
      }
      TimerExtendEvent event = new TimerExtendEvent(player, playerUUID, this, remaining, duration);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
        return false;
      }
      runnable.setRemaining(duration);
    }
    else
    {
      Bukkit.getPluginManager().callEvent(new TimerStartEvent(player, playerUUID, this, duration));
      runnable = new TimerRunnable(playerUUID, this, duration);
    }
    this.cooldowns.put(playerUUID, runnable);
    return true;
  }
  
  public void load(Config config)
  {
    if (!this.persistable) {
      return;
    }
    String path = "timer-cooldowns." + this.name;
    Object object = config.get(path);
    MemorySection section;
    long millis;
    if ((object instanceof MemorySection))
    {
      section = (MemorySection)object;
      millis = System.currentTimeMillis();
      for (String id : section.getKeys(false))
      {
        long remaining = config.getLong(section.getCurrentPath() + '.' + id) - millis;
        if (remaining > 0L) {
          setCooldown(null, UUID.fromString(id), remaining, true, null);
        }
      }
    }
    path = "timer-pauses." + this.name;
    if (((object = config.get(path)) instanceof MemorySection))
    {
      MemorySection section = (MemorySection)object;
      for (String id2 : section.getKeys(false))
      {
        TimerRunnable timerRunnable = (TimerRunnable)this.cooldowns.get(UUID.fromString(id2));
        if (timerRunnable != null) {
          timerRunnable.setPauseMillis(config.getLong(path + '.' + id2));
        }
      }
    }
  }
  
  public void onDisable(Config config)
  {
    if (this.persistable)
    {
      Set<Map.Entry<UUID, TimerRunnable>> entrySet = this.cooldowns.entrySet();
      Map<String, Long> pauseSavemap = new LinkedHashMap(entrySet.size());
      Map<String, Long> cooldownSavemap = new LinkedHashMap(entrySet.size());
      for (Map.Entry<UUID, TimerRunnable> entry : entrySet)
      {
        String id = ((UUID)entry.getKey()).toString();
        TimerRunnable runnable = (TimerRunnable)entry.getValue();
        pauseSavemap.put(id, Long.valueOf(runnable.getPauseMillis()));
        cooldownSavemap.put(id, Long.valueOf(runnable.getExpiryMillis()));
      }
      config.set("timer-pauses." + this.name, pauseSavemap);
      config.set("timer-cooldowns." + this.name, cooldownSavemap);
    }
  }
}

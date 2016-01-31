package io.ohho.hcf.combatlog;

import org.bukkit.scheduler.BukkitTask;

import com.exodon.hcfold.combatlog.LoggerEntity;

public class CombatLogEntry
{
  public final LoggerEntity loggerEntity;
  public final BukkitTask task;
  
  public CombatLogEntry(LoggerEntity loggerEntity, BukkitTask task)
  {
    this.loggerEntity = loggerEntity;
    this.task = task;
  }
}

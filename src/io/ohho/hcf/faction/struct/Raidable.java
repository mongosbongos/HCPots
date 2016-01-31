package io.ohho.hcf.faction.struct;

import com.exodon.hcf.faction.struct.RegenStatus;

public abstract interface Raidable
{
  public abstract boolean isRaidable();
  
  public abstract double getDeathsUntilRaidable();
  
  public abstract double getMaximumDeathsUntilRaidable();
  
  public abstract double setDeathsUntilRaidable(double paramDouble);
  
  public abstract long getRemainingRegenerationTime();
  
  public abstract void setRemainingRegenerationTime(long paramLong);
  
  public abstract RegenStatus getRegenStatus();
}

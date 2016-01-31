package io.ohho.hcf.pvpclass.bard;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class BardData
{
  public static final double MIN_ENERGY = 0.0D;
  public static final double MAX_ENERGY = 120.0D;
  public static final long MAX_ENERGY_MILLIS = 120000L;
  private static final double ENERGY_PER_MILLISECOND = 1.25D;
  public long buffCooldown;
  public BukkitTask heldTask;
  private long energyStart;
  
  public long getRemainingBuffDelay()
  {
    return this.buffCooldown - System.currentTimeMillis();
  }
  
  public void startEnergyTracking()
  {
    setEnergy(0.0D);
  }
  
  public long getEnergyMillis()
  {
    if (this.energyStart == 0L) {
      return 0L;
    }
    return Math.min(120000L, (1.25D * (System.currentTimeMillis() - this.energyStart)));
  }
  
  public double getEnergy()
  {
    double value = getEnergyMillis() / 1000.0D;
    return Math.round(value * 10.0D) / 10.0D;
  }
  
  public void setEnergy(double energy)
  {
    Preconditions.checkArgument(energy >= 0.0D, "Energy cannot be less than 0.0");
    Preconditions.checkArgument(energy <= 120.0D, "Energy cannot be more than 120.0");
    this.energyStart = ((System.currentTimeMillis() - 1000.0D * energy));
  }
}

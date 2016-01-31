package io.ohho.hcf.timer;

import com.parapvp.util.Config;

public abstract class Timer
{
  protected final String name;
  protected final long defaultCooldown;
  
  public Timer(String name, long defaultCooldown)
  {
    this.name = name;
    this.defaultCooldown = defaultCooldown;
  }
  
  public abstract String getScoreboardPrefix();
  
  public String getName()
  {
    return this.name;
  }
  
  public final String getDisplayName()
  {
    return getScoreboardPrefix() + this.name;
  }
  
  public void load(Config config) {}
  
  public void onDisable(Config config) {}
}

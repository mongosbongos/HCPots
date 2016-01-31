package io.ohho.hcf.eventgame.faction;

import java.util.Map;

import com.exodon.hcf.eventgame.faction.EventFaction;

public abstract class CapturableFaction
  extends EventFaction
{
  public CapturableFaction(String name)
  {
    super(name);
  }
  
  public CapturableFaction(Map<String, Object> map)
  {
    super(map);
  }
}

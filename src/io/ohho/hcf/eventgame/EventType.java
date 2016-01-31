package io.ohho.hcf.eventgame;

import com.exodon.hcf.eventgame.EventType;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.tracker.ConquestTracker;
import io.ohho.hcf.eventgame.tracker.EventTracker;
import io.ohho.hcf.eventgame.tracker.KothTracker;

public enum EventType
{
  CONQUEST("Conquest", new ConquestTracker(HCF.getPlugin())),  KOTH("KOTH", new KothTracker(HCF.getPlugin()));
  
  private static final ImmutableMap<String, EventType> byDisplayName;
  private final EventTracker eventTracker;
  private final String displayName;
  
  static
  {
    ImmutableMap.Builder<String, EventType> builder = new ImmutableBiMap.Builder();
    for (EventType eventType : values()) {
      builder.put(eventType.displayName.toLowerCase(), eventType);
    }
    byDisplayName = builder.build();
  }
  
  private EventType(String displayName, EventTracker eventTracker)
  {
    this.displayName = displayName;
    this.eventTracker = eventTracker;
  }
  
  @Deprecated
  public static EventType getByDisplayName(String name)
  {
    return (EventType)byDisplayName.get(name.toLowerCase());
  }
  
  public EventTracker getEventTracker()
  {
    return this.eventTracker;
  }
  
  public String getDisplayName()
  {
    return this.displayName;
  }
}

package io.ohho.hcf.eventgame.tracker;

import io.ohho.hcf.eventgame.CaptureZone;
import io.ohho.hcf.eventgame.EventTimer;
import io.ohho.hcf.eventgame.EventType;
import io.ohho.hcf.eventgame.faction.EventFaction;

import org.bukkit.entity.Player;

@Deprecated
public abstract interface EventTracker
{
  public abstract EventType getEventType();
  
  public abstract void tick(EventTimer paramEventTimer, EventFaction paramEventFaction);
  
  public abstract void onContest(EventFaction paramEventFaction, EventTimer paramEventTimer);
  
  public abstract boolean onControlTake(Player paramPlayer, CaptureZone paramCaptureZone);
  
  public abstract boolean onControlLoss(Player paramPlayer, CaptureZone paramCaptureZone, EventFaction paramEventFaction);
  
  public abstract void stopTiming();
}

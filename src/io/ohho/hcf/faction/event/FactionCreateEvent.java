package io.ohho.hcf.faction.event;

import com.exodon.hcf.faction.event.FactionEvent;

import io.ohho.hcf.faction.type.Faction;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class FactionCreateEvent
  extends FactionEvent
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private final CommandSender sender;
  private boolean cancelled;
  
  public FactionCreateEvent(Faction faction, CommandSender sender)
  {
    super(faction);
    this.sender = sender;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public CommandSender getSender()
  {
    return this.sender;
  }
  
  public boolean isCancelled()
  {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled)
  {
    this.cancelled = cancelled;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
}

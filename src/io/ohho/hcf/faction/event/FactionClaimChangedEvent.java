package io.ohho.hcf.faction.event;

import io.ohho.hcf.faction.claim.Claim;
import io.ohho.hcf.faction.event.cause.ClaimChangeCause;

import java.util.Collection;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionClaimChangedEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private final CommandSender sender;
  private final ClaimChangeCause cause;
  private final Collection<Claim> affectedClaims;
  
  public FactionClaimChangedEvent(CommandSender sender, ClaimChangeCause cause, Collection<Claim> affectedClaims)
  {
    this.sender = sender;
    this.cause = cause;
    this.affectedClaims = affectedClaims;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public CommandSender getSender()
  {
    return this.sender;
  }
  
  public ClaimChangeCause getCause()
  {
    return this.cause;
  }
  
  public Collection<Claim> getAffectedClaims()
  {
    return this.affectedClaims;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
}

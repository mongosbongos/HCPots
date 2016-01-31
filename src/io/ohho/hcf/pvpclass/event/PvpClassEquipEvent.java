package io.ohho.hcf.pvpclass.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import io.ohho.hcf.pvpclass.PvpClass;

public class PvpClassEquipEvent
  extends PlayerEvent
{
  private static final HandlerList handlers = new HandlerList();
  private final PvpClass pvpClass;
  
  public PvpClassEquipEvent(Player player, PvpClass pvpClass)
  {
    super(player);
    this.pvpClass = pvpClass;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public PvpClass getPvpClass()
  {
    return this.pvpClass;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
}

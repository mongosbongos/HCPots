package io.ohho.hcf.faction.event;

import com.exodon.hcf.faction.event.FactionEvent;

import io.ohho.hcf.faction.struct.ChatChannel;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.oCore.faction.FactionMember;

import java.util.Collection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class FactionChatEvent
  extends FactionEvent
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final FactionMember factionMember;
  private final ChatChannel chatChannel;
  private final String message;
  private final Collection<? extends CommandSender> recipients;
  private final String fallbackFormat;
  private boolean cancelled;
  
  public FactionChatEvent(boolean async, PlayerFaction faction, Player player, ChatChannel chatChannel, Collection<? extends CommandSender> recipients, String message)
  {
    super(faction, async);
    this.player = player;
    this.factionMember = faction.getMember(player.getUniqueId());
    this.chatChannel = chatChannel;
    this.recipients = recipients;
    this.message = message;
    this.fallbackFormat = chatChannel.getRawFormat(player);
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public FactionMember getFactionMember()
  {
    return this.factionMember;
  }
  
  public ChatChannel getChatChannel()
  {
    return this.chatChannel;
  }
  
  public Collection<? extends CommandSender> getRecipients()
  {
    return this.recipients;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public String getFallbackFormat()
  {
    return this.fallbackFormat;
  }
  
  public boolean isCancelled()
  {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancel)
  {
    this.cancelled = cancel;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
}

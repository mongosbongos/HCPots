package io.ohho.hcf.eventgame.eotw;

import com.exodon.hcf.eventgame.eotw.EotwHandler;
import com.parapvp.base.kit.event.KitApplyEvent;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.event.FactionClaimChangeEvent;
import io.ohho.hcf.faction.event.cause.ClaimChangeCause;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EotwListener
  implements Listener
{
  private final HCF plugin;
  
  public EotwListener(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
    if (runnable != null) {
      runnable.handleDisconnect(event.getPlayer());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerKick(PlayerKickEvent event)
  {
    EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
    if (runnable != null) {
      runnable.handleDisconnect(event.getPlayer());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
    if (runnable != null) {
      runnable.handleDisconnect(event.getEntity());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onLandClaim(KitApplyEvent event)
  {
    if ((!event.isForce()) && (this.plugin.getEotwHandler().isEndOfTheWorld()))
    {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "Kits cannot be applied during EOTW.");
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onFactionClaimChange(FactionClaimChangeEvent event)
  {
    if ((this.plugin.getEotwHandler().isEndOfTheWorld()) && (event.getCause() == ClaimChangeCause.CLAIM))
    {
      Faction faction = event.getClaimableFaction();
      if ((faction instanceof PlayerFaction))
      {
        event.setCancelled(true);
        event.getSender().sendMessage(ChatColor.RED + "Player based faction land cannot be claimed during EOTW.");
      }
    }
  }
}

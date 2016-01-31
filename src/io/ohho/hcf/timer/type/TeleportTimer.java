package io.ohho.hcf.timer.type;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.timer.PlayerTimer;
import io.ohho.hcf.timer.TimerRunnable;
import io.ohho.oCore.faction.FactionManager;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TeleportTimer
  extends PlayerTimer
  implements Listener
{
  private final ConcurrentMap<Object, Object> destinationMap;
  private final HCF plugin;
  
  public TeleportTimer(HCF plugin)
  {
    super("Teleportation", TimeUnit.SECONDS.toMillis(10L), false);
    this.plugin = plugin;
    this.destinationMap = CacheBuilder.newBuilder().expireAfterWrite(60000L, TimeUnit.MILLISECONDS).build().asMap();
  }
  
  public Object getDestination(Player player)
  {
    return this.destinationMap.get(player.getUniqueId());
  }
  
  public String getScoreboardPrefix()
  {
    return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD;
  }
  
  public TimerRunnable clearCooldown(UUID uuid)
  {
    TimerRunnable runnable = super.clearCooldown(uuid);
    if (runnable != null)
    {
      this.destinationMap.remove(uuid);
      return runnable;
    }
    return null;
  }
  
  public int getNearbyEnemies(Player player, int distance)
  {
    FactionManager factionManager = this.plugin.getFactionManager();
    Faction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
    int count = 0;
    Collection<Entity> nearby = player.getNearbyEntities(distance, distance, distance);
    for (Entity entity : nearby) {
      if ((entity instanceof Player))
      {
        Player target = (Player)entity;
        Faction targetFaction;
        if ((target.canSee(player)) && 
        
          (player.canSee(target)) && (
          
          (playerFaction == null) || ((targetFaction = factionManager.getPlayerFaction(target)) == null) || (!targetFaction.equals(playerFaction)))) {
          count++;
        }
      }
    }
    return count;
  }
  
  public boolean teleport(Player player, Location location, long millis, String warmupMessage, PlayerTeleportEvent.TeleportCause cause)
  {
    cancelTeleport(player, null);
    boolean result;
    if (millis <= 0L)
    {
      boolean result = player.teleport(location, cause);
      clearCooldown(player.getUniqueId());
    }
    else
    {
      UUID uuid = player.getUniqueId();
      player.sendMessage(warmupMessage);
      this.destinationMap.put(uuid, location.clone());
      setCooldown(player, uuid, millis, true, null);
      result = true;
    }
    return result;
  }
  
  public void cancelTeleport(Player player, String reason)
  {
    UUID uuid = player.getUniqueId();
    if (getRemaining(uuid) > 0L)
    {
      clearCooldown(uuid);
      if ((reason != null) && (!reason.isEmpty())) {
        player.sendMessage(reason);
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerMove(PlayerMoveEvent event)
  {
    Location from = event.getFrom();
    Location to = event.getTo();
    if ((from.getBlockX() == to.getBlockX()) && (from.getBlockY() == to.getBlockY()) && (from.getBlockZ() == to.getBlockZ())) {
      return;
    }
    cancelTeleport(event.getPlayer(), ChatColor.YELLOW + "You moved a block, therefore cancelling your teleport.");
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerDamage(EntityDamageEvent event)
  {
    Entity entity = event.getEntity();
    if ((entity instanceof Player)) {
      cancelTeleport((Player)entity, ChatColor.YELLOW + "You took damage, therefore cancelling your teleport.");
    }
  }
  
  public void onExpire(UUID userUUID)
  {
    Player player = Bukkit.getPlayer(userUUID);
    if (player == null) {
      return;
    }
    Location destination = (Location)this.destinationMap.remove(userUUID);
    if (destination != null)
    {
      destination.getChunk();
      player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
    }
  }
}

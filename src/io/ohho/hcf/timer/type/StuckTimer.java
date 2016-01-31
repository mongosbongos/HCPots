package io.ohho.hcf.timer.type;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.ohho.hcf.HCF;
import io.ohho.hcf.combatlog.CombatLogListener;
import io.ohho.hcf.timer.PlayerTimer;
import io.ohho.hcf.timer.TimerRunnable;
import io.ohho.oCore.faction.LandMap;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javafx.util.Callback;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class StuckTimer
  extends PlayerTimer
  implements Listener
{
  private final ConcurrentMap<Object, Object> startedLocations;
  
  public StuckTimer()
  {
    super("Stuck", TimeUnit.MINUTES.toMillis(2L) + TimeUnit.SECONDS.toMillis(45L), false);
    this.startedLocations = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000L, TimeUnit.MILLISECONDS).build().asMap();
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
      this.startedLocations.remove(uuid);
      return runnable;
    }
    return null;
  }
  
  public boolean setCooldown(@Nullable Player player, UUID playerUUID, long millis, boolean force, @Nullable Callback callback)
  {
    if ((player != null) && (super.setCooldown(player, playerUUID, millis, force, callback)))
    {
      this.startedLocations.put(playerUUID, player.getLocation());
      return true;
    }
    return false;
  }
  
  private void checkMovement(Player player, Location from, Location to)
  {
    UUID uuid = player.getUniqueId();
    if (getRemaining(uuid) > 0L)
    {
      if (from == null)
      {
        clearCooldown(uuid);
        return;
      }
      int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
      int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
      int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());
      if ((xDiff > 5) || (yDiff > 5) || (zDiff > 5))
      {
        clearCooldown(uuid);
        player.sendMessage(ChatColor.RED + "You moved more than " + ChatColor.BOLD + 5 + ChatColor.RED + " blocks. " + getDisplayName() + ChatColor.RED + " timer ended.");
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerMove(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (getRemaining(uuid) > 0L)
    {
      Location from = (Location)this.startedLocations.get(uuid);
      checkMovement(player, from, event.getTo());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerTeleport(PlayerTeleportEvent event)
  {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (getRemaining(uuid) > 0L)
    {
      Location from = (Location)this.startedLocations.get(uuid);
      checkMovement(player, from, event.getTo());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerKick(PlayerKickEvent event)
  {
    UUID uuid = event.getPlayer().getUniqueId();
    if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
      clearCooldown(uuid);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    UUID uuid = event.getPlayer().getUniqueId();
    if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
      clearCooldown(uuid);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerDamage(EntityDamageEvent event)
  {
    Entity entity = event.getEntity();
    if ((entity instanceof Player))
    {
      Player player = (Player)entity;
      if (getRemaining(player) > 0L)
      {
        player.sendMessage(ChatColor.RED + "You were damaged, " + getDisplayName() + ChatColor.RED + " timer ended.");
        clearCooldown(player);
      }
    }
  }
  
  public void onExpire(UUID userUUID)
  {
    Player player = Bukkit.getPlayer(userUUID);
    if (player == null) {
      return;
    }
    Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), 24);
    if (nearest == null)
    {
      CombatLogListener.safelyDisconnect(player, ChatColor.RED + "Unable to find a safe location, you have been safely logged out.");
      player.sendMessage(ChatColor.RED + "No safe-location found.");
      return;
    }
    if (player.teleport(nearest, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
      player.sendMessage(ChatColor.YELLOW + getDisplayName() + ChatColor.YELLOW + " timer has teleported you to the nearest safe area.");
    }
  }
  
  public void run(Player player)
  {
    long remainingMillis = getRemaining(player);
    if (remainingMillis > 0L) {
      player.sendMessage(getDisplayName() + ChatColor.BLUE + " timer is teleporting you in " + ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
    }
  }
}

package io.ohho.hcf.listener;

import io.ohho.hcf.HCF;
import io.ohho.hcf.timer.PlayerTimer;
import io.ohho.hcf.timer.TimerManager;

import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffectType;

public class PortalListener
  implements Listener
{
  private static final long PORTAL_MESSAGE_DELAY_THRESHOLD = 2500L;
  private final Location endExit;
  private final TObjectLongMap<UUID> messageDelays;
  private final HCF plugin;
  
  public PortalListener(HCF plugin)
  {
    this.endExit = new Location(Bukkit.getWorld("world"), 0.0D, 67.5D, 200.0D);
    this.messageDelays = new TObjectLongHashMap();
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onEntityPortal(EntityPortalEvent event)
  {
    if ((event.getEntity() instanceof EnderDragon)) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onPlayerPortal(PlayerPortalEvent event)
  {
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
      return;
    }
    World toWorld = event.getTo().getWorld();
    if ((toWorld != null) && (toWorld.getEnvironment() == World.Environment.THE_END))
    {
      event.useTravelAgent(false);
      event.setTo(toWorld.getSpawnLocation());
      return;
    }
    World fromWorld = event.getFrom().getWorld();
    if ((fromWorld != null) && (fromWorld.getEnvironment() == World.Environment.THE_END))
    {
      event.useTravelAgent(false);
      event.setTo(this.endExit);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onWorldChanged(PlayerChangedWorldEvent event)
  {
    Player player = event.getPlayer();
    World from = event.getFrom();
    World to = player.getWorld();
    if ((from.getEnvironment() != World.Environment.THE_END) && (to.getEnvironment() == World.Environment.THE_END) && (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))) {
      player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPortalEnter(PlayerPortalEvent event)
  {
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
      return;
    }
    Location to = event.getTo();
    World toWorld = to.getWorld();
    if (toWorld == null) {
      return;
    }
    if (toWorld.getEnvironment() == World.Environment.THE_END)
    {
      Player player = event.getPlayer();
      PlayerTimer timer = this.plugin.getTimerManager().spawnTagTimer;
      long remaining;
      if ((remaining = timer.getRemaining(player)) > 0L)
      {
        message(player, ChatColor.RED + "You cannot enter the End whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        event.setCancelled(true);
        return;
      }
      timer = this.plugin.getTimerManager().pvpProtectionTimer;
      if ((remaining = timer.getRemaining(player)) > 0L)
      {
        message(player, ChatColor.RED + "You cannot enter the End whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        event.setCancelled(true);
        return;
      }
      event.useTravelAgent(false);
      event.setTo(toWorld.getSpawnLocation().add(0.5D, 0.0D, 0.5D));
    }
  }
  
  private void message(Player player, String message)
  {
    long last = this.messageDelays.get(player.getUniqueId());
    long millis = System.currentTimeMillis();
    if ((last != this.messageDelays.getNoEntryValue()) && (last + 2500L - millis > 0L)) {
      return;
    }
    this.messageDelays.put(player.getUniqueId(), millis);
    player.sendMessage(message);
  }
}

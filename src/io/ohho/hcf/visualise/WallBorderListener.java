package io.ohho.hcf.visualise;

import com.exodon.hcf.visualise.VisualBlock;
import com.exodon.hcf.visualise.VisualType;
import com.exodon.hcf.visualise.WallBorderListener;
import com.exodon.hcf.visualise.WarpTimerRunnable;
import com.google.common.base.Predicate;
import com.parapvp.util.cuboid.Cuboid;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.claim.Claim;
import io.ohho.hcf.faction.type.ClaimableFaction;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.RoadFaction;
import io.ohho.hcf.timer.TimerManager;
import io.ohho.hcf.timer.type.PvpProtectionTimer;
import io.ohho.hcf.timer.type.SpawnTagTimer;
import io.ohho.oCore.faction.FactionManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class WallBorderListener
  implements Listener
{
  private static final int BORDER_PURPOSE_INFORM_THRESHOLD = 35;
  private static final int WALL_BORDER_HEIGHT_BELOW_DIFF = 3;
  private static final int WALL_BORDER_HEIGHT_ABOVE_DIFF = 4;
  private static final int WALL_BORDER_HORIZONTAL_DISTANCE = 7;
  private final boolean useTaskInstead;
  private final Map<UUID, BukkitTask> wallBorderTask;
  private final HCF plugin;
  
  public WallBorderListener(HCF plugin)
  {
    this.wallBorderTask = new HashMap();
    this.plugin = plugin;
    if (plugin.getRandom().nextBoolean()) {}
    this.useTaskInstead = false;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    if (!this.useTaskInstead) {
      return;
    }
    BukkitTask task = (BukkitTask)this.wallBorderTask.remove(event.getPlayer().getUniqueId());
    if (task != null) {
      task.cancel();
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    final Player player = event.getPlayer();
    if (this.useTaskInstead)
    {
      this.wallBorderTask.put(player.getUniqueId(), new WarpTimerRunnable(this, player).runTaskTimer(this.plugin, 3L, 3L));
      return;
    }
    final Location now = player.getLocation();
    new BukkitRunnable()
    {
      public void run()
      {
        Location location = player.getLocation();
        if (now.equals(location)) {
          WallBorderListener.this.handlePositionChanged(player, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
      }
    }
    
      .runTaskLater(this.plugin, 4L);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerMove(PlayerMoveEvent event)
  {
    if (this.useTaskInstead) {
      return;
    }
    Location to = event.getTo();
    int toX = to.getBlockX();
    int toY = to.getBlockY();
    int toZ = to.getBlockZ();
    Location from = event.getFrom();
    if ((from.getBlockX() != toX) || (from.getBlockY() != toY) || (from.getBlockZ() != toZ)) {
      handlePositionChanged(event.getPlayer(), to.getWorld(), toX, toY, toZ);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerTeleport(PlayerTeleportEvent event)
  {
    onPlayerMove(event);
  }
  
  private void handlePositionChanged(Player player, final World toWorld, final int toX, final int toY, final int toZ)
  {
    Object relevantTimer;
    VisualType visualType;
    Object relevantTimer;
    if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L)
    {
      VisualType visualType = VisualType.SPAWN_BORDER;
      relevantTimer = this.plugin.getTimerManager().spawnTagTimer;
    }
    else
    {
      if (this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(player) <= 0L) {
        return;
      }
      visualType = VisualType.CLAIM_BORDER;
      relevantTimer = this.plugin.getTimerManager().pvpProtectionTimer;
    }
    this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, new Predicate()
    {
      public boolean apply(VisualBlock visualBlock)
      {
        Location other = visualBlock.getLocation();
        if (!other.getWorld().equals(toWorld)) {
          return false;
        }
        if (Math.abs(toX - other.getBlockX()) > 7) {
          return true;
        }
        if (Math.abs(toY - other.getBlockY()) > 4) {
          return true;
        }
        if (Math.abs(toZ - other.getBlockZ()) <= 7) {
          return false;
        }
        return true;
      }
    });
    int minHeight = toY - 3;
    int maxHeight = toY + 4;
    int minX = toX - 7;
    int maxX = toX + 7;
    int minZ = toZ - 7;
    int maxZ = toZ + 7;
    Collection<Claim> added = new HashSet();
    for (int x = minX; x < maxX; x++) {
      for (int z = minZ; z < maxZ; z++)
      {
        Faction faction = this.plugin.getFactionManager().getFactionAt(toWorld, x, z);
        if ((faction instanceof ClaimableFaction))
        {
          if (visualType == VisualType.SPAWN_BORDER)
          {
            if (!faction.isSafezone()) {
              continue;
            }
          }
          else if (visualType == VisualType.CLAIM_BORDER)
          {
            if ((faction instanceof RoadFaction)) {
              continue;
            }
            if (faction.isSafezone()) {
              continue;
            }
          }
          Collection<Claim> claims = ((ClaimableFaction)faction).getClaims();
          for (Claim claim : claims) {
            if (toWorld.equals(claim.getWorld())) {
              added.add(claim);
            }
          }
        }
      }
    }
    if (!added.isEmpty())
    {
      int generated = 0;
      Iterator<Claim> iterator = added.iterator();
      while (iterator.hasNext())
      {
        Claim claim2 = (Claim)iterator.next();
        List<Vector> edges = claim2.edges();
        for (Vector edge : edges) {
          if ((Math.abs(edge.getBlockX() - toX) <= 7) && 
          
            (Math.abs(edge.getBlockZ() - toZ) <= 7))
          {
            Location location = edge.toLocation(toWorld);
            if (location != null)
            {
              Location first = location.clone();
              first.setY(minHeight);
              Location second = location.clone();
              second.setY(maxHeight);
              generated += this.plugin.getVisualiseHandler().generate(player, new Cuboid(first, second), visualType, false).size();
            }
          }
        }
        iterator.remove();
      }
    }
  }
  
  private static final class WarpTimerRunnable
    extends BukkitRunnable
  {
    private WallBorderListener listener;
    private Player player;
    private double lastX;
    private double lastY;
    private double lastZ;
    
    public WarpTimerRunnable(WallBorderListener listener, Player player)
    {
      this.lastX = Double.MAX_VALUE;
      this.lastY = Double.MAX_VALUE;
      this.lastZ = Double.MAX_VALUE;
      this.listener = listener;
      this.player = player;
    }
    
    public void run()
    {
      Location location = this.player.getLocation();
      double x = location.getBlockX();
      double y = location.getBlockY();
      double z = location.getBlockZ();
      if ((this.lastX == x) && (this.lastY == y) && (this.lastZ == z)) {
        return;
      }
      this.lastX = x;
      this.lastY = y;
      this.lastZ = z;
      this.listener.handlePositionChanged(this.player, this.player.getWorld(), (int)x, (int)y, (int)z);
    }
    
    public synchronized void cancel()
      throws IllegalStateException
    {
      super.cancel();
      this.listener = null;
      this.player = null;
    }
  }
}

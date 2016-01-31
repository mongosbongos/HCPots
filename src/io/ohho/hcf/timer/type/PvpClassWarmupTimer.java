package io.ohho.hcf.timer.type;

import io.ohho.hcf.HCF;
import io.ohho.hcf.pvpclass.PvpClass;
import com.exodon.hcf.pvpclass.PvpClassManager;
import com.exodon.hcf.timer.PlayerTimer;
import com.exodon.hcf.timer.TimerRunnable;
import io.ohho.hcf.timer.type.PvpClassWarmupTimer;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.parapvp.util.Config;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PvpClassWarmupTimer
  extends PlayerTimer
  implements Listener
{
  protected final ConcurrentMap<Object, Object> classWarmups;
  private final HCF plugin;
  
  public PvpClassWarmupTimer(HCF plugin)
  {
    super("Class Warmup", TimeUnit.SECONDS.toMillis(10L), false);
    this.plugin = plugin;
    this.classWarmups = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    new BukkitRunnable()
    {
      public void run()
      {
        for (Player player : ) {
          PvpClassWarmupTimer.this.attemptEquip(player);
        }
      }
    }
    
      .runTaskLater(plugin, 10L);
  }
  
  public void onDisable(Config config)
  {
    super.onDisable(config);
    this.classWarmups.clear();
  }
  
  public String getScoreboardPrefix()
  {
    return ChatColor.AQUA + ChatColor.BOLD.toString();
  }
  
  public TimerRunnable clearCooldown(UUID playerUUID)
  {
    TimerRunnable runnable = super.clearCooldown(playerUUID);
    if (runnable != null)
    {
      this.classWarmups.remove(playerUUID);
      return runnable;
    }
    return null;
  }
  
  public void onExpire(UUID userUUID)
  {
    Player player = Bukkit.getPlayer(userUUID);
    if (player == null) {
      return;
    }
    String className = (String)this.classWarmups.remove(userUUID);
    Preconditions.checkNotNull(className, "Attempted to equip a class for %s, but nothing was added", new Object[] { player.getName() });
    this.plugin.getPvpClassManager().setEquippedClass(player, this.plugin.getPvpClassManager().getPvpClass(className));
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerKick(PlayerQuitEvent event)
  {
    this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    attemptEquip(event.getPlayer());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onEquipmentSet(EquipmentSetEvent event)
  {
    HumanEntity humanEntity = event.getHumanEntity();
    if ((humanEntity instanceof Player)) {
      attemptEquip((Player)humanEntity);
    }
  }
  
  private void attemptEquip(Player player)
  {
    PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
    if (equipped != null)
    {
      if (equipped.isApplicableFor(player)) {
        return;
      }
      this.plugin.getPvpClassManager().setEquippedClass(player, null);
    }
    PvpClass warmupClass = null;
    String warmup = (String)this.classWarmups.get(player.getUniqueId());
    if (warmup != null)
    {
      warmupClass = this.plugin.getPvpClassManager().getPvpClass(warmup);
      if (!warmupClass.isApplicableFor(player)) {
        clearCooldown(player.getUniqueId());
      }
    }
    Collection<PvpClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();
    for (PvpClass pvpClass : pvpClasses) {
      if ((warmupClass != pvpClass) && (pvpClass.isApplicableFor(player)))
      {
        this.classWarmups.put(player.getUniqueId(), pvpClass.getName());
        setCooldown(player, player.getUniqueId(), pvpClass.getWarmupDelay(), false);
        break;
      }
    }
  }
}

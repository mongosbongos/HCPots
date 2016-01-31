package io.ohho.hcf.listener;

import com.parapvp.base.kit.event.KitApplyEvent;

import io.ohho.hcf.HCF;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class KitMapListener
  implements Listener
{
  final HCF plugin;
  
  public KitMapListener(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onCreatureSpawn(CreatureSpawnEvent event) {}
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {}
  
  @EventHandler
  public void onDeath(PlayerDeathEvent e) {}
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onPlayerDropItem(PlayerDropItemEvent event) {}
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onKitApplyMonitor(KitApplyEvent event) {}
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onItemSpawn(ItemSpawnEvent event) {}
}

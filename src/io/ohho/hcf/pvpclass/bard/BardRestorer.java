package io.ohho.hcf.pvpclass.bard;

import com.exodon.hcf.pvpclass.bard.BardClass;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import io.ohho.hcf.HCF;
import io.ohho.hcf.pvpclass.event.PvpClassUnequipEvent;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BardRestorer
  implements Listener
{
  private final Table<UUID, PotionEffectType, PotionEffect> restores;
  
  public BardRestorer(HCF plugin)
  {
    this.restores = HashBasedTable.create();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPvpClassUnequip(PvpClassUnequipEvent event)
  {
    this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
  }
  
  public void setRestoreEffect(Player player, PotionEffect effect)
  {
    boolean shouldCancel = true;
    Collection<PotionEffect> activeList = player.getActivePotionEffects();
    for (PotionEffect active : activeList) {
      if (active.getType().equals(effect.getType()))
      {
        if (effect.getAmplifier() < active.getAmplifier()) {
          return;
        }
        if ((effect.getAmplifier() == active.getAmplifier()) && (effect.getDuration() < active.getDuration())) {
          return;
        }
        this.restores.put(player.getUniqueId(), active.getType(), active);
        shouldCancel = false;
      }
    }
    player.addPotionEffect(effect, true);
    if ((shouldCancel) && (effect.getDuration() > 100) && (effect.getDuration() < BardClass.DEFAULT_MAX_DURATION)) {
      this.restores.remove(player.getUniqueId(), effect.getType());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPotionEffectExpire(PotionEffectExpireEvent event)
  {
    LivingEntity livingEntity = event.getEntity();
    if ((livingEntity instanceof Player))
    {
      Player player = (Player)livingEntity;
      PotionEffect previous = (PotionEffect)this.restores.remove(player.getUniqueId(), event.getEffect().getType());
      if (previous != null)
      {
        event.setCancelled(true);
        player.addPotionEffect(previous, true);
      }
    }
  }
}

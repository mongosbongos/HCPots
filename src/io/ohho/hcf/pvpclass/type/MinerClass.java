package io.ohho.hcf.pvpclass.type;

import com.parapvp.util.BukkitUtils;

import io.ohho.hcf.HCF;
import io.ohho.hcf.pvpclass.PvpClass;
import io.ohho.hcf.pvpclass.PvpClassManager;
import io.ohho.hcf.pvpclass.event.PvpClassEquipEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MinerClass
  extends PvpClass
  implements Listener
{
  private static final int INVISIBILITY_HEIGHT_LEVEL = 30;
  private static final PotionEffect HEIGHT_INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
  private final HCF plugin;
  
  public MinerClass(HCF plugin)
  {
    super("Miner", TimeUnit.SECONDS.toMillis(10L));
    this.plugin = plugin;
    this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
  }
  
  private void removeInvisibilitySafely(Player player)
  {
    for (PotionEffect active : player.getActivePotionEffects()) {
      if ((active.getType().equals(PotionEffectType.INVISIBILITY)) && (active.getDuration() > DEFAULT_MAX_DURATION))
      {
        player.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.YELLOW + " invisibility and haste disabled.");
        player.removePotionEffect(active.getType());
        break;
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerDamage(EntityDamageByEntityEvent event)
  {
    Entity entity = event.getEntity();
    if (((entity instanceof Player)) && (BukkitUtils.getFinalAttacker(event, false) != null))
    {
      Player player = (Player)entity;
      if (this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
        removeInvisibilitySafely(player);
      }
    }
  }
  
  public void onUnequip(Player player)
  {
    super.onUnequip(player);
    removeInvisibilitySafely(player);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerMove(PlayerMoveEvent event)
  {
    conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerTeleport(PlayerTeleportEvent event)
  {
    conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onClassEquip(PvpClassEquipEvent event)
  {
    Player player = event.getPlayer();
    if (event.getPvpClass().equals(this))
    {
      player.addPotionEffect(HEIGHT_INVISIBILITY, true);
      player.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.YELLOW + " invisibility and haste enabled.");
    }
  }
  
  private void conformMinerInvisibility(Player player, Location from, Location to)
  {
    int fromY = from.getBlockY();
    int toY = to.getBlockY();
    if ((fromY != toY) && (this.plugin.getPvpClassManager().hasClassEquipped(player, this)))
    {
      boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
      if (toY > 30)
      {
        if ((fromY <= 30) && (isInvisible)) {
          removeInvisibilitySafely(player);
        }
      }
      else if (!isInvisible)
      {
        player.addPotionEffect(HEIGHT_INVISIBILITY, true);
        player.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.YELLOW + " invisibility and haste enabled.");
      }
    }
  }
  
  public boolean isApplicableFor(Player player)
  {
    PlayerInventory playerInventory = player.getInventory();
    ItemStack helmet = playerInventory.getHelmet();
    if ((helmet == null) || (helmet.getType() != Material.IRON_HELMET) || (!helmet.getEnchantments().isEmpty())) {
      return false;
    }
    ItemStack chestplate = playerInventory.getChestplate();
    if ((chestplate == null) || (chestplate.getType() != Material.IRON_CHESTPLATE) || (!chestplate.getEnchantments().isEmpty())) {
      return false;
    }
    ItemStack leggings = playerInventory.getLeggings();
    if ((leggings == null) || (leggings.getType() != Material.IRON_LEGGINGS) || (!leggings.getEnchantments().isEmpty())) {
      return false;
    }
    ItemStack boots = playerInventory.getBoots();
    return (boots != null) && (boots.getType() == Material.IRON_BOOTS) && (boots.getEnchantments().isEmpty());
  }
}

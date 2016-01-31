package io.ohho.hcf.listener.fixes;

import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.ohho.hcf.ConfigurationService;

public class NoPermissionClickListener
  implements Listener
{
  @EventHandler
  public void onClick(PlayerInteractEvent e)
  {
    for (Enchantment enchantment : e.getItem().getEnchantments().keySet()) {
      if ((ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment)) && (((Integer)e.getItem().getEnchantments().get(enchantment)).intValue() > ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue()))
      {
        e.getItem().removeEnchantment(enchantment);
        e.getItem().addEnchantment(enchantment, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue());
      }
    }
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent e)
  {
    Player player = e.getPlayer();
    if ((player.getGameMode() == GameMode.CREATIVE) && (!player.hasPermission("base.command.gamemode")))
    {
      e.setCancelled(true);
      player.setGameMode(GameMode.SURVIVAL);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onBlockPlaceCreative(BlockBreakEvent event)
  {
    Player player = event.getPlayer();
    if ((player.getGameMode() == GameMode.CREATIVE) && (!player.hasPermission("base.command.gamemode")))
    {
      event.setCancelled(true);
      player.setGameMode(GameMode.SURVIVAL);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onBlockBreakCreative(BlockBreakEvent event)
  {
    Player player = event.getPlayer();
    if ((player.getGameMode() == GameMode.CREATIVE) && (!player.hasPermission("base.command.gamemode")))
    {
      event.setCancelled(true);
      player.setGameMode(GameMode.SURVIVAL);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onInventoryCreative(InventoryCreativeEvent event)
  {
    HumanEntity humanEntity = event.getWhoClicked();
    if (((humanEntity instanceof Player)) && (!humanEntity.hasPermission("base.command.gamemode")))
    {
      event.setCancelled(true);
      humanEntity.setGameMode(GameMode.SURVIVAL);
    }
  }
}

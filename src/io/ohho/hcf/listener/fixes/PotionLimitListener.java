package io.ohho.hcf.listener.fixes;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import io.ohho.hcf.ConfigurationService;

public class PotionLimitListener
  implements Listener
{
  private static final int EMPTY_BREW_TIME = 400;
  
  public int getMaxLevel(PotionType type)
  {
    return ((Integer)ConfigurationService.POTION_LIMITS.getOrDefault(type, Integer.valueOf(type.getMaxLevel()))).intValue();
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onBrew(BrewEvent event)
  {
    for (ItemStack stack : event.getContents().getContents()) {
      if (stack.getType() == Material.FERMENTED_SPIDER_EYE) {
        event.setCancelled(true);
      }
    }
    if (!testValidity(event.getResults())) {
      event.setCancelled(true);
    }
  }
  
  private boolean testValidity(ItemStack[] contents)
  {
    for (ItemStack stack : contents) {
      if ((stack != null) && (stack.getType() == Material.POTION) && (stack.getDurability() != 0))
      {
        Potion potion = Potion.fromItemStack(stack);
        if (potion != null)
        {
          PotionType type = potion.getType();
          if (type != null)
          {
            if (((type != PotionType.POISON) || (potion.hasExtendedDuration()) || (potion.getLevel() != 1)) && 
              (potion.getLevel() > getMaxLevel(type))) {
              return false;
            }
            if ((type == PotionType.POISON) && 
              (potion.hasExtendedDuration())) {
              return false;
            }
            if ((type == PotionType.SLOWNESS) && 
              (potion.hasExtendedDuration())) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }
}

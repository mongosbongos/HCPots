package io.ohho.hcf.listener;

import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ExpListener
  implements Listener
{
  String permission = "hcf.doublexp";
  
  @EventHandler
  public void onXPDrop(EntityDeathEvent e)
  {
    if (((e.getEntity().getKiller() instanceof Player)) && (e.getEntity().getKiller() != null))
    {
      if (e.getEntity().getKiller().getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.LOOT_BONUS_MOBS)) {
        e.setDroppedExp(e.getDroppedExp() * ((Integer)e.getEntity().getKiller().getPlayer().getItemInHand().getEnchantments().get(Enchantment.LOOT_BONUS_MOBS)).intValue());
      }
      if (e.getEntity().getKiller().hasPermission(this.permission)) {
        e.setDroppedExp(e.getDroppedExp() * 2);
      }
    }
  }
}

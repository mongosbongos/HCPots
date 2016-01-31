package io.ohho.hcf.listener.fixes;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.oCore.faction.FactionManager;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class HungerFixListener
  implements Listener
{
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    EntityPlayer entityPlayer = ((CraftPlayer)event.getPlayer()).getHandle();
    entityPlayer.knockbackReductionX = 0.6F;
    entityPlayer.knockbackReductionY = 0.55F;
    entityPlayer.knockbackReductionZ = 0.6F;
  }
  
  @EventHandler
  public void onMove(PlayerMoveEvent e)
  {
    if ((HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).isSafezone()) && 
      (e.getPlayer().getFoodLevel() < 20))
    {
      e.getPlayer().setFoodLevel(20);
      e.getPlayer().setSaturation(20.0F);
    }
  }
  
  @EventHandler
  public void onHungerChange(FoodLevelChangeEvent e)
  {
    if ((e.getEntity() instanceof Player))
    {
      Player p = (Player)e.getEntity();
      if (HCF.getPlugin().getFactionManager().getFactionAt(p.getLocation()).isSafezone())
      {
        p.setSaturation(20.0F);
        p.setHealth(20.0D);
      }
      p.setSaturation(10.0F);
    }
  }
}

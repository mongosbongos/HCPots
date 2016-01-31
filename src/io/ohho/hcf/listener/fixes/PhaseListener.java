package io.ohho.hcf.listener.fixes;

import com.parapvp.base.BasePlugin;
import com.parapvp.base.PlayTimeManager;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.oCore.faction.FactionManager;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PhaseListener
  implements Listener
{
  long gravityBlock = TimeUnit.HOURS.toMillis(6L);
  long utilityBlock = TimeUnit.HOURS.toMillis(3L);
  
  @EventHandler
  public void onMove(PlayerInteractEvent e)
  {
    if ((e.getPlayer().getLocation().getBlock() != null) && 
      (e.getPlayer().getLocation().getBlock().getType() == Material.TRAP_DOOR) && 
      (!HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).equals(HCF.getPlugin().getFactionManager().getPlayerFaction(e.getPlayer().getUniqueId()))))
    {
      e.getPlayer().sendMessage(ChatColor.RED + "Glitch detected. Now reporting, and fixing.");
      e.getPlayer().teleport(e.getPlayer().getLocation().add(0.0D, 1.0D, 0.0D));
    }
  }
  
  @EventHandler
  public void onPlayerTimePlace(BlockPlaceEvent event)
  {
    if ((!event.getPlayer().hasPermission("hcf.gravity.bypass")) && (event.getPlayer().getGameMode() != GameMode.CREATIVE))
    {
      Player player = event.getPlayer();
      if ((event.getBlockPlaced().getType() == Material.SAND) || (event.getBlockPlaced().getType() == Material.GRAVEL))
      {
        if (BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()) <= this.gravityBlock)
        {
          player.sendMessage(ChatColor.RED + "You must wait another " + DurationFormatUtils.formatDurationWords(this.gravityBlock - BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()), true, true) + " before placing a gravity block.");
          event.setCancelled(true);
        }
      }
      else if ((event.getBlockPlaced().getType() == Material.ANVIL) && 
        (BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()) <= this.utilityBlock))
      {
        player.sendMessage(ChatColor.RED + "You must wait another " + DurationFormatUtils.formatDurationWords(this.utilityBlock - BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()), true, true) + " before placing an anvil.");
        event.setCancelled(true);
        return;
      }
    }
  }
}

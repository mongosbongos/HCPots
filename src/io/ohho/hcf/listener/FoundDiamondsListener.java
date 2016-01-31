package io.ohho.hcf.listener;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.struct.Relation;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.hcf.user.FactionUser;
import io.ohho.hcf.user.UserManager;
import io.ohho.oCore.faction.FactionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class FoundDiamondsListener
  implements Listener
{
  private static final String NOTIFICATION_PERMISSION = "hcf.founddiamonds.alert";
  public static final Material SEARCH_TYPE = Material.DIAMOND_ORE;
  private static final int SEARCH_RADIUS = 3;
  public final Set<String> foundLocations;
  private final HCF plugin;
  
  public FoundDiamondsListener(HCF plugin)
  {
    this.foundLocations = new HashSet();
    this.plugin = plugin;
    this.foundLocations.addAll(plugin.getConfig().getStringList("registered-diamonds"));
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPistonExtend(BlockPistonExtendEvent event)
  {
    for (Block block : event.getBlocks()) {
      if (block.getType() == SEARCH_TYPE) {
        this.foundLocations.add(block.getLocation().toString());
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onBlockPlace(BlockPlaceEvent event)
  {
    Block block = event.getBlock();
    if (block.getType() == SEARCH_TYPE) {
      this.foundLocations.add(block.getLocation().toString());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onBlockBreak(BlockBreakEvent event)
  {
    Player player = event.getPlayer();
    if (player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    Block block = event.getBlock();
    Location blockLocation = block.getLocation();
    int count;
    int x;
    if ((block.getType() == SEARCH_TYPE) && (this.foundLocations.add(blockLocation.toString())))
    {
      count = 1;
      for (x = -5; x < 5; x++) {
        for (int y = -5; y < 5; y++) {
          for (int z = -5; z < 5; z++)
          {
            Block otherBlock = blockLocation.clone().add(x, y, z).getBlock();
            if ((!otherBlock.equals(block)) && (otherBlock.getType() == SEARCH_TYPE) && (this.foundLocations.add(otherBlock.getLocation().toString()))) {
              count++;
            }
          }
        }
      }
      this.plugin.getUserManager().getUser(player.getUniqueId()).setDiamondsMined(this.plugin.getUserManager().getUser(player.getUniqueId()).getDiamondsMined() + count);
      for (Player on : Bukkit.getOnlinePlayers())
      {
        String message = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getRelation(on).toChatColour() + player.getName() + ChatColor.GRAY + " has found" + ChatColor.AQUA + " Diamonds " + ChatColor.GRAY + '[' + ChatColor.AQUA + count + ChatColor.GRAY + ']';
        on.sendMessage(message);
      }
    }
  }
  
  public void saveConfig()
  {
    this.plugin.getConfig().set("registered-diamonds", new ArrayList(this.foundLocations));
    this.plugin.saveConfig();
  }
}

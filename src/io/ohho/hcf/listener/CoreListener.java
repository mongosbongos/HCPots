package io.ohho.hcf.listener;

import com.parapvp.base.BasePlugin;
import com.parapvp.base.user.BaseUser;

import io.ohho.hcf.HCF;
import io.ohho.hcf.deathban.DeathbanManager;
import io.ohho.hcf.user.FactionUser;
import io.ohho.hcf.visualise.VisualiseHandler;
import io.ohho.hcfold.crate.Key;
import io.ohho.hcfold.crate.KeyManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CoreListener
  implements Listener
{
  private final HCF plugin;
  
  public CoreListener(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPlayerJoinKit(PlayerJoinEvent event)
  {
    BaseUser user = BasePlugin.getPlugin().getUserManager().getUser(event.getPlayer().getUniqueId());
    if (!user.hasStarterKit())
    {
      Player player = event.getPlayer();
      if (player.hasPermission("Omega")) {
        setGear(player, Integer.valueOf(150), Integer.valueOf(16));
      }
      if (player.hasPermission("Exo")) {
        setGear(player, Integer.valueOf(11), Integer.valueOf(11));
      }
      if (player.hasPermission("Elite")) {
        setGear(player, Integer.valueOf(8), Integer.valueOf(8));
      }
      if (player.hasPermission("Advanced")) {
        setGear(player, Integer.valueOf(4), Integer.valueOf(5));
      }
      if (player.hasPermission("Ultra")) {
        setGear(player, Integer.valueOf(2), Integer.valueOf(3));
      }
      if (player.hasPermission("Basic")) {
        setGear(player, Integer.valueOf(0), Integer.valueOf(1));
      }
    }
  }
  
  public void setGear(Player user, Integer lives, Integer keys)
  {
    BaseUser baseUser = BasePlugin.getPlugin().getUserManager().getUser(user.getUniqueId());
    if (!baseUser.hasStarterKit())
    {
      Key key = this.plugin.getKeyManager().getKey("Moku");
      ItemStack stack = key.getItemStack().clone();
      stack.setAmount(keys.intValue());
      user.getInventory().addItem(new ItemStack[] { stack });
      user.sendMessage(ChatColor.YELLOW + "You have recieved your Exo Keys.");
      this.plugin.getDeathbanManager().addLives(user.getUniqueId(), lives.intValue());
      user.sendMessage(ChatColor.YELLOW + "You have recieved your Lives.");
      baseUser.setStarterKit(true);
      if (!baseUser.hasStarterKit()) {
        user.sendMessage(ChatColor.RED + "Get an admin. Do not log off.");
      } else {
        user.sendMessage(ChatColor.YELLOW + "Thank you for donating.");
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    event.setJoinMessage((String)null);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerQuit(PlayerKickEvent event)
  {
    event.setLeaveMessage((String)null);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    event.setQuitMessage((String)null);
    Player player = event.getPlayer();
    this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
    this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
  {
    Player player = event.getPlayer();
    this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
    this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
  }
}

package io.ohho.hcf.timer.type;

import com.google.common.base.Optional;

import io.ohho.hcf.HCF;
import io.ohho.hcf.pvpclass.archer.ArcherClass;
import io.ohho.hcf.scoreboard.PlayerBoard;
import io.ohho.hcf.scoreboard.ScoreboardHandler;
import io.ohho.hcf.timer.PlayerTimer;
import io.ohho.hcf.timer.event.TimerExpireEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ArcherTimer
  extends PlayerTimer
  implements Listener
{
  private final HCF plugin;
  
  public String getScoreboardPrefix()
  {
    return ChatColor.GOLD.toString() + ChatColor.BOLD;
  }
  
  public ArcherTimer(HCF plugin)
  {
    super("Archer Mark", TimeUnit.SECONDS.toMillis(7L));
    this.plugin = plugin;
  }
  
  public void run() {}
  
  @EventHandler
  public void onExpire(TimerExpireEvent e)
  {
    if ((e.getUserUUID().isPresent()) && 
      (e.getTimer().equals(this)))
    {
      UUID userUUID = (UUID)e.getUserUUID().get();
      Player player = Bukkit.getPlayer(userUUID);
      if (player == null) {
        return;
      }
      Bukkit.getPlayer((UUID)ArcherClass.tagged.get(userUUID)).sendMessage(ChatColor.YELLOW + "Your archer mark on " + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has expired.");
      player.sendMessage(ChatColor.YELLOW + "You're no longer archer marked.");
      ArcherClass.tagged.remove(player.getUniqueId());
      for (Player players : Bukkit.getOnlinePlayers()) {
        this.plugin.getScoreboardHandler().getPlayerBoard(players.getUniqueId()).addUpdates(Bukkit.getOnlinePlayers());
      }
    }
  }
  
  @EventHandler
  public void onHit(EntityDamageByEntityEvent e)
  {
    if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player)))
    {
      Player entity = (Player)e.getEntity();
      Entity damager = (Player)e.getDamager();
      if (getRemaining(entity) > 0L)
      {
        Double damage = Double.valueOf(e.getDamage() * 0.3D);
        e.setDamage(e.getDamage() + damage.doubleValue());
      }
    }
    if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow)))
    {
      Player entity = (Player)e.getEntity();
      Entity damager = (Player)((Arrow)e.getDamager()).getShooter();
      if (((damager instanceof Player)) && 
        (getRemaining(entity) > 0L))
      {
        if (((UUID)ArcherClass.tagged.get(entity.getUniqueId())).equals(damager.getUniqueId())) {
          setCooldown(entity, entity.getUniqueId());
        }
        Double damage = Double.valueOf(e.getDamage() * 0.3D);
        e.setDamage(e.getDamage() + damage.doubleValue());
      }
    }
  }
}

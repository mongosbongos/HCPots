package io.ohho.hcf.deathban;

import com.exodon.hcf.deathban.Deathban;
import io.ohho.hcf.deathban.DeathbanListener;
import com.exodon.hcf.deathban.LoginMessageRunnable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.parapvp.base.BasePlugin;
import com.parapvp.base.user.BaseUser;
import com.parapvp.base.user.ServerParticipator;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.eotw.EotwHandler;
import com.exodon.hcf.user.FactionUser;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathbanListener
  implements Listener
{
  private static final long LIFE_USE_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30L);
  private static final String LIFE_USE_DELAY_WORDS = DurationFormatUtils.formatDurationWords(LIFE_USE_DELAY_MILLIS, true, true);
  private static final String DEATH_BAN_BYPASS_PERMISSION = "hcf.deathban.bypass";
  private final ConcurrentMap<Object, Object> lastAttemptedJoinMap;
  private final HCF plugin;
  
  public DeathbanListener(HCF plugin)
  {
    this.plugin = plugin;
    this.lastAttemptedJoinMap = CacheBuilder.newBuilder().expireAfterWrite(LIFE_USE_DELAY_MILLIS, TimeUnit.MILLISECONDS).build().asMap();
  }
  
  @EventHandler
  public void onLookAtServer(ServerListPingEvent e)
  {
    if (Bukkit.spigot().getTPS()[0] > 15.0D) {
      for (ServerParticipator participator : BasePlugin.getPlugin().getUserManager().getParticipators().values()) {
        if ((participator instanceof BaseUser))
        {
          BaseUser baseUser = (BaseUser)participator;
          if (baseUser.getAddressHistories().contains(e.getAddress().toString().replace("/", "")))
          {
            UUID uuid = baseUser.getUniqueId();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String playerName = player.getName();
            if (playerName != null) {
              if ((this.plugin.getUserManager().getUser(player.getUniqueId()).getDeathban() != null) && (this.plugin.getUserManager().getUser(player.getUniqueId()).getDeathban().isActive())) {
                e.setMotd(e.getMotd() + "\n" + ChatColor.RED + "Deathbanned for another: " + ChatColor.BOLD + HCF.getRemaining(this.plugin.getUserManager().getUser(player.getUniqueId()).getDeathban().getRemaining(), true));
              }
            }
          }
        }
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPlayerLogin(PlayerLoginEvent event)
  {
    Player player = event.getPlayer();
    FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
    Deathban deathban = user.getDeathban();
    if ((deathban == null) || (!deathban.isActive())) {
      return;
    }
    if (player.hasPermission("hcf.deathban.bypass"))
    {
      new LoginMessageRunnable(player, ChatColor.RED + "You would be death-banned for " + deathban.getReason() + ChatColor.RED + ", but you have access to bypass.").runTask(this.plugin);
      return;
    }
    if (this.plugin.getEotwHandler().isEndOfTheWorld())
    {
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.\nCome back for SOTW.");
    }
    else
    {
      UUID uuid = player.getUniqueId();
      int lives = this.plugin.getDeathbanManager().getLives(uuid);
      String formattedDuration = HCF.getRemaining(deathban.getRemaining(), true, false);
      String reason = deathban.getReason();
      String prefix = ChatColor.RED + "You are currently death-banned" + (reason != null ? " for " + reason + ".\n" : ".") + ChatColor.YELLOW + formattedDuration + " remaining.\n" + ChatColor.RED + "You currently have " + (lives <= 0 ? "no" : Integer.valueOf(lives)) + " lives.";
      if (lives > 0)
      {
        long millis = System.currentTimeMillis();
        Long lastAttemptedJoinMillis = (Long)this.lastAttemptedJoinMap.get(uuid);
        if ((lastAttemptedJoinMillis != null) && (lastAttemptedJoinMillis.longValue() - System.currentTimeMillis() < LIFE_USE_DELAY_MILLIS))
        {
          this.lastAttemptedJoinMap.remove(uuid);
          user.removeDeathban();
          lives = this.plugin.getDeathbanManager().takeLives(uuid, 1);
          event.setResult(PlayerLoginEvent.Result.ALLOWED);
          new LoginMessageRunnable(player, ChatColor.YELLOW + "You have used a life bypass your death. You now have " + ChatColor.LIGHT_PURPLE + lives + ChatColor.YELLOW + " lives.").runTask(this.plugin);
        }
        else
        {
          this.lastAttemptedJoinMap.put(uuid, Long.valueOf(millis + LIFE_USE_DELAY_MILLIS));
          event.disallow(PlayerLoginEvent.Result.KICK_OTHER, prefix + ChatColor.GOLD + "\n\n" + "You may use a life by reconnecting within " + ChatColor.YELLOW + LIFE_USE_DELAY_WORDS + ChatColor.GOLD + '.');
        }
        return;
      }
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Still deathbanned for " + formattedDuration + ": " + ChatColor.YELLOW + deathban.getReason() + ChatColor.RED + '.' + "\nYou can purchase lives at " + "donate.Moku.us" + " to bypass this.");
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    final Player player = event.getEntity();
    final Deathban deathban = this.plugin.getDeathbanManager().applyDeathBan(player, event.getDeathMessage());
    final String durationString = HCF.getRemaining(deathban.getRemaining(), true, false);
    new BukkitRunnable()
    {
      public void run()
      {
        if (DeathbanListener.this.plugin.getEotwHandler().isEndOfTheWorld()) {
          player.kickPlayer(ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.\nCome back tomorrow for SOTW!");
        } else {
          player.kickPlayer(ChatColor.RED + "Deathbanned for " + durationString + ": " + ChatColor.YELLOW + deathban.getReason());
        }
      }
    }
    
      .runTaskLater(this.plugin, 1L);
  }
  
  private static class LoginMessageRunnable
    extends BukkitRunnable
  {
    private final Player player;
    private final String message;
    
    public LoginMessageRunnable(Player player, String message)
    {
      this.player = player;
      this.message = message;
    }
    
    public void run()
    {
      this.player.sendMessage(this.message);
    }
  }
}

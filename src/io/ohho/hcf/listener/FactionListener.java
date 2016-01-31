package io.ohho.hcf.listener;

import com.google.common.base.Optional;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.CaptureZone;
import io.ohho.hcf.eventgame.eotw.EotwHandler;
import io.ohho.hcf.eventgame.faction.CapturableFaction;
import io.ohho.hcf.eventgame.faction.KothFaction;
import io.ohho.hcf.faction.event.CaptureZoneEnterEvent;
import io.ohho.hcf.faction.event.CaptureZoneLeaveEvent;
import io.ohho.hcf.faction.event.FactionCreateEvent;
import io.ohho.hcf.faction.event.FactionRemoveEvent;
import io.ohho.hcf.faction.event.FactionRenameEvent;
import io.ohho.hcf.faction.event.PlayerClaimEnterEvent;
import io.ohho.hcf.faction.event.PlayerJoinFactionEvent;
import io.ohho.hcf.faction.event.PlayerLeaveFactionEvent;
import io.ohho.hcf.faction.event.PlayerLeftFactionEvent;
import io.ohho.hcf.faction.struct.RegenStatus;
import io.ohho.hcf.faction.struct.Role;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.hcf.user.FactionUser;
import io.ohho.hcf.user.UserManager;
import io.ohho.oCore.faction.FactionManager;
import io.ohho.oCore.faction.FactionMember;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class FactionListener
  implements Listener
{
  private static final long FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
  private static final String FACTION_JOIN_WAIT_WORDS = DurationFormatUtils.formatDurationWords(FACTION_JOIN_WAIT_MILLIS, true, true);
  private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
  private static final long LAND_CHANGE_MSG_THRESHOLD = 225L;
  private final HCF plugin;
  
  public FactionListener(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onFactionCreate(FactionCreateEvent event)
  {
    Faction faction = event.getFaction();
    if ((faction instanceof PlayerFaction))
    {
      CommandSender sender = event.getSender();
      Bukkit.broadcastMessage(ChatColor.RED + "" + event.getFaction().getName() + ChatColor.YELLOW + " has been created by " + ChatColor.WHITE + "" + sender.getName());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onFactionRemove(FactionRemoveEvent event)
  {
    Faction faction = event.getFaction();
    if ((faction instanceof PlayerFaction))
    {
      CommandSender sender = event.getSender();
      Bukkit.broadcastMessage(ChatColor.RED + "" + event.getFaction().getName() + ChatColor.YELLOW + " has been disbanded by " + ChatColor.WHITE + sender.getName());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onFactionRename(FactionRenameEvent event)
  {
    Faction faction = event.getFaction();
    if ((faction instanceof PlayerFaction)) {
      Bukkit.broadcastMessage(ChatColor.RED + event.getOriginalName() + ChatColor.YELLOW + " has been renamed to " + ChatColor.RED + "" + event.getNewName() + ChatColor.YELLOW + " by " + ChatColor.WHITE + event.getSender().getName());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onFactionRenameMonitor(FactionRenameEvent event)
  {
    Faction faction = event.getFaction();
    if ((faction instanceof KothFaction)) {
      ((KothFaction)faction).getCaptureZone().setName(event.getNewName());
    }
  }
  
  private long getLastLandChangedMeta(Player player)
  {
    MetadataValue value = player.getMetadata("landChangedMessage", this.plugin);
    long millis = System.currentTimeMillis();
    long remaining = value == null ? 0L : value.asLong() - millis;
    if (remaining <= 0L) {
      player.setMetadata("landChangedMessage", new FixedMetadataValue(this.plugin, Long.valueOf(millis + 225L)));
    }
    return remaining;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onCaptureZoneEnter(CaptureZoneEnterEvent event)
  {
    Player player = event.getPlayer();
    if ((getLastLandChangedMeta(player) <= 0L) && 
      (this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts())) {
      player.sendMessage(ChatColor.YELLOW + "Now entering capture zone: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onCaptureZoneLeave(CaptureZoneLeaveEvent event)
  {
    Player player = event.getPlayer();
    if ((getLastLandChangedMeta(player) <= 0L) && 
      (this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts())) {
      player.sendMessage(ChatColor.YELLOW + "Now leaving capture zone: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  private void onPlayerClaimEnter(PlayerClaimEnterEvent event)
  {
    Faction toFaction = event.getToFaction();
    if (toFaction.isSafezone())
    {
      Player player = event.getPlayer();
      player.setHealth(player.getMaxHealth());
      player.setFoodLevel(20);
      player.setFireTicks(0);
      player.setSaturation(4.0F);
    }
    Player player = event.getPlayer();
    if (getLastLandChangedMeta(player) <= 0L)
    {
      Faction fromFaction = event.getFromFaction();
      player.sendMessage(ChatColor.YELLOW + "Now leaving: " + fromFaction.getDisplayName(player) + ChatColor.YELLOW + '(' + (fromFaction.isDeathban() ? ChatColor.RED + "Deathban" : new StringBuilder().append(ChatColor.GREEN).append("Non-Deathban").toString()) + ChatColor.YELLOW + ')');
      player.sendMessage(ChatColor.YELLOW + "Now entering: " + toFaction.getDisplayName(player) + ChatColor.YELLOW + '(' + (toFaction.isDeathban() ? ChatColor.RED + "Deathban" : new StringBuilder().append(ChatColor.GREEN).append("Non-Deathban").toString()) + ChatColor.YELLOW + ')');
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerLeftFaction(PlayerLeftFactionEvent event)
  {
    Optional<Player> optionalPlayer = event.getPlayer();
    if (optionalPlayer.isPresent()) {
      this.plugin.getUserManager().getUser(((Player)optionalPlayer.get()).getUniqueId()).setLastFactionLeaveMillis(System.currentTimeMillis());
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event)
  {
    Faction faction = event.getFaction();
    Optional optionalPlayer = event.getPlayer();
    if (((faction instanceof PlayerFaction)) && (optionalPlayer.isPresent()))
    {
      Player player = (Player)optionalPlayer.get();
      PlayerFaction playerFaction = (PlayerFaction)faction;
      if ((!this.plugin.getEotwHandler().isEndOfTheWorld()) && (playerFaction.getRegenStatus() == RegenStatus.PAUSED))
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
        return;
      }
      long difference = this.plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis() - System.currentTimeMillis() + FACTION_JOIN_WAIT_MILLIS;
      if ((difference > 0L) && (!player.hasPermission("hcf.faction.argument.staff.forcejoin")))
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot join factions after just leaving within " + FACTION_JOIN_WAIT_WORDS + ". " + "You gotta wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onFactionLeave(PlayerLeaveFactionEvent event)
  {
    Faction faction = event.getFaction();
    if ((faction instanceof PlayerFaction))
    {
      Optional optional = event.getPlayer();
      if (optional.isPresent())
      {
        Player player = (Player)optional.get();
        if (this.plugin.getFactionManager().getFactionAt(player.getLocation()).equals(faction))
        {
          event.setCancelled(true);
          player.sendMessage(ChatColor.RED + "You cannot leave your faction whilst you remain in its' territory.");
        }
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    Player player = event.getPlayer();
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
    if (playerFaction != null)
    {
      playerFaction.printDetails(player);
      playerFaction.broadcast(ChatColor.GOLD + "Member Online: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.', new UUID[] { player.getUniqueId() });
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    Player player = event.getPlayer();
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
    if (playerFaction != null) {
      playerFaction.broadcast(ChatColor.GOLD + "Member Offline: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.');
    }
  }
}

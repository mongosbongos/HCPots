package io.ohho.hcf.eventgame;

import com.exodon.hcf.eventgame.CaptureZone;
import com.exodon.hcf.eventgame.EventTimer;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.parapvp.util.cuboid.Cuboid;

import io.ohho.hcf.DateTimeFormats;
import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.faction.ConquestFaction;
import io.ohho.hcf.eventgame.faction.EventFaction;
import io.ohho.hcf.eventgame.faction.KothFaction;
import io.ohho.hcf.eventgame.faction.ConquestFaction.ConquestZone;
import io.ohho.hcf.eventgame.tracker.EventTracker;
import io.ohho.hcf.faction.event.CaptureZoneEnterEvent;
import io.ohho.hcf.faction.event.CaptureZoneLeaveEvent;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.hcf.hcfold.EventSignListener;
import io.ohho.hcf.timer.GlobalTimer;
import io.ohho.hcfold.crate.Key;
import io.ohho.hcfold.crate.KeyManager;
import io.ohho.oCore.faction.FactionManager;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class EventTimer
  extends GlobalTimer
  implements Listener
{
  private static final long RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
  private static final String RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords(RESCHEDULE_FREEZE_MILLIS, true, true);
  private final HCF plugin;
  private long startStamp;
  private long lastContestedEventMillis;
  private EventFaction eventFaction;
  
  public EventTimer(final HCF plugin)
  {
    super("Event", 0L);
    this.plugin = plugin;
    new BukkitRunnable()
    {
      public void run()
      {
        if (EventTimer.this.eventFaction != null)
        {
          EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
          return;
        }
        LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        int day = now.getDayOfYear();
        int hour = now.getHour();
        int minute = now.getMinute();
        for (Map.Entry<LocalDateTime, String> entry : plugin.eventScheduler.getScheduleMap().entrySet())
        {
          LocalDateTime scheduledTime = (LocalDateTime)entry.getKey();
          if ((day == scheduledTime.getDayOfYear()) && (hour == scheduledTime.getHour())) {
            if (minute == scheduledTime.getMinute())
            {
              Faction faction = plugin.getFactionManager().getFaction((String)entry.getValue());
              if (((faction instanceof EventFaction)) && (EventTimer.this.tryContesting((EventFaction)faction, Bukkit.getConsoleSender()))) {
                break;
              }
            }
          }
        }
      }
    }
    
      .runTaskTimer(plugin, 20L, 20L);
  }
  
  public EventFaction getEventFaction()
  {
    return this.eventFaction;
  }
  
  public String getScoreboardPrefix()
  {
    return ChatColor.BLUE.toString();
  }
  
  public String getName()
  {
    return this.eventFaction == null ? "Event" : this.eventFaction.getName();
  }
  
  public boolean clearCooldown()
  {
    boolean result = super.clearCooldown();
    if (this.eventFaction != null)
    {
      for (CaptureZone captureZone : this.eventFaction.getCaptureZones()) {
        captureZone.setCappingPlayer(null);
      }
      this.eventFaction.setDeathban(true);
      this.eventFaction.getEventType().getEventTracker().stopTiming();
      this.eventFaction = null;
      this.startStamp = -1L;
      result = true;
    }
    return result;
  }
  
  public long getRemaining()
  {
    if (this.eventFaction == null) {
      return 0L;
    }
    if ((this.eventFaction instanceof KothFaction)) {
      return ((KothFaction)this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
    }
    return super.getRemaining();
  }
  
  public void handleWinner(Player winner)
  {
    if (this.eventFaction == null) {
      return;
    }
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner);
    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + this.eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + (playerFaction == null ? winner.getName() : playerFaction.getName()) + ChatColor.GOLD + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.GOLD + " after " + ChatColor.YELLOW + DurationFormatUtils.formatDurationWords(getUptime(), true, true) + ChatColor.GOLD + " of up-time" + ChatColor.GOLD + '.');
    World world = winner.getWorld();
    Location location = winner.getLocation();
    Key key = this.plugin.getKeyManager().getKey(ChatColor.stripColor(this.eventFaction.getEventType().getDisplayName()));
    Preconditions.checkNotNull(key, "Key on: EventTime error.");
    ItemStack stack = key.getItemStack().clone();
    Map<Integer, ItemStack> excess = winner.getInventory().addItem(new ItemStack[] { stack, EventSignListener.getEventSign(this.eventFaction.getName(), winner.getName()) });
    for (ItemStack entry : excess.values()) {
      world.dropItemNaturally(location, entry);
    }
    clearCooldown();
  }
  
  public boolean tryContesting(EventFaction eventFaction, CommandSender sender)
  {
    if (this.eventFaction != null)
    {
      sender.sendMessage(ChatColor.RED + "There is already an active event, use /event cancel to end it.");
      return false;
    }
    if ((eventFaction instanceof KothFaction))
    {
      KothFaction kothFaction = (KothFaction)eventFaction;
      if (kothFaction.getCaptureZone() == null)
      {
        sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
        return false;
      }
    }
    else if ((eventFaction instanceof ConquestFaction))
    {
      ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
      Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
      for (ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()) {
        if (!zones.contains(zone))
        {
          sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as capture zone '" + zone.getDisplayName() + ChatColor.RED + "' is not set.");
          return false;
        }
      }
    }
    long millis = System.currentTimeMillis();
    if (this.lastContestedEventMillis + RESCHEDULE_FREEZE_MILLIS - millis > 0L)
    {
      sender.sendMessage(ChatColor.RED + "Cannot reschedule events within " + RESCHEDULE_FREEZE_WORDS + '.');
      return false;
    }
    this.lastContestedEventMillis = millis;
    this.startStamp = millis;
    this.eventFaction = eventFaction;
    eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
    if ((eventFaction instanceof ConquestFaction))
    {
      setRemaining(1000L, true);
      setPaused(true);
    }
    Object captureZones = eventFaction.getCaptureZones();
    for (CaptureZone captureZone : (Collection)captureZones) {
      if (captureZone.isActive())
      {
        Player player = (Player)Iterables.getFirst(captureZone.getCuboid().getPlayers(), null);
        if ((player != null) && (eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone))) {
          captureZone.setCappingPlayer(player);
        }
      }
    }
    eventFaction.setDeathban(true);
    return true;
  }
  
  public long getUptime()
  {
    return System.currentTimeMillis() - this.startStamp;
  }
  
  public long getStartStamp()
  {
    return this.startStamp;
  }
  
  private void handleDisconnect(Player player)
  {
    Preconditions.checkNotNull(player);
    if (this.eventFaction == null) {
      return;
    }
    Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
    for (CaptureZone captureZone : captureZones) {
      if (Objects.equal(captureZone.getCappingPlayer(), player))
      {
        this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction);
        captureZone.setCappingPlayer(null);
        break;
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    handleDisconnect(event.getEntity());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerLogout(PlayerQuitEvent event)
  {
    handleDisconnect(event.getPlayer());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerKick(PlayerKickEvent event)
  {
    handleDisconnect(event.getPlayer());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onCaptureZoneEnter(CaptureZoneEnterEvent event)
  {
    if (this.eventFaction == null) {
      return;
    }
    CaptureZone captureZone = event.getCaptureZone();
    if (!this.eventFaction.getCaptureZones().contains(captureZone)) {
      return;
    }
    Player player = event.getPlayer();
    if ((captureZone.getCappingPlayer() == null) && (this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone))) {
      captureZone.setCappingPlayer(player);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onCaptureZoneLeave(CaptureZoneLeaveEvent event)
  {
    Player player;
    CaptureZone captureZone;
    if (Objects.equal(event.getFaction(), this.eventFaction))
    {
      player = event.getPlayer();
      captureZone = event.getCaptureZone();
      if ((Objects.equal(player, captureZone.getCappingPlayer())) && (this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)))
      {
        captureZone.setCappingPlayer(null);
        for (Player target : captureZone.getCuboid().getPlayers()) {
          if ((target != null) && (!target.equals(player)) && (this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)))
          {
            captureZone.setCappingPlayer(target);
            break;
          }
        }
      }
    }
  }
}

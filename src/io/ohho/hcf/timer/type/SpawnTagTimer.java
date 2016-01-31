package io.ohho.hcf.timer.type;

import com.google.common.base.Optional;
import com.parapvp.base.kit.event.KitApplyEvent;
import com.parapvp.util.BukkitUtils;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.event.PlayerClaimEnterEvent;
import io.ohho.hcf.faction.event.PlayerJoinFactionEvent;
import io.ohho.hcf.faction.event.PlayerLeaveFactionEvent;
import io.ohho.hcf.faction.event.PlayerClaimEnterEvent.EnterCause;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.timer.PlayerTimer;
import io.ohho.hcf.timer.event.TimerClearEvent;
import io.ohho.hcf.timer.event.TimerStartEvent;
import io.ohho.hcf.visualise.VisualType;
import io.ohho.hcf.visualise.VisualiseHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnTagTimer
  extends PlayerTimer
  implements Listener
{
  private static final long NON_WEAPON_TAG = 5000L;
  private final HCF plugin;
  
  public SpawnTagTimer(HCF plugin)
  {
    super("Spawn Tag", TimeUnit.SECONDS.toMillis(30L));
    this.plugin = plugin;
  }
  
  public String getScoreboardPrefix()
  {
    return ChatColor.RED.toString() + ChatColor.BOLD;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onKitApply(KitApplyEvent event)
  {
    Player player = event.getPlayer();
    long remaining;
    if ((!event.isForce()) && ((remaining = getRemaining(player)) > 0L))
    {
      event.setCancelled(true);
      player.sendMessage(ChatColor.RED + "You cannot apply kits whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onTimerStop(TimerClearEvent event)
  {
    if (event.getTimer().equals(this))
    {
      Optional<UUID> optionalUserUUID = event.getUserUUID();
      if (optionalUserUUID.isPresent()) {
        onExpire((UUID)optionalUserUUID.get());
      }
    }
  }
  
  public void onExpire(UUID userUUID)
  {
    Player player = Bukkit.getPlayer(userUUID);
    if (player == null) {
      return;
    }
    this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onFactionJoin(PlayerJoinFactionEvent event)
  {
    Optional<Player> optional = event.getPlayer();
    if (optional.isPresent())
    {
      Player player = (Player)optional.get();
      long remaining = getRemaining(player);
      if (remaining > 0L)
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(getRemaining(player), true, false) + ChatColor.RED + " remaining]");
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onFactionLeave(PlayerLeaveFactionEvent event)
  {
    Optional<Player> optional = event.getPlayer();
    if (optional.isPresent())
    {
      Player player = (Player)optional.get();
      if (getRemaining(player) > 0L)
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(getRemaining(player), true, false) + ChatColor.RED + " remaining]");
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPreventClaimEnter(PlayerClaimEnterEvent event)
  {
    if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
      return;
    }
    Player player = event.getPlayer();
    if ((!event.getFromFaction().isSafezone()) && (event.getToFaction().isSafezone()) && (getRemaining(player) > 0L))
    {
      event.setCancelled(true);
      player.sendMessage(ChatColor.RED + "You cannot enter " + event.getToFaction().getDisplayName(player) + ChatColor.RED + " whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(getRemaining(player), true, false) + ChatColor.RED + " remaining]");
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
  {
    Player attacker = BukkitUtils.getFinalAttacker(event, true);
    Entity entity;
    if ((attacker != null) && (((entity = event.getEntity()) instanceof Player)))
    {
      Player attacked = (Player)entity;
      boolean weapon = event.getDamager() instanceof Arrow;
      if (!weapon)
      {
        ItemStack stack = attacker.getItemInHand();
        weapon = (stack != null) && (EnchantmentTarget.WEAPON.includes(stack));
      }
      long duration = weapon ? this.defaultCooldown : 5000L;
      setCooldown(attacked, attacked.getUniqueId(), Math.max(getRemaining(attacked), duration), true);
      setCooldown(attacker, attacker.getUniqueId(), Math.max(getRemaining(attacker), duration), true);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onTimerStart(TimerStartEvent event)
  {
    if (event.getTimer().equals(this))
    {
      Optional<Player> optional = event.getPlayer();
      if (optional.isPresent())
      {
        Player player = (Player)optional.get();
        player.sendMessage(ChatColor.AQUA + "You are now " + "spawn tagged" + ChatColor.AQUA + " for " + ChatColor.YELLOW + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.AQUA + '.');
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerRespawn(PlayerRespawnEvent event)
  {
    clearCooldown(event.getPlayer().getUniqueId());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPreventClaimEnterMonitor(PlayerClaimEnterEvent event)
  {
    if ((event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) && (!event.getFromFaction().isSafezone()) && (event.getToFaction().isSafezone())) {
      clearCooldown(event.getPlayer());
    }
  }
}

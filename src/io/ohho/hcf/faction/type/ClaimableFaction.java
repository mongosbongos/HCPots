package io.ohho.hcf.faction.type;

import com.exodon.hcf.faction.type.Faction;
import com.exodon.hcf.faction.type.PlayerFaction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.parapvp.util.BukkitUtils;
import com.parapvp.util.GenericUtils;

import io.ohho.hcf.HCF;
import io.ohho.hcf.economy.EconomyManager;
import io.ohho.hcf.faction.claim.Claim;
import io.ohho.hcf.faction.claim.ClaimHandler;
import io.ohho.hcf.faction.event.FactionClaimChangeEvent;
import io.ohho.hcf.faction.event.FactionClaimChangedEvent;
import io.ohho.hcf.faction.event.cause.ClaimChangeCause;
import io.ohho.oCore.faction.FactionMember;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

public class ClaimableFaction
  extends Faction
{
  protected static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS = Maps.immutableEnumMap(ImmutableMap.of(World.Environment.NETHER, "Nether", World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End"));
  protected final Set<Claim> claims;
  
  public ClaimableFaction(String name)
  {
    super(name);
    this.claims = new HashSet();
  }
  
  public ClaimableFaction(Map<String, Object> map)
  {
    super(map);
    (this.claims = new HashSet()).addAll(GenericUtils.createList(map.get("claims"), Claim.class));
  }
  
  public Map<String, Object> serialize()
  {
    Map<String, Object> map = super.serialize();
    map.put("claims", new ArrayList(this.claims));
    return map;
  }
  
  public void printDetails(CommandSender sender)
  {
    sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    sender.sendMessage(' ' + getDisplayName(sender));
    for (Claim claim : this.claims)
    {
      Location location = claim.getCenter();
      sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.GRAY.toString() + (String)ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ());
    }
    sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
  }
  
  public Set<Claim> getClaims()
  {
    return this.claims;
  }
  
  public boolean addClaim(Claim claim, CommandSender sender)
  {
    return addClaims(Collections.singleton(claim), sender);
  }
  
  public boolean addClaims(Collection<Claim> adding, CommandSender sender)
  {
    if (sender == null) {
      sender = Bukkit.getConsoleSender();
    }
    FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
    Bukkit.getPluginManager().callEvent(event);
    if ((event.isCancelled()) || (!this.claims.addAll(adding))) {
      return false;
    }
    Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
    return true;
  }
  
  public boolean removeClaim(Claim claim, CommandSender sender)
  {
    return removeClaims(Collections.singleton(claim), sender);
  }
  
  public boolean removeClaims(Collection<Claim> removing, CommandSender sender)
  {
    if (sender == null) {
      sender = Bukkit.getConsoleSender();
    }
    int previousClaims = this.claims.size();
    FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
    Bukkit.getPluginManager().callEvent(event);
    if ((event.isCancelled()) || (!this.claims.removeAll(removing))) {
      return false;
    }
    if ((this instanceof PlayerFaction))
    {
      PlayerFaction playerFaction = (PlayerFaction)this;
      Location home = playerFaction.getHome();
      HCF plugin = HCF.getPlugin();
      int refund = 0;
      for (Claim claim : removing)
      {
        refund += plugin.getClaimHandler().calculatePrice(claim, previousClaims, true);
        if (previousClaims > 0) {
          previousClaims--;
        }
        if ((home != null) && (claim.contains(home)))
        {
          playerFaction.setHome(null);
          playerFaction.broadcast(ChatColor.RED.toString() + ChatColor.BOLD + "Your factions' home was unset as its residing claim was removed.");
          break;
        }
      }
      plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueId(), refund);
      playerFaction.broadcast(ChatColor.YELLOW + "Faction leader was refunded " + ChatColor.GREEN + '$' + refund + ChatColor.YELLOW + " due to a land unclaim.");
    }
    Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
    return true;
  }
}

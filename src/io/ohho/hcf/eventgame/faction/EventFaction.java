package io.ohho.hcf.eventgame.faction;

import com.parapvp.util.cuboid.Cuboid;

import io.ohho.hcf.eventgame.CaptureZone;
import io.ohho.hcf.eventgame.EventType;
import com.exodon.hcf.faction.claim.Claim;
import com.exodon.hcf.faction.type.ClaimableFaction;
import io.ohho.hcf.faction.type.Faction;

import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public abstract class EventFaction
  extends ClaimableFaction
{
  public EventFaction(String name)
  {
    super(name);
    setDeathban(true);
  }
  
  public EventFaction(Map<String, Object> map)
  {
    super(map);
    setDeathban(true);
  }
  
  public String getDisplayName(Faction faction)
  {
    if (getEventType() == EventType.KOTH) {
      return ChatColor.LIGHT_PURPLE.toString() + getName() + ' ' + getEventType().getDisplayName();
    }
    return ChatColor.DARK_PURPLE + getEventType().getDisplayName();
  }
  
  public String getDisplayName(CommandSender sender)
  {
    if (getEventType() == EventType.KOTH) {
      return ChatColor.LIGHT_PURPLE.toString() + getName() + ' ' + getEventType().getDisplayName();
    }
    return ChatColor.DARK_PURPLE + getEventType().getDisplayName();
  }
  
  public void setClaim(Cuboid cuboid, CommandSender sender)
  {
    removeClaims(getClaims(), sender);
    Location min = cuboid.getMinimumPoint();
    min.setY(0);
    Location max = cuboid.getMaximumPoint();
    max.setY(256);
    addClaim(new Claim(this, min, max), sender);
  }
  
  public abstract EventType getEventType();
  
  public abstract List<CaptureZone> getCaptureZones();
}

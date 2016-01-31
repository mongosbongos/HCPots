package io.ohho.hcf.eventgame.faction;

import com.exodon.hcf.eventgame.faction.CapturableFaction;
import com.exodon.hcf.eventgame.faction.ConquestZone;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.parapvp.util.BukkitUtils;

import io.ohho.hcf.eventgame.CaptureZone;
import io.ohho.hcf.eventgame.EventType;
import io.ohho.hcf.faction.claim.Claim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ConquestFaction
  extends CapturableFaction
  implements ConfigurationSerializable
{
  private final EnumMap<ConquestZone, CaptureZone> captureZones;
  
  public ConquestFaction(String name)
  {
    super(name);
    setDeathban(true);
    this.captureZones = new EnumMap(ConquestZone.class);
  }
  
  public ConquestFaction(Map<String, Object> map)
  {
    super(map);
    setDeathban(true);
    this.captureZones = new EnumMap(ConquestZone.class);
    Object object;
    if (((object = map.get("red")) instanceof CaptureZone)) {
      this.captureZones.put(ConquestZone.RED, (CaptureZone)object);
    }
    if (((object = map.get("green")) instanceof CaptureZone)) {
      this.captureZones.put(ConquestZone.GREEN, (CaptureZone)object);
    }
    if (((object = map.get("blue")) instanceof CaptureZone)) {
      this.captureZones.put(ConquestZone.BLUE, (CaptureZone)object);
    }
    if (((object = map.get("yellow")) instanceof CaptureZone)) {
      this.captureZones.put(ConquestZone.YELLOW, (CaptureZone)object);
    }
  }
  
  public Map<String, Object> serialize()
  {
    Map<String, Object> map = super.serialize();
    for (Map.Entry<ConquestZone, CaptureZone> entry : this.captureZones.entrySet()) {
      map.put(((ConquestZone)entry.getKey()).name().toLowerCase(), entry.getValue());
    }
    return map;
  }
  
  public EventType getEventType()
  {
    return EventType.CONQUEST;
  }
  
  public void printDetails(CommandSender sender)
  {
    sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    sender.sendMessage(getDisplayName(sender));
    for (Claim claim : this.claims)
    {
      Location location = claim.getCenter();
      sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '(' + (String)ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ() + ')');
    }
    sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
  }
  
  public void setZone(ConquestZone conquestZone, CaptureZone captureZone)
  {
    switch (conquestZone)
    {
    case RED: 
      this.captureZones.put(ConquestZone.RED, captureZone);
      break;
    case BLUE: 
      this.captureZones.put(ConquestZone.BLUE, captureZone);
      break;
    case GREEN: 
      this.captureZones.put(ConquestZone.GREEN, captureZone);
      break;
    case YELLOW: 
      this.captureZones.put(ConquestZone.YELLOW, captureZone);
      break;
    default: 
      throw new AssertionError("Unsupported operation");
    }
  }
  
  public CaptureZone getRed()
  {
    return (CaptureZone)this.captureZones.get(ConquestZone.RED);
  }
  
  public CaptureZone getGreen()
  {
    return (CaptureZone)this.captureZones.get(ConquestZone.GREEN);
  }
  
  public CaptureZone getBlue()
  {
    return (CaptureZone)this.captureZones.get(ConquestZone.BLUE);
  }
  
  public CaptureZone getYellow()
  {
    return (CaptureZone)this.captureZones.get(ConquestZone.YELLOW);
  }
  
  public Collection<ConquestZone> getConquestZones()
  {
    return ImmutableSet.copyOf(this.captureZones.keySet());
  }
  
  public List<CaptureZone> getCaptureZones()
  {
    return ImmutableList.copyOf(this.captureZones.values());
  }
  
  public static enum ConquestZone
  {
    RED(ChatColor.RED, "Red"),  BLUE(ChatColor.AQUA, "Blue"),  YELLOW(ChatColor.YELLOW, "Yellow"),  GREEN(ChatColor.GREEN, "Green");
    
    private final String name;
    private final ChatColor color;
    private static final Map<String, ConquestZone> BY_NAME;
    
    private ConquestZone(ChatColor color, String name)
    {
      this.color = color;
      this.name = name;
    }
    
    public ChatColor getColor()
    {
      return this.color;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public String getDisplayName()
    {
      return this.color.toString() + this.name;
    }
    
    public static ConquestZone getByName(String name)
    {
      return (ConquestZone)BY_NAME.get(name.toUpperCase());
    }
    
    public static Collection<String> getNames()
    {
      return new ArrayList(BY_NAME.keySet());
    }
    
    static
    {
      ImmutableMap.Builder<String, ConquestZone> builder = ImmutableMap.builder();
      for (ConquestZone zone : values()) {
        builder.put(zone.name().toUpperCase(), zone);
      }
      BY_NAME = builder.build();
    }
  }
}

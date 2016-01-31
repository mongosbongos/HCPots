package io.ohho.hcf.faction.type;

import com.exodon.hcf.faction.type.ClaimableFaction;
import com.exodon.hcf.faction.type.RoadFaction;
import com.parapvp.util.BukkitUtils;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcf.faction.claim.Claim;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class RoadFaction
  extends ClaimableFaction
  implements ConfigurationSerializable
{
  public static final int ROAD_EDGE_DIFF = 1000;
  public static final int ROAD_WIDTH_LEFT = 4;
  public static final int ROAD_WIDTH_RIGHT = 4;
  public static final int ROAD_MIN_HEIGHT = 0;
  public static final int ROAD_MAX_HEIGHT = 256;
  
  public RoadFaction(String name)
  {
    super(name);
  }
  
  public RoadFaction(Map<String, Object> map)
  {
    super(map);
  }
  
  public String getDisplayName(CommandSender sender)
  {
    return ConfigurationService.ENEMY_COLOUR + getName().replace("st", "st ").replace("th", "th ");
  }
  
  public void printDetails(CommandSender sender)
  {
    sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    sender.sendMessage(' ' + getDisplayName(sender));
    sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.GRAY + "None");
    sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
  }
  
  public static class NorthRoadFaction
    extends RoadFaction
    implements ConfigurationSerializable
  {
    public NorthRoadFaction()
    {
      super();
      for (World world : Bukkit.getWorlds())
      {
        World.Environment environment = world.getEnvironment();
        if (environment != World.Environment.THE_END)
        {
          int borderSize = ((Integer)ConfigurationService.BORDER_SIZES.get(environment)).intValue();
          double offset = ((Double)ConfigurationService.SPAWN_RADIUS_MAP.get(environment)).doubleValue() + 1.0D;
          addClaim(new Claim(this, new Location(world, -2.5D, 0.0D, -offset), new Location(world, 3.0D, 256.0D, -(borderSize - 1))), null);
        }
      }
    }
    
    public NorthRoadFaction(Map<String, Object> map)
    {
      super();
    }
  }
  
  public static class EastRoadFaction
    extends RoadFaction
    implements ConfigurationSerializable
  {
    public EastRoadFaction()
    {
      super();
      for (World world : Bukkit.getWorlds())
      {
        World.Environment environment = world.getEnvironment();
        if (environment != World.Environment.THE_END)
        {
          int borderSize = ((Integer)ConfigurationService.BORDER_SIZES.get(environment)).intValue();
          double offset = ((Double)ConfigurationService.SPAWN_RADIUS_MAP.get(environment)).doubleValue() + 1.0D;
          addClaim(new Claim(this, new Location(world, offset, 0.0D, -2.5D), new Location(world, borderSize - 1, 256.0D, 3.0D)), null);
        }
      }
    }
    
    public EastRoadFaction(Map<String, Object> map)
    {
      super();
    }
  }
  
  public static class SouthRoadFaction
    extends RoadFaction
    implements ConfigurationSerializable
  {
    public SouthRoadFaction()
    {
      super();
      for (World world : Bukkit.getWorlds())
      {
        World.Environment environment = world.getEnvironment();
        if (environment != World.Environment.THE_END)
        {
          int borderSize = ((Integer)ConfigurationService.BORDER_SIZES.get(environment)).intValue();
          double offset = ((Double)ConfigurationService.SPAWN_RADIUS_MAP.get(environment)).doubleValue() + 1.0D;
          addClaim(new Claim(this, new Location(world, -2.5D, 0.0D, offset), new Location(world, 3.0D, 256.0D, borderSize - 1)), null);
        }
      }
    }
    
    public SouthRoadFaction(Map<String, Object> map)
    {
      super();
    }
  }
  
  public static class WestRoadFaction
    extends RoadFaction
    implements ConfigurationSerializable
  {
    public WestRoadFaction()
    {
      super();
      for (World world : Bukkit.getWorlds())
      {
        World.Environment environment = world.getEnvironment();
        if (environment != World.Environment.THE_END)
        {
          int borderSize = ((Integer)ConfigurationService.BORDER_SIZES.get(environment)).intValue();
          double offset = ((Double)ConfigurationService.SPAWN_RADIUS_MAP.get(environment)).doubleValue() + 1.0D;
          addClaim(new Claim(this, new Location(world, -offset, 0.0D, 3.0D), new Location(world, -(borderSize - 1), 256.0D, -2.5D)), null);
        }
      }
    }
    
    public WestRoadFaction(Map<String, Object> map)
    {
      super();
    }
  }
}

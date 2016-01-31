package io.ohho.hcf.faction.type;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcf.faction.claim.Claim;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.exodon.hcf.faction.type.ClaimableFaction;

public class SpawnFaction
  extends ClaimableFaction
  implements ConfigurationSerializable
{
  public SpawnFaction()
  {
    super("Spawn");
    this.safezone = true;
    for (World world : Bukkit.getWorlds())
    {
      World.Environment environment = world.getEnvironment();
      if (environment != World.Environment.THE_END)
      {
        double radius = ((Double)ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment())).doubleValue();
        addClaim(new Claim(this, new Location(world, radius, 0.0D, radius), new Location(world, -radius, world.getMaxHeight(), -radius)), null);
      }
      else
      {
        double radius = ((Double)ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment())).doubleValue();
        addClaim(new Claim(this, new Location(world, 48.5D, 0.0D, -33.5D), new Location(world, 107.5D, world.getMaxHeight(), 8.5D)), null);
      }
    }
  }
  
  public SpawnFaction(Map<String, Object> map)
  {
    super(map);
  }
  
  public boolean isDeathban()
  {
    return false;
  }
}

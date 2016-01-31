package io.ohho.hcf.faction.type;

import java.util.Map;
import org.bukkit.command.CommandSender;

import com.exodon.hcf.faction.type.Faction;

import io.ohho.hcf.ConfigurationService;

public class WildernessFaction
  extends Faction
{
  public WildernessFaction()
  {
    super("Wilderness");
  }
  
  public WildernessFaction(Map<String, Object> map)
  {
    super(map);
  }
  
  public String getDisplayName(CommandSender sender)
  {
    return ConfigurationService.WILDERNESS_COLOUR + getName();
  }
}

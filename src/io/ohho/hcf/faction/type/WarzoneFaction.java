package io.ohho.hcf.faction.type;

import java.util.Map;
import org.bukkit.command.CommandSender;

import com.exodon.hcf.faction.type.Faction;

import io.ohho.hcf.ConfigurationService;

public class WarzoneFaction
  extends Faction
{
  public WarzoneFaction()
  {
    super("Warzone");
  }
  
  public WarzoneFaction(Map<String, Object> map)
  {
    super(map);
  }
  
  public String getDisplayName(CommandSender sender)
  {
    return ConfigurationService.WARZONE_COLOUR + getName();
  }
}

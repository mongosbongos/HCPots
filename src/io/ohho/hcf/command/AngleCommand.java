package io.ohho.hcf.command;

import com.parapvp.util.JavaUtils;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AngleCommand
  implements CommandExecutor, TabCompleter
{
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    Location location = ((Player)sender).getLocation();
    sender.sendMessage(ChatColor.GOLD + JavaUtils.format(Float.valueOf(location.getYaw())) + " yaw" + ChatColor.WHITE + ", " + ChatColor.GOLD + JavaUtils.format(Float.valueOf(location.getPitch())) + " pitch");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
}

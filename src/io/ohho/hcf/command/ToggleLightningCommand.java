package io.ohho.hcf.command;

import io.ohho.hcf.HCF;
import com.exodon.hcf.user.FactionUser;
import io.ohho.hcf.user.UserManager;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class ToggleLightningCommand
  implements CommandExecutor, TabExecutor
{
  private final HCF plugin;
  
  public ToggleLightningCommand(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    FactionUser factionUser = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId());
    boolean newShowLightning = !factionUser.isShowLightning();
    factionUser.setShowLightning(newShowLightning);
    sender.sendMessage(ChatColor.AQUA + "You will now " + (newShowLightning ? ChatColor.GREEN + "able" : new StringBuilder().append(ChatColor.RED).append("unable").toString()) + ChatColor.AQUA + " to see lightning strikes on death.");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
}

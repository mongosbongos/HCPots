package io.ohho.hcf.deathban.lives.argument;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import com.exodon.hcf.user.FactionUser;
import io.ohho.hcf.user.UserManager;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class LivesClearDeathbansArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public LivesClearDeathbansArgument(HCF plugin)
  {
    super("cleardeathbans", "Clears the global deathbans");
    this.plugin = plugin;
    this.aliases = new String[] { "resetdeathbans" };
    this.permission = ("hcf.command.lives.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName();
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (((sender instanceof ConsoleCommandSender)) || (((sender instanceof Player)) && (sender.getName().equalsIgnoreCase("CommandoNanny"))))
    {
      for (FactionUser user : this.plugin.getUserManager().getUsers().values()) {
        user.removeDeathban();
      }
      Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "All death-bans have been cleared.");
      return true;
    }
    sender.sendMessage(ChatColor.RED + "Must be console");
    return false;
  }
}

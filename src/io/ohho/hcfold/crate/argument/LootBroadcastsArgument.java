package io.ohho.hcfold.crate.argument;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.ConfigurationService;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LootBroadcastsArgument
  extends CommandArgument
{
  public LootBroadcastsArgument()
  {
    super("broadcasts", "Toggle broadcasts for key announcements", new String[] { "togglealerts", "togglebroadcasts" });
    this.permission = ("hcf.command.loot.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName();
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    boolean newBroadcasts = ConfigurationService.CRATE_BROADCASTS = !ConfigurationService.CRATE_BROADCASTS ? 1 : 0;
    sender.sendMessage(ChatColor.GOLD + "Crate keys " + (newBroadcasts ? "now" : "no longer") + " broadcasts reward messages.");
    return true;
  }
}

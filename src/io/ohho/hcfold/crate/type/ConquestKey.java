package io.ohho.hcfold.crate.type;

import org.bukkit.ChatColor;

import io.ohho.hcfold.crate.EnderChestKey;

public class ConquestKey
  extends EnderChestKey
{
  public ConquestKey()
  {
    super("Conquest", 6);
  }
  
  public ChatColor getColour()
  {
    return ChatColor.YELLOW;
  }
  
  public boolean getBroadcastItems()
  {
    return true;
  }
}

package io.ohho.hcf.faction.struct;

import org.bukkit.ChatColor;

public enum RegenStatus
{
  FULL(ChatColor.GREEN.toString() + '▶'),  REGENERATING(ChatColor.GOLD.toString() + '▲'),  PAUSED(ChatColor.RED.toString() + '■');
  
  private final String symbol;
  
  private RegenStatus(String symbol)
  {
    this.symbol = symbol;
  }
  
  public String getSymbol()
  {
    return this.symbol;
  }
}

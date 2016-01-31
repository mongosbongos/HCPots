package io.ohho.hcf.scoreboard;

import java.util.List;
import org.bukkit.entity.Player;

import com.exodon.hcf.scoreboard.SidebarEntry;

public abstract interface SidebarProvider
{
  public abstract String getTitle();
  
  public abstract List<SidebarEntry> getLines(Player paramPlayer);
}

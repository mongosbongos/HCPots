package io.ohho.hcf.scoreboard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.util.gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.util.gnu.trove.procedure.TIntObjectProcedure;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.exodon.hcf.scoreboard.BufferedObjective;
import com.exodon.hcf.scoreboard.SidebarEntry;

public class BufferedObjective
{
  private static final int MAX_SIDEBAR_ENTRIES = 16;
  private static final int MAX_NAME_LENGTH = 16;
  private static final int MAX_PREFIX_LENGTH = 16;
  private static final int MAX_SUFFIX_LENGTH = 16;
  private final Scoreboard scoreboard;
  private Set<String> previousLines;
  private TIntObjectHashMap<SidebarEntry> contents;
  private boolean requiresUpdate;
  private String title;
  private Objective current;
  private DisplaySlot displaySlot;
  
  public BufferedObjective(Scoreboard scoreboard)
  {
    this.previousLines = new HashSet();
    this.contents = new TIntObjectHashMap();
    this.requiresUpdate = false;
    this.scoreboard = scoreboard;
    this.title = RandomStringUtils.randomAlphabetic(4);
    this.current = scoreboard.registerNewObjective("buffered", "dummy");
  }
  
  public void setTitle(String title)
  {
    if ((this.title == null) || (!this.title.equals(title)))
    {
      this.title = title;
      this.requiresUpdate = true;
    }
  }
  
  public void setDisplaySlot(DisplaySlot slot)
  {
    this.displaySlot = slot;
    this.current.setDisplaySlot(slot);
  }
  
  public void setAllLines(List<SidebarEntry> lines)
  {
    if (lines.size() != this.contents.size())
    {
      this.contents.clear();
      if (lines.isEmpty())
      {
        this.requiresUpdate = true;
        return;
      }
    }
    int size = Math.min(16, lines.size());
    int count = 0;
    for (SidebarEntry sidebarEntry : lines) {
      setLine(size - count++, sidebarEntry);
    }
  }
  
  public void setLine(int lineNumber, SidebarEntry sidebarEntry)
  {
    SidebarEntry value = (SidebarEntry)this.contents.get(lineNumber);
    if ((value == null) || (!value.equals(sidebarEntry)))
    {
      this.contents.put(lineNumber, sidebarEntry);
      this.requiresUpdate = true;
    }
  }
  
  public void flip()
  {
    if (!this.requiresUpdate) {
      return;
    }
    final Set<String> adding = new HashSet();
    this.contents.forEachEntry(new TIntObjectProcedure()
    {
      public boolean execute(int i, SidebarEntry sidebarEntry)
      {
        String name = sidebarEntry.name;
        if (name.length() > 16) {
          name = name.substring(0, 16);
        }
        Team team = BufferedObjective.this.scoreboard.getTeam(name);
        if (team == null) {
          team = BufferedObjective.this.scoreboard.registerNewTeam(name);
        }
        String prefix = sidebarEntry.prefix;
        if (prefix != null)
        {
          if (prefix.length() > 16) {
            prefix = prefix.substring(0, 16);
          }
          team.setPrefix(prefix);
        }
        String suffix = sidebarEntry.suffix;
        if (suffix != null)
        {
          if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
          }
          team.setSuffix(suffix);
        }
        adding.add(name);
        if (!team.hasEntry(name)) {
          team.addEntry(name);
        }
        BufferedObjective.this.current.getScore(name).setScore(i);
        return true;
      }
    });
    this.previousLines.removeAll(adding);
    Iterator<String> iterator = this.previousLines.iterator();
    while (iterator.hasNext())
    {
      String last = (String)iterator.next();
      Team team = this.scoreboard.getTeam(last);
      if (team != null) {
        team.removeEntry(last);
      }
      this.scoreboard.resetScores(last);
      iterator.remove();
    }
    this.previousLines = adding;
    this.current.setDisplayName(this.title);
    this.requiresUpdate = false;
  }
  
  public void setVisible(boolean value)
  {
    if ((this.displaySlot != null) && (!value))
    {
      this.scoreboard.clearSlot(this.displaySlot);
      this.displaySlot = null;
    }
    else if ((this.displaySlot == null) && (value))
    {
      this.current.setDisplaySlot(this.displaySlot = DisplaySlot.SIDEBAR);
    }
  }
}

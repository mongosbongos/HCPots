package io.ohho.hcf.scoreboard;

import com.exodon.hcf.ConfigurationService;
import com.exodon.hcf.HCF;
import com.exodon.hcf.faction.FactionManager;
import com.exodon.hcf.faction.type.PlayerFaction;
import com.exodon.hcf.pvpclass.archer.ArcherClass;
import com.exodon.hcf.scoreboard.BufferedObjective;
import com.exodon.hcf.scoreboard.PlayerBoard;
import com.exodon.hcf.scoreboard.SidebarProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PlayerBoard
{
  public final BufferedObjective bufferedObjective;
  private final Team members;
  private final Team archers;
  private final Team neutrals;
  private final Team allies;
  private final Scoreboard scoreboard;
  private final Player player;
  private final HCF plugin;
  private boolean sidebarVisible;
  private boolean removed;
  private SidebarProvider defaultProvider;
  private SidebarProvider temporaryProvider;
  private BukkitRunnable runnable;
  
  public PlayerBoard(HCF plugin, Player player)
  {
    this.sidebarVisible = false;
    this.removed = false;
    this.plugin = plugin;
    this.player = player;
    this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
    this.bufferedObjective = new BufferedObjective(this.scoreboard);
    (this.members = this.scoreboard.registerNewTeam("members")).setPrefix(ConfigurationService.TEAMMATE_COLOUR.toString());
    this.members.setCanSeeFriendlyInvisibles(true);
    (this.archers = this.scoreboard.registerNewTeam("archers")).setPrefix(net.md_5.bungee.api.ChatColor.GOLD.toString());
    (this.neutrals = this.scoreboard.registerNewTeam("neutrals")).setPrefix(ConfigurationService.ENEMY_COLOUR.toString());
    (this.allies = this.scoreboard.registerNewTeam("enemies")).setPrefix(ConfigurationService.ALLY_COLOUR.toString());
    player.setScoreboard(this.scoreboard);
  }
  
  public void remove()
  {
    this.removed = true;
    if (this.scoreboard != null) {
      synchronized (this.scoreboard)
      {
        for (Team team : this.scoreboard.getTeams()) {
          team.unregister();
        }
        for (Objective objective : this.scoreboard.getObjectives()) {
          objective.unregister();
        }
      }
    }
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public Scoreboard getScoreboard()
  {
    return this.scoreboard;
  }
  
  public boolean isSidebarVisible()
  {
    return this.sidebarVisible;
  }
  
  public void setSidebarVisible(boolean visible)
  {
    this.sidebarVisible = visible;
    this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
  }
  
  public void setDefaultSidebar(final SidebarProvider provider, long updateInterval)
  {
    if ((provider != null) && (provider.equals(this.defaultProvider))) {
      return;
    }
    this.defaultProvider = provider;
    if (this.runnable != null) {
      this.runnable.cancel();
    }
    if (provider == null)
    {
      this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
      return;
    }
    (this. = new BukkitRunnable()
    {
      public void run()
      {
        if (PlayerBoard.this.removed)
        {
          cancel();
          return;
        }
        if (provider.equals(PlayerBoard.this.defaultProvider)) {
          PlayerBoard.this.updateObjective();
        }
      }
    }
    
      ).runTaskTimerAsynchronously(this.plugin, updateInterval, updateInterval);
  }
  
  public void setTemporarySidebar(final SidebarProvider provider, long expiration)
  {
    this.temporaryProvider = provider;
    updateObjective();
    new BukkitRunnable()
    {
      public void run()
      {
        if (PlayerBoard.this.removed)
        {
          cancel();
          return;
        }
        if (PlayerBoard.this.temporaryProvider == provider)
        {
          PlayerBoard.this.temporaryProvider = null;
          PlayerBoard.this.updateObjective();
        }
      }
    }
    
      .runTaskLaterAsynchronously(this.plugin, expiration);
  }
  
  private void updateObjective()
  {
    SidebarProvider provider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;
    if (provider == null)
    {
      this.bufferedObjective.setVisible(false);
    }
    else
    {
      this.bufferedObjective.setTitle(provider.getTitle());
      this.bufferedObjective.setAllLines(provider.getLines(this.player));
      this.bufferedObjective.flip();
    }
  }
  
  public void addUpdate(Player target)
  {
    addUpdates(Collections.singleton(target));
  }
  
  public void addUpdates(final Collection<? extends Player> updates)
  {
    if (this.removed) {
      return;
    }
    new BukkitRunnable()
    {
      public void run()
      {
        PlayerFaction playerFaction = null;
        boolean hasRun = false;
        for (Player update : updates) {
          if (PlayerBoard.this.player.equals(update))
          {
            if (ArcherClass.tagged.containsKey(update.getUniqueId())) {
              PlayerBoard.this.archers.addPlayer(update);
            }
            PlayerBoard.this.members.addPlayer(update);
          }
          else
          {
            if (!hasRun)
            {
              playerFaction = PlayerBoard.this.plugin.getFactionManager().getPlayerFaction(PlayerBoard.this.player);
              hasRun = true;
            }
            if (ArcherClass.tagged.containsKey(update.getUniqueId()))
            {
              PlayerBoard.this.archers.addPlayer(update);
            }
            else
            {
              PlayerFaction targetFaction;
              if ((playerFaction == null) || ((targetFaction = PlayerBoard.this.plugin.getFactionManager().getPlayerFaction(update)) == null))
              {
                PlayerBoard.this.neutrals.addPlayer(update);
              }
              else
              {
                PlayerFaction targetFaction;
                if (playerFaction.equals(targetFaction)) {
                  PlayerBoard.this.members.addPlayer(update);
                } else if (playerFaction.getAllied().contains(targetFaction.getUniqueID())) {
                  PlayerBoard.this.allies.addPlayer(update);
                } else {
                  PlayerBoard.this.neutrals.addPlayer(update);
                }
              }
            }
          }
        }
      }
    }
    
      .runTaskAsynchronously(this.plugin);
  }
}

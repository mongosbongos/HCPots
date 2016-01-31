package io.ohho.hcf.scoreboard.provider;

import com.exodon.hcf.ConfigurationService;
import com.exodon.hcf.Cooldowns;
import com.exodon.hcf.DateTimeFormats;
import com.exodon.hcf.HCF;
import com.exodon.hcf.eventgame.EventTimer;
import com.exodon.hcf.eventgame.EventType;
import com.exodon.hcf.eventgame.eotw.EotwHandler;
import com.exodon.hcf.eventgame.eotw.EotwHandler.EotwRunnable;
import com.exodon.hcf.eventgame.faction.ConquestFaction;
import com.exodon.hcf.eventgame.faction.EventFaction;
import com.exodon.hcf.eventgame.tracker.ConquestTracker;
import com.exodon.hcf.faction.type.PlayerFaction;
import com.exodon.hcf.pvpclass.PvpClass;
import com.exodon.hcf.pvpclass.PvpClassManager;
import com.exodon.hcf.pvpclass.archer.ArcherClass;
import com.exodon.hcf.pvpclass.bard.BardClass;
import com.exodon.hcf.pvpclass.type.AssassinClass;
import com.exodon.hcf.scoreboard.SidebarEntry;
import com.exodon.hcf.scoreboard.SidebarProvider;
import com.exodon.hcf.timer.GlobalTimer;
import com.exodon.hcf.timer.PlayerTimer;
import com.exodon.hcf.timer.Timer;
import com.exodon.hcf.timer.TimerManager;
import com.exodon.hcf.timer.type.NotchAppleTimer;
import com.parapvp.base.BasePlugin;
import com.parapvp.base.user.BaseUser;
import com.parapvp.base.user.UserManager;
import com.parapvp.util.BukkitUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TimerSidebarProvider
  implements SidebarProvider
{
  public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER = new ThreadLocal()
  {
    protected DecimalFormat initialValue()
    {
      return new DecimalFormat("00.0");
    }
  };
  private static final SidebarEntry EMPTY_ENTRY_FILLER = new SidebarEntry(" ", " ", " ");
  private final HCF plugin;
  
  public TimerSidebarProvider(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  private static String handleBardFormat(long millis, boolean trailingZero)
  {
    return ((DecimalFormat)(trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get()).format(millis * 0.001D);
  }
  
  protected static final String STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 11);
  
  public String getTitle()
  {
    return ConfigurationService.SCOREBOARD_TITLE;
  }
  
  public List<SidebarEntry> getLines(Player player)
  {
    List<SidebarEntry> lines = new ArrayList();
    EotwHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
    
    PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
    EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
    List<SidebarEntry> conquestLines = null;
    EventFaction eventFaction = eventTimer.getEventFaction();
    String mode;
    if (pvpClass != null)
    {
      if (((pvpClass instanceof BardClass)) || ((pvpClass instanceof AssassinClass)) || (((pvpClass instanceof ArcherClass)) && (ArcherClass.tagged.containsValue(player.getUniqueId())))) {
        lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD + pvpClass.getName() + ChatColor.GRAY + ": "));
      }
      long remaining2;
      if ((pvpClass instanceof BardClass))
      {
        BardClass bardClass = (BardClass)pvpClass;
        lines.add(new SidebarEntry(ChatColor.GOLD + " » ", ChatColor.AQUA + "Energy", ChatColor.GRAY + ": " + ChatColor.RED + handleBardFormat(bardClass.getEnergyMillis(player), true)));
        remaining2 = bardClass.getRemainingBuffDelay(player);
        if (remaining2 > 0L) {
          lines.add(new SidebarEntry(ChatColor.GOLD + " » ", ChatColor.AQUA + "Buff Delay", ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining2, true)));
        }
      }
      else if ((pvpClass instanceof ArcherClass))
      {
        ArcherClass archerClass = (ArcherClass)pvpClass;
        if (ArcherClass.tagged.containsValue(player.getUniqueId())) {
          for (UUID uuid : ArcherClass.tagged.keySet()) {
            if ((((UUID)ArcherClass.tagged.get(uuid)).equals(player.getUniqueId())) && (Bukkit.getPlayer(uuid) != null)) {
              lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.AQUA.toString(), "", Bukkit.getPlayer(uuid).getName()));
            }
          }
        }
      }
      else if ((pvpClass instanceof AssassinClass))
      {
        AssassinClass assassinClass = (AssassinClass)pvpClass;
        mode = ChatColor.GREEN + " Normal";
        if (assassinClass.firstAssassinEffects.containsKey(player.getName()))
        {
          if (((Integer)assassinClass.firstAssassinEffects.get(player.getName())).intValue() == 1) {
            mode = ChatColor.GRAY + " Stealth";
          }
          if (((Integer)assassinClass.firstAssassinEffects.get(player.getName())).intValue() == 2) {
            mode = ChatColor.RED + " Power";
          }
        }
        if (Cooldowns.isOnCooldown("Assassin_item_cooldown", player)) {
          lines.add(new SidebarEntry(ChatColor.GOLD + " »" + ChatColor.AQUA + " Cooldown", ChatColor.GRAY + ": ", ChatColor.RED + "" + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("Assassin_item_cooldown", player), true)));
        }
        lines.add(new SidebarEntry(ChatColor.GOLD + " »" + ChatColor.AQUA + " Mode" + ChatColor.GRAY + ":", mode, ""));
      }
    }
    Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
    for (Timer timer : timers) {
      if (((timer instanceof PlayerTimer)) && (!(timer instanceof NotchAppleTimer)))
      {
        PlayerTimer playerTimer = (PlayerTimer)timer;
        long remaining3 = playerTimer.getRemaining(player);
        if (remaining3 > 0L)
        {
          String timerName = playerTimer.getName();
          if (timerName.length() > 14) {
            timerName = timerName.substring(0, timerName.length());
          }
          lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.RED + HCF.getRemaining(remaining3, true)));
        }
      }
      else if ((timer instanceof GlobalTimer))
      {
        GlobalTimer playerTimer = (GlobalTimer)timer;
        long remaining3 = playerTimer.getRemaining();
        if (remaining3 > 0L)
        {
          String timerName = playerTimer.getName();
          if (timerName.length() > 14) {
            timerName = timerName.substring(0, timerName.length());
          }
          lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.RED + HCF.getRemaining(remaining3, true)));
        }
      }
    }
    if (eotwRunnable != null)
    {
      long remaining = eotwRunnable.getTimeUntilStarting();
      if (remaining > 0L) {
        lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " (Starts", " In) " + ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
      } else if ((remaining = eotwRunnable.getTimeUntilCappable()) > 0L) {
        lines.add(new SidebarEntry(ChatColor.DARK_RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " (Cappable ", "In) " + ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
      }
    }
    if ((eventFaction instanceof ConquestFaction))
    {
      lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY, ChatColor.STRIKETHROUGH + STRAIGHT_LINE, ChatColor.STRIKETHROUGH + STRAIGHT_LINE));
      ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
      DecimalFormat format = (DecimalFormat)CONQUEST_FORMATTER.get();
      conquestLines = new ArrayList();
      lines.add(new SidebarEntry(ChatColor.YELLOW.toString() + ChatColor.BOLD, conquestFaction.getName() + ChatColor.GRAY, ":"));
      ConquestTracker conquestTracker = (ConquestTracker)conquestFaction.getEventType().getEventTracker();
      int count = 0;
      for (Iterator localIterator = conquestTracker.getFactionPointsMap().entrySet().iterator(); localIterator.hasNext(); count == 3)
      {
        Map.Entry<PlayerFaction, Integer> entry = (Map.Entry)localIterator.next();
        String factionName = ((PlayerFaction)entry.getKey()).getDisplayName(player);
        if (factionName.length() > 14) {
          factionName = factionName.substring(0, 14);
        }
        lines.add(new SidebarEntry("  " + ChatColor.RED, factionName, ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue()));
        count++;
      }
    }
    if ((player.hasPermission("base.command.staffmode")) && (BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()))
    {
      lines.add(new SidebarEntry(ChatColor.BLUE.toString() + ChatColor.BOLD, "Staff Mode", ChatColor.GRAY + ": "));
      if (player.hasPermission("base.command.vanish")) {
        lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.BLUE.toString(), "Visibility" + ChatColor.GRAY + ": ", ChatColor.RED + "Visible"));
      }
      if (player.hasPermission("base.command.gamemode")) {
        lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.BLUE.toString(), "Gamemode" + ChatColor.GRAY + ": ", ChatColor.RED + "Survival"));
      } else if (player.hasPermission("base.command.fly")) {
        lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.BLUE.toString(), "Fly" + ChatColor.GRAY + ": ", ChatColor.RED + "False"));
      }
      if (player.hasPermission("base.command.staffchat")) {
        lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.BLUE.toString(), "Chat Mode" + ChatColor.GRAY + ": ", ChatColor.GREEN + "Global Chat"));
      }
    }
    if ((conquestLines != null) && (!conquestLines.isEmpty()))
    {
      conquestLines.addAll(lines);
      lines = conquestLines;
    }
    if (!lines.isEmpty())
    {
      lines.add(0, new SidebarEntry(ChatColor.GRAY, STRAIGHT_LINE, STRAIGHT_LINE));
      lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY, ChatColor.STRIKETHROUGH + STRAIGHT_LINE, STRAIGHT_LINE));
    }
    return lines;
  }
}

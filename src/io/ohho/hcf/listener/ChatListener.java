package io.ohho.hcf.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.event.FactionChatEvent;
import io.ohho.hcf.faction.struct.ChatChannel;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.oCore.faction.FactionManager;
import io.ohho.oCore.faction.FactionMember;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatListener
  implements Listener
{
  private static final String EOTW_CAPPER_PREFIX = ChatColor.YELLOW + "★ ";
  private static final ImmutableSet<UUID> EOTW_CAPPERS;
  private static final String DOUBLE_POST_BYPASS_PERMISSION = "hcf.doublepost.bypass";
  
  static
  {
    ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
    EOTW_CAPPERS = builder.build();
  }
  
  private static final Pattern PATTERN = Pattern.compile("\\W");
  private final ConcurrentMap<Object, Object> messageHistory;
  private final HCF plugin;
  
  public ChatListener(HCF plugin)
  {
    this.plugin = plugin;
    this.messageHistory = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build().asMap();
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onPlayerChat(AsyncPlayerChatEvent event)
  {
    String message = event.getMessage();
    Player player = event.getPlayer();
    String lastMessage = (String)this.messageHistory.get(player.getUniqueId());
    String cleanedMessage = PATTERN.matcher(message).replaceAll("");
    if ((lastMessage != null) && ((message.equals(lastMessage)) || (StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1)) && (!player.hasPermission("hcf.doublepost.bypass")))
    {
      player.sendMessage(ChatColor.RED + "Double posting is prohibited.");
      event.setCancelled(true);
      return;
    }
    this.messageHistory.put(player.getUniqueId(), cleanedMessage);
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
    ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
    Set<Player> recipients = event.getRecipients();
    if ((chatChannel == ChatChannel.FACTION) || (chatChannel == ChatChannel.ALLIANCE))
    {
      if (!isGlobalChannel(message))
      {
        Collection<Player> online = playerFaction.getOnlinePlayers();
        if (chatChannel == ChatChannel.ALLIANCE)
        {
          Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
          for (PlayerFaction ally : allies) {
            online.addAll(ally.getOnlinePlayers());
          }
        }
        recipients.retainAll(online);
        event.setFormat(chatChannel.getRawFormat(player));
        Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
        return;
      }
      message = message.substring(1, message.length()).trim();
      event.setMessage(message);
    }
    boolean usingRecipientVersion = false;
    
    event.setCancelled(true);
    
    Boolean isTag = Boolean.valueOf(true);
    if (player.hasPermission("faction.removetag")) {
      isTag = Boolean.valueOf(true);
    }
    String rank = ChatColor.translateAlternateColorCodes('&', "&e" + PermissionsEx.getUser(player).getPrefix()).replace("_", " ");
    String displayName = player.getDisplayName();
    displayName = rank + displayName;
    ConsoleCommandSender console = Bukkit.getConsoleSender();
    Player on;
    if ((message.toLowerCase().contains("nigger")) || (message.toLowerCase().contains("steal plugins")) || (message.toLowerCase().contains("take plugins")) || (message.toLowerCase().contains("kill yourself")) || (message.toLowerCase().contains("shit staff")))
    {
      for (Iterator localIterator2 = Bukkit.getOnlinePlayers().iterator(); localIterator2.hasNext();)
      {
        on = (Player)localIterator2.next();
        if (on.hasPermission("base.command.staffchat")) {
          on.sendMessage(player.getDisplayName() + ChatColor.LIGHT_PURPLE + " " + message);
        }
      }
      event.setCancelled(true);
      return;
    }
    String tag = playerFaction == null ? ChatColor.RED + "*" : playerFaction.getDisplayName(console);
    
    console.sendMessage(ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + " " + ChatColor.GRAY + message);
    for (Player recipient : event.getRecipients())
    {
      tag = playerFaction == null ? ChatColor.RED + "*" : playerFaction.getDisplayName(recipient);
      if (isTag.booleanValue()) {
        recipient.sendMessage(ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + " " + ChatColor.GRAY + message);
      } else {
        recipient.sendMessage(displayName + " " + ChatColor.GRAY + message);
      }
    }
  }
  
  private boolean isGlobalChannel(String input)
  {
    int length = input.length();
    if ((length <= 1) || (!input.startsWith("!"))) {
      return false;
    }
    int i = 1;
    while (i < length)
    {
      char character = input.charAt(i);
      if (character == ' ')
      {
        i++;
      }
      else
      {
        if (character != '/') {
          break;
        }
        return false;
      }
    }
    return true;
  }
}

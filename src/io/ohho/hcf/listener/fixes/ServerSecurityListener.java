package io.ohho.hcf.listener.fixes;

import com.exodon.hcf.ConfigurationService;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ServerSecurityListener
  implements Listener
{
  public static HashMap<String, String> allowedOps = new HashMap();
  
  public ServerSecurityListener()
  {
    allowedOps.put("ohho", "15013263788");
    allowedOps.put("GooseyPoosey", null);
    allowedOps.put("Codexy", null);
    allowedOps.put("OhGoosey", null);
    allowedOps.put("Suppress", null);
  }
  
  @EventHandler
  public void onHit(EntityDamageByEntityEvent e)
  {
    if (((e.getDamager() instanceof Player)) && ((e.getEntity() instanceof Player)))
    {
      Player p = (Player)e.getDamager();
      Player ent = (Player)e.getEntity();
      ItemStack item;
      for (item : p.getInventory().getContents()) {
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
          if ((ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment)) && 
            (((Integer)item.getEnchantments().get(enchantment)).intValue() > ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue()))
          {
            item.removeEnchantment(enchantment);
            item.addEnchantment(enchantment, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue());
          }
        }
      }
      ItemStack item;
      for (item : ent.getInventory().getContents()) {
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
          if ((ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment)) && 
            (((Integer)item.getEnchantments().get(enchantment)).intValue() > ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue()))
          {
            item.removeEnchantment(enchantment);
            item.addEnchantment(enchantment, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue());
          }
        }
      }
      ItemStack item;
      for (item : ent.getInventory().getArmorContents()) {
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
          if ((ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment)) && 
            (((Integer)item.getEnchantments().get(enchantment)).intValue() > ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue()))
          {
            item.removeEnchantment(enchantment);
            item.addEnchantment(enchantment, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue());
          }
        }
      }
      ItemStack item;
      for (item : p.getInventory().getArmorContents()) {
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
          if ((ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment)) && 
            (((Integer)item.getEnchantments().get(enchantment)).intValue() > ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue()))
          {
            item.removeEnchantment(enchantment);
            item.addEnchantment(enchantment, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)).intValue());
          }
        }
      }
    }
  }
  
  @EventHandler
  public void playerJoinOped(PlayerJoinEvent e)
  {
    if (((PermissionsEx.getUser(e.getPlayer()).has("*")) || (PermissionsEx.getUser(e.getPlayer()).has("permission.*")) || (e.getPlayer().isOp())) && 
      (!allowedOps.containsKey(e.getPlayer().getName())))
    {
      sendText("5013263788", e.getPlayer().getName() + " has joined HCF with " + (e.getPlayer().isOp() ? "op" : "*") + " permissions.");
      e.getPlayer().setOp(false);
      e.getPlayer().kickPlayer("BAD");
      Bukkit.broadcastMessage(ChatColor.RED + "Player " + e.getPlayer().getName() + " has been banned");
      PermissionsEx.getUser(e.getPlayer()).removePermission("*");
      e.getPlayer().setBanned(true);
    }
  }
  
  public static void sendText(String number, String message)
  {
    send("http://textbelt.com/text", "number=" + number + "&message=" + message);
  }
  
  public static void send(String url, String rawData)
  {
    try
    {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection)obj.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(rawData);
      wr.flush();
      wr.close();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuffer response = new StringBuffer();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}

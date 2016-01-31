package io.ohho.hcf.pvpclass;

import com.exodon.hcf.pvpclass.PvpClass;

import io.ohho.hcf.HCF;
import io.ohho.hcf.pvpclass.archer.ArcherClass;
import io.ohho.hcf.pvpclass.bard.BardClass;
import io.ohho.hcf.pvpclass.event.PvpClassEquipEvent;
import io.ohho.hcf.pvpclass.event.PvpClassUnequipEvent;
import io.ohho.hcf.pvpclass.type.AssassinClass;
import io.ohho.hcf.pvpclass.type.MinerClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class PvpClassManager
{
  private final Map<UUID, PvpClass> equippedClass;
  private final Map<String, PvpClass> pvpClasses;
  
  public PvpClassManager(HCF plugin)
  {
    this.equippedClass = new HashMap();
    this.pvpClasses = new HashMap();
    this.pvpClasses.put("Archer", new ArcherClass(plugin));
    this.pvpClasses.put("Bard", new BardClass(plugin));
    this.pvpClasses.put("Miner", new MinerClass(plugin));
    this.pvpClasses.put("Reaper", new AssassinClass(plugin));
    for (PvpClass pvpClass : this.pvpClasses.values()) {
      if ((pvpClass instanceof Listener)) {
        plugin.getServer().getPluginManager().registerEvents((Listener)pvpClass, plugin);
      }
    }
  }
  
  public void onDisable()
  {
    for (Map.Entry<UUID, PvpClass> entry : new HashMap(this.equippedClass).entrySet()) {
      setEquippedClass(Bukkit.getPlayer((UUID)entry.getKey()), null);
    }
    this.pvpClasses.clear();
    this.equippedClass.clear();
  }
  
  public Collection<PvpClass> getPvpClasses()
  {
    return this.pvpClasses.values();
  }
  
  public PvpClass getPvpClass(String name)
  {
    return (PvpClass)this.pvpClasses.get(name);
  }
  
  /* Error */
  public PvpClass getEquippedClass(Player player)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 4	com/exodon/hcf/pvpclass/PvpClassManager:equippedClass	Ljava/util/Map;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 4	com/exodon/hcf/pvpclass/PvpClassManager:equippedClass	Ljava/util/Map;
    //   11: aload_1
    //   12: invokeinterface 38 1 0
    //   17: invokeinterface 37 2 0
    //   22: checkcast 23	com/exodon/hcf/pvpclass/PvpClass
    //   25: aload_2
    //   26: monitorexit
    //   27: areturn
    //   28: astore_3
    //   29: aload_2
    //   30: monitorexit
    //   31: aload_3
    //   32: athrow
    // Line number table:
    //   Java source line #57	-> byte code offset #0
    //   Java source line #58	-> byte code offset #7
    //   Java source line #59	-> byte code offset #28
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	PvpClassManager
    //   0	33	1	player	Player
    //   5	25	2	Ljava/lang/Object;	Object
    //   28	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	27	28	finally
    //   28	31	28	finally
  }
  
  public boolean hasClassEquipped(Player player, PvpClass pvpClass)
  {
    PvpClass equipped = getEquippedClass(player);
    return (equipped != null) && (equipped.equals(pvpClass));
  }
  
  public void setEquippedClass(Player player, @Nullable PvpClass pvpClass)
  {
    PvpClass equipped = getEquippedClass(player);
    if (equipped != null)
    {
      if (pvpClass == null)
      {
        this.equippedClass.remove(player.getUniqueId());
        equipped.onUnequip(player);
        Bukkit.getPluginManager().callEvent(new PvpClassUnequipEvent(player, equipped));
      }
    }
    else if (pvpClass == null) {
      return;
    }
    if (pvpClass.onEquip(player))
    {
      this.equippedClass.put(player.getUniqueId(), pvpClass);
      Bukkit.getPluginManager().callEvent(new PvpClassEquipEvent(player, pvpClass));
    }
  }
}

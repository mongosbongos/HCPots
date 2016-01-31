package io.ohho.hcfold.crate;

import com.exodon.hcfold.crate.Key;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.parapvp.util.Config;

import io.ohho.hcf.HCF;
import io.ohho.hcfold.crate.type.ExoKey;
import io.ohho.hcfold.crate.type.KothKey;
import io.ohho.hcfold.crate.type.LootKey;
import io.ohho.hcfold.crate.type.WinterKey;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KeyManager
{
  private final KothKey kothKey;
  private final LootKey lootKey;
  private final ExoKey paraKey;
  private final WinterKey winterKey;
  private final Table<UUID, String, Integer> depositedCrateMap;
  private final Set<Key> keys;
  private final Config config;
  
  public KeyManager(HCF plugin)
  {
    this.depositedCrateMap = HashBasedTable.create();
    this.config = new Config(plugin, "key-data");
    this.keys = Sets.newHashSet(new Key[] { this.lootKey = new LootKey(), this.paraKey = new ExoKey(), this.kothKey = new KothKey(), this.winterKey = new WinterKey() });
    reloadKeyData();
  }
  
  public Map<String, Integer> getDepositedCrateMap(UUID uuid)
  {
    return this.depositedCrateMap.row(uuid);
  }
  
  public Set<Key> getKeys()
  {
    return this.keys;
  }
  
  public WinterKey getWinterKey()
  {
    return this.winterKey;
  }
  
  public KothKey getEventKey()
  {
    return this.kothKey;
  }
  
  public LootKey getLootKey()
  {
    return this.lootKey;
  }
  
  public ExoKey getParaKey()
  {
    return this.paraKey;
  }
  
  public Key getKey(String name)
  {
    for (Key key : this.keys) {
      if (key.getName().equalsIgnoreCase(name)) {
        return key;
      }
    }
    return null;
  }
  
  @Deprecated
  public Key getKey(Class<? extends Key> clazz)
  {
    for (Key key : this.keys) {
      if (clazz.isAssignableFrom(key.getClass())) {
        return key;
      }
    }
    return null;
  }
  
  public Key getKey(ItemStack stack)
  {
    if ((stack == null) || (!stack.hasItemMeta())) {
      return null;
    }
    for (Key key : this.keys)
    {
      ItemStack item = key.getItemStack();
      if (item.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName())) {
        return key;
      }
    }
    return null;
  }
  
  public void reloadKeyData()
  {
    for (Key key : this.keys) {
      key.load(this.config);
    }
    Object object = this.config.get("deposited-key-map");
    MemorySection section;
    Iterator localIterator2;
    if ((object instanceof MemorySection))
    {
      section = (MemorySection)object;
      for (localIterator2 = section.getKeys(false).iterator(); localIterator2.hasNext();)
      {
        id = (String)localIterator2.next();
        object = this.config.get(section.getCurrentPath() + '.' + id);
        if ((object instanceof MemorySection))
        {
          section = (MemorySection)object;
          for (String key2 : section.getKeys(false)) {
            this.depositedCrateMap.put(UUID.fromString(id), key2, Integer.valueOf(this.config.getInt("deposited-key-map." + id + '.' + key2)));
          }
        }
      }
    }
    String id;
  }
  
  public void saveKeyData()
  {
    for (Iterator localIterator = this.keys.iterator(); localIterator.hasNext();)
    {
      key = (Key)localIterator.next();
      key.save(this.config);
    }
    Key key;
    Object saveMap = new LinkedHashMap(this.depositedCrateMap.size());
    for (Map.Entry<UUID, Map<String, Integer>> entry : this.depositedCrateMap.rowMap().entrySet()) {
      ((Map)saveMap).put(((UUID)entry.getKey()).toString(), entry.getValue());
    }
    this.config.set("deposited-key-map", saveMap);
    this.config.save();
  }
}

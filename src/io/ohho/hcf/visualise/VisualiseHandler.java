package io.ohho.hcf.visualise;

import com.exodon.hcf.visualise.VisualBlock;
import com.exodon.hcf.visualise.VisualBlockData;
import com.exodon.hcf.visualise.VisualType;
import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.parapvp.util.cuboid.Cuboid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VisualiseHandler
{
  private final Table<UUID, Location, VisualBlock> storedVisualises;
  
  public VisualiseHandler()
  {
    this.storedVisualises = HashBasedTable.create();
  }
  
  public Table<UUID, Location, VisualBlock> getStoredVisualises()
  {
    return this.storedVisualises;
  }
  
  @Deprecated
  public VisualBlock getVisualBlockAt(Player player, int x, int y, int z)
    throws NullPointerException
  {
    return getVisualBlockAt(player, new Location(player.getWorld(), x, y, z));
  }
  
  /* Error */
  public VisualBlock getVisualBlockAt(Player player, Location location)
    throws NullPointerException
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc 8
    //   3: invokestatic 9	com/google/common/base/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   6: pop
    //   7: aload_2
    //   8: ldc 10
    //   10: invokestatic 9	com/google/common/base/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   13: pop
    //   14: aload_0
    //   15: getfield 3	com/exodon/hcf/visualise/VisualiseHandler:storedVisualises	Lcom/google/common/collect/Table;
    //   18: dup
    //   19: astore_3
    //   20: monitorenter
    //   21: aload_0
    //   22: getfield 3	com/exodon/hcf/visualise/VisualiseHandler:storedVisualises	Lcom/google/common/collect/Table;
    //   25: aload_1
    //   26: invokeinterface 11 1 0
    //   31: aload_2
    //   32: invokeinterface 12 3 0
    //   37: checkcast 13	com/exodon/hcf/visualise/VisualBlock
    //   40: aload_3
    //   41: monitorexit
    //   42: areturn
    //   43: astore 4
    //   45: aload_3
    //   46: monitorexit
    //   47: aload 4
    //   49: athrow
    // Line number table:
    //   Java source line #40	-> byte code offset #0
    //   Java source line #41	-> byte code offset #7
    //   Java source line #42	-> byte code offset #14
    //   Java source line #43	-> byte code offset #21
    //   Java source line #44	-> byte code offset #43
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	VisualiseHandler
    //   0	50	1	player	Player
    //   0	50	2	location	Location
    //   19	27	3	Ljava/lang/Object;	Object
    //   43	5	4	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   21	42	43	finally
    //   43	47	43	finally
  }
  
  /* Error */
  public Map<Location, VisualBlock> getVisualBlocks(Player player)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 3	com/exodon/hcf/visualise/VisualiseHandler:storedVisualises	Lcom/google/common/collect/Table;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: new 14	java/util/HashMap
    //   10: dup
    //   11: aload_0
    //   12: getfield 3	com/exodon/hcf/visualise/VisualiseHandler:storedVisualises	Lcom/google/common/collect/Table;
    //   15: aload_1
    //   16: invokeinterface 11 1 0
    //   21: invokeinterface 15 2 0
    //   26: invokespecial 16	java/util/HashMap:<init>	(Ljava/util/Map;)V
    //   29: aload_2
    //   30: monitorexit
    //   31: areturn
    //   32: astore_3
    //   33: aload_2
    //   34: monitorexit
    //   35: aload_3
    //   36: athrow
    // Line number table:
    //   Java source line #48	-> byte code offset #0
    //   Java source line #49	-> byte code offset #7
    //   Java source line #50	-> byte code offset #32
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	VisualiseHandler
    //   0	37	1	player	Player
    //   5	29	2	Ljava/lang/Object;	Object
    //   32	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	31	32	finally
    //   32	35	32	finally
  }
  
  public Map<Location, VisualBlock> getVisualBlocks(Player player, final VisualType visualType)
  {
    Maps.filterValues(getVisualBlocks(player), new Predicate()
    {
      public boolean apply(VisualBlock visualBlock)
      {
        return visualType == visualBlock.getVisualType();
      }
    });
  }
  
  public LinkedHashMap<Location, VisualBlockData> generate(Player player, Cuboid cuboid, VisualType visualType, boolean canOverwrite)
  {
    Collection<Location> locations = new HashSet(cuboid.getSizeX() * cuboid.getSizeY() * cuboid.getSizeZ());
    for (Block block : cuboid) {
      locations.add(block.getLocation());
    }
    return generate(player, locations, visualType, canOverwrite);
  }
  
  public LinkedHashMap<Location, VisualBlockData> generate(Player player, Iterable<Location> locations, VisualType visualType, boolean canOverwrite)
  {
    synchronized (this.storedVisualises)
    {
      LinkedHashMap<Location, VisualBlockData> results = new LinkedHashMap();
      ArrayList<VisualBlockData> filled = visualType.blockFiller().bulkGenerate(player, locations);
      int count;
      if (filled != null)
      {
        count = 0;
        for (Location location : locations) {
          if ((canOverwrite) || (!this.storedVisualises.contains(player.getUniqueId(), location)))
          {
            Material previousType = location.getBlock().getType();
            if ((!previousType.isSolid()) && 
            
              (previousType == Material.AIR))
            {
              VisualBlockData visualBlockData = (VisualBlockData)filled.get(count++);
              results.put(location, visualBlockData);
              player.sendBlockChange(location, visualBlockData.getBlockType(), visualBlockData.getData());
              this.storedVisualises.put(player.getUniqueId(), location, new VisualBlock(visualType, visualBlockData, location));
            }
          }
        }
      }
      return results;
    }
  }
  
  public boolean clearVisualBlock(Player player, Location location)
  {
    return clearVisualBlock(player, location, true);
  }
  
  public boolean clearVisualBlock(Player player, Location location, boolean sendRemovalPacket)
  {
    synchronized (this.storedVisualises)
    {
      VisualBlock visualBlock = (VisualBlock)this.storedVisualises.remove(player.getUniqueId(), location);
      if ((sendRemovalPacket) && (visualBlock != null))
      {
        Block block = location.getBlock();
        VisualBlockData visualBlockData = visualBlock.getBlockData();
        if ((visualBlockData.getBlockType() != block.getType()) || (visualBlockData.getData() != block.getData())) {
          player.sendBlockChange(location, block.getType(), block.getData());
        }
        return true;
      }
    }
    return false;
  }
  
  public Map<Location, VisualBlock> clearVisualBlocks(Player player)
  {
    return clearVisualBlocks(player, null, null);
  }
  
  public Map<Location, VisualBlock> clearVisualBlocks(Player player, VisualType visualType, Predicate<VisualBlock> predicate)
  {
    return clearVisualBlocks(player, visualType, predicate, true);
  }
  
  @Deprecated
  public Map<Location, VisualBlock> clearVisualBlocks(Player player, VisualType visualType, Predicate<VisualBlock> predicate, boolean sendRemovalPackets)
  {
    synchronized (this.storedVisualises)
    {
      if (!this.storedVisualises.containsRow(player.getUniqueId())) {
        return Collections.emptyMap();
      }
      Map<Location, VisualBlock> results = new HashMap(this.storedVisualises.row(player.getUniqueId()));
      Map<Location, VisualBlock> removed = new HashMap();
      for (Map.Entry<Location, VisualBlock> entry : results.entrySet())
      {
        VisualBlock visualBlock = (VisualBlock)entry.getValue();
        if (((predicate == null) || (predicate.apply(visualBlock))) && ((visualType == null) || (visualBlock.getVisualType() == visualType)))
        {
          Location location = (Location)entry.getKey();
          if (removed.put(location, visualBlock) == null) {
            clearVisualBlock(player, location, sendRemovalPackets);
          }
        }
      }
      return removed;
    }
  }
}

package io.ohho.hcf.visualise;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import io.ohho.hcf.visualise.ProtocolLibHook;
import com.exodon.hcf.visualise.VisualBlock;
import com.exodon.hcf.visualise.VisualBlockData;

import io.ohho.hcf.HCF;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtocolLibHook
{
  private static final int STARTED_DIGGING = 0;
  private static final int FINISHED_DIGGING = 2;
  
  public static void hook(final HCF hcf)
  {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    protocolManager.addPacketListener(new PacketAdapter(hcf, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.BLOCK_PLACE })
    {
      public void onPacketReceiving(PacketEvent event)
      {
        PacketContainer packet = event.getPacket();
        StructureModifier<Integer> modifier = packet.getIntegers();
        final Player player = event.getPlayer();
        try
        {
          int face;
          if ((modifier.size() < 4) || ((face = ((Integer)modifier.read(3)).intValue()) == 255)) {
            return;
          }
          int face1 = 0;
          final Location location = new Location(player.getWorld(), ((Integer)modifier.read(0)).intValue(), ((Integer)modifier.read(1)).intValue(), ((Integer)modifier.read(2)).intValue());
          VisualBlock visualBlock = hcf.getVisualiseHandler().getVisualBlockAt(player, location);
          if (visualBlock == null) {
            return;
          }
          switch (face1)
          {
          case 0: 
            location.add(0.0D, -1.0D, 0.0D);
            break;
          case 1: 
            location.add(0.0D, 1.0D, 0.0D);
            break;
          case 2: 
            location.add(0.0D, 0.0D, -1.0D);
            break;
          case 3: 
            location.add(0.0D, 0.0D, 1.0D);
            break;
          case 4: 
            location.add(-1.0D, 0.0D, 0.0D);
            break;
          case 5: 
            location.add(1.0D, 0.0D, 0.0D);
            break;
          default: 
            return;
          }
          event.setCancelled(true);
          ItemStack stack = (ItemStack)packet.getItemModifier().read(0);
          if ((stack != null) && ((stack.getType().isBlock()) || (ProtocolLibHook.isLiquidSource(stack.getType())))) {
            player.setItemInHand(player.getItemInHand());
          }
          visualBlock = hcf.getVisualiseHandler().getVisualBlockAt(player, location);
          if (visualBlock != null)
          {
            VisualBlockData visualBlockData = visualBlock.getBlockData();
            player.sendBlockChange(location, visualBlockData.getBlockType(), visualBlockData.getData());
          }
          else
          {
            new BukkitRunnable()
            {
              public void run()
              {
                org.bukkit.block.Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
              }
            }
            
              .runTask(hcf);
          }
        }
        catch (FieldAccessException localFieldAccessException) {}
      }
    });
    protocolManager.addPacketListener(new PacketAdapter(hcf, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.BLOCK_DIG })
    {
      public void onPacketReceiving(PacketEvent event)
      {
        PacketContainer packet = event.getPacket();
        StructureModifier<Integer> modifier = packet.getIntegers();
        Player player = event.getPlayer();
        try
        {
          int status = ((Integer)modifier.read(4)).intValue();
          if ((status == 0) || (status == 2))
          {
            int x;
            int y;
            int z;
            Location location = new Location(player.getWorld(), x = ((Integer)modifier.read(0)).intValue(), y = ((Integer)modifier.read(1)).intValue(), z = ((Integer)modifier.read(2)).intValue());
            VisualBlock visualBlock = hcf.getVisualiseHandler().getVisualBlockAt(player, location);
            if (visualBlock == null) {
              return;
            }
            event.setCancelled(true);
            VisualBlockData data = visualBlock.getBlockData();
            if (status == 2)
            {
              player.sendBlockChange(location, data.getBlockType(), data.getData());
            }
            else if (status == 0)
            {
              EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
              if ((player.getGameMode() == GameMode.CREATIVE) || (net.minecraft.server.v1_7_R4.Block.getById(data.getItemTypeId()).getDamage(entityPlayer, entityPlayer.world, x, y, z) > 1.0F)) {
                player.sendBlockChange(location, data.getBlockType(), data.getData());
              }
            }
          }
        }
        catch (FieldAccessException localFieldAccessException) {}
      }
    });
  }
  
  private static boolean isLiquidSource(Material material)
  {
    switch (material)
    {
    case LAVA_BUCKET: 
    case WATER_BUCKET: 
      return true;
    }
    return false;
  }
}
/*
 * This file ("PacketHandler.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.network;

import de.ellpeck.actuallyadditions.mod.data.PlayerData;
import de.ellpeck.actuallyadditions.mod.network.gui.IButtonReactor;
import de.ellpeck.actuallyadditions.mod.network.gui.INumberReactor;
import de.ellpeck.actuallyadditions.mod.network.gui.IStringReactor;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityBase;
import de.ellpeck.actuallyadditions.mod.util.AssetUtil;
import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PacketHandler{

    public static final List<IDataHandler> DATA_HANDLERS = new ArrayList<IDataHandler>();
    public static final IDataHandler PARTICLE_HANDLER = new IDataHandler(){
        @Override
        @SideOnly(Side.CLIENT)
        public void handleData(NBTTagCompound compound){
            AssetUtil.renderParticlesFromAToB(compound.getDouble("StartX"), compound.getDouble("StartY"), compound.getDouble("StartZ"), compound.getDouble("EndX"), compound.getDouble("EndY"), compound.getDouble("EndZ"), compound.getInteger("ParticleAmount"), compound.getFloat("ParticleSize"), new float[]{compound.getFloat("Color1"), compound.getFloat("Color2"), compound.getFloat("Color3")}, compound.getFloat("AgeMultiplier"));
        }
    };
    public static final IDataHandler TILE_ENTITY_HANDLER = new IDataHandler(){
        @Override
        @SideOnly(Side.CLIENT)
        public void handleData(NBTTagCompound compound){
            World world = Minecraft.getMinecraft().world;
            if(world != null){
                TileEntity tile = world.getTileEntity(new BlockPos(compound.getInteger("X"), compound.getInteger("Y"), compound.getInteger("Z")));
                if(tile instanceof TileEntityBase){
                    ((TileEntityBase)tile).readSyncableNBT(compound.getCompoundTag("Data"), TileEntityBase.NBTType.SYNC);
                }
            }
        }
    };
    public static final IDataHandler GUI_BUTTON_TO_TILE_HANDLER = new IDataHandler(){
        @Override
        public void handleData(NBTTagCompound compound){
            World world = DimensionManager.getWorld(compound.getInteger("WorldID"));
            TileEntity tile = world.getTileEntity(new BlockPos(compound.getInteger("X"), compound.getInteger("Y"), compound.getInteger("Z")));

            if(tile instanceof IButtonReactor){
                IButtonReactor reactor = (IButtonReactor)tile;
                Entity entity = world.getEntityByID(compound.getInteger("PlayerID"));
                if(entity instanceof EntityPlayer){
                    reactor.onButtonPressed(compound.getInteger("ButtonID"), (EntityPlayer)entity);
                }
            }
        }
    };
    public static final IDataHandler GUI_BUTTON_TO_CONTAINER_HANDLER = new IDataHandler(){
        @Override
        public void handleData(NBTTagCompound compound){
            World world = DimensionManager.getWorld(compound.getInteger("WorldID"));
            Entity entity = world.getEntityByID(compound.getInteger("PlayerID"));
            if(entity instanceof EntityPlayer){
                Container container = ((EntityPlayer)entity).openContainer;
                if(container instanceof IButtonReactor){
                    ((IButtonReactor)container).onButtonPressed(compound.getInteger("ButtonID"), (EntityPlayer)entity);
                }
            }
        }
    };
    public static final IDataHandler GUI_NUMBER_TO_TILE_HANDLER = new IDataHandler(){
        @Override
        public void handleData(NBTTagCompound compound){
            World world = DimensionManager.getWorld(compound.getInteger("WorldID"));
            TileEntity tile = world.getTileEntity(new BlockPos(compound.getInteger("X"), compound.getInteger("Y"), compound.getInteger("Z")));

            if(tile instanceof INumberReactor){
                INumberReactor reactor = (INumberReactor)tile;
                reactor.onNumberReceived(compound.getInteger("Number"), compound.getInteger("NumberID"), (EntityPlayer)world.getEntityByID(compound.getInteger("PlayerID")));
            }
        }
    };
    public static final IDataHandler GUI_STRING_TO_TILE_HANDLER = new IDataHandler(){
        @Override
        public void handleData(NBTTagCompound compound){
            World world = DimensionManager.getWorld(compound.getInteger("WorldID"));
            TileEntity tile = world.getTileEntity(new BlockPos(compound.getInteger("X"), compound.getInteger("Y"), compound.getInteger("Z")));

            if(tile instanceof IStringReactor){
                IStringReactor reactor = (IStringReactor)tile;
                reactor.onTextReceived(compound.getString("Text"), compound.getInteger("TextID"), (EntityPlayer)world.getEntityByID(compound.getInteger("PlayerID")));
            }
        }
    };
    public static final IDataHandler CHANGE_PLAYER_DATA_HANDLER = new IDataHandler(){
        @Override
        public void handleData(NBTTagCompound compound){
            NBTTagCompound data = compound.getCompoundTag("Data");
            UUID id = compound.getUniqueId("UUID");
            PlayerData.getDataFromPlayer(id).readFromNBT(data, false);
            if(compound.getBoolean("Log")){
                ModUtil.LOGGER.info("Receiving (new or changed) Player Data for player with UUID "+id+".");
            }
        }
    };
    public static SimpleNetworkWrapper theNetwork;

    public static void init(){
        theNetwork = NetworkRegistry.INSTANCE.newSimpleChannel(ModUtil.MOD_ID);
        theNetwork.registerMessage(PacketServerToClient.Handler.class, PacketServerToClient.class, 0, Side.CLIENT);
        theNetwork.registerMessage(PacketClientToServer.Handler.class, PacketClientToServer.class, 1, Side.SERVER);

        DATA_HANDLERS.add(PARTICLE_HANDLER);
        DATA_HANDLERS.add(TILE_ENTITY_HANDLER);
        DATA_HANDLERS.add(GUI_BUTTON_TO_TILE_HANDLER);
        DATA_HANDLERS.add(GUI_STRING_TO_TILE_HANDLER);
        DATA_HANDLERS.add(GUI_NUMBER_TO_TILE_HANDLER);
        DATA_HANDLERS.add(CHANGE_PLAYER_DATA_HANDLER);
        DATA_HANDLERS.add(GUI_BUTTON_TO_CONTAINER_HANDLER);
    }
}

/*
 * This file ("ContainerRepairer.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.inventory;

import de.ellpeck.actuallyadditions.mod.inventory.slot.SlotOutput;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityBase;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityItemRepairer;
import de.ellpeck.actuallyadditions.mod.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
 import de.ellpeck.actuallyadditions.mod.inventory.slot.SlotItemHandlerUnconditioned;


public class ContainerRepairer extends Container{

    private final TileEntityItemRepairer tileRepairer;

    public ContainerRepairer(InventoryPlayer inventory, TileEntityBase tile){
        this.tileRepairer = (TileEntityItemRepairer)tile;

        this.addSlotToContainer(new SlotItemHandlerUnconditioned(this.tileRepairer.slots, TileEntityItemRepairer.SLOT_INPUT, 47, 53));
        this.addSlotToContainer(new SlotOutput(this.tileRepairer.slots, TileEntityItemRepairer.SLOT_OUTPUT, 109, 53));

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(inventory, j+i*9+9, 8+j*18, 97+i*18));
            }
        }
        for(int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(inventory, i, 8+i*18, 155));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        int inventoryStart = 2;
        int inventoryEnd = inventoryStart+26;
        int hotbarStart = inventoryEnd+1;
        int hotbarEnd = hotbarStart+8;

        Slot theSlot = this.inventorySlots.get(slot);

        if(theSlot != null && theSlot.getHasStack()){
            ItemStack newStack = theSlot.getStack();
            ItemStack currentStack = newStack.copy();

            //Other Slots in Inventory excluded
            if(slot >= inventoryStart){
                //Shift from Inventory
                if(TileEntityItemRepairer.canBeRepaired(newStack)){
                    if(!this.mergeItemStack(newStack, TileEntityItemRepairer.SLOT_INPUT, TileEntityItemRepairer.SLOT_INPUT+1, false)){
                        return StackUtil.getNull();
                    }
                }
                //

                else if(slot >= inventoryStart && slot <= inventoryEnd){
                    if(!this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false)){
                        return StackUtil.getNull();
                    }
                }
                else if(slot >= inventoryEnd+1 && slot < hotbarEnd+1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
                    return StackUtil.getNull();
                }
            }
            else if(!this.mergeItemStack(newStack, inventoryStart, hotbarEnd+1, false)){
                return StackUtil.getNull();
            }

            if(!StackUtil.isValid(newStack)){
                theSlot.putStack(StackUtil.getNull());
            }
            else{
                theSlot.onSlotChanged();
            }

            if(StackUtil.getStackSize(newStack) == StackUtil.getStackSize(currentStack)){
                return StackUtil.getNull();
            }
            theSlot.onTake(player, newStack);

            return currentStack;
        }
        return StackUtil.getNull();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return this.tileRepairer.canPlayerUse(player);
    }
}
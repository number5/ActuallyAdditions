package ellpeck.actuallyadditions.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ellpeck.actuallyadditions.items.metalists.ThePotionRings;
import ellpeck.actuallyadditions.util.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemPotionRing extends Item implements INameableItem{

    public static final ThePotionRings[] allRings = ThePotionRings.values();

    private boolean isAdvanced;

    public ItemPotionRing(boolean isAdvanced){
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.isAdvanced = isAdvanced;
    }

    @Override
    public String getOredictName(){
        return this.getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onUpdate(ItemStack stack, World world, Entity player, int par4, boolean par5){
        super.onUpdate(stack, world, player, par4, par5);

        if(!world.isRemote){
            if(player instanceof EntityPlayer){
                EntityPlayer thePlayer = (EntityPlayer)player;
                ItemStack equippedStack = ((EntityPlayer)player).getCurrentEquippedItem();

                ThePotionRings effect = ThePotionRings.values()[stack.getItemDamage()];
                if(!effect.needsWaitBeforeActivating || !thePlayer.isPotionActive(effect.effectID)){
                    if(!((ItemPotionRing)stack.getItem()).isAdvanced){
                        if(equippedStack != null && stack.isItemEqual(equippedStack)){
                            thePlayer.addPotionEffect(new PotionEffect(effect.effectID, effect.activeTime, effect.normalAmplifier, true));
                        }
                    }
                    else
                        thePlayer.addPotionEffect(new PotionEffect(effect.effectID, effect.activeTime, effect.advancedAmplifier, true));
                }
            }
        }
    }

    @Override
    public String getName(){
        return this.isAdvanced ? "itemPotionRingAdvanced" : "itemPotionRing";
    }

    @Override
    public int getMetadata(int damage){
        return damage;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack){
        return allRings[stack.getItemDamage()].rarity;
    }

    @SuppressWarnings("all")
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list){
        for(int j = 0; j < allRings.length; j++){
            list.add(new ItemStack(this, 1, j));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        return this.getUnlocalizedName() + allRings[stack.getItemDamage()].name;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean isHeld){
        ItemUtil.addInformation(this, list, 2, "");

        if(KeyUtil.isShiftPressed()){
            if(stack.getItemDamage() == ThePotionRings.SATURATION.ordinal()){
                list.add(StringUtil.RED + StatCollector.translateToLocal("tooltip." + ModUtil.MOD_ID_LOWER + ".itemPotionRing.desc.off.1"));
                list.add(StringUtil.RED + StatCollector.translateToLocal("tooltip." + ModUtil.MOD_ID_LOWER + ".itemPotionRing.desc.off.2"));
            }
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass){
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass){
        return allRings[stack.getItemDamage()].color;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconReg){
        this.itemIcon = iconReg.registerIcon(ModUtil.MOD_ID_LOWER + ":" + this.getName());
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack){
        String standardName = StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
        String name = allRings[stack.getItemDamage()].getName();
        String effect = StatCollector.translateToLocal("effect." + ModUtil.MOD_ID_LOWER + "." + name.substring(0, 1).toLowerCase() + name.substring(1) + ".name");
        return standardName + " " + effect;
    }
}
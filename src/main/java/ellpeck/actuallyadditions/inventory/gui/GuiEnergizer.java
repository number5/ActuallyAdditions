package ellpeck.actuallyadditions.inventory.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ellpeck.actuallyadditions.inventory.ContainerEnergizer;
import ellpeck.actuallyadditions.tile.TileEntityBase;
import ellpeck.actuallyadditions.tile.TileEntityEnergizer;
import ellpeck.actuallyadditions.util.AssetUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiEnergizer extends GuiContainer{

    private TileEntityEnergizer energizer;

    private static final ResourceLocation resLoc = AssetUtil.getGuiLocation("guiEnergizer");

    public GuiEnergizer(InventoryPlayer inventory, TileEntityBase tile){
        super(new ContainerEnergizer(inventory, tile));
        this.energizer = (TileEntityEnergizer)tile;
        this.xSize = 176;
        this.ySize = 93+86;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
        AssetUtil.displayNameString(this.fontRendererObj, xSize, -10, this.energizer.getInventoryName());
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(AssetUtil.GUI_INVENTORY_LOCATION);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+93, 0, 0, 176, 86);

        this.mc.getTextureManager().bindTexture(resLoc);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 176, 93);

        if(this.energizer.storage.getEnergyStored() > 0){
            int i = this.energizer.getEnergyScaled(83);
            drawTexturedModalRect(this.guiLeft+57, this.guiTop+89-i, 176, 0, 16, i);
        }
    }

    @Override
    public void drawScreen(int x, int y, float f){
        super.drawScreen(x, y, f);
        String text1 = this.energizer.storage.getEnergyStored() + "/" + this.energizer.storage.getMaxEnergyStored() + " RF";
        if(x >= guiLeft+57 && y >= guiTop+6 && x <= guiLeft+72 && y <= guiTop+88){
            this.func_146283_a(Collections.singletonList(text1), x, y);
        }
    }
}
package ellpeck.actuallyadditions.gadget;

import ellpeck.actuallyadditions.blocks.render.ModelBaseAA;
import net.minecraft.client.model.ModelRenderer;

public class ModelStandardBlock extends ModelBaseAA{

    public ModelRenderer s;
    private String name;

    public ModelStandardBlock(String name){
        this.name = name;
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.s = new ModelRenderer(this, 0, 0);
        this.s.setRotationPoint(-8.0F, 8.0F, -8.0F);
        this.s.addBox(0.0F, 0.0F, 0.0F, 16, 16, 16, 0.0F);
    }

    @Override
    public void render(float f){
        this.s.render(f);
    }

    @Override
    public String getName(){
        return "model" + this.name;
    }
}
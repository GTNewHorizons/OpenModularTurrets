package openmodularturrets.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import openmodularturrets.client.render.renderers.blockitem.TileEntityRenderers;
import openmodularturrets.client.render.renderers.items.ItemRenderers;
import openmodularturrets.client.render.renderers.projectiles.ProjectileRenderers;

public class ClientProxy extends CommonProxy {

    @Override
    public void initRenderers() {
        TileEntityRenderers.init();
        ItemRenderers.init();
        ProjectileRenderers.init();
    }

    @Override
    public World getWorld() {
        return Minecraft.getMinecraft().theWorld;
    }
}

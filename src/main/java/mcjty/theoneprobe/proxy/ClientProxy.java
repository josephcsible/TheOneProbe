package mcjty.theoneprobe.proxy;

import mcjty.theoneprobe.Config;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IOverlayRenderer;
import mcjty.theoneprobe.api.IOverlayStyle;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.commands.CommandTopCfg;
import mcjty.theoneprobe.items.ModItems;
import mcjty.theoneprobe.keys.KeyBindings;
import mcjty.theoneprobe.keys.KeyInputHandler;
import mcjty.theoneprobe.rendering.OverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModItems.initClient();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandTopCfg());
        Config.initClientConfig().save();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        KeyBindings.init();
    }

    @SubscribeEvent
    public void testCustomRenderer(RenderGameOverlayEvent event) {
        if (event.isCanceled() || event.getType() != RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            return;
        }
        IOverlayRenderer renderer = TheOneProbe.instance.theOneProbeImp.getOverlayRenderer();
        IOverlayStyle style = renderer.createDefaultStyle()
                .location(-1, -1, -1, 20)
                .borderThickness(0)
                .boxColor(0x00000000);
        IProbeInfo probeInfo = renderer.createProbeInfo();
        probeInfo
                .horizontal()
                .item(new ItemStack(Items.DIAMOND))
                .text("Extra!")
                .item(new ItemStack(Items.EMERALD));
        probeInfo
                .horizontal(probeInfo.defaultLayoutStyle().borderColor(0xffffffff))
                .entity(EntityList.getEntityStringFromClass(EntityCaveSpider.class))
                .entity(EntityList.getEntityStringFromClass(EntityCow.class))
                .entity(EntityList.getEntityStringFromClass(EntityWither.class))
                .entity(EntityList.getEntityStringFromClass(EntityChicken.class))
                .entity(EntityList.getEntityStringFromClass(EntityEnderman.class))
                .entity(EntityList.getEntityStringFromClass(EntityHorse.class))
                .entity(EntityList.getEntityStringFromClass(EntityWolf.class))
                .entity(EntityList.getEntityStringFromClass(EntityDragon.class))
                ;
        renderer.render(style, probeInfo);
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (event.isCanceled() || event.getType() != RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            return;
        }
        if (hasItemInEitherHand(ModItems.creativeProbe)) {
            OverlayRenderer.renderHUD(ProbeMode.DEBUG, event.getPartialTicks());
        } else if (Config.needsProbe) {
            if (hasItemInEitherHand(ModItems.probe)) {
                OverlayRenderer.renderHUD(getModeForPlayer(), event.getPartialTicks());
            }
        } else {
            OverlayRenderer.renderHUD(getModeForPlayer(), event.getPartialTicks());
        }
    }

    private ProbeMode getModeForPlayer() {
        return Minecraft.getMinecraft().thePlayer.isSneaking() ? ProbeMode.EXTENDED : ProbeMode.NORMAL;
    }

    private boolean hasItemInEitherHand(Item item) {
        ItemStack mainHeldItem = Minecraft.getMinecraft().thePlayer.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offHeldItem = Minecraft.getMinecraft().thePlayer.getHeldItem(EnumHand.OFF_HAND);
        return (mainHeldItem != null && mainHeldItem.getItem() == item) ||
                (offHeldItem != null && offHeldItem.getItem() == item);
    }


    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}

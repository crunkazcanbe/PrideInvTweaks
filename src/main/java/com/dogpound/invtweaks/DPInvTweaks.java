package com.dogpound.invtweaks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * DogPound InvTweaks — client-side 1.12.2 (Cleanroom).
 *
 * Hides items/blocks drawn with the "missing model" (purple/black checkerboard)
 * everywhere — vanilla inventory AND HEI/JEI lists. A toggle button in Video
 * Settings turns the hiding on/off live (so you can see what's being hidden).
 */
@Mod(modid = DPInvTweaks.MODID, name = DPInvTweaks.NAME, version = DPInvTweaks.VERSION,
     clientSideOnly = true, acceptedMinecraftVersions = "[1.12.2]")
public class DPInvTweaks {
    public static final String MODID = "dpinvtweaks";
    public static final String NAME = "Pride InvTweaks";
    public static final String VERSION = "1.7.0";

    /** Read by RenderItemMixin every frame. Toggled from the Video Settings button. */
    public static volatile boolean hideMissing = true;

    /** Center HBM/Space/Cursed item icons in their GUI slots. Toggled in Video Settings. */
    public static volatile boolean centerItems = true;

    /**
     * Pin the active-potion-effect display to the far LEFT of every container GUI (vanilla, creative,
     * AND every mod's GUI) instead of vanilla only drawing it next to the survival/creative inventory.
     * Read by GuiContainerEffectMixin (draws) + InvEffectSuppressMixin (hides vanilla's own draw).
     * Toggled in Video Settings.
     */
    public static volatile boolean effectsLeft = true;

    /**
     * Y offset substituted into HBM's hardcoded GUI item transform (HbmItemCenterMixin).
     * HBM's original is 11.3 (too high — sits on the slot line). Lower = icon moves down.
     * Tune this in config/dpinvtweaks.cfg until icons sit dead-center, then it's saved.
     */
    public static volatile float centerYOffset = 6.5f;

    /**
     * Scale substituted for HBM's hardcoded GUI item scale (0.062), which renders HBM/NTM/Cursed
     * block-items too big — they overflow the slot up-left. Lower = smaller so they fit + center.
     * Tunable in config/dpinvtweaks.cfg (edit + restart). 0.062 = HBM original (too big here).
     */
    public static volatile float centerScale = 0.044f;

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void init(FMLInitializationEvent e) {
        hideMissing = DPConfig.loadHideMissing();
        centerItems = DPConfig.loadCenterItems();
        centerYOffset = DPConfig.loadCenterYOffset();
        centerScale = DPConfig.loadCenterScale();
        effectsLeft = DPConfig.loadEffectsLeft();
        MinecraftForge.EVENT_BUS.register(new DPInvGui());
        DPKeybind.register();
        MinecraftForge.EVENT_BUS.register(new DPKeybind());
    }
}

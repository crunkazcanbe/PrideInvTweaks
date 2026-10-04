package com.dogpound.invtweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

/**
 * In-game keybind to toggle "Hide Bad Textures" instantly — works everywhere,
 * INCLUDING while the inventory / NEI-HEI is open (we poll the raw key in the
 * client tick, which fires inside GUIs, instead of relying on the normal keybind
 * dispatch which a GUI swallows). Shows HIDDEN/SHOWN above the hotbar so there's
 * no doubt it flipped. Default key = Insert; rebind it in Controls (or map it to
 * a controller button) under the "DogPound InvTweaks" category.
 */
public class DPKeybind {

    public static final KeyBinding TOGGLE =
            new KeyBinding("Toggle Hide Bad Textures", Keyboard.KEY_INSERT, "Pride InvTweaks");

    private boolean wasDown = false;

    public static void register() {
        ClientRegistry.registerKeyBinding(TOGGLE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        int code = TOGGLE.getKeyCode();
        boolean down = code > 0 && code < Keyboard.KEYBOARD_SIZE && Keyboard.isKeyDown(code);
        if (down && !wasDown) {                       // rising edge = one toggle per press
            DPConfig.saveHideMissing(!DPInvTweaks.hideMissing);
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.player != null) {
                mc.player.sendStatusMessage(new TextComponentString(
                        "§eBad textures: " + (DPInvTweaks.hideMissing ? "§cHIDDEN" : "§aSHOWN")), true);
            }
        }
        wasDown = down;
    }
}

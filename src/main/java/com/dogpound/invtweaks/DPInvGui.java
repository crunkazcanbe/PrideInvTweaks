package com.dogpound.invtweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Adds a single "Pride Tweaks…" button to the main OPTIONS menu (GuiOptions), which opens
 * {@link DPConfigScreen} with all the toggles. It lives in Options — NOT Video Settings — because
 * the Celeritas/Sodium port replaces the Video Settings screen with its own, hiding anything injected
 * there. The Options menu is left untouched by that port, so the button is always reachable.
 */
public class DPInvGui {

    private static final int BTN_OPENER = 0x44504810; // "DPO" — opener

    @SubscribeEvent
    public void onInit(GuiScreenEvent.InitGuiEvent.Post e) {
        final GuiScreen gui = e.getGui();
        if (!(gui instanceof GuiOptions)) return;
        // Bottom-left corner: clear of the option grid and the centered Done button, so no overlap.
        CycleButton opener = new CycleButton(BTN_OPENER, 5, gui.height - 25, 130, 20, "Pride Tweaks…", new Runnable() {
            public void run() {
                Minecraft.getMinecraft().displayGuiScreen(new DPConfigScreen(gui));
            }
        });
        e.getButtonList().add(opener);
    }

    /** A GuiButton that runs an action on click. Shared with DPConfigScreen. */
    static class CycleButton extends GuiButton {
        private final Runnable action;
        CycleButton(int id, int x, int y, int w, int h, String text, Runnable action) {
            super(id, x, y, w, h, text);
            this.action = action;
        }
        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                this.playPressSound(mc.getSoundHandler());
                action.run();
                return true;
            }
            return false;
        }
    }
}

package com.dogpound.invtweaks;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Small standalone config screen for DogPound InvTweaks, opened from a button in the main Options menu.
 * Lives in Options (not Video Settings) because the Celeritas/Sodium port replaces the Video Settings
 * screen, which would hide the toggles there.
 */
public class DPConfigScreen extends GuiScreen {

    private final GuiScreen parent;
    private DPInvGui.CycleButton hideBtn;
    private DPInvGui.CycleButton centerBtn;
    private DPInvGui.CycleButton effectsBtn;

    public DPConfigScreen(GuiScreen parent) {
        this.parent = parent;
    }

    private static String hideLabel()    { return "Hide Bad Textures: " + (DPInvTweaks.hideMissing ? "ON" : "OFF"); }
    private static String centerLabel()  { return "Center Items: "      + (DPInvTweaks.centerItems ? "ON" : "OFF"); }
    private static String effectsLabel() { return "Effects On Left: "    + (DPInvTweaks.effectsLeft ? "ON" : "OFF"); }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int cx = this.width / 2;
        int y = this.height / 4;

        hideBtn = new DPInvGui.CycleButton(1, cx - 155, y, 310, 20, hideLabel(), new Runnable() {
            public void run() { DPConfig.saveHideMissing(!DPInvTweaks.hideMissing); hideBtn.displayString = hideLabel(); }
        });
        centerBtn = new DPInvGui.CycleButton(2, cx - 155, y + 24, 310, 20, centerLabel(), new Runnable() {
            public void run() { DPConfig.saveCenterItems(!DPInvTweaks.centerItems); centerBtn.displayString = centerLabel(); }
        });
        effectsBtn = new DPInvGui.CycleButton(3, cx - 155, y + 48, 310, 20, effectsLabel(), new Runnable() {
            public void run() { DPConfig.saveEffectsLeft(!DPInvTweaks.effectsLeft); effectsBtn.displayString = effectsLabel(); }
        });
        this.buttonList.add(hideBtn);
        this.buttonList.add(centerBtn);
        this.buttonList.add(effectsBtn);
        this.buttonList.add(new GuiButton(0, cx - 100, this.height - 30, 200, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Pride Inventory Tweaks", this.width / 2, this.height / 4 - 24, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

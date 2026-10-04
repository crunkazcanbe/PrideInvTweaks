package com.dogpound.invtweaks;

import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/** Flat-file config at config/dpinvtweaks.cfg so the toggles survive restarts. */
public final class DPConfig {
    private DPConfig() {}

    private static File file() {
        return new File(Loader.instance().getConfigDir(), "dpinvtweaks.cfg");
    }

    /** Read a single boolean key, defaulting if the file/key is absent. */
    private static boolean read(String key, boolean def) {
        try {
            File f = file();
            if (!f.isFile()) return def;
            BufferedReader r = new BufferedReader(new FileReader(f));
            try {
                String line;
                while ((line = r.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("#") || line.indexOf('=') < 0) continue;
                    String k = line.substring(0, line.indexOf('=')).trim();
                    String v = line.substring(line.indexOf('=') + 1).trim();
                    if (k.equals(key)) return Boolean.parseBoolean(v);
                }
            } finally { r.close(); }
        } catch (Exception ignored) { }
        return def;
    }

    /** Read a single float key, defaulting if the file/key is absent or unparseable. */
    private static float readFloat(String key, float def) {
        try {
            File f = file();
            if (!f.isFile()) return def;
            BufferedReader r = new BufferedReader(new FileReader(f));
            try {
                String line;
                while ((line = r.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("#") || line.indexOf('=') < 0) continue;
                    String k = line.substring(0, line.indexOf('=')).trim();
                    String v = line.substring(line.indexOf('=') + 1).trim();
                    if (k.equals(key)) {
                        try { return Float.parseFloat(v); } catch (NumberFormatException e) { return def; }
                    }
                }
            } finally { r.close(); }
        } catch (Exception ignored) { }
        return def;
    }

    /** Rewrite the whole file with all current settings (never clobbers another key). */
    private static void writeAll() {
        try {
            File f = file();
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
            PrintWriter w = new PrintWriter(new FileWriter(f));
            try {
                w.println("# Pride InvTweaks");
                w.println("# hideMissing: true = hide missing-texture/model items, false = show them");
                w.println("# centerItems: true = center HBM/Space/Cursed item icons in their GUI slots");
                w.println("# centerYOffset: tune until icons sit dead-center. 11.3 = HBM original (high); lower = down.");
                w.println("hideMissing=" + DPInvTweaks.hideMissing);
                w.println("centerItems=" + DPInvTweaks.centerItems);
                w.println("centerYOffset=" + DPInvTweaks.centerYOffset);
                w.println("# centerScale: HBM GUI item size. 0.062 = HBM original (too big); lower = smaller/fits.");
                w.println("centerScale=" + DPInvTweaks.centerScale);
                w.println("# effectsLeft: true = pin active potion effects to the far left of every GUI (all mods)");
                w.println("effectsLeft=" + DPInvTweaks.effectsLeft);
            } finally { w.close(); }
        } catch (Exception ignored) { }
    }

    public static boolean loadHideMissing() { return read("hideMissing", true); }
    public static boolean loadCenterItems() { return read("centerItems", true); }
    public static float loadCenterYOffset() { return readFloat("centerYOffset", 6.5f); }
    public static float loadCenterScale() { return readFloat("centerScale", 0.044f); }
    public static boolean loadEffectsLeft() { return read("effectsLeft", true); }

    public static void saveHideMissing(boolean val) { DPInvTweaks.hideMissing = val; writeAll(); }
    public static void saveCenterItems(boolean val) { DPInvTweaks.centerItems = val; writeAll(); }
    public static void saveCenterYOffset(float val) { DPInvTweaks.centerYOffset = val; writeAll(); }
    public static void saveEffectsLeft(boolean val) { DPInvTweaks.effectsLeft = val; writeAll(); }
}

package net.bettermc.expanse.util;

import net.bettermc.expanse.VarietySavanna;
import net.minecraft.util.Identifier;

public class ModIdentifier extends Identifier {
    public ModIdentifier(String path) {
        super(VarietySavanna.MOD_ID, path);
    }
}

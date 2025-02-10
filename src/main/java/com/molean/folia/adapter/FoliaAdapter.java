package com.molean.folia.adapter;

import org.bukkit.plugin.java.JavaPlugin;

public class FoliaAdapter extends JavaPlugin {

    @Override
    public void onDisable() {
        Folia.disable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}

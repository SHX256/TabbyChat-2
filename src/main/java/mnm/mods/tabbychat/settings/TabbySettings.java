package mnm.mods.tabbychat.settings;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import mnm.mods.tabbychat.util.TabbyRef;
import mnm.mods.util.config.SettingsFile;
import net.minecraft.util.EnumTypeAdapterFactory;

public class TabbySettings extends SettingsFile {

    @Expose
    public GeneralSettings general = new GeneralSettings();
    @Expose
    public AdvancedSettings advanced = new AdvancedSettings();
    @Expose
    public ColorSettings colors = new ColorSettings();

    public TabbySettings() {
        super(TabbyRef.MOD_ID, "tabbychat");
    }

    @Override
    public void setupGsonSerialiser(GsonBuilder gsonBuilder) {
        super.setupGsonSerialiser(gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory()));
    }
}
package mnm.mods.tabbychat.gui.settings;

import java.awt.Rectangle;
import java.util.List;

import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.gui.PrefsButton;
import mnm.mods.tabbychat.util.Translation;
import mnm.mods.util.Color;
import mnm.mods.util.gui.BorderLayout;
import mnm.mods.util.gui.ComponentScreen;
import mnm.mods.util.gui.FlowLayout;
import mnm.mods.util.gui.GuiComponent;
import mnm.mods.util.gui.GuiPanel;
import mnm.mods.util.gui.SettingPanel;
import mnm.mods.util.gui.VerticalLayout;
import mnm.mods.util.gui.events.ActionPerformed;
import mnm.mods.util.gui.events.GuiEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

import com.google.common.collect.Lists;

@SuppressWarnings("rawtypes")
public class GuiSettingsScreen extends ComponentScreen {

    private static List<Class<? extends SettingPanel>> settings = Lists.newArrayList();

    static {
        registerSetting(GuiSettingsGeneral.class);
        registerSetting(GuiFilterSettings.class);
        registerSetting(GuiSettingsColors.class);
    }

    private GuiPanel panel;
    private GuiPanel settingsList;
    private GuiPanel closeSaveButtons;
    private SettingPanel selectedSetting;

    @Override
    public void initGui() {
        getPanel().addComponent(panel = new GuiPanel());
        panel.setLayout(new BorderLayout());
        panel.setSize(300, 200);
        panel.setPosition(width / 2 - panel.getBounds().width / 2, height / 2
                - panel.getBounds().height / 2);
        panel.addComponent(
                new PrefsButton(EnumChatFormatting.BOLD + Translation.SETTINGS_TITLE.translate()),
                BorderLayout.Position.NORTH);
        panel.addComponent(settingsList = new GuiPanel(new VerticalLayout()),
                BorderLayout.Position.WEST);
        panel.addComponent(closeSaveButtons = new GuiPanel(new FlowLayout()),
                BorderLayout.Position.SOUTH);
        PrefsButton save = new PrefsButton(Translation.SETTINGS_SAVE.translate());
        save.setSize(40, 10);
        save.setBackColor(Color.getColor(0, 255, 0, 127));
        save.addEventListener(new ActionPerformed() {
            @Override
            public void actionPerformed(GuiEvent event) {
                selectedSetting.saveSettings();
                selectedSetting.getSettings().saveSettingsFile();
            }
        });
        closeSaveButtons.addComponent(save);
        PrefsButton close = new PrefsButton(Translation.SETTINGS_CLOSE.translate());
        close.setSize(40, 10);
        close.setBackColor(Color.getColor(0, 255, 0, 127));
        close.addEventListener(new ActionPerformed() {
            @Override
            public void actionPerformed(GuiEvent event) {
                mc.displayGuiScreen(null);
            }
        });
        closeSaveButtons.addComponent(close);

        {
            // Populate the settings
            for (Class<? extends SettingPanel> sett : settings) {
                try {
                    SettingsButton button = new SettingsButton(sett.newInstance());
                    button.addEventListener(new ActionPerformed() {
                        @Override
                        public void actionPerformed(GuiEvent event) {
                            selectSetting(((SettingsButton) event.getComponent()).getSettings()
                                    .getClass());
                        }
                    });
                    settingsList.addComponent(button);
                } catch (Exception e) {
                    TabbyChat.getLogger().error(
                            "Unable to add " + sett.getName() + " as a setting.", e);
                }
            }
        }
        Class<? extends SettingPanel> panelClass;
        if (selectedSetting == null) {
            panelClass = settings.get(0);
        } else {
            panelClass = selectedSetting.getClass();
        }
        selectSetting(panelClass);

    }

    private void deactivateAll() {
        for (GuiComponent comp : settingsList) {
            if (comp instanceof SettingsButton) {
                ((SettingsButton) comp).setActive(false);
            }
        }
    }

    private void activate(Class<? extends SettingPanel> settingClass) {
        for (GuiComponent comp : settingsList) {
            if (comp instanceof SettingsButton
                    && ((SettingsButton) comp).getSettings().getClass().equals(settingClass)) {
                ((SettingsButton) comp).setActive(true);
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        // drawDefaultBackground();
        Rectangle rect = panel.getBounds();
        Gui.drawRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, Integer.MIN_VALUE);
        super.drawScreen(mouseX, mouseY, tick);
    }

    private void selectSetting(Class<? extends SettingPanel> settingClass) {
        if (!settings.contains(settingClass)) {
            throw new IllegalArgumentException(settingClass.getName()
                    + " is not a registered setting category.");
        }
        try {
            deactivateAll();
            panel.removeComponent(selectedSetting);
            selectedSetting = settingClass.newInstance();
            // if (init) {
            selectedSetting.initGUI();
            // }
            activate(settingClass);
            panel.addComponent(selectedSetting, BorderLayout.Position.CENTER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSettings() {
        for (GuiComponent comp : settingsList) {
            if (comp instanceof SettingPanel) {
                ((SettingPanel) comp).getSettings().saveSettingsFile();
            }
        }
    }

    public static void registerSetting(Class<? extends SettingPanel> settings) {
        if (!GuiSettingsScreen.settings.contains(settings)) {
            GuiSettingsScreen.settings.add(settings);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}

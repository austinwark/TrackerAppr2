package com.sandboxcode.trackerappr2.models;

public class SettingsModel {

    private boolean notificationsEnabled;
    private boolean darkModeEnabled;

    public SettingsModel(boolean notificationsEnabled, boolean darkModeEnabled) {
        this.notificationsEnabled = notificationsEnabled;
        this.darkModeEnabled = darkModeEnabled;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}

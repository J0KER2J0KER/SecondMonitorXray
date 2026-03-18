package com.j0ker2j0ker.second_monitor_xray.client;

import net.fabricmc.api.ClientModInitializer;


public class SecondMonitorXrayClient implements ClientModInitializer {

    private static SecondMonitorXrayClient instance;
    public SecondWindow secondWindow;

    @Override
    public void onInitializeClient() {
        instance = this;
        secondWindow = new SecondWindow();
    }

    public static SecondMonitorXrayClient getInstance() {
        return instance;
    }
}

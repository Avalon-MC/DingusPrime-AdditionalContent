package net.petercashel.dingusprimeacm.client;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.RegistryEvent;
import net.minecraft.world.level.Level;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.world.zones.ZoneManagerClient;
import net.petercashel.dingusprimeacm.world.zones.ZoneManagerData;

import java.util.ArrayList;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@journeymap.client.api.ClientPlugin
public class DingusJourneyMapPlugin implements IClientPlugin, IClientZoneUpdates {

    // API reference
    private IClientAPI jmAPI = null;
    private static DingusJourneyMapPlugin INSTANCE;
    private ClientProperties clientProperties;
    private ArrayList<PolygonOverlay> zonePolygons = new ArrayList<>();

    public ClientProperties getClientProperties()
    {
        return clientProperties;
    }

    public DingusJourneyMapPlugin() {
        INSTANCE = this;
    }
    public static DingusJourneyMapPlugin getInstance()
    {
        return INSTANCE;
    }

    /**
     * Called by JourneyMap during the init phase of mod loading.  The IClientAPI reference is how the mod
     * will add overlays, etc. to JourneyMap.
     *
     * @param iClientAPI Client API implementation
     */
    @Override
    public void initialize(IClientAPI iClientAPI) {
        jmAPI = iClientAPI;

        // Subscribe to desired ClientEvent types from JourneyMap
        this.jmAPI.subscribe(getModId(), EnumSet.of(MAPPING_STARTED, MAPPING_STOPPED, REGISTRY));


    }

    @Override
    public String getModId() {
        return "dingusprimeacm";
    }

    /**
     * Called by JourneyMap on the main Minecraft thread when a {@link journeymap.client.api.event.ClientEvent} occurs.
     * Be careful to minimize the time spent in this method so you don't lag the game.
     * <p>
     * You must call {@link IClientAPI#subscribe(String, EnumSet)} at some point to subscribe to these events, otherwise this
     * method will never be called.
     * <p>
     * If the event type is {@link journeymap.client.api.event.ClientEvent.Type#DISPLAY_UPDATE},
     * this is a signal to {@link journeymap.client.api.IClientAPI#show(journeymap.client.api.display.Displayable)}
     * all relevant Displayables for the {@link journeymap.client.api.event.ClientEvent#dimension} indicated.
     * (Note: ModWaypoints with persisted==true will already be shown.)
     *
     * @param event the event
     */
    @Override
    public void onEvent(ClientEvent event) {
        try
        {
            switch (event.type)
            {
                case MAPPING_STARTED:
                    onMappingStarted(event);
                    break;

                case MAPPING_STOPPED:
                    onMappingStopped(event);
                    break;
                case REGISTRY:
                    RegistryEvent registryEvent = (RegistryEvent) event;
                    switch(registryEvent.getRegistryType()) {
                        case OPTIONS:
                            this.clientProperties = new ClientProperties();
                            break;
                    }
                    break;
            }
        }
        catch (Throwable t)
        {
            dingusprimeacm.LOGGER.error(t.getMessage(), t);
        }
    }

    private void onMappingStarted(ClientEvent event) {

        if (jmAPI.playerAccepts(getModId(), DisplayType.Polygon) && event.dimension == Level.OVERWORLD && clientProperties.showMarkers.get())
        {
            zonePolygons = ZonePolygonOverlayFactory.create(jmAPI, event.dimension);
            ZoneManagerClient.journeyMapPlugin = this;
        }


        //Sprite Markers
        //https://github.com/TeamJM/journeymap-api/blob/1.18.2_1.9/src/main/java/example/mod/client/plugin/SampleMarkerOverlayFactory.java
        //https://github.com/TeamJM/journeymap-api/blob/1.18.2_1.9/src/main/resources/assets/examplemod/images/sprites.png
        //https://github.com/TeamJM/journeymap-api/blob/0a5eaddcf7ba2323d62ec0ddc1d14084a4b003ea/src/main/java/example/mod/client/plugin/ExampleJourneymapPlugin.java#L199

    }

    private void onMappingStopped(ClientEvent event) {
        jmAPI.removeAll(getModId());
        ZoneManagerClient.journeyMapPlugin = null;
    }

    @Override
    public void UpdateZones(ZoneManagerData zoneManagerData_client) {

        jmAPI.removeAll(getModId(), DisplayType.Polygon);
        if (jmAPI.playerAccepts(getModId(), DisplayType.Polygon) && clientProperties.showMarkers.get())
        {
            zonePolygons = ZonePolygonOverlayFactory.create(jmAPI, Level.OVERWORLD);
        }
    }
}

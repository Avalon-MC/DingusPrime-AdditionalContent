package net.petercashel.dingusprimeacm.client;

import journeymap.client.api.option.BooleanOption;
import journeymap.client.api.option.CustomIntegerOption;
import journeymap.client.api.option.CustomTextOption;
import journeymap.client.api.option.EnumOption;
import journeymap.client.api.option.FloatOption;
import journeymap.client.api.option.IntegerOption;
import journeymap.client.api.option.KeyedEnum;
import journeymap.client.api.option.OptionCategory;

public class ClientProperties
{
    private OptionCategory category = new OptionCategory("dingusprimeacm", "Zone Markers", "Adjust Zone Settings");

    public final BooleanOption showMarkers;

    public ClientProperties()
    {
        this.showMarkers = new BooleanOption(category, "showMarkers", "Show Zone Markers", true);
    }

}

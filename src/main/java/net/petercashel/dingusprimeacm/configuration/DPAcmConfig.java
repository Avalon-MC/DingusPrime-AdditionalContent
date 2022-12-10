package net.petercashel.dingusprimeacm.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Files;

public class DPAcmConfig {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
    public static File cfgFile = new File("config/dingusprimeacm.json").getAbsoluteFile();
    public static DPAcmConfig ConfigInstance = new DPAcmConfig();

    private static void SaveConfig(File cfgFile, DPAcmConfig CSconfig) {
        try {
            try(FileWriter writer = new FileWriter(cfgFile.getAbsoluteFile().getPath())) {
                gson.toJson(CSconfig, writer);
                writer.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SaveConfig() {
        SaveConfig(cfgFile, ConfigInstance);
    }

    private static DPAcmConfig LoadConfig(File cfgFile, DPAcmConfig CSconfig) {
        if (cfgFile.exists()) {
            try {
                // create a reader
                Reader reader = Files.newBufferedReader(cfgFile.toPath());

                CSconfig = gson.fromJson(reader, DPAcmConfig.class);

                // close reader
                reader.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (!cfgFile.exists() || CSconfig == null) {
            CSconfig = new DPAcmConfig();
            CSconfig.ConfigVersion = 1;
        }
        return CSconfig;
    }

    public static DPAcmConfig LoadConfig() {
        ConfigInstance = LoadConfig(cfgFile, ConfigInstance);

        ConfigInstance.Migrate();

        //Finally
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            //Enforce proper DS settings

        }

        return ConfigInstance;
    }


    private void Migrate() {


    }


    @Expose
    public int ConfigVersion = 1;

    @Expose
    public CommonSettings CommonSettings = new CommonSettings();
    public class CommonSettings {
//        @Expose
//        public boolean IsConfigured = false;
//        @Expose
//        public boolean HideMenuButton = false;
//        @Expose
//        public boolean DisableUI = false;
    }

    public enum ShopSortEnum {
        Name,
        NamePrice,
        Price,
        PriceName,


    }

    @Expose
    public ShopSettings ShopSettings = new ShopSettings();
    public class ShopSettings {
        @Expose
        public ShopSortEnum SortType = ShopSortEnum.PriceName;

        @Expose
        public int RandomShopTradesCount = 3;

    }



}

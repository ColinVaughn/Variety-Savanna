package net.bettermc.expanse.util;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
public class ConfigUtil    {
    private ConfigUtil() {}

    public static final ConfigUtil INSTANCE = new ConfigUtil();

    private boolean loaded;
    private Properties aqprop = new Properties();


    public boolean getBooleanProperty(String key) {
        if (!loaded) load();
        return Boolean.parseBoolean(aqprop.getProperty(key));
    }
    public String getStringProperty(String key) {
        if (!loaded) load();
        return aqprop.getProperty(key);
    }
    public int getNumberProperty(String key) {
        if (!loaded) load();
        return Integer.parseInt(aqprop.getProperty(key));
    }
    public double getDoubleProperty(String key) {
        if (!loaded) load();
        return Double.parseDouble(aqprop.getProperty(key));
    }

    private final File file = new File("./config/Variety/savanna.config");

    private void load() {
        loaded = true;
        try {
            Files.createDirectories(Paths.get("./config/Variety/"));

            if(file.exists() && file.length() != 0) {
                var reader = new FileReader(file);
                aqprop.load(reader);
                reader.close();
            } else {
                var writer = new FileOutputStream(file);
                file.createNewFile();
                aqprop.setProperty("config.version","1");
                aqprop.setProperty("entity.angertimemin","20");
                aqprop.setProperty("entity.angertimemax","39");
                aqprop.setProperty("entity.friendly","false");
                aqprop.setProperty("entity.health","30.0");
                aqprop.setProperty("entity.speed","0.25");
                aqprop.setProperty("entity.follow","20.0");
                aqprop.setProperty("entity.damage","10.0");
                aqprop.store(writer, "Configuration file for Variety Savanna mod");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
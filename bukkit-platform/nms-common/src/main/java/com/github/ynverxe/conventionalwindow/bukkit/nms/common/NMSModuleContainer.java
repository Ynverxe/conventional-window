package com.github.ynverxe.conventionalwindow.bukkit.nms.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NMSModuleContainer {

  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Class.class,
          (JsonDeserializer<Class<NMSModule>>) (jsonElement, type, jsonDeserializationContext) -> {
            try {
              return (Class<NMSModule>) Class.forName(jsonElement.getAsString());
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }).create();
  private static final Logger LOGGER = LoggerFactory.getLogger(NMSModuleContainer.class);
  public static final NMSModuleContainer INSTANCE = new NMSModuleContainer();

  private final Map<String, NMSModule> nmsModuleMap = new HashMap<>();

  private NMSModuleContainer() {
    InputStream stream = NMSModuleContainer.class.getClassLoader().getResourceAsStream("nms-modules.json");

    if (stream == null) {
      LOGGER.warn("Unable to find nms modules manifest.");
      return;
    }

    Reader reader = new InputStreamReader(stream);

    Map<Class<NMSModule>, List<String>> builtInModules = GSON.fromJson(reader, new TypeToken<Map<Class<NMSModule>, List<String>>>() {}.getType());
    if (builtInModules == null) {
      LOGGER.warn("Unable to load built-in modules.");
      return;
    }

    builtInModules.forEach((moduleClass,supportedVersions) -> {
      try {
        NMSModule module = moduleClass.getDeclaredConstructor().newInstance();
        supportedVersions.forEach(version -> registerModule(version, module));
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException e) {
        LOGGER.error("Unable to load module {}", moduleClass, e);
      }
    });

    LOGGER.info("{} built-in NMSModules loaded", builtInModules.size());
  }

  public void registerModule(@NotNull String minecraftVersion, @NotNull NMSModule module) {
    this.nmsModuleMap.put(
        Objects.requireNonNull(minecraftVersion, "minecraftVersion"),
        Objects.requireNonNull(module, "module"));
  }

  public @NotNull Optional<NMSModule> getConvenientModule() {
    return Optional.ofNullable(this.nmsModuleMap.get(Bukkit.getMinecraftVersion()));
  }

  public @NotNull NMSModule convenientModule() throws IllegalStateException {
    return getConvenientModule()
        .orElseThrow(() -> new IllegalStateException("Cannot found a convenient NMSModel for the current MC Version"));
  }
}
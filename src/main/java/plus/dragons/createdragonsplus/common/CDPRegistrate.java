/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.simibubi.create.api.registrate.CreateRegistrateRegistrationCallback;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CDPRegistrate extends CreateRegistrate {
    public CDPRegistrate(String modid) {
        super(modid);
        CreateRegistrateRegistrationCallback.provideRegistrate(this);
    }

    public final ResourceLocation asResource(String path) {
        return new ResourceLocation(getModid(), path);
    }

    public CDPRegistrate setTooltipModifier(Function<Item, TooltipModifier> tooltipModifier) {
        return (CDPRegistrate) setTooltipModifierFactory(tooltipModifier);
    }

    public CDPRegistrate registerBuiltinLocalization(String name) {
        addDataGenerator(ProviderType.LANG, provider -> generateBuiltinLocalization(name, provider));
        return this;
    }

    public CDPRegistrate registerPonderLocalization(Supplier<?> plugin) {
        return this;
    }

    public CDPRegistrate registerForeignLocalization() {
        return this;
    }

    public CDPRegistrate registerForeignLocalization(String templateLocale) {
        return this;
    }

    protected void generateBuiltinLocalization(String name, RegistrateLangProvider provider) {
        JsonObject json = readBuiltinLocalization(name);
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            provider.add(entry.getKey(), entry.getValue().getAsString());
        }
    }

    protected JsonObject readBuiltinLocalization(String name) {
        String resourcePath = "assets/" + getModid() + "/lang/builtin/" + name + ".json";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream != null) {
                return JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read builtin localization: " + resourcePath, exception);
        }

        Path filePath = Path.of("src/main/resources", resourcePath);
        if (Files.exists(filePath)) {
            try (InputStream stream = Files.newInputStream(filePath)) {
                return JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to read builtin localization: " + filePath, exception);
            }
        }
        throw new IllegalStateException("Failed to find builtin localization: " + resourcePath);
    }
}

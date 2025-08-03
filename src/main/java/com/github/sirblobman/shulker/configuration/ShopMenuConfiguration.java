package com.github.sirblobman.shulker.configuration;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

public final class ShopMenuConfiguration implements IConfigurable {
    private boolean enabled;
    private double defaultPrice;
    private final Map<XMaterial, Double> priceMap;

    public ShopMenuConfiguration() {
        this.enabled = true;
        this.defaultPrice = 5.0D;
        this.priceMap = new EnumMap<>(XMaterial.class);
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setEnabled(section.getBoolean("enabled", true));
        setDefaultPrice(section.getDouble("default-price", 5.0D));

        double defaultPrice = getDefaultPrice();
        this.priceMap.clear();

        ConfigurationSection priceSection = getOrCreateSection(section, "price");
        Set<String> materialNameSet = priceSection.getKeys(false);
        for (String materialName : materialNameSet) {
            Optional<XMaterial> optionalMaterial = XMaterial.matchXMaterial(materialName);
            if (optionalMaterial.isEmpty()) {
                continue;
            }

            XMaterial material = optionalMaterial.get();
            double price = priceSection.getDouble(materialName, defaultPrice);
            this.priceMap.put(material, price);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getPrice(@NotNull XMaterial material) {
        double defaultPrice = getDefaultPrice();
        return this.priceMap.getOrDefault(material, defaultPrice);
    }

    public double getDefaultPrice() {
        return this.defaultPrice;
    }

    public void setDefaultPrice(double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }
}

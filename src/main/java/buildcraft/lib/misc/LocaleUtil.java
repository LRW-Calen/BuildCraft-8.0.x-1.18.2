/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.misc;

import buildcraft.api.mj.MjAPI;
import buildcraft.api.core.BCDebugging;
import buildcraft.api.core.BCLog;
import buildcraft.lib.BCLibConfig;
import buildcraft.lib.BCLibConfig.TimeGap;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Set;

/**
 * The central class for localizing objects.
 */
public class LocaleUtil
{

    public static final boolean DEBUG = BCDebugging.shouldDebugLog("lib.locale");
    private static final Set<String> failedStrings = new HashSet<>();

    private static final NumberFormat FORMAT_FLUID = NumberFormat.getNumberInstance();

    private static String localeKeyFluidStatic, localeKeyFluidFlow;
    private static String localeKeyFluidStaticCap, localeKeyFluidStaticEmpty, localeKeyFluidStaticFull;
    private static String localeKeyMjStatic, localeKeyMjFlow;

    static
    {
        BCLibConfig.configChangeListeners.add(LocaleUtil::onConfigChanged);
        onConfigChanged();
    }

    /**
     * Should be called whenever any of the {@link BCLibConfig} options are changed that affect any of the methods in
     * this class.
     */
    public static void onConfigChanged()
    {
        boolean bucketStatic = BCLibConfig.useBucketsStatic;
        boolean bucketFlow = BCLibConfig.useBucketsFlow;
        String longName = BCLibConfig.useLongLocalizedName ? "long" : "short";
        String timeGap = BCLibConfig.displayTimeGap == TimeGap.SECONDS ? "seconds." : "";
        localeKeyFluidStatic = "buildcraft.fluid.static." + (bucketStatic ? "bucket." : "milli.") + longName;
        localeKeyFluidFlow = "buildcraft.fluid.flow." + (bucketFlow ? "bucket." : "milli.") + longName;
        localeKeyFluidStaticCap = "buildcraft.fluid.static.cap." + (bucketStatic ? "bucket." : "milli.") + longName;
        localeKeyFluidStaticEmpty = "buildcraft.fluid.empty." + (bucketFlow ? "bucket." : "milli.") + longName;
        localeKeyFluidStaticFull = "buildcraft.fluid.full." + (bucketFlow ? "bucket." : "milli.") + longName;
        localeKeyMjStatic = "buildcraft.mj.static." + longName;
        localeKeyMjFlow = "buildcraft.mj.flow." + timeGap + longName;
    }

    /**
     * Localizes the give key to the current locale.
     *
     * @param key The key to localize
     * @return The localized key, or the input key if no localization was found.
     */
    public static String localize(String key)
    {
        String localized = I18n.get(key);
        if (localized == key)
        {
            if (DEBUG && failedStrings.add(localized))
            {
                BCLog.logger.warn("[lib.locale] Attempted to localize '" + key + "' but no localization existed!");
            }
            return key;
        }
        return localized;
    }

    /**
     * Localizes the given key, and performs {@link String#format(String, Object...)} with the localized value and the
     * arguments given.
     *
     * @param key  The key to localize
     * @param args The arguments to put into the localized key
     * @return The localized string.
     */
    public static String localize(String key, Object... args)
    {
//        String localized = I18n.get(key);
        String localized = new TranslatableComponent(key, args).getString();
        if (localized == key)
        {
            if (DEBUG && failedStrings.add(localized))
            {
                BCLog.logger.warn("[lib.locale] Attempted to localize '" + key + "' but no localization existed!");
            }
            return key + " " + Arrays.toString(args);
        }
        try
        {
//            return String.format(localized, args);
            return I18n.get(key, args);
        }
        catch (IllegalFormatException ife)
        {
            return "Bad Format: " + ife.getMessage();
        }
    }

    /**
     * Checks to see if the given key can be localized.
     *
     * @param key The key to check
     * @return True if the key could be localized, false if not.
     */
    public static boolean canLocalize(String key)
    {
        return I18n.exists(key);
    }

    /**
     * @param colour The {@link DyeColor} to localize.
     * @return a localised name for the given colour.
     */
    public static String localizeColour(DyeColor colour)
    {
//        return localize("item.fireworksCharge." + colour.getName());
        return localize("item.minecraft.firework_star." + colour.getName());
    }

    // Calen
    public static String getColorTranslateKey(DyeColor colour)
    {
//        return localize("item.fireworksCharge." + colour.getName());
        return "item.minecraft.firework_star." + colour.getName();
    }

    /**
     * @param face The {@link Direction} to localize.
     * @return a localised name for the given face.
     */
    public static String localizeFacing(@Nullable Direction face)
    {
        return localize("direction." + (face == null ? "center" : face.getName()));
    }

    // Calen
    public static Component localizeFacingComponent(@Nullable Direction face)
    {
        return new TranslatableComponent("direction." + (face == null ? "center" : face.getName()));
    }

    public static String localizeFluidStaticAmount(IFluidTank tank)
    {
        return localizeFluidStaticAmount(tank.getFluidAmount(), tank.getCapacity());
    }

    public static Component localizeFluidStaticAmountComponent(IFluidTank tank)
    {
        return localizeFluidStaticAmountComponent(tank.getFluidAmount(), tank.getCapacity());
    }

    public static String localizeFluidStaticAmount(int fluidAmount)
    {
        return localizeFluidStaticAmount(fluidAmount, -1);
    }

    public static MutableComponent localizeFluidStaticAmountComponent(int fluidAmount)
    {
        return localizeFluidStaticAmountComponent(fluidAmount, -1);
    }

    /**
     * Localizes the given fluid amount, out of a given capacity
     */
    public static String localizeFluidStaticAmount(int fluidAmount, int capacity)
    {
        if (fluidAmount <= 0)
        {
            if (capacity > 0)
            {
                String cap;
                if (BCLibConfig.useBucketsStatic)
                {
                    cap = FORMAT_FLUID.format(capacity / 1000.0);
                }
                else
                {
                    cap = FORMAT_FLUID.format(capacity);
                }
                return localize(localeKeyFluidStaticEmpty, cap);
            }
            return localize("buildcraft.fluid.empty");
        }
        else
        {
            String amount;
            String cap;
            if (BCLibConfig.useBucketsStatic)
            {
                amount = FORMAT_FLUID.format(fluidAmount / 1000.0);
                cap = FORMAT_FLUID.format(capacity / 1000.0);
            }
            else
            {
                amount = FORMAT_FLUID.format(fluidAmount);
                cap = FORMAT_FLUID.format(capacity);
            }
            if (capacity == fluidAmount)
            {
                return localize(localeKeyFluidStaticFull, amount);
            }
            return localize(capacity > 0 ? localeKeyFluidStaticCap : localeKeyFluidStatic, amount, cap);
        }
    }

    // Calen
    public static MutableComponent localizeFluidStaticAmountComponent(int fluidAmount, int capacity)
    {
        if (fluidAmount <= 0)
        {
            if (capacity > 0)
            {
                String cap;
                if (BCLibConfig.useBucketsStatic)
                {
                    cap = FORMAT_FLUID.format(capacity / 1000.0);
                }
                else
                {
                    cap = FORMAT_FLUID.format(capacity);
                }
                return new TranslatableComponent(localeKeyFluidStaticEmpty, cap);
            }
            return new TranslatableComponent("buildcraft.fluid.empty");
        }
        else
        {
            String amount;
            String cap;
            if (BCLibConfig.useBucketsStatic)
            {
                amount = FORMAT_FLUID.format(fluidAmount / 1000.0);
                cap = FORMAT_FLUID.format(capacity / 1000.0);
            }
            else
            {
                amount = FORMAT_FLUID.format(fluidAmount);
                cap = FORMAT_FLUID.format(capacity);
            }
            if (capacity == fluidAmount)
            {
                return new TranslatableComponent(localeKeyFluidStaticFull, amount);
            }
            return new TranslatableComponent(capacity > 0 ? localeKeyFluidStaticCap : localeKeyFluidStatic, amount, cap);
        }
    }

    public static String localizeFluidFlow(int milliBucketsPerTick)
    {
        String amount;
        if (BCLibConfig.useBucketsFlow)
        {
            amount = FORMAT_FLUID.format(milliBucketsPerTick / 50.0);
        }
        else
        {
            amount = FORMAT_FLUID.format(milliBucketsPerTick);
        }
        return localize(localeKeyFluidFlow, amount);
    }

    // Calen
    public static TranslatableComponent localizeFluidFlowToTranslatableComponent(int milliBucketsPerTick)
    {
        String amount;
        if (BCLibConfig.useBucketsFlow)
        {
            amount = FORMAT_FLUID.format(milliBucketsPerTick / 50.0);
        }
        else
        {
            amount = FORMAT_FLUID.format(milliBucketsPerTick);
        }
        return new TranslatableComponent(localeKeyFluidFlow, amount);
    }

    public static String localizeMj(long mj)
    {
        return localize(localeKeyMjStatic, MjAPI.formatMj(mj));
    }

    // Calen
    //    public static String localizeMj(long mj)
    public static MutableComponent localizeMjComponent(long mj)
    {
//        return localize(localeKeyMjStatic, MjAPI.formatMj(mj));
        return new TranslatableComponent(localeKeyMjStatic, MjAPI.formatMj(mj));
    }

    public static String localizeMjFlow(long mj)
    {
        mj = BCLibConfig.displayTimeGap.convertTicksToGap(mj);
        return localize(localeKeyMjFlow, MjAPI.formatMj(mj));
    }
    // Calen
    public static TranslatableComponent localizeMjFlowComponent(long mj)
    {
        mj = BCLibConfig.displayTimeGap.convertTicksToGap(mj);
        return new TranslatableComponent(localeKeyMjFlow, MjAPI.formatMj(mj));
    }

    public static String localizeHeat(double heat)
    {
        // 原本就是注释
        // if (BCLibConfig.useLongLocalizedName) {
        // return localize("buildcraft.heat.long", heat);
        // } else {
        return String.format("%.2f \u00B0C", heat);
        // }
    }

    // Calen
    public static boolean modLangResourceNotLoaded()
    {
        return new TranslatableComponent("color.clear").getString().equals("color.clear");
    }
}
/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.lib.item;

import buildcraft.lib.registry.CreativeTabManager;
import buildcraft.lib.registry.TagManager;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

import java.util.Arrays;

public class ItemBC_Neptune extends Item implements IItemBuildCraft, IForgeItem
{
    /**
     * The tag used to identify this in the {@link TagManager}
     */
    public final String idBC;

    public ItemBC_Neptune(String idBC, Item.Properties properties,Runnable... runBeforeInit)
    {
//        super(properties);
        super(properties.tab(CreativeTabManager.getTab(TagManager.getTag(idBC, TagManager.EnumTagType.CREATIVE_TAB))));
        Arrays.stream(runBeforeInit).forEach(Runnable::run); // Calen
        this.idBC = idBC;
        init();
    }

    @Override
    public String getIdBC()
    {
        return idBC;
    }

    @Override
    public final void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items)
    {
        if (allowdedIn(tab))
        {
            addSubItems(tab, items);
        }
    }

    /**
     * Identical to {@link #fillItemCategory(CreativeModeTab, NonNullList)} in every way, EXCEPT that this is only called if
     * this is actually in the given creative tab.
     *
     * @param tab The {@link CreativeModeTab} to display the items in. This is provided just in case an item has multiple
     *            subtypes, split across different tabs
     */
    protected void addSubItems(CreativeModeTab tab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this));
    }

    // Calen: from IItemBuildCraft#init
    // in 1.18.2 setUnlocalizedName setRegistryName are unvailable
    @Override
    public String getDescriptionId(ItemStack stack)
    {
        return this.unlocalizedName;
    }

    protected String unlocalizedName;

    @Override
    public void setUnlocalizedName(String unlocalizedName)
    {
        this.unlocalizedName = unlocalizedName;
    }
}
/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.core.item;

import buildcraft.api.items.IList;
import buildcraft.builders.item.ItemSnapshot;
import buildcraft.core.BCCoreGuis;
import buildcraft.core.BCCoreMenuTypes;
import buildcraft.core.list.ContainerList;
import buildcraft.lib.BCLibMenuTypes;
import buildcraft.lib.container.ContainerGuide;
import buildcraft.lib.item.ItemBC_Neptune;
import buildcraft.lib.item.ItemGuide;
import buildcraft.lib.list.ListHandler;
import buildcraft.lib.misc.AdvancementUtil;
import buildcraft.lib.misc.NBTUtilBC;
import buildcraft.lib.misc.StackUtil;
import buildcraft.lib.misc.StringUtilBC;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

//public class ItemList_BC8 extends ItemBC_Neptune implements IList
public class ItemList_BC8 extends ItemBC_Neptune implements IList, MenuProvider
{
    private static final ResourceLocation ADVANCEMENT = new ResourceLocation("buildcraftcore:list");

    // Calen
    public static final String NBT_KEY = "label";

    public ItemList_BC8(String idBC, Item.Properties properties)
    {
        super(idBC, properties);
//        setMaxStackSize(1);
    }

    @Override
//    public ActionResult<ItemStack> onItemRightClick(Level world, Player player, EnumHand hand)
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        AdvancementUtil.unlockAdvancement(player, ADVANCEMENT);
        ItemStack handItemStack = player.getItemInHand(hand);
//        BCCoreGuis.LIST.openGUI(player);
        BCCoreGuis.LIST.openGUI(player, handItemStack);
//        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, handItemStack);
    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void addModelVariants(TIntObjectHashMap<ModelResourceLocation> variants)
//    {
//        addVariant(variants, 0, "clean");
//        addVariant(variants, 1, "used");
//    }

//    @Override
//    public int getMetadata(ItemStack stack)
//    {
//        return ListHandler.hasItems(StackUtil.asNonNull(stack)) ? 1 : 0;
//    }

    @Override
    @OnlyIn(Dist.CLIENT)
//    public void addInformation(ItemStack stack, Level world, List<String> tooltip, ITooltipFlag flag)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        String name = getName_INamedItem(StackUtil.asNonNull(stack));
//        if (StringUtils.isNullOrEmpty(name)) return;
        if (StringUtilBC.isNullOrEmpty(name)) return;
//        tooltip.add(TextFormatting.ITALIC + name);
        tooltip.add(new TextComponent(ChatFormatting.ITALIC + name));
    }

    // IList

    @Override
//    public Component getName(@Nonnull ItemStack stack)
    public String getName_INamedItem(@Nonnull ItemStack stack)
    {
//        return new TextComponent(NBTUtilBC.getItemData(stack).getString("label"));
//        return new TextComponent(NBTUtilBC.getItemData(stack).getString(NBT_KEY));
        return NBTUtilBC.getItemData(stack).getString(NBT_KEY);
    }

    @Override
    public boolean setName(@Nonnull ItemStack stack, String name)
    {
//        NBTUtilBC.getItemData(stack).putString("label", name);
        NBTUtilBC.getItemData(stack).putString(NBT_KEY, name);
        return true;
    }

    @Override
    public boolean matches(@Nonnull ItemStack stackList, @Nonnull ItemStack item)
    {
        return ListHandler.matches(stackList, item);
    }

    // Calen
    public static boolean isUsed(ItemStack stack)
    {
        return ListHandler.hasItems(StackUtil.asNonNull(stack));
    }

    // Calen: GUI
    @Override
    public Component getDisplayName()
    {
        return new TextComponent("list");
    }

    @Nullable
    @Override
    public ContainerList createMenu(int id, Inventory inv, Player player)
    {
        return new ContainerList(BCCoreMenuTypes.LIST, id, player);
    }
}
/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.core.item;

import buildcraft.api.blocks.CustomPaintHelper;
import buildcraft.core.BCCoreItems;
import buildcraft.lib.item.ItemBC_Neptune;
import buildcraft.lib.misc.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPaintbrush_BC8 extends ItemBC_Neptune
{
    private static final String DAMAGE = "damage";
    private static final int MAX_USES = 64;

    private DyeColor colour;

    //    public ItemPaintbrush_BC8(String idBC, Item.Properties properties)
    public ItemPaintbrush_BC8(String idBC, Item.Properties properties, DyeColor colour)
    {
        super(idBC, properties);
        this.colour = colour;
//        setMaxStackSize(1);
//        setHasSubtypes(true);
    }

//    @Override
//    protected void addSubItems(CreativeModeTab tab, NonNullList<ItemStack> subItems)
//    {
////        // Calen: Without Color
////        ItemStack stack = new ItemStack(this, 1);
////        subItems.add(stack);
////        // Calen: 16 Colors
////        for (DyeColor color : DyeColor.values())
////        {
////            stack = new ItemStack(this, 1);
////            CompoundTag stackTag = new CompoundTag();
////            dyeColorToTag(color, stackTag);
////            stack.setTag(stackTag);
////            subItems.add(stack);
////        }
//    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void addModelVariants(TIntObjectHashMap<ModelResourceLocation> variants)
//    {
//        addVariant(variants, 0, "clean");
//        for (DyeColor colour : DyeColor.values())
//        {
//            addVariant(variants, colour.getMetadata() + 1, colour.getName());
//        }
//    }

    @Override
//    public InteractionResult onItemUse(Player player, Level world, BlockPos pos, InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ)
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        Direction facing = context.getHorizontalDirection();
        Vec3 hitPos = context.getClickLocation();
        Brush brush = new Brush(stack);
        if (brush.useOnBlock(world, pos, world.getBlockState(pos), hitPos, facing, player))
        {
            ItemStack newStack = brush.save(stack);
            if (!newStack.isEmpty())
            {
                player.setItemInHand(hand, newStack);
            }
            // We just changed the damage NBT value
            player.getInventory().setChanged();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public Brush getBrushFromStack(ItemStack stack)
    {
        return new Brush(stack);
    }

    @Override
//    public String getItemStackDisplayName(ItemStack stack)
    public Component getName(ItemStack stack)
    {
////        Brush brush = getBrushFromStack(stack);
//        String colourComponent = "";
////        if (brush.colour != null)
//        if (this.colour != null)
//        {
////            colourComponent = ColourUtil.getTextFullTooltipSpecial(brush.colour) + " ";
//            colourComponent = ColourUtil.getTextFullTooltipSpecial(this.colour) + " ";
//        }
//        return new TextComponent(colourComponent).append(new TranslatableComponent(this.unlocalizedName));

        if (LocaleUtil.modLangResourceNotLoaded())
        {
            if (this.colour != null)
            {
                MutableComponent colourComponent = ColourUtil.getTextFullTooltipSpecialComponent(this.colour).append(new TextComponent(" "));
                return colourComponent.append(new TranslatableComponent(this.unlocalizedName));
            }
            else
            {
                return new TranslatableComponent(this.unlocalizedName);
            }
        }
        else
        {
            if (this.colour != null)
            {
                String colourStr = ColourUtil.getTextFullTooltipSpecial(this.colour) + " ";
                return new TextComponent(colourStr).append(new TranslatableComponent(this.unlocalizedName));
            }
            else
            {
                return new TranslatableComponent(this.unlocalizedName);
            }
        }
    }

    // TODO Calen getFontRenderer?
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public Font getFontRenderer(ItemStack stack) {
//        return SpecialColourFontRenderer.INSTANCE;
//    }

    @Override
    public int getDamage(ItemStack stack)
    {
        Brush brush = new Brush(stack);
        return MAX_USES - brush.usesLeft;
//        return super.getDamage(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage)
    {
        // Explicitly disallow this- some core use cases mistake this for metadata and fail
    }

    @Override
    public boolean isDamaged(ItemStack stack)
    {
//        Brush brush = new Brush(stack);
//        return brush.colour != null && brush.usesLeft < MAX_USES;
        return this.colour != null && stack.getDamageValue() > 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return isDamaged(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, world, tooltip, flag);
        Brush brush = new Brush(stack);
        tooltip.add(new TextComponent(brush.usesLeft + " / " + MAX_USES));
    }

    // Calen: in 1.18.2 durability is calced by mc with #getDamage and #getMaxDamage
//    @Override
//    public double getDurabilityForDisplay(ItemStack stack)
////    public int getDamage(ItemStack stack)
//    {
//        Brush brush = new Brush(stack);
//        return 1 - (brush.usesLeft / (double) MAX_USES);
//    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return MAX_USES;
    }

    // Calen: in 1.18.2 use different item to differ brush colours
//    public DyeColor getColorFromStack(ItemStack stack)
//    {
////        CompoundTag tag = stack.getTag();
////        if (tag != null && tag.contains("colour"))
////        {
////            return DyeColor.valueOf(tag.getString("colour"));
////        }
////        else
////        {
////            return null;
////        }
//        return ColourUtil.getStackColourFromTag(stack);
//    }

    ////    @Override
    //    public int getMetadata(ItemStack stack)
//    public void dyeColorToTag(@Nullable DyeColor color, @Nonnull CompoundTag tag)
//    {
//        if (color == null)
//        {
//            // 无色的刷子
//            return;
//        }
//        else
//        {
//            tag.putString("colour", color.getName());
//        }
//    }

    /**
     * Delegate class for handling
     */
    public class Brush
    {
        public DyeColor colour;
        public int usesLeft;

        public Brush(DyeColor colour)
        {
            this.colour = colour;
            usesLeft = MAX_USES;
        }

        public Brush(ItemStack stack)
        {
//            DyeColor meta = getColorFromStack(stack);
//            DyeColor meta = ColourUtil.getStackColourFromTag(stack);
            DyeColor meta = ((ItemPaintbrush_BC8) stack.getItem()).colour;
            if (meta != null)
            {
                colour = meta;
                CompoundTag nbt = stack.getTag();
                if (nbt == null)
                {
                    usesLeft = MAX_USES;
                }
                else
                {
                    usesLeft = MAX_USES - nbt.getByte(DAMAGE);
                }
            }
            else
            {
                usesLeft = 0;
            }
        }

        @Nonnull
        public ItemStack save()
        {
            return save(StackUtil.EMPTY);
        }

        @Nonnull
        public ItemStack save(@Nonnull ItemStack existing)
        {
            ItemStack stack = existing;
//            if (existing.isEmpty() || getColorFromStack(existing) != getMeta())
            if (existing.isEmpty() || ColourUtil.getStackColourFromTag(stack) != getMeta())
            {
                stack = new ItemStack(ItemPaintbrush_BC8.this, 1);
//                CompoundTag stackTag = new CompoundTag();
//                dyeColorToTag(getMeta(), stackTag);
//                stack.setTag(stackTag);
            }
            if (usesLeft != MAX_USES && colour != null)
            {
                CompoundTag nbt = stack.getTag();
                if (nbt == null)
                {
                    nbt = new CompoundTag();
                    stack.setTag(nbt);
                }
                nbt.putByte(DAMAGE, (byte) (MAX_USES - usesLeft));
            }
            else if (usesLeft == 0)
            {
                stack = new ItemStack(BCCoreItems.colourBrushMap.get(null).get());
            }
            return stack == existing ? StackUtil.EMPTY : stack;
//            return stack;
        }

        public DyeColor getMeta()
        {
            return (usesLeft <= 0 || colour == null) ? null : colour;
        }

        public boolean useOnBlock(Level world, BlockPos pos, BlockState state, Vec3 hitPos, Direction side, Player player)
        {
            if (colour != null && usesLeft <= 0)
            {
                return false;
            }

            InteractionResult result = CustomPaintHelper.INSTANCE.attemptPaintBlock(world, pos, state, hitPos, side, colour);

            if (result == InteractionResult.SUCCESS)
            {
                ParticleUtil.showChangeColour(world, hitPos, colour);
                SoundUtil.playChangeColour(world, pos, colour);

                if (!player.isCreative())
                {
                    usesLeft--;
                }

                if (usesLeft <= 0)
                {
                    colour = null;
                    usesLeft = 0;
                }
                return true;
            }
            return false;
        }

        @Override
        public String toString()
        {
            return "[" + usesLeft + " of " + (colour == null ? "nothing" : colour.getName()) + "]";
        }
    }
}
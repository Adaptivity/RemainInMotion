/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.transport.pluggable;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.pluggable.IPipePluggableItem;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.core.ItemBuildCraft;
import buildcraft.core.utils.ColorUtils;
import buildcraft.core.utils.StringUtils;

public class ItemLens extends ItemBuildCraft implements IPipePluggableItem {

	private IIcon[] icons;

	public ItemLens() {
		super();
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		return icons[meta >= 16 ? (1 + (pass & 1)) : (1 - (pass & 1))];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public int getDye(ItemStack stack) {
		return 15 - (stack.getItemDamage() & 15);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 0 ? ColorUtils.getRGBColor(getDye(stack)) : 16777215;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return StringUtils.localize(itemstack.getItemDamage() >= 16 ? "item.Filter.name" : "item.Lens.name") + " (" + StringUtils.localize("color." + ColorUtils.getName(getDye(itemstack))) + ")";
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
	    icons = new IIcon[] {
				register.registerIcon("buildcraft:pipeLensItem0"),
				register.registerIcon("buildcraft:pipeLensItem1"),
				register.registerIcon("buildcraft:pipeFilterItem0")
		};
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List itemList) {
		for (int i = 0; i < 32; i++) {
			itemList.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public PipePluggable createPipePluggable(IPipe pipe, ForgeDirection side, ItemStack stack) {
		if (pipe.getTile().getPipeType() == IPipeTile.PipeType.ITEM) {
			return new LensPluggable(stack);
		} else {
			return null;
		}
	}
}

package me.planetguy.remaininmotion.core.interop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.ASMEventHandler;
import me.planetguy.lib.util.Debug;
import me.planetguy.remaininmotion.api.RiMRegistry;
import me.planetguy.remaininmotion.core.Core;
import me.planetguy.remaininmotion.core.interop.chickenchunks.CCHandler;
import me.planetguy.remaininmotion.core.interop.chickenchunks.CChunksEventHandler;
import me.planetguy.remaininmotion.core.interop.chickenchunks.DummyCCHandler;
import me.planetguy.remaininmotion.core.interop.chickenchunks.IChickenChunksHandler;
import me.planetguy.remaininmotion.core.interop.fmp.FMPEventHandler;
import me.planetguy.remaininmotion.drive.TileEntityCarriageController;
import me.planetguy.remaininmotion.util.general.Computers;
import net.minecraft.item.ItemStack;

public abstract class ModInteraction {
	
	public static Field		PendingBlockUpdateSetField;
	public static Method	RemovePendingBlockUpdate;

	public static boolean BCInstalled;
	public static boolean MPInstalled;

	public static void Establish() {
		BCInstalled = Loader.isModLoaded("BuildCraft|Transport");
		MPInstalled = Loader.isModLoaded("ForgeMultipart");
        
		RiMRegistry.blockMoveBus.register(new APIEventHandler());
		
        if(Loader.isModLoaded("ChickenChunks")) {
        	RiMRegistry.blockMoveBus.register(new CChunksEventHandler());
        }
		
		Wrenches.init();

		Computers.setup();

		if (Computers.load) {
			Core.CarriageControllerEntity = TileEntityCarriageController.class;
		}

		if(MPInstalled)
		{
			RiMRegistry.blockMoveBus.register(new FMPEventHandler());
		}

		PendingBlockUpdateSetField = getField(net.minecraft.world.WorldServer.class, "tickEntryQueue");

		RemovePendingBlockUpdate = getMethod(net.minecraft.world.WorldServer.class, "removeNextTickIfNeeded",
				net.minecraft.world.NextTickListEntry.class);
		
	}

	static Class getClass(String string) {
		try {
			return Class.forName(string);
		} catch (Exception e) {

		}
		return null;
	}

	private static Method getMethod(Class c, String string, Class... params) {
		try {
			return c.getDeclaredMethod(string, params);
		} catch (Exception e) {

		}
		return null;
	}

	private static Field getField(Class c, String string) {
		try {
			return c.getField(string);
		} catch (Exception e) {

		}
		return null;
	}

	public static abstract class Wrenches {

		static ArrayList<Class>	wrenchClasses	= new ArrayList<Class>();

		public static void init() {
			for (String className : new String[] {
					// can add or remove FQCNs here
					// should probably prefer API interface names where possible
					// note that many mods implement BC wrench API only if BC is
					// installed
					"buildcraft.api.tools.IToolWrench", // Buildcraft
					"resonant.core.content.ItemScrewdriver", // Resonant
					// Induction
					"ic2.core.item.tool.ItemToolWrench", // IC2
					"ic2.core.item.tool.ItemToolWrenchElectric", // IC2 (more)
					"mrtjp.projectred.api.IScrewdriver", // Project Red
					"mods.railcraft.api.core.items.IToolCrowbar", // Railcraft
					"com.bluepowermod.items.ItemScrewdriver", // BluePower
					"cofh.api.item.IToolHammer", // Thermal Expansion and
					// compatible
					"appeng.items.tools.quartz.ToolQuartzWrench", // Applied
					// Energistics
					"crazypants.enderio.api.tool.ITool", // Ender IO
					"mekanism.api.IMekWrench", // Mekanism
			}) {
				try {
					wrenchClasses.add(Class.forName(className));
				} catch (ClassNotFoundException e) {
					Debug.dbg("Failed to load wrench class " + className);
				}
			}
		}

		public static boolean isAWrench(ItemStack stk) {
			for (Class c : wrenchClasses) {
				if (stk != null && stk.getItem() != null
						&& (c.isInstance(stk.getItem()))) { return true; }
			}
			return false;
		}

	}
}

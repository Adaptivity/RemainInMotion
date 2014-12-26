package me.planetguy.remaininmotion.core;

import me.planetguy.lib.PLHelper;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

@cpw.mods.fml.common.Mod(modid = Mod.Handle, name = Mod.Title, version = Mod.Version, dependencies = "required-after:planetguyLib")
public class Mod {
	public static final String	Namespace	= "me.planetguy.remaininmotion.";

	public static final String	Handle		= "JAKJ_RedstoneInMotion";

	public static final String	Title		= "Remain In Motion";

	public static final String	Version		= "2.3.3";

	public static final String	Channel		= "JAKJ_RIM";

	public static PLHelper		plHelper	= new PLHelper(Handle);

	@cpw.mods.fml.common.Mod.EventHandler
	public void PreInit(cpw.mods.fml.common.event.FMLPreInitializationEvent Event) {
		(new Configuration(Event.getSuggestedConfigurationFile())).Process();

		Core.HandlePreInit();
	}

	@cpw.mods.fml.common.Mod.EventHandler
	public void Init(cpw.mods.fml.common.event.FMLInitializationEvent Event) {
		Core.HandleInit();
	}

	@cpw.mods.fml.common.Mod.EventHandler
	public void PostInit(cpw.mods.fml.common.event.FMLPostInitializationEvent Event) {
		ClientSetupProxy.Instance.Execute();

		Core.HandlePostInit();
	}

	@cpw.mods.fml.common.Mod.EventHandler
	public void ServerStopping(cpw.mods.fml.common.event.FMLServerStoppingEvent Event) {
		Core.HandleServerStopping();
	}

	@EventHandler
	public void handleIMCMessage(FMLInterModComms.IMCEvent event) {
		for (final FMLInterModComms.IMCMessage message : event.getMessages()) {
			if (message.key.equals("blacklist")) {
				String block = message.getStringValue();
				try {
					int id = Integer.parseInt(block);

				} catch (NumberFormatException e) {
					System.err.println("Recieved bad blacklist request from " + message.getSender() + ": " + block);
				}
			}
		}
	}
}

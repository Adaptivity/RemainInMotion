/*
import me.planetguy.remaininmotion.CarriageMotionException
import me.planetguy.remaininmotion.CarriageObstructionException
import me.planetguy.remaininmotion.CarriagePackage
import me.planetguy.remaininmotion.Directions
import me.planetguy.remaininmotion.util.MultiTypeCarriageUtil
import net.minecraft.tileentity.TileEntity
import me.planetguy.remaininmotion.util.general.TComputerInterface._
import me.planetguy.util.ECIExpose

object Commands extends Enumeration {

	val move = new Commands()

	val anchored_move = new Commands()

	val check_anchored_move = new Commands()

	val unanchored_move = new Commands()

	val check_unanchored_move = new Commands()

	class Commands extends Val

	implicit def convertValue(v: Value): Commands = v.asInstanceOf[Commands]
}

class CarriageControllerEntity extends CarriageDriveEntity with EasyComputerInterfaceUtil {

  var Simulating: Boolean = _

  var MotionDirection: Directions = _

  var Error: CarriageMotionException = _

  var Obstructed: Boolean = _

  var ObstructionX: Int = _

  var ObstructionY: Int = _

  var ObstructionZ: Int = _
  
  val motionThread=java.util.concurrent.Executors.newSingleThreadExecutor();

  override def HandleToolUsage(Side: Int, Sneaking: Boolean) {
  }

  override def updateEntity() {
    this.synchronized {
      if (worldObj.isRemote) {
        return
      }
      if (Stale) {
        HandleNeighbourBlockChange()
      }
      if (MotionDirection == null) {
        return
      }
      try {
        Move()
      } catch {
        case error: CarriageMotionException => {
          this.Error = error
          if (Error.isInstanceOf[CarriageObstructionException]) {
            Obstructed = true
            ObstructionX = Error.asInstanceOf[CarriageObstructionException].X
            ObstructionY = Error.asInstanceOf[CarriageObstructionException].Y
            ObstructionZ = Error.asInstanceOf[CarriageObstructionException].Z
          }
        }
      }
      MotionDirection = null
      notify()
    }
  }

  var anchored: Boolean = _

  override def Anchored(): Boolean = (anchored)

  def AssertArgumentCount(Arguments: Array[Any], ArgumentCount: Int) {
    if (Arguments.length < ArgumentCount) {
      throw (new Exception("too few arguments"))
    }
    if (Arguments.length > ArgumentCount) {
      throw (new Exception("too many arguments"))
    }
  }

  def ParseBooleanArgument(Argument: Any, Label: String): Boolean = {
    Argument.asInstanceOf[java.lang.Boolean]
  }

  def ParseDirectionArgument(Argument: Any): Directions = {
    if (Argument.isInstanceOf[java.lang.Double]) {
      return (Directions.values()(Math.round(Argument.asInstanceOf[java.lang.Double]).toInt))
    }
    try {
      val Direction = Argument.asInstanceOf[String]
      if (Direction.equalsIgnoreCase("down") || Direction.equalsIgnoreCase("negy")) {
        return (Directions.NegY)
      }
      if (Direction.equalsIgnoreCase("up") || Direction.equalsIgnoreCase("posy")) {
        return (Directions.PosY)
      }
      if (Direction.equalsIgnoreCase("north") || Direction.equalsIgnoreCase("negz")) {
        return (Directions.NegZ)
      }
      if (Direction.equalsIgnoreCase("south") || Direction.equalsIgnoreCase("posz")) {
        return (Directions.PosZ)
      }
      if (Direction.equalsIgnoreCase("west") || Direction.equalsIgnoreCase("negx")) {
        return (Directions.NegX)
      }
      if (Direction.equalsIgnoreCase("east") || Direction.equalsIgnoreCase("posx")) {
        return (Directions.PosX)
      }
    } catch {
      case throwable: Throwable => 
    }
    throw (new Exception("invalid direction"))
  }

  def SetupMotion(MotionDirection: Directions, Simulating: Boolean, anchored: Boolean) {
	  this.synchronized{
		  this.Simulating = Simulating
		  this.anchored = anchored
		  this.MotionDirection = MotionDirection
	  }
  }

  @ECIExpose
  def move(arguments: Array[Any]): Array[Any] = {
	var ret: Array[Any]=null
	motionThread.submit(new Runnable(){
		def run(){
    AssertArgumentCount(arguments, 3)
    SetupMotion(ParseDirectionArgument(arguments(0)), ParseBooleanArgument(arguments(1), "simulation"), ParseBooleanArgument(arguments(2), "anchoring"))
    Error = null
    Obstructed = false
    try {
      while (MotionDirection != null) {
        wait()
      }
    } catch {
      case exc: Exception => 
    }
    if (Error == null) {
      ret= (Array(true))
    }
    if (Obstructed == false) {
      ret= (Array(false, Error.getMessage))
    }
    ret=(Array(false, Error.getMessage, ObstructionX, ObstructionY, ObstructionZ))
  }
	})
	ret
  }

  def Move() {
    if (Active) {
      throw (new CarriageMotionException("controller already active"))
    }
    if (CarriageDirection == null) {
      throw (new CarriageMotionException("no carriage or too many carriages attached to controller"))
    }
    val Package = PreparePackage(MotionDirection)
    if (Simulating) {
      return
    }
    InitiateMotion(Package)
  }

  override def GeneratePackage(carriage: TileEntity, CarriageDirection: Directions, MotionDirection: Directions): CarriagePackage = {
    var Package: CarriagePackage = null
    if (Anchored) {
      if (MotionDirection == CarriageDirection) {
        throw (new CarriageMotionException("cannot push carriage away from controller in anchored mode"))
      }
      if (MotionDirection == CarriageDirection.Opposite()) {
        throw (new CarriageMotionException("cannot pull carriage into controller in anchored mode"))
      }
      Package = new CarriagePackage(this, carriage, MotionDirection)
      MultiTypeCarriageUtil.fillPackage(Package, carriage)
      if (Package.Body.contains(Package.DriveRecord)) {
        throw (new CarriageMotionException("carriage is attempting to move controller while in anchored mode"))
      }
      if (Package.Body.contains(Package.DriveRecord.NextInDirection(MotionDirection.Opposite()))) {
        throw (new CarriageMotionException("carriage is obstructed by controller while in anchored mode"))
      }
    } else {
      Package = new CarriagePackage(this, carriage, MotionDirection)
      Package.AddBlock(Package.DriveRecord)
      if (MotionDirection != CarriageDirection) {
        Package.AddPotentialObstruction(Package.DriveRecord.NextInDirection(MotionDirection))
      }
      MultiTypeCarriageUtil.fillPackage(Package, carriage)
    }
    Package.Finalize()
    (Package)
  }
  
  


Original Java:*/
package me.planetguy.remaininmotion.drive ;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import me.planetguy.lib.util.Lang;
import me.planetguy.remaininmotion.CarriageMotionException;
import me.planetguy.remaininmotion.CarriageObstructionException;
import me.planetguy.remaininmotion.CarriagePackage;
import me.planetguy.remaininmotion.Directions;
import me.planetguy.remaininmotion.core.Mod;
import me.planetguy.remaininmotion.util.MultiTypeCarriageUtil;
import me.planetguy.remaininmotion.util.general.ECIExpose;
import net.minecraft.tileentity.TileEntity;

@Optional.InterfaceList(value = { @Interface(iface = "", modid = "") })
public class CarriageControllerEntity extends CarriageDriveEntity implements IPeripheral
{
	
	public Object ThreadLockObject = new Object ( ) ;
	
	public boolean Simulating ;

	public Directions MotionDirection ;

	public CarriageMotionException Error ;

	public boolean Obstructed ;

	public int ObstructionX ;
	public int ObstructionY ;
	public int ObstructionZ ;

	@Override
	public void HandleToolUsage ( int Side , boolean Sneaking )
	{
		
	}

	@Override
	public synchronized void updateEntity ( )
	{
		if ( worldObj . isRemote )
		{
			return ;
		}

		if ( Stale )
		{
			HandleNeighbourBlockChange ( ) ;
		}

		if ( MotionDirection == null )
		{
			return ;
		}

		try
		{
			Move ( ) ;
		}
		catch ( CarriageMotionException Error )
		{
			this . Error = Error ;

			if ( Error instanceof CarriageObstructionException )
			{
				Obstructed = true ;

				ObstructionX = ( ( CarriageObstructionException ) Error ) . X ;
				ObstructionY = ( ( CarriageObstructionException ) Error ) . Y ;
				ObstructionZ = ( ( CarriageObstructionException ) Error ) . Z ;
			}
		}

		MotionDirection = null ;

	}

	public boolean Anchored ;

	@Override
	public boolean Anchored ( )
	{
		return ( Anchored ) ;
	}

	public enum Commands
	{
		move ,
		anchored_move ,
		check_anchored_move ,
		unanchored_move ,
		check_unanchored_move ;
	}

	public void AssertArgumentCount ( Object [ ] Arguments , int ArgumentCount ) throws Exception
	{
		if ( Arguments . length < ArgumentCount )
		{
			throw ( new Exception ( "too few arguments" ) ) ;
		}

		if ( Arguments . length > ArgumentCount )
		{
			throw ( new Exception ( "too many arguments" ) ) ;
		}
	}

	public boolean ParseBooleanArgument ( Object Argument , String Label ) throws Exception
	{
		try
		{
			return ( ( Boolean ) Argument ) ;
		}
		catch ( Throwable Throwable )
		{
			throw ( new Exception ( "invalid " + Label + " flag" ) ) ;
		}
	}

	public Directions ParseDirectionArgument ( Object Argument ) throws Exception
	{
		if ( Argument instanceof Double )
		{
			try
			{
				return ( Directions . values ( ) [ ( int ) Math . round ( ( Double ) Argument ) ] ) ;
			}
			catch ( Throwable Throwable )
			{
				throw ( new Exception ( "direction index out of range" ) ) ;
			}
		}

		try
		{
			String Direction = ( String ) Argument ;

			if ( Direction . equalsIgnoreCase ( "down" ) || Direction . equalsIgnoreCase ( "negy" ) )
			{
				return ( Directions . NegY ) ;
			}

			if ( Direction . equalsIgnoreCase ( "up" ) || Direction . equalsIgnoreCase ( "posy" ) )
			{
				return ( Directions. PosY ) ;
			}

			if ( Direction . equalsIgnoreCase ( "north" ) || Direction . equalsIgnoreCase ( "negz" ) )
			{
				return ( Directions . NegZ ) ;
			}

			if ( Direction . equalsIgnoreCase ( "south" ) || Direction . equalsIgnoreCase ( "posz" ) )
			{
				return ( Directions . PosZ ) ;
			}

			if ( Direction . equalsIgnoreCase ( "west" ) || Direction . equalsIgnoreCase ( "negx" ) )
			{
				return ( Directions . NegX ) ;
			}

			if ( Direction . equalsIgnoreCase ( "east" ) || Direction . equalsIgnoreCase ( "posx" ) )
			{
				return ( Directions . PosX ) ;
			}
		}
		catch ( Throwable Throwable )
		{
		}

		throw ( new Exception ( "invalid direction" ) ) ;
	}

	public void SetupMotion ( Directions MotionDirection , boolean Simulating , boolean Anchored )
	{
		this . MotionDirection = MotionDirection ;

		this . Simulating = Simulating ;

		this . Anchored = Anchored ;
	}

	@ECIExpose
	public Object[] move(Object[] Arguments ) throws Exception{

		notify();
		
		AssertArgumentCount ( Arguments , 3 ) ;

		SetupMotion ( ParseDirectionArgument ( Arguments [ 0 ] ) , ParseBooleanArgument ( Arguments [ 1 ] , "simulation" ) , ParseBooleanArgument ( Arguments [ 2 ] , "anchoring" ) ) ;

		Error = null ;

		Obstructed = false ;

		try
		{
			while ( MotionDirection != null )
			{
				wait ( ) ;
			}
		
		}
		catch ( Exception exc )
		{
			//exc.printStackTrace();
		}

		if ( Error == null )
		{
			return ( new Object [ ] { true } ) ;
		}

		if ( Obstructed == false )
		{
			return ( new Object [ ] { false , Error . getMessage ( ) } ) ;
		}

		return ( new Object [ ] { false , Error . getMessage ( ) , ObstructionX , ObstructionY , ObstructionZ } ) ;
	}

	public void Move ( ) throws CarriageMotionException
	{
		if ( Active )
		{
			throw ( new CarriageMotionException ( Lang.translate(Mod.Handle+".active") ) ) ;
		}

		if ( CarriageDirection == null )
		{
			throw ( new CarriageMotionException ( Lang.translate(Mod.Handle+".noValidCarriage") ) ) ;
		}

		CarriagePackage Package = PreparePackage ( MotionDirection ) ;

		if ( Simulating )
		{
			return ;
		}

		InitiateMotion ( Package ) ;
	}

	@Override
	public CarriagePackage GeneratePackage ( TileEntity carriage , Directions CarriageDirection , Directions MotionDirection ) throws CarriageMotionException
	{
		CarriagePackage Package ;

		if ( Anchored )
		{
			if ( MotionDirection == CarriageDirection )
			{
				throw ( new CarriageMotionException ( Lang.translate(Mod.Handle+".noPushWhenAnchored") ) ) ;
			}

			if ( MotionDirection == CarriageDirection . Opposite ( ) )
			{
				throw ( new CarriageMotionException ( Lang.translate(Mod.Handle+".noPullWhenAnchored") ) ) ;
			}

			Package = new CarriagePackage ( this , carriage , MotionDirection ) ;
			
			MultiTypeCarriageUtil.fillPackage(Package, carriage ) ;

			if ( Package . Body . contains ( Package . DriveRecord ) )
			{
				throw ( new CarriageMotionException ( "carriage is attempting to move controller while in anchored mode" ) ) ;
			}

			if ( Package . Body . contains ( Package . DriveRecord . NextInDirection ( MotionDirection . Opposite ( ) ) ) )
			{
				throw ( new CarriageMotionException ( "carriage is obstructed by controller while in anchored mode" ) ) ;
			}
		}
		else
		{
			Package = new CarriagePackage ( this , carriage , MotionDirection ) ;

			Package . AddBlock ( Package . DriveRecord ) ;

			if ( MotionDirection != CarriageDirection )
			{
				Package . AddPotentialObstruction ( Package . DriveRecord . NextInDirection ( MotionDirection ) ) ;
			}
			
			MultiTypeCarriageUtil.fillPackage(Package, carriage ) ;

		}

		Package . Finalize ( ) ;

		return ( Package ) ;
	}
	
	/* =====================================
	 * Begin ECI methods
	 * =====================================
	 */
	
	private Object[] status;

	public Method[] methods(){
		ArrayList<Method> methods=new ArrayList<Method>();
		for(Method m:this.getClass().getMethods()){
			if(m.isAnnotationPresent(ECIExpose.class)){
				methods.add(m);
			}
		}
		return methods.toArray(new Method[0]);
	}
	
	/* =====================================
	 * ComputerCraft integration
	 * =====================================
	 */
	
	
	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String[] getMethodNames() {
		Method[] methods=methods();
		String[] names=new String[methods.length];
		for(int i=0; i<names.length; i++){
			names[i]=methods[i].getName();
		}
		return names;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		try{
			Method m=methods()[method];
			m.invoke(this, arguments);
			wait();
			return this.status;
		}catch(Exception e){
			
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other==this;
	}

}


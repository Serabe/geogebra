package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoScriptAction;
import geogebra.common.main.MyError;

public abstract class CmdScripting extends CommandProcessor implements CmdScriptingInterface{

	

	public CmdScripting(Kernel kernel) {
		super(kernel);
		// TODO Auto-generated constructor stub
	}
	public abstract void perform(Command c);
		
	
	@Override
	public final GeoElement[] process (Command c) throws MyError,
			CircularDefinitionException {
		GeoScriptAction sa =  new GeoScriptAction(cons,this,c);	
		return new GeoElement[] {sa};
	}

}

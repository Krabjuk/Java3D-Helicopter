import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;

public class Mainrotor
{
	private int objDivisions = 100;
	private int primFlags = 1;
	
	private BranchGroup objRoot = null;
	private Transform3D movement = new Transform3D();
	private Appearance appearance = new Appearance();
	
	private Cylinder axis;
	private Box connector1;
	private Box connector2;
	private Box blade1;
	private Box blade2;
	
	Mainrotor()
	{
		objRoot = new BranchGroup();
		
		// Axis
		movement.set(new Vector3f(0.0f, 0.0f, 0.0f));
		TransformGroup axisMovement = new TransformGroup(movement);
		axis = new Cylinder(0.02f, 0.5f, primFlags, objDivisions, objDivisions, appearance);
		objRoot.addChild(axisMovement);
		axisMovement.addChild(axis);
				
		// Connector 1
		movement.set(new Vector3f(-0.3f, 0.25f, 0.0f));
		TransformGroup connector1Movement = new TransformGroup(movement);
		connector1 = new Box(0.3f, 0.02f, 0.02f, primFlags, appearance);
		objRoot.addChild(connector1Movement);
		connector1Movement.addChild(connector1);
		
		// Connector 2
		movement.set(new Vector3f(0.3f, 0.25f, 0.0f));
		TransformGroup connector2Movement = new TransformGroup(movement);
		connector2 = new Box(0.3f, 0.02f, 0.02f, primFlags, appearance);
		objRoot.addChild(connector2Movement);
		connector2Movement.addChild(connector2);
		
		// Blade 1
		movement.set(new Vector3f(-2.45f, 0.25f, -0.06f));
		TransformGroup blade1Movement = new TransformGroup(movement);
		blade1 = new Box(2.3f, 0.01f, 0.1f, primFlags, appearance);
		objRoot.addChild(blade1Movement);
		blade1Movement.addChild(blade1);
		
		// Blade 2
		movement.set(new Vector3f(2.45f, 0.25f, 0.06f));
		TransformGroup blade2Movement = new TransformGroup(movement);
		blade2 = new Box(2.3f, 0.01f, 0.1f, primFlags, appearance);
		objRoot.addChild(blade2Movement);
		blade2Movement.addChild(blade2);
	}
	
	BranchGroup getScene()
	{
		return objRoot;
	}
}

import java.awt.event.*;

import javax.media.j3d.*;
import javax.swing.Timer;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

public class Fuselage implements ActionListener
{
	private Timer timer  = new Timer(50, this);
	
	private int objDivisions = 100;
	private int primFlags = 1;
	
	private BranchGroup objRoot = null;
	private Transform3D rotation = new Transform3D();
	private Transform3D movement = new Transform3D();
	private Appearance appearance = new Sphere().getAppearance();//new Appearance();
	
	private Sphere noseTip;
	private Cylinder neckBottom;
	private Cone neckTop;
	private Cylinder bodyMain;
	private Cone tailFront;
	private Cylinder tailMiddle;
	private Sphere tailEnd;
	private Box tailFin;
	
	private Mainrotor mainrotor;
	private TransformGroup mainRotorRotation = new TransformGroup();
	private double mainRotorBladePosition = 0.0;
	private double mainRotorSpeed = 0.0; 
	
	private Tailrotor tailrotor;
	private TransformGroup tailRotorRotation = new TransformGroup();
	private double tailRotorBladePosition = 0.0;
	private double tailRotorSpeed = 0.0;
	
	private SpotLight frontLight;
		
	Fuselage()
	{
		objRoot = new BranchGroup();
		
		frontLight = new SpotLight(new Color3f(1.5f, 1.0f, 1.0f),
				new Point3f(0.0f, -0.6f, -2.25f),	// position
				new Point3f(0, 0, 5),				// attenuation
				new Vector3f(0.0f, 0.0f, -1.0f),	// direction
                (float)Math.PI,						// spreadAngle in RAD
                5.0f);								// concentration
		frontLight.setCapability(SpotLight.ALLOW_STATE_WRITE);
		frontLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		objRoot.addChild(frontLight);
		
		SpotLight leftLight = new SpotLight(new Color3f(1.5f, 0.0f, 0.0f),
				new Point3f(-0.35f, -0.6f, 0.0f),	// position
				new Point3f(0, 0, 20),				// attenuation
				new Vector3f(-1.0f, 0.0f, 0.0f),	// direction
				(float)Math.PI,						// spreadAngle in RAD
				1.0f);								// concentration
		leftLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 0.2));
		
		SpotLight rightLight = new SpotLight(new Color3f(0.0f, 1.5f, 0.0f),
				new Point3f(0.35f, -0.6f, 0.0f),	// position
				new Point3f(0, 0, 20),				// attenuation
				new Vector3f(1.0f, 0.0f, 0.0f),		// direction
				(float)Math.PI,						// spreadAngle in RAD
				1.0f);								// concentration
		rightLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 0.2));
		
		// Nose tip
		rotation.rotX(Math.PI / 2.0);
		TransformGroup noseTipRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, -0.4f, -1.8f));
		TransformGroup noseTipMovement = new TransformGroup(movement);
		noseTip = new Sphere(0.4f, primFlags, objDivisions, appearance);
		objRoot.addChild(noseTipMovement);
		noseTipMovement.addChild(noseTipRotation);
		noseTipRotation.addChild(noseTip);
		
		// Neck bottom
		rotation.rotX(Math.PI / 2.0);
		TransformGroup neckBottomRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, -0.4f, -1.2f));
		TransformGroup neckBottomMovement = new TransformGroup(movement);
		neckBottom = new Cylinder(0.4f, 1.2f, primFlags, objDivisions, objDivisions, appearance);
		objRoot.addChild(neckBottomMovement);
		neckBottomMovement.addChild(neckBottomRotation);
		neckBottomRotation.addChild(neckBottom);
		
		// Neck top
		rotation.rotX(-Math.PI / 2.0);
		TransformGroup neckTopRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, -1.0f));
		TransformGroup neckTopMovement = new TransformGroup(movement);
		neckTop = new Cone(0.8f, 0.6f, primFlags, objDivisions, objDivisions, appearance);
		objRoot.addChild(neckTopMovement);
		neckTopMovement.addChild(neckTopRotation);
		neckTopRotation.addChild(neckTop);
		
		// Body main
		rotation.rotX(Math.PI / 2.0);
		TransformGroup bodyMainRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, 0.0f));
		TransformGroup bodyMainMovement = new TransformGroup(movement);
		bodyMain = new Cylinder(0.8f, 1.4f, primFlags, objDivisions, objDivisions, appearance);
		objRoot.addChild(bodyMainMovement);
		bodyMainMovement.addChild(bodyMainRotation);
		bodyMainRotation.addChild(bodyMain);
		
		// Tail front
		rotation.rotX(Math.PI / 2.0);
		TransformGroup tailFrontRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, 1.1f));
		TransformGroup tailFrontMovement = new TransformGroup(movement);
		tailFront = new Cone(0.8f, 0.8f, primFlags, objDivisions, objDivisions, appearance);
		objRoot.addChild(tailFrontMovement);
		tailFrontMovement.addChild(tailFrontRotation);
		tailFrontRotation.addChild(tailFront);
		
		// Tail middle
		rotation.rotX(Math.PI / 2.0);
		TransformGroup tailMiddleRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, 2.1f));
		TransformGroup tailMiddleMovement = new TransformGroup(movement);
		tailMiddle = new Cylinder(0.3f, 1.9f, primFlags, objDivisions, objDivisions, appearance);
		objRoot.addChild(tailMiddleMovement);
		tailMiddleMovement.addChild(tailMiddleRotation);
		tailMiddleRotation.addChild(tailMiddle);
		tailMiddleRotation.addChild(leftLight);
		tailMiddleRotation.addChild(rightLight);
		
		// Tail end
		rotation.rotX(Math.PI / 2.0);
		TransformGroup tailEndRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, 3.0f));
		TransformGroup tailEndMovement = new TransformGroup(movement);
		tailEnd = new Sphere(0.3f, primFlags, objDivisions, appearance);
		objRoot.addChild(tailEndMovement);
		tailEndMovement.addChild(tailEndRotation);
		tailEndRotation.addChild(tailEnd);
		
		// Tail fin
		rotation.rotX(Math.PI / 4.0);
		TransformGroup tailFinRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.3f, 3.0f));
		TransformGroup tailFinMovement = new TransformGroup(movement);
		tailFin = new Box(0.04f, 0.55f, 0.3f, primFlags, appearance);
		objRoot.addChild(tailFinMovement);
		tailFinMovement.addChild(tailFinRotation);
		tailFinRotation.addChild(tailFin);
		
		// Main rotor
		mainrotor = new Mainrotor();
		mainRotorRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		movement.set(new Vector3f(0.0f, 0.8f, 0.0f));
		TransformGroup mainRotorMovement = new TransformGroup(movement);
		objRoot.addChild(mainRotorMovement);
		mainRotorMovement.addChild(mainRotorRotation);
		mainRotorRotation.addChild(mainrotor.getScene());
		
		// Tail rotor
		tailrotor = new Tailrotor();
		tailRotorRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		movement.set(new Vector3f(-0.3f, 0.0f, 3.0f));
		TransformGroup tailRotorMovement = new TransformGroup(movement);
		objRoot.addChild(tailRotorMovement);
		tailRotorMovement.addChild(tailRotorRotation);
		tailRotorRotation.addChild(tailrotor.getScene());
		
		timer.start();
	}
	
	BranchGroup getScene()
	{
		return objRoot;
	}
	
	void setMainRotorSpeed(double deg_each_tick)
	{
		mainRotorSpeed = deg_each_tick;
	}
	
	double getMainRotorSpeed()
	{
		return mainRotorSpeed;
	}
	
	void setTailRotorSpeed(double deg_each_tick)
	{
		tailRotorSpeed = deg_each_tick;
	}
	
	double getTailRotorSpeed()
	{
		return tailRotorSpeed;
	}
	
	boolean getStateFrontLight()
	{
		return frontLight.getEnable();
	}
	
	void switchOnFrontLight()
	{
		frontLight.setEnable(true);
	}
	
	void switchOffFrontLight()
	{
		frontLight.setEnable(false);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		mainRotorBladePosition += mainRotorSpeed;
		if(mainRotorBladePosition > 360) mainRotorBladePosition -= 360;
		rotation.rotY(-mainRotorBladePosition * 2 * Math.PI / 360);
		mainRotorRotation.setTransform(rotation);
		
		tailRotorBladePosition += tailRotorSpeed;
		if(tailRotorBladePosition > 360) tailRotorBladePosition -= 360;
		rotation.rotX(tailRotorBladePosition * 2 * Math.PI / 360);
		tailRotorRotation.setTransform(rotation);
	}
}

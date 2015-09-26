import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.*;
import javax.swing.Timer;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

public class Fuselage implements ActionListener
{
	private Timer timer  = new Timer(40, this);
	
	private int objDivisions = 100;
	private int primFlags = 1;
	
	private BranchGroup objRoot = null;
	private Transform3D rotation = new Transform3D();
	//private Transform3D rotaHelp = new Transform3D();
	private Transform3D movement = new Transform3D();
	private TransformGroup mainRotation = new TransformGroup();
		
	private Appearance appearance = new Appearance();
	
	private Material material = new Material();
	private ColoringAttributes colorAttributes = new ColoringAttributes();
	
	private Sphere noseTip;
	private Cylinder cockpitBottom;
	private Cone cockpitTop;
	private Cylinder bodyMain;
	private Cone tailFront;
	private Cylinder tailMiddle;
	private Sphere tailEnd;
	private Box tailFin;
	
	private boolean engineState = false;
	private boolean flyState = false;
	
	private double mainRotationSpeed = 1.0;
	private double mainRotationAngle = 0.0;
	private Mainrotor mainrotor;
	private TransformGroup mainRotorRotation = new TransformGroup();
	
	private double tailRotationSpeed = 1.0;
	private double tailRotationAngle = 0.0;
	private Tailrotor tailrotor;
	private TransformGroup tailRotorRotation = new TransformGroup();
	
	private SpotLight frontLight;
		
	Fuselage()
	{
		objRoot = new BranchGroup();
		
		colorAttributes.setColor(new Color3f(Color.LIGHT_GRAY));
		colorAttributes.setShadeModel(ColoringAttributes.NICEST);
		appearance.setColoringAttributes(colorAttributes);
		
		material.setDiffuseColor(new Color3f(Color.LIGHT_GRAY));	// indirect lighting color
		material.setSpecularColor(new Color3f(Color.LIGHT_GRAY));	// direct lighting color
		material.setShininess(80.0f);	// intensity of direct lighting [1 - 128, default 64]
		appearance.setMaterial(material);
				
		frontLight = new SpotLight(new Color3f(1.5f, 1.5f, 0.0f),
				new Point3f(0.0f, -0.3f, 0.4f),		// position
				new Point3f(0, 0, 5),				// attenuation
				new Vector3f(0.0f, 0.0f, -1.0f),	// direction
                (float)Math.PI,						// spreadAngle in RAD
                5.0f);								// concentration
		frontLight.setCapability(SpotLight.ALLOW_STATE_WRITE);
		frontLight.setInfluencingBounds(MainWindow.worldBounds);
		
		SpotLight leftLight = new SpotLight(new Color3f(1.5f, 0.0f, 0.0f),
				new Point3f(-0.35f, -0.6f, 0.0f),	// position
				new Point3f(0, 0, 20),				// attenuation
				new Vector3f(-1.0f, 0.0f, 0.0f),	// direction
				(float)Math.PI,						// spreadAngle in RAD
				1.0f);								// concentration
		leftLight.setInfluencingBounds(MainWindow.worldBounds);
		
		SpotLight rightLight = new SpotLight(new Color3f(0.0f, 1.5f, 0.0f),
				new Point3f(0.35f, -0.6f, 0.0f),	// position
				new Point3f(0, 0, 20),				// attenuation
				new Vector3f(1.0f, 0.0f, 0.0f),		// direction
				(float)Math.PI,						// spreadAngle in RAD
				1.0f);								// concentration
		rightLight.setInfluencingBounds(MainWindow.worldBounds);
		
		// Nose
		rotation.rotX(Math.PI / 2.0);
		TransformGroup noseTipRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, -0.4f, -1.8f));
		TransformGroup noseTipMovement = new TransformGroup(movement);
		noseTip = new Sphere(0.4f, primFlags, objDivisions, appearance);
		mainRotation.addChild(noseTipMovement);
		noseTipMovement.addChild(noseTipRotation);
		noseTipRotation.addChild(noseTip);
		noseTipRotation.addChild(frontLight);
		
		// Cockpit bottom
		rotation.rotX(Math.PI / 2.0);
		TransformGroup cockpitBottomRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, -0.4f, -1.2f));
		TransformGroup cockpitBottomMovement = new TransformGroup(movement);
		cockpitBottom = new Cylinder(0.4f, 1.2f, primFlags, objDivisions, objDivisions, appearance);
		mainRotation.addChild(cockpitBottomMovement);
		cockpitBottomMovement.addChild(cockpitBottomRotation);
		cockpitBottomRotation.addChild(cockpitBottom);
		
		// Cockpit top
		rotation.rotX(-Math.PI / 2.0);
		TransformGroup cockpitTopRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, -1.1f));
		TransformGroup cockpitTopMovement = new TransformGroup(movement);
		cockpitTop = new Cone(0.8f, 0.8f, primFlags, objDivisions, objDivisions, appearance);
		mainRotation.addChild(cockpitTopMovement);
		cockpitTopMovement.addChild(cockpitTopRotation);
		cockpitTopRotation.addChild(cockpitTop);
		
		// Body main
		rotation.rotX(Math.PI / 2.0);
		TransformGroup bodyMainRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, 0.0f));
		TransformGroup bodyMainMovement = new TransformGroup(movement);
		bodyMain = new Cylinder(0.8f, 1.4f, primFlags, objDivisions, objDivisions, appearance);
		mainRotation.addChild(bodyMainMovement);
		bodyMainMovement.addChild(bodyMainRotation);
		bodyMainRotation.addChild(bodyMain);
		
		// Tail front
		rotation.rotX(Math.PI / 2.0);
		TransformGroup tailFrontRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, 1.1f));
		TransformGroup tailFrontMovement = new TransformGroup(movement);
		tailFront = new Cone(0.8f, 0.8f, primFlags, objDivisions, objDivisions, appearance);
		mainRotation.addChild(tailFrontMovement);
		tailFrontMovement.addChild(tailFrontRotation);
		tailFrontRotation.addChild(tailFront);
		
		// Tail middle
		rotation.rotX(Math.PI / 2.1);
		TransformGroup tailMiddleRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.0f, 2.1f));
		TransformGroup tailMiddleMovement = new TransformGroup(movement);
		tailMiddle = new Cylinder(0.3f, 1.95f, primFlags, objDivisions, objDivisions, appearance);
		mainRotation.addChild(tailMiddleMovement);
		tailMiddleMovement.addChild(tailMiddleRotation);
		tailMiddleRotation.addChild(tailMiddle);
		tailMiddleRotation.addChild(leftLight);
		tailMiddleRotation.addChild(rightLight);
		
		// Tail end
		rotation.rotX(Math.PI / 2.0);
		TransformGroup tailEndRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.07f, 3.07f));
		TransformGroup tailEndMovement = new TransformGroup(movement);
		tailEnd = new Sphere(0.3f, primFlags, objDivisions, appearance);
		mainRotation.addChild(tailEndMovement);
		tailEndMovement.addChild(tailEndRotation);
		tailEndRotation.addChild(tailEnd);
		
		// Tail fin
		rotation.rotX(Math.PI / 4.0);
		TransformGroup tailFinRotation = new TransformGroup(rotation);
		movement.set(new Vector3f(0.0f, 0.37f, 3.0f));
		TransformGroup tailFinMovement = new TransformGroup(movement);
		tailFin = new Box(0.04f, 0.55f, 0.3f, primFlags, appearance);
		mainRotation.addChild(tailFinMovement);
		tailFinMovement.addChild(tailFinRotation);
		tailFinRotation.addChild(tailFin);
		
		// Main rotor
		mainrotor = new Mainrotor();
		mainRotorRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		movement.set(new Vector3f(0.0f, 0.8f, 0.0f));
		TransformGroup mainRotorMovement = new TransformGroup(movement);
		mainRotation.addChild(mainRotorMovement);
		mainRotorMovement.addChild(mainRotorRotation);
		mainRotorRotation.addChild(mainrotor.getScene());
		
		// Tail rotor
		tailrotor = new Tailrotor();
		tailRotorRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		movement.set(new Vector3f(-0.3f, 0.07f, 3.0f));
		TransformGroup tailRotorMovement = new TransformGroup(movement);
		mainRotation.addChild(tailRotorMovement);
		tailRotorMovement.addChild(tailRotorRotation);
		tailRotorRotation.addChild(tailrotor.getScene());
		
		mainRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(mainRotation);
	}
	
	BranchGroup getScene()
	{
		return objRoot;
	}
	
	void rotate(double angleX, double angleZ)
	{
		Transform3D rotationHelper = new Transform3D();
		rotation.rotX(angleX);
		rotationHelper.rotZ(angleZ);
		rotation.mul(rotationHelper);
		mainRotation.setTransform(rotation);
	}
	
	void startEngine()
	{
		engineState = true;
		if(!timer.isRunning()) timer.start();
	}
	
	private void increaseRotationSpeed()
	{
		if(mainRotationSpeed < 43)
		{
			mainRotationSpeed += 0.1;
			flyState = false;
		}
		else flyState = true;
		if(tailRotationSpeed < 57)
		{
			tailRotationSpeed += 0.2;
			flyState = false;
		}
	}
	
	void stopEngine()
	{
		engineState = false;
	}
	
	private void decreaseRotationSpeed()
	{
		mainRotationSpeed *= 0.985;
		tailRotationSpeed *= 0.98;
		if(mainRotationSpeed < 35) flyState = false;
		if(mainRotationSpeed < 0.1)
		{
			mainRotationSpeed = 1;
			tailRotationSpeed = 1;
			if(timer.isRunning()) timer.stop();
		}
	}
	
	boolean isFlyable()
	{
		return flyState;
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
		if(engineState && ! flyState) increaseRotationSpeed();
		if(!engineState) decreaseRotationSpeed();
		
		mainRotationAngle += mainRotationSpeed;
		rotation.rotY(mainRotationAngle * 2 * Math.PI / 360);
		mainRotorRotation.setTransform(rotation);
		
		tailRotationAngle += tailRotationSpeed;
		rotation.rotX(tailRotationAngle * 2 * Math.PI / 360);
		tailRotorRotation.setTransform(rotation);
	}
}

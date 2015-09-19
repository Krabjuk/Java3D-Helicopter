import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.vecmath.*;

import com.sun.j3d.utils.universe.*;

public class MainWindow extends JFrame implements ActionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Timer timer  = new Timer(100, this);
	
	private BranchGroup objRoot;
	private Fuselage fuselage;
	private Transform3D rotation = new Transform3D();
	private Transform3D movement = new Transform3D();
	private TransformGroup mainRotation = new TransformGroup();
	private TransformGroup mainMovement = new TransformGroup();
	
	private double mainRotorMinSpeed = 54.0;
	private double mainRotorMaxSpeed = 108.0;
	private double tailRotorMinSpeed = 81.0;
	private double tailRotorMaxSpeed = 120.0;
		
	private boolean shutDownEngine = false;
	private boolean shutDownMainRotor = false;
	private boolean shutDownTailRotor = false;
				
	private final Set<Character> pressed = new HashSet<Character>();
	
	private double rotateX = 0;
	private double rotateY = 0;
	private double rotateZ = 0;
	private double rotateStep = 10.0;	// in degrees
		
	private float height;				// Y-Position
	private float deep = -15.0f;		// Z-Position
	
	MainWindow()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
				
		setLayout(new BorderLayout());
		Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		canvas3D.addKeyListener(this);
		add("Center", canvas3D);
		
		createScene();
		objRoot.compile();

		SimpleUniverse universum = new SimpleUniverse(canvas3D);
		universum.getViewingPlatform().setNominalViewingTransform();
		universum.addBranchGraph(objRoot);	
	}
	
	void createScene()
	{
		objRoot = new BranchGroup();
		fuselage = new Fuselage();
		
		height = deep / 10;
		movement.set(new Vector3f(0.0f, height, deep));
		mainMovement.setTransform(movement);
				
		objRoot.addChild(mainMovement);
		mainMovement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		mainMovement.addChild(mainRotation);
		mainRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		mainRotation.addChild(fuselage.getScene());
		
		// Light
		AmbientLight ambLight = new AmbientLight(new Color3f(0.8f, 0.8f, 0.6f));
		ambLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 200));
		objRoot.addChild(ambLight);
		
		DirectionalLight dirLight = new DirectionalLight(new Color3f(0.8f, 0.8f, 0.6f),
													  new Vector3f(0.0f, -7.0f, 0.0f));
		dirLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		objRoot.addChild(dirLight);
		
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(fuselage.getMainRotorSpeed() >= mainRotorMinSpeed)
		{
			Transform3D rotationHelper = new Transform3D();
			// Pitch
			if(pressed.contains('s')) rotateX += rotateStep;
			if(pressed.contains('w')) rotateX -= rotateStep;
			if(rotateX > 360) rotateX -= 360;
			else if(rotateX < 0) rotateX += 360;
			rotation.rotX(rotateX * 2 * Math.PI / 360);
			// Yaw
			if(pressed.contains('a')) rotateY += rotateStep;
			if(pressed.contains('d')) rotateY -= rotateStep;
			if(rotateY > 360) rotateY -= 360;
			else if(rotateY < 0) rotateY += 360;
			rotationHelper.rotY(rotateY * 2 * Math.PI / 360);
			rotation.mul(rotationHelper);
			// Roll
			if(pressed.contains('q')) rotateZ += rotateStep;
			if(pressed.contains('e')) rotateZ -= rotateStep;
			if(rotateZ > 360) rotateZ -= 360;
			else if(rotateZ < 0) rotateZ += 360;
			rotationHelper.rotZ(rotateZ * 2 * Math.PI / 360);
			rotation.mul(rotationHelper);
			
			mainRotation.setTransform(rotation);
			
			// Height
			double distance;
			if(deep == 0) distance = 1;
			else distance = Math.abs(deep);
			if(pressed.contains('r')) height += 2.0f / distance;
			if(pressed.contains('f')) height -= 2.0f / distance;
			movement.set(new Vector3f(0.0f, height, deep));
			
			mainMovement.setTransform(movement);
		}
			
		// Rotorstuff
		if(pressed.contains('0')) shutDownEngine = true;
		
		// Speed
		if(pressed.contains('+') && shutDownEngine == false)
		{
			double speed = fuselage.getMainRotorSpeed();
			speed += 1.0;
			if(speed > mainRotorMaxSpeed) speed = mainRotorMaxSpeed;
			fuselage.setMainRotorSpeed(speed);
			
			speed = fuselage.getTailRotorSpeed();
			speed += 1.5;
			if(speed > tailRotorMaxSpeed) speed = tailRotorMaxSpeed;
			fuselage.setTailRotorSpeed(speed);
		}
		if(pressed.contains('-') && shutDownEngine == false)
		{
			double speed = fuselage.getMainRotorSpeed();
			if(speed > mainRotorMinSpeed) speed -= 1.5;
			fuselage.setMainRotorSpeed(speed);
			
			speed = fuselage.getTailRotorSpeed();
			if(speed > tailRotorMinSpeed) speed -= 2.25;
			fuselage.setTailRotorSpeed(speed);
		}
		
		if(shutDownEngine == true)
		{
			shutDownMainRotor = true;
			shutDownTailRotor = true;
		}
		
		if(shutDownMainRotor == true)
		{
			shutdownMainRotor();
		}
		
		if(shutDownTailRotor == true)
		{
			shutdownTailRotor();
		}
		
		if(shutDownMainRotor == false && shutDownTailRotor == false)
		{
			shutDownEngine = false;
		}
	}
	
	void shutdownMainRotor()
	{
		double speed = fuselage.getMainRotorSpeed();
		if(speed > 60) speed -= 1.5;
		else if(speed > 30) speed -= speed / 100.0;
		else speed -= 0.21;
		if(speed < 0.01)
		{
			speed = 0.0;
			shutDownMainRotor = false;
		}
		fuselage.setMainRotorSpeed(speed);
	}
	
	void shutdownTailRotor()
	{
		double speed = fuselage.getTailRotorSpeed();
		if(speed > 90) speed -= 2.25;
		else if( speed > 45) speed -= speed / 70.0;
		else speed -= 0.315;
		if(speed < 0.01)
		{
			speed = 0.0;
			shutDownTailRotor = false;
		}
		fuselage.setTailRotorSpeed(speed);
	}

	@Override
	public synchronized void keyPressed(KeyEvent e)
	{
		pressed.add(e.getKeyChar());
		if(e.getKeyChar() == 'l')
		{
			if(fuselage.getStateFrontLight())
			{
				fuselage.switchOffFrontLight();
			}
			else
			{
				fuselage.switchOnFrontLight();
			}
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e)
	{
		pressed.remove(e.getKeyChar());
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}

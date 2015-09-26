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
	
	private Timer timer  = new Timer(20, this);
	
	private BranchGroup objRoot;
	private Background background = new Background();
	private Fuselage fuselage;
	private Transform3D rotation = new Transform3D();
	private Transform3D movement = new Transform3D();
	private TransformGroup mainRotation = new TransformGroup();
	private TransformGroup mainMovement = new TransformGroup();
	
	public static BoundingSphere worldBounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);
		
	private boolean engineOn = false;
	
	private final Set<Character> pressed = new HashSet<Character>();
	
	private double rotateX = 0;
	private double rotateY = 0;
	private double rotateZ = 0;
	private double rotateStep = 2.5;	// in degrees
		
	private float height;				// Y-Position
	private float heightStep = 0.1f;
	
	MainWindow()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Hubschraubääääääääär");
		setSize(1400, 800);
				
		setLayout(new BorderLayout(0, 0));
		Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		canvas3D.addKeyListener(this);
		add("Center", canvas3D);
		
		background.setColor(RGB_to_Color3f(109, 205, 237));
		background.setApplicationBounds(worldBounds);
				
		createScene();
		objRoot.addChild(background);
		objRoot.compile();
		
		SimpleUniverse universum = new SimpleUniverse(canvas3D);
		universum.getViewingPlatform().setNominalViewingTransform();
		universum.getViewer().getView().setBackClipDistance(60);
		Transform3D viewPosition = new Transform3D();
		viewPosition.set(new Vector3d(0.0, 7.5, 50.0));
		universum.getViewingPlatform().getViewPlatformTransform().setTransform(viewPosition);
		
		canvas3D.getView().setSceneAntialiasingEnable(true);
		canvas3D.getView().setWindowEyepointPolicy(View.RELATIVE_TO_WINDOW);
		canvas3D.getView().setWindowMovementPolicy(View.VIRTUAL_WORLD);
	    canvas3D.getView().setWindowResizePolicy(View.VIRTUAL_WORLD);
		universum.addBranchGraph(objRoot);
	}
	
	void createScene()
	{
		objRoot = new BranchGroup();
		fuselage = new Fuselage();
				
		objRoot.addChild(mainMovement);
		mainMovement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		mainMovement.addChild(mainRotation);
		mainRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		mainRotation.addChild(fuselage.getScene());
		
		// Light
		AmbientLight ambLight = new AmbientLight(new Color3f(Color.WHITE));
		ambLight.setInfluencingBounds(worldBounds);
		objRoot.addChild(ambLight);
		
		DirectionalLight dirLight = new DirectionalLight();
		dirLight.setInfluencingBounds(worldBounds);
		dirLight.setColor(new Color3f(Color.WHITE));
		dirLight.setEnable(true);
		objRoot.addChild(dirLight);
		
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(fuselage.isFlyable())
		{
			// Pitch
			if(pressed.contains('s')) rotateX += rotateStep;
			if(pressed.contains('w')) rotateX -= rotateStep;
			if(rotateX > 360) rotateX -= 360;
			else if(rotateX < 0) rotateX += 360;
			// Yaw
			if(pressed.contains('a')) rotateY += rotateStep;
			if(pressed.contains('d')) rotateY -= rotateStep;
			if(rotateY > 360) rotateY -= 360;
			else if(rotateY < 0) rotateY += 360;
			// Roll
			if(pressed.contains('q')) rotateZ += rotateStep;
			if(pressed.contains('e')) rotateZ -= rotateStep;
			if(rotateZ > 360) rotateZ -= 360;
			else if(rotateZ < 0) rotateZ += 360;
			double angleX = rotateX * 2 * Math.PI / 360;
			double angleY = rotateY * 2 * Math.PI / 360;
			double angleZ = rotateZ * 2 * Math.PI / 360;
			
			rotation.rotY(angleY);
			mainRotation.setTransform(rotation);
			fuselage.rotate(angleX, angleZ);
		
			// Height
			if(pressed.contains('r')) height += heightStep;
			if(pressed.contains('f')) height -= heightStep;
			if(height < 0) { height = 0; System.out.println("on ground"); }
			movement.set(new Vector3f(0.0f, height, 0.0f));
		}
		
		mainMovement.setTransform(movement);
	}

	@Override
	public synchronized void keyPressed(KeyEvent e)
	{
		pressed.add(e.getKeyChar());
		// Rotorstuff
		if(e.getKeyChar() == 'p') 
		{
			if(!engineOn)
			{
				fuselage.startEngine();
				System.out.println("Engine on");
			}
			else
			{
				fuselage.stopEngine();
				System.out.println("Engine off");
			}
			engineOn = !engineOn;
		}
		
		// Mainlightstuff
		if(e.getKeyChar() == 'l')
		{
			if(fuselage.getStateFrontLight()) fuselage.switchOffFrontLight();
			else fuselage.switchOnFrontLight();
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e)
	{
		pressed.remove(e.getKeyChar());
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static Color3f RGB_to_Color3f(int red, int green, int blue)
	{
		return new Color3f(red / 255f, green / 255f, blue / 255f);
	}
}

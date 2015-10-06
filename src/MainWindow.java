import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

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
	private surfaceTerrain chunk1, chunk2, chunk3, chunk4;
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
		
	private float height = 1.7f;		// Y-Position
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
		int lod = 6;	// Level of Detail
		int roughnessPercent = 35;
		chunk1 = new surfaceTerrain(lod, roughnessPercent);	// vorne rechts ( x+, z+ )
		chunk2 = new surfaceTerrain(lod, roughnessPercent);	// vorne links	( x-, z+ )
		chunk3 = new surfaceTerrain(lod, roughnessPercent);	// hinten links	( x-, z- )
		chunk4 = new surfaceTerrain(lod, roughnessPercent);	// hinten rechts( x+, z- )
		
		float divisor = (float)Math.pow(2, lod - 1);
		
		movement.set(new Vector3f(-chunk2.getSizeWidth() / divisor, 0.0f, 0.0f));
		TransformGroup t2 = new TransformGroup();
		t2.setTransform(movement);
		t2.addChild(chunk2.getScene());
		
		movement.set(new Vector3f(-chunk3.getSizeWidth() / divisor, 0.0f, -chunk3.getSizeDeep() / divisor));
		TransformGroup t3 = new TransformGroup();
		t3.setTransform(movement);
		t3.addChild(chunk3.getScene());
		
		movement.set(new Vector3f(0.0f, 0.0f, -chunk4.getSizeDeep() / divisor));
		TransformGroup t4 = new TransformGroup();
		t4.setTransform(movement);
		t4.addChild(chunk4.getScene());
				
		objRoot.addChild(t2);
		objRoot.addChild(t3);
		objRoot.addChild(t4);
		objRoot.addChild(chunk1.getScene());
				
		objRoot.addChild(mainMovement);
		mainMovement.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		mainMovement.addChild(mainRotation);
		mainRotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		mainRotation.addChild(fuselage.getScene());
		
		movement.set(new Vector3f(0.0f, height, 0.0f));
		mainMovement.setTransform(movement);
		
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
			if(height < 0.7f) { height = 0.7f; System.out.println("on ground"); }
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

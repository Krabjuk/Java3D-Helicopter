import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;

public class MainWindow extends JFrame implements ActionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Timer timer  = new Timer(100, this);;
	
	private BranchGroup objRoot;
	private TransformGroup fuselage;
	private Transform3D rotation;
	//private Transform3D movement;
		
	private final Set<Character> pressed = new HashSet<Character>();
	
	private double rotateX = 0;
	private double rotateY = 0;
	private double rotateZ = 0;
	private double rotateStep = 5.0;
	
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
		rotation = new Transform3D();
		//movement = new Transform3D();
		
		fuselage = new TransformGroup(/*rotation*/);
		fuselage.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Sphere base = new Sphere(0.2f, 1, 200);		
		//Cylinder body = new Cylinder(0.8f, 0.05f);
		Cone engine = new Cone(0.8f, 0.2f, 1, 200, 200,
				new Cone(0.8f, 0.2f).getAppearance());
		
		SpotLight spotLight1 = new SpotLight(new Color3f(1, 3, 1),
				new Point3f(0, 0.09f, -0.2f),
				new Point3f(1, 0, 0),
				new Vector3f(0, 1, -20),
				(float)Math.PI, 10);
		spotLight1.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		SpotLight spotLight2 = new SpotLight(new Color3f(1.0f, 0.5f, 0),
			   	new Point3f(-0.03f, 0.08f, 0.2f),
			   	new Point3f(0, 20, 0),
			   	new Vector3f(0, 1, 20),
			   	(float)Math.PI, 10);
		spotLight2.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		SpotLight spotLight3 = new SpotLight(new Color3f(1.0f, 0.5f, 0),
			   	new Point3f(0.03f, 0.08f, 0.2f),
			   	new Point3f(0, 20, 0),
			   	new Vector3f(0, 1, 20),
			   	(float)Math.PI, 10);
		spotLight3.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		SpotLight spotLight4 = new SpotLight(new Color3f(0, 0, 1),
			   	new Point3f(-0.2f, 0.09f, 0),
			   	new Point3f(0, 0, 20),
			   	new Vector3f(-20, 1, 0),
			   	(float)Math.PI, 10);
		spotLight4.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		SpotLight spotLight5 = new SpotLight(new Color3f(0, 0, 1),
			   	new Point3f(0.2f, 0.09f, 0),
			   	new Point3f(0, 0, 20),
			   	new Vector3f(20, 1, 0),
			   	(float)Math.PI, 10);
		spotLight5.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));

		fuselage.addChild(spotLight1);
		fuselage.addChild(spotLight2);
		fuselage.addChild(spotLight3);
		fuselage.addChild(spotLight4);
		fuselage.addChild(spotLight5);
		
		fuselage.addChild(base);
		//fuselage.addChild(body);
		fuselage.addChild(engine);
				
		
		objRoot.addChild(fuselage);
		
		// Light
		AmbientLight ambLight = new AmbientLight(new Color3f(0.6f, 0.8f, 0.2f));
		ambLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 200));
		objRoot.addChild(ambLight);
		
		DirectionalLight dirLight = new DirectionalLight(new Color3f(0.5f, 0.5f, 0.5f),
													  new Vector3f(0.0f, -7.0f, 0.0f));
		dirLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		objRoot.addChild(dirLight);
		
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e)
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
		
		fuselage.setTransform(rotation);
	}

	@Override
	public synchronized void keyPressed(KeyEvent e)
	{
		pressed.add(e.getKeyChar());
	}

	@Override
	public synchronized void keyReleased(KeyEvent e)
	{
		pressed.remove(e.getKeyChar());
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}

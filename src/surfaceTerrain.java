
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

import J3DTerrain.*;

public class surfaceTerrain
{
	private BranchGroup objRoot = new BranchGroup();
	private Transform3D move = new Transform3D();
	private TransformGroup movement = null;
	private Appearance app = null;
		
	private int lod;
	private int divisions;
	
	private float sizeX = 0;
	private float sizeZ = 0;
	
	surfaceTerrain(int detailLevel, float roughness)
	{
		objRoot.addChild(movement);
		
		lod = detailLevel > 0 ? detailLevel : 1;
		divisions = 1 << lod;
		Terrain terrain = new FractalTerrain(lod, roughness / 100.0f);
		
		float altitude = 0;
		float x = 0, z = 0;
		for(int i = 0; i < divisions; ++i)
		{
			//System.out.println((int)((double)i / (double)divisions * 100) + "%");
			for(int j = 0; j < divisions; ++j)
			{				
				x = 1.0f * i / divisions;
				z = 1.0f * j / divisions;
			    
				altitude = terrain.getAltitude(x, z);
				float areasize = 2.0f;
				float posX = 2 * areasize * j + areasize; sizeX += areasize;
				float posZ = 2 * areasize * i + areasize; sizeZ += areasize;
				
				move.set(new Vector3f(posX, altitude, posZ));
				movement = new TransformGroup();
				movement.setTransform(move);
				app = new Appearance();
				app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
				app.setColoringAttributes(new ColoringAttributes(new Color3f(altitude, 0.5f, 0.2f), ColoringAttributes.NICEST));
				movement.addChild(new Box(areasize, altitude, areasize, app));
				objRoot.addChild(movement);
			}
		}
	}
	
	public BranchGroup getScene()
	{
		return objRoot;
	}
	
	public float getSizeWidth()
	{
		return sizeX;
	}
	
	public float getSizeDeep()
	{
		return sizeZ;
	}
}


package J3DTerrain;

import java.util.Random;

public class FractalTerrain implements Terrain
{
	private float[][] terrain;
	private float roughness, min, max;
	private int divisions;
	private Random rng;
	
	public FractalTerrain(int lod, float roughness)
	{
		this.roughness = roughness;
		this.divisions = 1 << lod;
		terrain = new float[divisions + 1][divisions + 1];
		rng = new Random();
		
		terrain[0][0] = rnd();
		terrain[0][divisions] = rnd();
		terrain[divisions][divisions] = rnd();
		terrain[divisions][0] = rnd();
		
		float rough = this.roughness;
		for(int i = 0; i < lod; ++ i)
		{
			int r = 1 << (lod - i);
			int s = r >> 1;
			for(int j = 0; j < divisions; j += r)
			{
				for(int k = 0; k < divisions; k += r)
				{
					diamond(j, k, r, rough);
				}
			}
			if(s > 0)
			{
				for(int j = 0; j <= divisions; j += s)
				{
					for(int k = (j + s) % r; k <= divisions; k += r)
					{
						square(j - s, k - s, r, rough);
					}
				}
			}
			rough *= this.roughness;
		}
		min = max = terrain[0][0];
		for(int i = 0; i <= divisions; ++i)
		{
			for(int j = 0; j <= divisions; ++j)
			{
				if(terrain[i][j] < min) min = terrain[i][j];
				else if(terrain[i][j] > max) max = terrain[i][j];
			}
		}
	}
	
	private void diamond(int x, int y, int side, float scale)
	{
		if(side > 1)
		{
			int half = side / 2;
			float avg = 0.25f * (terrain[x][y] + terrain[x + side][y]
					+ terrain[x + side][y + side] + terrain[x][y + side]);
			terrain[x + half][y + half] = avg + rnd () * scale;
		}
	}
	
	private void square(int x, int y, int side, float scale)
	{
		int half = side / 2;
		float avg = 0.0f, sum = 0.0f;
		if (x >= 0) { avg += terrain[x][y + half]; sum += 1.0; }
		if (y >= 0) { avg += terrain[x + half][y]; sum += 1.0; }
		if (x + side <= divisions) { avg += terrain[x + side][y + half]; sum += 1.0; }
		if (y + side <= divisions) { avg += terrain[x + half][y + side]; sum += 1.0; }
		terrain[x + half][y + half] = avg / sum + rnd () * scale;
	}
	
	private float rnd()
	{
		return 2.0f * rng.nextFloat() - 1.0f;
	}
	
	public float getAltitude(float i, float j)
	{
		float alt = terrain[(int) (i * divisions)][(int) (j * divisions)];
		return (alt - min) / (max - min);
	}
}

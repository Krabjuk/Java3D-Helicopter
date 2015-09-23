import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Texture;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.image.TextureLoader;


public class ObjectLoader
{
	/// Class not ready for use
	private BranchGroup objRoot = null;
	private Appearance appearance = new Appearance();
	
	ObjectFile rumpf = new ObjectFile(ObjectFile.RESIZE);
	
	private TextureLoader textureLoader = new TextureLoader("C:/Development/eisenoberflaeche_512.jpg", null);
	private Texture texture;
	
	ObjectLoader()
	{
		Scene scn = null;
		
		try {
			scn = rumpf.load(new FileReader("C:/Development/Bell222/Bell222.obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IncorrectFormatException e) {
			e.printStackTrace();
		} catch (ParsingErrorException e) {
			e.printStackTrace();
		}
		
		texture = textureLoader.getTexture();
		appearance.setTexture(texture);
		
		objRoot.addChild(scn.getSceneGroup());
	}
}

package Main;

import SH.File.FileTools;
import Scenes.MainWindowController;
import imgscalr.Scalr;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mag. Stefan Hagmann
 */
public class ImageTask extends Task<Void>{
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageTask.class);

  private final String filename;
  private final String origStr;
  private final String baseStr;
  private SynchronizedImageProperties info;
  private final int maxsize;
  
  public ImageTask(String filename, String baseStr, String origStr, int maxsize) {
    this.filename = filename;
    this.baseStr = baseStr;
    this.origStr = origStr;
    this.maxsize = maxsize;
  }
  
  @Override
  protected Void call(){
    try {  
      //Infos
      
      File workingFilename = new File(this.filename);
      //Original kopieren-----------------
      String orig = workingFilename.getAbsolutePath();
      Path root = Paths.get(orig).getParent();

      Path src = Paths.get(root.toString(), baseStr, workingFilename.getName());
      Path target = Paths.get(root.toString(), baseStr, origStr, workingFilename.getName());
      logger.info("Kopie von: "+src.toString());

      Files.copy(src, target);
      
      long origsize = FileTools.getSize(src);
      info.addSize(origsize);
      
      // Load image
      BufferedImage srcImage = ImageIO.read(src.toFile());
      logger.info("Datei gelesen: "+src.toString());
      // Scale image
      BufferedImage scaledImage = Scalr.resize(srcImage, maxsize);
      ImageIO.write(scaledImage, "JPG", src.toFile());
      logger.info("Umgerechnet zu JPG");
      //Websicheren Namen machen
      String newname = FileTools.MakeSaveFileName(Paths.get(root.toString(), origStr).toString(), workingFilename.getName());
      
      //neuer Name
      long newsize = FileTools.getSize(src);
      info.addCalculatedsize(newsize);
      logger.info(origsize+"Byte -> "+newsize);

      updateMessage(newname);      
      updateProgress(1, 1);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return null;
  }
  
  public void setInfo(SynchronizedImageProperties info){
    this.info = info;
  }

  
  
}

package Main;

import SH.File.FileTools;
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
public class ImageTask extends Task<Integer> {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageTask.class);
  public static final boolean IS_THUMB = true;
  public static final boolean IS_IMAGE = false;

  private final String filename;
  private final String targetStr;
  private final String baseStr;
  private SynchronizedImageProperties info;
  private final int maxsize;
  private final boolean isthumb;
  
  public ImageTask(String filename, String baseStr, String targetStr, int maxsize, boolean isthumb) {
    this.filename = filename;
    this.baseStr = baseStr;
    this.targetStr = targetStr;
    this.maxsize = maxsize;
    this.isthumb = isthumb;
  }
  
  @Override
  protected Integer call(){
    try {  
      //Infos
      
      File workingFilename = new File(this.filename);
      //Original kopieren-----------------
      String orig = workingFilename.getAbsolutePath();
      Path root = Paths.get(orig).getParent();

      Path src = Paths.get(root.toString(), baseStr, workingFilename.getName());
      Path target = Paths.get(root.toString(), baseStr, targetStr, workingFilename.getName());
      String newname = "";
      long origsize=0;
      if(isthumb==ImageTask.IS_IMAGE){
        logger.info("Kopie von: "+src.toString());
        Files.copy(src, target);
        origsize = FileTools.getSize(src);
        info.addSize(origsize);

        // Load image
        BufferedImage srcImage = ImageIO.read(src.toFile());
        logger.info("Datei gelesen: "+src.toString());
        
        //Dimensionen zu groß?
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        if(width>maxsize || height>maxsize){        
          // Scale image
          BufferedImage scaledImage = Scalr.resize(srcImage, maxsize);
          ImageIO.write(scaledImage, "JPG", src.toFile());
          logger.info("Umgerechnet zu JPG");
        }
        //Websicheren Namen machen
        newname = FileTools.MakeSaveFileName(Paths.get(root.toString(), baseStr).toString(), workingFilename.getName());
        
        //neuer Name
        long newsize = FileTools.getSize(src);
        info.addCalculatedsize(newsize);
        logger.info(origsize+" Byte -> "+newsize+" Byte");
      }
      
      if(isthumb==ImageTask.IS_THUMB){
        logger.info("Kopie von: "+src.toString());
        Files.copy(src, target);

        // Load Image in Thumbdir
        BufferedImage srcImage = ImageIO.read(target.toFile());
        logger.info("Datei gelesen: "+target.toString());
        //Dimensionen zu groß?
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        if(width>maxsize || height>maxsize){  
          // Scale image
          BufferedImage scaledImage = Scalr.resize(srcImage, maxsize);
          ImageIO.write(scaledImage, "JPG", target.toFile());
          logger.info("Umgerechnet zu JPG");
        }
        //Websicheren Namen machen
        newname = FileTools.MakeSaveFileName(Paths.get(root.toString(), targetStr).toString(), workingFilename.getName());
        //Umbenennen
        Path oldfile = Paths.get(root.toString(), baseStr, targetStr, newname);
        FileTools.RenameFile(oldfile, "thumb_"+newname);
        newname = "thumb_"+newname;
      }
      
      

      updateMessage(newname);      
      updateProgress(1, 1);
    } catch (IOException ex) {
      ex.printStackTrace();
      return -1;
    }
    return 1;
  }
  
  public void setInfo(SynchronizedImageProperties info){
    this.info = info;
  }

  
  
}

package Scenes;

import FX.FXSystem;
import Main.Main;
import Main.ImageTask;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Main.SynchronizedImageProperties;
import SH.File.FileTools;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.DoubleBinding;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Mag. Stefan Hagmann
 */
public class MainWindowController implements Initializable {
    
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MainWindowController.class);
  
  @FXML
  private AnchorPane root;
  @FXML
  private Label txtleft;
  @FXML
  private Label txtright;
  @FXML
  private  ProgressBar progress;
  
  public int counter;
  private File[] filenames;
  private String baseStr="";
  private String origStr="";
  
  //Updating UI
  public StringProperty txtright_Property = new SimpleStringProperty("");
  //Progress für alle Tasks
  //https://stackoverflow.com/questions/12986916/javafx-updating-progress-for-the-multiple-tasks
  DoubleBinding overallProgress = null;
    
  private SynchronizedImageProperties imgProp;
  private Task<Void> allImagesTask;
  private Thread worker;
  private Scene scene;
  private ShutdownController shutdowncontroller;
  private int maxsize;
  private int thumbsize;
  private String thumbStr;
  

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.imgProp = new SynchronizedImageProperties(); 
  }  
  
  public String getBaseStr() {
    return baseStr;
  }

  public void setBaseStr(String baseStr) {
    this.baseStr = baseStr;
  }

  public String getOrigStr() {
    return origStr;
  }

  public void setOrigStr(String origStr) {
    this.origStr = origStr;
  }
  
  public String getThumbStr() {
    return thumbStr;
  }
  
  public void setThumbStr(String thumbStr) {
    this.thumbStr = thumbStr;
  }
  

  public void setTxtLeft(String txtleft) {
    this.txtleft.setText(txtleft);
  }

  public void setTxtRight(String txtright) {
    this.txtright.setText(txtright);
  }

  public void setProgress(double val) {
    this.progress.setProgress(val);
  }
  
  public void setFilenames(File[] filenames) {
    this.filenames = filenames;
  }

  public SynchronizedImageProperties getImgProp() {
    return imgProp;
  }

  public void setImgProp(SynchronizedImageProperties imgProp) {
    this.imgProp = imgProp;
  }
  
  public void startConverting(){  
    //Alle Tasks erstellen
    List<Task<Integer>> tasklist = new ArrayList<>();
    for(File file : filenames){
      /* Single Image Thread */
      ImageTask imageTask = new ImageTask(
              file.getName(), baseStr, origStr, maxsize, ImageTask.IS_IMAGE);
      tasklist.add(imageTask);
      /* Thumbnails auch */
      ImageTask thumbTask = new ImageTask(
              file.getName(), baseStr, thumbStr, thumbsize, ImageTask.IS_THUMB);
      tasklist.add(thumbTask);
    }
    
    imgProp.setAnzahl(tasklist.size()*2);
    
    //Ausführen lassen
    allImagesTask = new Task() {
      @Override
      protected Integer call() throws Exception {
        for (Task<Integer> t : tasklist) {
          //jeder Task liefert einen Beitrag zum GesamtProgress
          //Der Wert wird im Constructor festgelegt = 1
          DoubleBinding scaledProgress = t.progressProperty().divide((double)tasklist.size());
          if (overallProgress == null) {
            overallProgress = scaledProgress;
          } else {
            overallProgress = overallProgress.add(scaledProgress);
          }
        
          //Arbeiten an der UI müssen im FX Thread passieren
          Platform.runLater(() -> {
            progress.progressProperty().bind(overallProgress);
          });
          
          ImageTask theTask = (ImageTask)t;
          theTask.setInfo(imgProp);
          
          theTask.setOnSucceeded((event) -> {
            //Bindung zum Task aufheben
            Platform.runLater(() -> {
              txtright_Property.bind(theTask.messageProperty());
              txtright.textProperty().unbind();
            });
          });
          
          Platform.runLater(() -> {
            txtright.textProperty().bind(txtright_Property);
          });
          
          Thread.sleep(40);
          new Thread(theTask).start();
          
          // run task in single-thread executor (will queue if another task is running):
          //exec.submit(theTask);
        }
        return 1;
      }
    };
    
    
    worker = new Thread(allImagesTask);
    worker.setName("Runnable-Convert all Images");
    allImagesTask.setOnSucceeded((event) -> {
      //Alles Fertig
      logger.info("Alle Tasks fertig");
      this.changeScene("/Scenes/Shutdown.fxml");
    });
    worker.start();
  }
  
  /**
   * Wechselt die Scene über ein FXML File
   * @param fxmlFile 
   * @param e 
   */
  public void changeScene(String fxmlFile){
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource(fxmlFile));
      Parent parent = loader.load();
      shutdowncontroller = loader.getController();
      
      //Parent parent = FXMLLoader.load(getClass().getResource(fxmlFile));
      Scene scene = new Scene(parent);
      //Replace Progress Indicator      
      shutdowncontroller.ReplaceProgress(parent);
      
      Platform.runLater(() -> {
        String formattedString = String.format("Bilder bearbeitet: %d", filenames.length);
        shutdowncontroller.setTxt1(formattedString);
        shutdowncontroller.setTxt2("Originalgröße: "+FileTools.getSizeFormated(imgProp.getSize()));        
        shutdowncontroller.setTxt3("Umgewandelt: "+FileTools.getSizeFormated(imgProp.getCalculatedsize()));
        
        double fact = (double)imgProp.getCalculatedsize() / (double)imgProp.getSize();
        
       
        shutdowncontroller.setProgress(1.0-fact);
      });
      shutdowncontroller.startTicker();
      
      Stage appStage = (Stage) this.scene.getWindow();
      appStage.setScene(scene);
      appStage.show();
      FXSystem.CenterOnScreen(appStage);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Wait for some Time
   * @param i 
   */
  private void Wait(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void stop() {
    worker.interrupt();
  }

  public void setScene(Scene scene) {
    this.scene = scene;
  }

  public void setMaxSize(int maxsize) {
    this.maxsize = maxsize;
  }

  public void setThumbSize(int thumbsize) {
    this.thumbsize = thumbsize;
  }

  


}

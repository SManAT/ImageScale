package Main;

import FX.FXSystem;
import SH.File.FileTools;
import SH.Xml.XMLTool;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xml.configHandler;
import Scenes.MainWindowController;
import java.nio.file.Path;
import javafx.application.Platform;

/**
 *
 * @author Mag. Stefan Hagmann
 */
public class Main extends Application {

  private Pane rootLayout;

  final String configFileName;
  private File[] filenames;
  private MainWindowController controller;

  public Main() {
    this.configFileName = "config.xml";
  }

  @Override
  public void start(Stage primaryStage) {

    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/Scenes/MainWindow.fxml"));
      rootLayout = loader.load();
      controller = loader.getController();

      controller.setProgress(0);
    } catch (Exception ex) {
      Logger.getLogger("FXML not found");
      ex.printStackTrace();
    }
    
    LoadConfig();
    filenames = FileTools.ReadDir(controller.getBaseStr(), FileTools.ONLYFILES, "jpg|png|jpeg");
    controller.setTxtLeft("Bilder gefunden: " + filenames.length);
    
    // Show the scene containing the root layout.
    Scene scene = new Scene(rootLayout);
    
    //Merke dir die Scene
    controller.setScene(scene);
    
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setScene(scene);
    //primaryStage.getIcons.add(new Image("file:icon.png"));
    
    primaryStage.show();
    //Center on Screen
    FXSystem.CenterOnScreen(primaryStage);
    
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        controller.setFilenames(filenames);
        controller.startConverting();
      }
    });
  }

  @Override
  /**
   * Called when Application ist closed with 
   * Platform.exit();
   */
  public void stop() throws Exception {
    super.stop();
    controller.stop();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Lädt aus einer XML Datei alle Config einstellungen
   */
  private void LoadConfig() {
    try {
      configHandler handle = new configHandler();
      String configFile = Paths.get(configFileName).toAbsolutePath().toString();

      XMLTool.LoadXMLFile(configFile, handle);
      
      controller.setMaxSize(handle.maxsize);
      controller.setBaseStr(handle.imagePath);
      controller.setOrigStr(handle.backupPath);
      

    } catch (Exception ex) {
      System.exit(1);
    }
    
    createDirs();
  }
  
  /**
   * Legt einen Ordner für die Bilder
   */
  private void createDirs() {
    Path workingDirectory=Paths.get(".").toAbsolutePath();
    Path baseDir = Paths.get(workingDirectory.toString(), controller.getBaseStr());
    FileTools.createDirectory(baseDir);
    Path originalDir = Paths.get(baseDir.toString(), controller.getOrigStr());
    //Alte Inhalte löschen
    FileTools.deleteDir(originalDir.toFile());
    FileTools.createDirectory(originalDir);
  }
  
   

}

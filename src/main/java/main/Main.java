package main;

import SH.FX.Tools.FXSystem;
import SH.FileTools.FileTools;
import SH.Xml.XMLTool;
import java.io.File;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controller.MainWindowController;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import javafx.application.Platform;
import org.slf4j.LoggerFactory;
import xml.configHandler;

/**
 *
 * @author Mag. Stefan Hagmann
 */
public class Main extends Application {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

  private Pane rootLayout;

  final String configFileName;
  private File[] filenames;
  private MainWindowController controller;

  public Main() {
    this.configFileName = "config.xml";
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
        launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      URL fxml = this.getClass().getResource("/fxml/MainWindow.fxml");
      FXMLLoader loader = new FXMLLoader(fxml);
      rootLayout = loader.load();
      controller = loader.getController();

      controller.setProgress(0);
    } catch (IOException ex) {
      logger.error("FXML not found ...");
      logger.info("Check if CSS and Controllers are found ...");  
      System.exit(-1);
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
   * L�dt aus einer XML Datei alle Config einstellungen
   */
  private void LoadConfig() {
    try {
      configHandler handle = new configHandler();
      
      File file = Paths.get(configFileName).toFile();
      XMLTool.LoadXMLFile(file, handle);      
      controller.setThumbSize(handle.thumbsize);
      controller.setMaxSize(handle.maxsize);
      controller.setBaseStr(handle.imagePath);
      controller.setOrigStr(handle.backupPath);
      controller.setThumbStr(handle.thumbPath);
    } catch (Exception ex) {
      ex.printStackTrace();
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
    Path thumbDir = Paths.get(baseDir.toString(), controller.getThumbStr());
    //Alte Inhalte löschen
    FileTools.deleteDir(originalDir.toFile());
    FileTools.createDirectory(originalDir);
    
    FileTools.deleteDir(thumbDir.toFile());
    FileTools.createDirectory(thumbDir);
  }
  
  
   

}

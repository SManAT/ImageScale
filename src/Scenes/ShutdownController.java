package Scenes;

import RingProgress.FillProgressIndicator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Mag. Stefan Hagmann
 */
public class ShutdownController implements Initializable {
  @FXML
  private Label txt1;
  
  @FXML
  private Label txt2;
  
  @FXML
  private Label txt3;
  
  @FXML
  private Button btn;
  
  @FXML
  private VBox vbox;
  
  @FXML
  private ProgressIndicator progress;
  private FillProgressIndicator indicator;
  private Thread ticker;
  private Task<Integer> waitertask;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
  }  

  public String getTxt1() {
    return txt1.getText();
  }

  public void setTxt1(String txt1) {
    this.txt1.setText(txt1);
  }
  
  public void setTxt3(String txt1) {
    this.txt3.setText(txt1);
  }

  public String getTxt2() {
    return txt2.getText();
  }

  public void setTxt2(String txt2) {
    this.txt2.setText(txt2);
  }

  public void setProgress(double progress) {
    this.indicator.setProgress((int) Math.floor(progress*100));
  }
  
  @FXML
  public void clickAction(){
    waitertask.cancel(true);
    ticker.interrupt();
    Platform.exit();
  }

  /**
   * Timmer für Shutdown
   */
  void startTicker() {
    waitertask = new Task<Integer>() {
      @Override
      protected Integer call() throws Exception {
        int time=10; //Sekunden
        for (int i=0; i<time; i++){
          if (isCancelled()) {
            return null ;
          }
          updateMessage("Beenden ("+(time - i)+"s)");
          Thread.sleep(1000);
        }  
        return time;
      }
    };
    btn.textProperty().bind(waitertask.messageProperty());   
    waitertask.setOnSucceeded(e -> {
      Platform.exit();
    });
    ticker = new Thread(waitertask);
    ticker.start();
  }

  void ReplaceProgress(Parent root) {
    indicator = new FillProgressIndicator();
    indicator.setPadding(new Insets(5, 0, 5, 0));
    indicator.autosize();
    
    
    root.getChildrenUnmodifiable().remove(progress);
    vbox.getChildren().add(indicator);
    
    
    /*
    <ProgressIndicator fx:id="progress" prefHeight="92.0" prefWidth="105.0" progress="0.49">
       <padding>
          <Insets bottom="5.0" top="5.0" />
       </padding>
    </ProgressIndicator>
     */
  }
  
  
  
}

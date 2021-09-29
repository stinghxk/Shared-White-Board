import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.AbstractList;
import java.util.Optional;
import java.util.ResourceBundle;

public class LauncherController
{
    @FXML
    private TextField userName;

    @FXML
    public TextField serverIp;

    @FXML
    public TextField serverPort;



    private static Boolean isConnected = false;
    private static Boolean isManager = false;

    private String userID;
    private String ipAddress;
    private int port = 5000;


    public void initialize()
    {
        isManager = false;

    }


    @FXML
    public void create() throws IOException
    {
        isManager = true;
        if (userName.getText() == null || userName.getText().trim().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter a Username");
            alert.showAndWait();
        }
        else
        {
            userID = userName.getText();
            Stage stage = loadCanvas(isManager, userID);

            quit(stage);
        }

    }

    @FXML
    public void join() throws IOException
    {
        isManager = false;
        if (userName.getText() == null || userName.getText().trim().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Warning  Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please Enter a Username");
            alert.showAndWait();
        }
        else
        {
            userID = userName.getText();
            Stage stage = loadCanvas(isManager, userID);

            quit(stage);
        }

    }

    public Stage loadCanvas(boolean isManager, String userID) throws IOException
    {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Canvas.fxml"));
        CanvasController controller = new CanvasController(isManager, userID);
        loader.setController(controller);
        Pane root = loader.load();

        Stage stage = new Stage();
        if(isManager)
            stage.setTitle("Manager Tab! - Enjoy your canvas, " + userID);
        else
            stage.setTitle("User Tab! - Enjoy your canvas, " + userID);
        stage.setScene(new Scene(root));
        stage.show();

        return stage;
    }

    public void quit(Stage stage)
    {
        stage.setOnCloseRequest(e ->
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Closing the Canvas");
            alert.setContentText("Are you sure about this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK)
            {
                System.exit(0); // ... user chose OK
            }
            else
            {
                e.consume(); // ... user chose CANCEL or closed the dialog
            }
        });
    }

}

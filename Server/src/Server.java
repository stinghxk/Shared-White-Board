import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Server extends Application
{

    private static int port = 5000;
    private String address;

    public void start(Stage primaryStage) throws Exception
    {
        Locale.setDefault(Locale.ENGLISH);

        try
        {
            // Load root layout from fxml file.
//            FXMLLoader loader = new FXMLLoader();
//            URL fxmlUrl = this.getClass().getClassLoader().getResource("Server.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Server.fxml"));
            ServerController controller = new ServerController(address,Integer.toString(port));
            loader.setController(controller);
            Pane mainPane = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(mainPane);
            primaryStage.setTitle("Canvas Server - Welcome!");
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(e ->
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Closing the server");
                alert.setContentText("Are you sure about this?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK)
                {
                    System.exit(0);// ... user chose OK
                }
                else
                {
                    e.consume(); // ... user chose CANCEL or closed the dialog
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        if (args.length != 0)
            port = Integer.valueOf(args[0]);

        launch(args);
    }
}
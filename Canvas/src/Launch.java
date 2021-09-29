import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class Launch extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void init() throws Exception
    {
        System.out.println("before start");
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            URL fxmlUrl = this.getClass().getClassLoader().getResource("Launcher.fxml");
            Pane root = loader.<Pane>load(fxmlUrl);

//            Parent root = FXMLLoader.load(getClass().getResource("Canvas.fxml"));

            stage.setTitle("Hello Canvas! - Enjoy your canvas.");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() throws Exception
    {
        System.out.println("while close");
    }
}

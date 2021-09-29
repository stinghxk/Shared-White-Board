import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class CanvasController implements Runnable
{

    ObservableList<String> m_names = FXCollections.observableArrayList();

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    OutputStream outputStream;
    InputStream inputStream;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Canvas canvas;

    @FXML
    private Slider slider;

    @FXML
    Label labelLW;

    @FXML
    TextField textInput;

    @FXML
    Menu fileMenu;

    @FXML
    Menu editMenu;

    @FXML
    BorderPane backPane;

    @FXML
    TextArea chatLog;

    @FXML
    ListView userList;


    enum Mode
    {BRUSH, LINE, CIRCLE, OVAL, RECTANGLE, TEXT, NULL}

    private boolean isManager;
    private String userID;

    private GraphicsContext gc;
    private double lineWidth = 1;
    private Mode currentMode = Mode.NULL;
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    private double temp = 0;
    private double diameter = 0;
    private double centerX = 0;
    private double centerY = 0;
    private double tempX = 0;
    private double tempY = 0;


    private String textString;

    public CanvasController(boolean isManager, String userID)
    {
        this.isManager = isManager;
        this.userID = userID;
    }

    public void initialize()
    {
        Locale.setDefault(Locale.ENGLISH);

        if (!isManager)
        {
            fileMenu.setDisable(true);
            editMenu.setDisable(true);
        }
        else
        {
            backPane.setStyle("-fx-background-color: #f58282");
        }

        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(lineWidth);

        colorPicker.setValue(Color.BLACK);
        colorPicker.setOnAction(e -> gc.setStroke(colorPicker.getValue()));

        slider.valueProperty().addListener(e ->
        {
            lineWidth = slider.getValue();
            String str = String.format("LW: %.1f", lineWidth);
            labelLW.setText(str);
            gc.setLineWidth(lineWidth);
        });


        canvas.setOnMousePressed(e ->
        {
            startX = e.getX();
            startY = e.getY();
            switch (currentMode)
            {
                case BRUSH:
                case LINE:
                    gc.beginPath(); //reset the coordinate allowing breaking points
                    gc.lineTo(startX, startY); //straight line to this coordinate
                    gc.stroke(); //
                    break;


                case OVAL:
                case CIRCLE:
                case RECTANGLE:
                    gc.beginPath(); //reset the coordinate allowing breaking points
                    gc.stroke(); //
                    break;
                default:
                    break;
            }

        });

        canvas.setOnMouseDragged(e ->
        {
            endX = e.getX();
            endY = e.getY();
            switch (currentMode)
            {
                case BRUSH:
                    gc.lineTo(endX, endY);
                    gc.stroke();
                    break;
                case LINE:
                case OVAL:
                case CIRCLE:
                case RECTANGLE:
                default:
                    break;
            }

        });

        canvas.setOnMouseReleased(e ->
        {
            endX = e.getX();
            endY = e.getY();

            switch (currentMode)
            {
                case BRUSH:
                    break;
                case LINE:
                    gc.lineTo(endX, endY);
                    gc.stroke();
                    break;
                case OVAL:
                    if (startX > endX)
                    {
                        temp = startX;
                        startX = endX;
                        endX = temp;
                    }
                    if (startY > endY)
                    {
                        temp = startY;
                        startY = endY;
                        endY = temp;
                    }
                    gc.strokeOval(startX, startY, abs(startX - endX), abs(startY - endY));
                    break;
                case CIRCLE:
                    centerX = startX + (endX - startX) / 2;
                    centerY = startY + (endY - startY) / 2;
                    diameter = sqrt((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY));
                    gc.strokeOval(centerX - diameter / 2, centerY - diameter / 2, diameter, diameter);
                    break;
                case RECTANGLE:
                    if (startX > endX)
                    {
                        temp = startX;
                        startX = endX;
                        endX = temp;
                    }
                    if (startY > endY)
                    {
                        temp = startY;
                        startY = endY;
                        endY = temp;
                    }
                    gc.strokeRect(startX, startY, abs(startX - endX), abs(startY - endY));
                    break;
                default:
                    break;
            }

        });

        canvas.setOnMouseClicked(e ->
        {
            switch (currentMode)
            {
                case TEXT:
                    if (!textInput.isVisible())
                    {
                        tempX = e.getX();
                        tempY = e.getY();
                        textInput.setLayoutX(e.getX());
                        textInput.setLayoutY(e.getY());
                        textInput.setVisible(true);
                    }
                    else
                    {
                        textString = textInput.getText();
                        textInput.setVisible(false);
                        gc.setLineWidth(1);
                        gc.strokeText(textString, tempX + textInput.getHeight() / 2, tempY + textInput.getHeight() / 2);
                        gc.setLineWidth(lineWidth);
                        textInput.clear();
                    }


                    break;
                default:
                    textInput.setVisible(false);
                    break;
            }
        });


    }

    @Override
    public void run()
    {

    }


    @FXML
    public void brushSelected()
    {
        currentMode = Mode.BRUSH;
    }

    @FXML
    public void lineSelected()
    {
        currentMode = Mode.LINE;
    }

    @FXML
    public void circleSelected()
    {
        currentMode = Mode.CIRCLE;
    }

    @FXML
    public void ovalSelected()
    {
        currentMode = Mode.OVAL;
    }

    @FXML
    public void rectangleSelected()
    {
        currentMode = Mode.RECTANGLE;
    }

    @FXML
    public void textSelected()
    {
        currentMode = Mode.TEXT;
    }

    @FXML
    public void newCanvas()
    {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    public void sendMessage()
    {

    }

    public void sendDraw(Mode mode, CanvasController cc)
    {
        int sX = (int) startX;
        int sY = (int) startY;
        int eX = (int) endX;
        int eY = (int) endY;
        int color = colorToInt(cc.colorPicker.getValue());
        String text = textInput.getText();

        String drawing = mode.name()+"|"+sX+"|"+sY+"|"+eX+"|"+eY+"|"+color+"|"+text;
        Message drawMsg = new Message(userID, Message.DRAW_OPERATION,drawing);
//        try {
//            out.writeObject(message);
//        } catch (IOException io) {
//            System.out.println("IOException: " + io.getMessage());
//        }

    }

    public void readDraw(String drawing)
    {
        String[] args = new String[6];
        args = drawing.split("|");
        startX = Double.parseDouble(args[1]);
        startY = Double.parseDouble(args[2]);
        endX = Double.parseDouble(args[3]);
        endY = Double.parseDouble(args[4]);
        int colorInt = Integer.valueOf(args[5]);
        String text = "";
        if(args.length > 6)
            text = args[6];
        gc.setStroke(intToColor(colorInt));

        switch ( args[0])
        {
            case "BRUSH":
            case "LINE":
                gc.lineTo(endX, endY);
                gc.stroke();
                break;
            case "OVAL":
                if (startX > endX)
                {
                    temp = startX;
                    startX = endX;
                    endX = temp;
                }
                if (startY > endY)
                {
                    temp = startY;
                    startY = endY;
                    endY = temp;
                }
                gc.strokeOval(startX, startY, abs(startX - endX), abs(startY - endY));
                break;
            case "CIRCLE":
                centerX = startX + (endX - startX) / 2;
                centerY = startY + (endY - startY) / 2;
                diameter = sqrt((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY));
                gc.strokeOval(centerX - diameter / 2, centerY - diameter / 2, diameter, diameter);
                break;
            case "RECTANGLE":
                if (startX > endX)
                {
                    temp = startX;
                    startX = endX;
                    endX = temp;
                }
                if (startY > endY)
                {
                    temp = startY;
                    startY = endY;
                    endY = temp;
                }
                gc.strokeRect(startX, startY, abs(startX - endX), abs(startY - endY));
                break;
            case "TEXT":
                gc.setLineWidth(1);
                gc.strokeText(textString, startX + textInput.getHeight() / 2, startY + textInput.getHeight() / 2);
                gc.setLineWidth(lineWidth);
            default:
                break;
        }

    }

    private int colorToInt(Color c)
    {
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        return (r << 16) | (g << 8) | b;
    }

    private Color intToColor(int value)
    {
        int r = (value >>> 16) & 0xFF;
        int g = (value >>> 8) & 0xFF;
        int b = value & 0xFF;
        return Color.rgb(r, g, b);
    }


}

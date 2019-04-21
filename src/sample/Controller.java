package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Controller {
    @FXML
    TextField xcmCenterField;
    @FXML
    TextField ycmCenterField;
    @FXML
    TextField rcField;
    @FXML
    Tab tabCircle;
    @FXML
    Tab singleCircleTab;
    @FXML
    TextField xcCenterField;
    @FXML
    TextField ycCenterField;
    @FXML
    Tab multiCircleTab;
    @FXML
    TextField rcBeginField;
    @FXML
    TextField rcEndField;
    @FXML
    TextField circlesCountField;
    @FXML
    Tab tabEllipse;
    @FXML
    Tab singleEllipseTab;
    @FXML
    TextField axisElField;
    @FXML
    TextField ordsElField;
    @FXML
    TextField xeCenterField;
    @FXML
    TextField yeCenterField;
    @FXML
    Tab multiEllipseTab;
    @FXML
    TextField xemCenterField;
    @FXML
    TextField yemCenterField;
    @FXML
    TextField stepOsField;
    @FXML
    TextField countElField;
    @FXML
    ChoiceBox algorithmChoice;
    @FXML
    ColorPicker colorChoice;
    @FXML
    Button drawChosenButton;
    @FXML
    Button drawBackgroundButton;
    @FXML
    Canvas canvas;
    @FXML
    Label cursorLabel;
    @FXML
    ColorPicker backgroundPicker;
    @FXML
    TextField aBeginField;
    @FXML
    TextField bBeginField;
    @FXML
    public void initialize() {
        ObservableList<String> algorithmsList = FXCollections.observableList(
                Arrays.asList(
                        DrawAlgorithms.ALG_CANONIC,
                        DrawAlgorithms.ALG_PARAMETRIC,
                        DrawAlgorithms.ALG_BRESENHAM,
                        DrawAlgorithms.ALG_MIDDLE_POINT,
                        DrawAlgorithms.ALG_LIB
                ));
        algorithmChoice.setItems(algorithmsList);
        algorithmChoice.getSelectionModel().selectFirst();
        canvas.getGraphicsContext2D().setFill(backgroundPicker.getValue());
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        backgroundPicker.setOnAction(actionEvent -> {
            canvas.getGraphicsContext2D().setFill(backgroundPicker.getValue());
            canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        canvas.setOnMouseMoved(mouseEvent -> {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
                cursorLabel.setText("Текущая позиция курсора:\n" + mouseEvent.getX() + "," + mouseEvent.getY());
            }
        });


        drawChosenButton.setOnMouseClicked(mouseEvent -> {
            draw(colorChoice);
        });

        drawBackgroundButton.setOnMouseClicked(mouseEvent -> {
            draw(backgroundPicker);
        });
    }

    private void draw(ColorPicker colorChoice) {
        try {
            String alg = (String) algorithmChoice.getSelectionModel().getSelectedItem();
            Color color = colorChoice.getValue();
            DrawAlgorithms drawAlgorithms = new DrawAlgorithms(canvas.getGraphicsContext2D(), backgroundPicker.getValue());
            if (tabCircle.isSelected()) {
                if (singleCircleTab.isSelected()) {
                    int xc = Integer.parseInt(xcCenterField.getText());
                    int yc = Integer.parseInt(ycCenterField.getText());
                    int radius = Integer.parseInt(rcField.getText());
                    drawAlgorithms.runSingleCircle(alg, xc, yc, radius, color);
                } else if (multiCircleTab.isSelected()) {
                    int xc = Integer.parseInt(xcmCenterField.getText());
                    int yc = Integer.parseInt(ycmCenterField.getText());
                    int rb = Integer.parseInt(rcBeginField.getText());
                    int re = Integer.parseInt(rcEndField.getText());
                    int count = Integer.parseInt(circlesCountField.getText());
                    drawAlgorithms.runMultiCircle(alg, xc, yc, rb, re, count, color);
                }
            } else if (tabEllipse.isSelected()) {
                if (singleEllipseTab.isSelected()) {
                    int xc = Integer.parseInt(xeCenterField.getText());
                    int yc = Integer.parseInt(yeCenterField.getText());
                    int a = Integer.parseInt(axisElField.getText());
                    int b = Integer.parseInt(ordsElField.getText());
                    if (a < 0 || b < 0) {
                        throw new Exception("Введены неверные данные!");
                    }
                    drawAlgorithms.runSingleEllipse(alg, xc, yc, a, b, color);
                } else if (multiEllipseTab.isSelected()) {
                    int xc = Integer.parseInt(xemCenterField.getText());
                    int yc = Integer.parseInt(yemCenterField.getText());
                    int d = Integer.parseInt(stepOsField.getText());
                    int aBeg = Integer.parseInt(aBeginField.getText());
                    int bBeg = Integer.parseInt(bBeginField.getText());
                    int count = Integer.parseInt(countElField.getText());
                    if (d <= 0 || count <= 0) {
                        throw new Exception("Введены неверные данные!");
                    }
                    drawAlgorithms.runMultiEllipse(alg, xc, yc, d, aBeg, bBeg, count, color);
                }
            }
        } catch (Exception e) {
            showError("Введены некорректные данные!");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("Произошла ошибка :(");
        alert.setHeaderText("ОШИБКА");
        alert.show();
    }
}

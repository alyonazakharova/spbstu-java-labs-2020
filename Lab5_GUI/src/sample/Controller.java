package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private TextField priceFromField;

    @FXML
    private TextField priceToField;

    @FXML
    private Button filterButton;

    @FXML
    private TextField titleField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button showPriceButton;

    @FXML
    private Button changePriceButton;

    @FXML
    private TextField newPriceField;

    @FXML
    private Button showAllButton;

    @FXML
    private Button addButton;

    @FXML
    private TextField prodidToAddField;

    @FXML
    private TextField titleToAddField;

    @FXML
    private TextField costToAddField;

    @FXML
    private TableView<Goods> table;

    @FXML
    private TableColumn<Goods, Integer> idColumn;

    @FXML
    private TableColumn<Goods, Integer> prodidColumn;

    @FXML
    private TableColumn<Goods, String> titleColumn;

    @FXML
    private TableColumn<Goods, Double> costColumn;

    @FXML
    private Button exitButton;

    @FXML
    private Label resultLabel;

    @FXML
    void initialize() {
        Database db = new Database();
        db.connect();
        db.fill();

        idColumn.setCellValueFactory(new PropertyValueFactory<Goods, Integer>("id"));
        prodidColumn.setCellValueFactory(new PropertyValueFactory<Goods, Integer>("prodid"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Goods, String>("title"));
        costColumn.setCellValueFactory(new PropertyValueFactory<Goods, Double>("cost"));

        showAllButton.setOnAction(event -> {
            resultLabel.setText("");
            table.setItems(db.showAll());
        });

        addButton.setOnAction(event -> {
            resultLabel.setText("");
            int prodid = 0;
            double cost = 0;
            try {
                prodid = Integer.parseInt(prodidToAddField.getText());
                cost = Double.parseDouble(costToAddField.getText());
            } catch (NumberFormatException e) {
                resultLabel.setText("ERROR: incorrect input");
                return;
            }
            try {
                db.addItem(prodid, titleToAddField.getText(), cost);
                resultLabel.setText("Item successfully added");
            } catch (MyException e) {
                resultLabel.setText(e.getMessage());
            }
        });

        deleteButton.setOnAction(event -> {
            if (!titleField.getText().isEmpty()) {
                try {
                    db.deleteItem(titleField.getText());
                    resultLabel.setText("Item successfully deleted");
                } catch (MyException e) {
                    resultLabel.setText(e.getMessage());
                }
            } else {
                resultLabel.setText("ERROR: enter title");
            }
        });

        showPriceButton.setOnAction(event -> {
            if (!titleField.getText().isEmpty()) {
                try {
                    Double price = db.price(titleField.getText());
                    resultLabel.setText("Price: " + price.toString());
                } catch (MyException e) {
                    resultLabel.setText(e.getMessage());
                }
            } else {
                resultLabel.setText("ERROR: enter title");
            }
        });

        changePriceButton.setOnAction(event -> {
            if (!titleField.getText().isEmpty() & !newPriceField.getText().isEmpty()) {
                double newPrice = 0;
                try {
                    newPrice = Double.parseDouble(newPriceField.getText());
                    db.changePrice(titleField.getText(), newPrice);
                    resultLabel.setText("Price changed successfully");
                } catch (NumberFormatException e) {
                    resultLabel.setText("ERROR: incorrect input");
                } catch (MyException e) {
                    resultLabel.setText(e.getMessage());
                }
            } else {
                resultLabel.setText("ERROR: enter title and new price");
            }
        });

        filterButton.setOnAction(event -> {
            double bound1 = 0;
            double bound2 = 0;
            try {
                bound1 = Double.parseDouble(priceFromField.getText());
                bound2 = Double.parseDouble(priceToField.getText());
                table.setItems(db.filterByPrice(bound1, bound2));
                resultLabel.setText("");
            } catch (NumberFormatException e) {
                resultLabel.setText("ERROR: incorrect input");
            }
        });

        exitButton.setOnAction(event -> {
            db.disconnect();
            Stage stage = (Stage)exitButton.getScene().getWindow();
            stage.close();
        });
    }
}


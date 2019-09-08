package main_controller.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main_controller.UserProcessClass;

import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable{

    @FXML TextField accountField;
    @FXML PasswordField passwordField;
    @FXML Button button_reg, button_log;
    @FXML GridPane scene_log;

    private UserProcessClass userp = UserProcessClass.getUserp();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button_log.setDisable(true);
    }

    @FXML public void buttonAction(ActionEvent event) throws Exception{
        // get current stage
        Stage currentStage = (Stage) scene_log.getScene().getWindow();
        Parent temp;
        if(event.getSource().equals(button_log)){
            if(userp.logIn(accountField.getText(), passwordField.getText())){
                temp = FXMLLoader.load(getClass().getResource("../fxml/user_main.fxml"));
                if(accountField.getText().equals("admin"))
                    temp = FXMLLoader.load(getClass().getResource("../fxml/admin_main.fxml"));
                currentStage.setTitle("Welcome, "+ accountField.getText());
                userp.setUser(accountField.getText());
                currentStage.setScene(new Scene(temp, 800, 600));
            }else{
                message("Account or Password is incorrect");
            }

        }else if(event.getSource().equals(button_reg)){
            temp = FXMLLoader.load(getClass().getResource("../fxml/register.fxml"));
            currentStage.setTitle("Register");
            currentStage.setScene(new Scene(temp, 800, 600));
        }
    }

    @FXML public void inputChecker(){
        String account = accountField.getText();
        String pw = passwordField.getText();
        boolean check = (account.isEmpty() || pw.isEmpty()) || (account.trim().isEmpty() || pw.trim().isEmpty());
        button_log.setDisable(check);
    }

    public void message(String message){
        // create a new window
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("message");
        window.setWidth(400);
        // create controls
        Label label = new Label(message);
        Button back = new Button("return");
        back.setOnAction(event -> window.close());
        // create a container and add controls to the container
        VBox container = new VBox();
        container.getChildren().addAll(label, back);
        container.setAlignment(Pos.CENTER);
        // create a scene with the container
        Scene scene = new Scene(container);
        // add the scene to the window
        window.setScene(scene);
        window.showAndWait();

    }
}

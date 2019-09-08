package main_controller.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main_controller.UserProcessClass;

import java.net.URL;
import java.util.ResourceBundle;


public class RegisterController implements Initializable {
    @FXML GridPane scene_register;
    @FXML TextField account;
    @FXML PasswordField password;
    @FXML Button submit, back_log;
    private UserProcessClass userp = UserProcessClass.getUserp();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submit.setDisable(true);
    }

    @FXML public void buttonAction(ActionEvent event) throws Exception{
        Stage currentStage = (Stage)scene_register.getScene().getWindow();
        Parent temp;
        if(event.getSource().equals(submit)){
            String acc = account.getText();
            if(!userp.accountExist(acc)){
                String pw = password.getText();
                userp.addUser(acc, pw);
                message("Account Register Succeed.");
            }else{
                message("Account Already Exited.");
            }
        }else if(event.getSource().equals(back_log)){
            temp = FXMLLoader.load(getClass().getResource("../fxml/log_in.fxml"));
            currentStage.setTitle("Craigslist App");
            currentStage.setScene(new Scene(temp, 800, 600));
        }
    }

    // check whether textfields empty
    @FXML public void inputChecker(){
        String ac = account.getText();
        String pw = password.getText();
        boolean check = (ac.isEmpty() || pw.isEmpty()) || (ac.trim().isEmpty() || pw.trim().isEmpty());
        submit.setDisable(check);
    }

    // pop window
    public void message(String message){
        // create a new window
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("message");
        window.setWidth(200);
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
        account.setText("");
        password.setText("");
        inputChecker();
        window.showAndWait();

    }


}

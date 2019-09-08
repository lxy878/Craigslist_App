package main_controller.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main_controller.FileProcessClass;
import main_controller.UserProcessClass;

import java.io.FileInputStream;
import java.util.List;


public class HistoryController {
    @FXML ComboBox modify_target;
    @FXML TextField modify_value;
    @FXML BorderPane user_history;
    @FXML Button button_back;
    @FXML Button button_delete;
    @FXML Button button_modify;
    @FXML Label region;
    @FXML Label place;
    @FXML Label price;
    @FXML ImageView imageView;
    @FXML TextArea content;
    @FXML
    ListView<String> list_result;


    private UserProcessClass userp = UserProcessClass.getUserp();

    public void initialize() {
        setHistory();
    }

    @FXML public void buttonAction(ActionEvent event) throws Exception{
        Stage current = (Stage) user_history.getScene().getWindow();

        if(event.getSource().equals(button_back)){
            Parent scene = FXMLLoader.load(getClass().getResource("../fxml/user_main.fxml"));
            current.setTitle("Welcome, "+userp.getUser());
            current.setScene(new Scene(scene, 800,600));
        }else if(event.getSource().equals(button_delete)){
            deleteHistory();
        }else if(event.getSource().equals(button_modify)){
            modifyHistory();
        }
    }

    private void deleteHistory(){
        String id = userp.getId(list_result.getSelectionModel().getSelectedIndex());
        userp.delete(id);
        setHistory();
    }

    private void modifyHistory(){
        String id = userp.getId(list_result.getSelectionModel().getSelectedIndex());
        String value = modify_value.getText();
        if(id.isEmpty() || value.isEmpty() || value.trim().isEmpty()) return;
        String target = modify_target.getSelectionModel().getSelectedItem().toString();
        userp.modify(id, target, value);
        setHistory();
        setContent(id);
        setOther(id);

    }

    private void setHistory(){
        List<String> titleList = userp.getTitleList();
        list_result.setItems(FXCollections.observableArrayList(titleList));
    }

    @FXML private void selected(){
        String id = userp.getId(list_result.getSelectionModel().getSelectedIndex());
        setImage(id);
        setContent(id);
        setOther(id);
    }

    private void setContent(String id){
        String con = userp.getContent(id);
        if(con.isEmpty()||con.trim().isEmpty()){
            con = userp.getDescription(id);
        }
        if(con.isEmpty()||con.trim().isEmpty()){
            con = userp.getTitle(id);
        }
        content.setText(con);
        content.setWrapText(true);
    }

    private void setOther(String id){
        region.setText("Region: "+userp.getRegion(id));
        place.setText("City: "+userp.getPlace(id));
        price.setText("Price: "+userp.getPrice(id));
    }

    private void setImage(String id){
        String imagePath = userp.getImagePath(id);
        if(imagePath.length()==0) imagePath = FileProcessClass.image_path+"no_image.png";

        try (FileInputStream input = new FileInputStream(imagePath)){
            imageView.setImage(new Image(input, 400, 200, false, false));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}

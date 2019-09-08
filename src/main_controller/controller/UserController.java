package main_controller.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;

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
import java.util.*;


public class UserController {
    @FXML Button button_history;
    @FXML BorderPane user_main;
    @FXML Button button_return, button_search;
    @FXML CheckBox cb_offline;
    @FXML
    ComboBox<String> city_list;
    @FXML
    ComboBox<String> search_type;
    @FXML TextField target, limit;
    @FXML
    ListView<String> list_result;
    @FXML ImageView imageView;
    @FXML TextArea content;
    @FXML Label region, place, price;

    private UserProcessClass userp = UserProcessClass.getUserp();
    private Hashtable<String, String> cities;

    public void initialize() {
        setCities();
        setType();
        inputChecker();
        listCheck();
        cities = FileProcessClass.read_and_set(FileProcessClass.path+"server.txt");
    }

    @FXML public void buttonAction(ActionEvent event) throws Exception{
        Stage currentStage = (Stage)user_main.getScene().getWindow();
        Parent temp;
        if(event.getSource().equals(button_history)){
            temp = FXMLLoader.load(getClass().getResource("../fxml/user_history.fxml"));
            currentStage.setTitle("History");
            currentStage.setScene(new Scene(temp, 800, 600));
        }else if(event.getSource().equals(button_return)){
            temp = FXMLLoader.load(getClass().getResource("../fxml/log_in.fxml"));
            currentStage.setTitle("Craigslist App");
            userp.setUser("");
            currentStage.setScene(new Scene(temp, 800, 600));
        }else if(event.getSource().equals(button_search)){
            if(!cb_offline.isSelected()) online();
            else offline();
            listCheck();
        }
    }

    @FXML public void inputChecker(){
        String input = target.getText();
        boolean check = (input.isEmpty() || input.trim().isEmpty());
        button_search.setDisable(check);
    }


    private void setType(){
        Hashtable<String, String> types = FileProcessClass.read_and_set(FileProcessClass.path+"searchType.txt");
        List<String> list = new ArrayList<>(types.keySet());
        // set default search type as housing
        search_type.setValue("housing");
        search_type.setItems(FXCollections.observableArrayList(list));
    }

    private void setCities(){
        Hashtable<String, String> cities = FileProcessClass.read_and_set(FileProcessClass.path+"server.txt");
        List<String> list = new ArrayList<>(cities.keySet());
        // set default city as new york
        city_list.setValue("new york");
        city_list.setItems(FXCollections.observableArrayList(list));
    }

    private void online(){
        String input = "serverCity="+city_list.getValue()+";searchType="+search_type.getValue()
                +";search="+target.getText()+";limit="+limit.getText();
        List<String> titleList = userp.online(input);
        list_result.setItems(FXCollections.observableArrayList(titleList));
    }

    private void offline(){
        String search = target.getText();
        String city = city_list.getValue();
        String url = cities.get(city);
        List<String> titleList = userp.offline(url, search);
        list_result.setItems(FXCollections.observableArrayList(titleList));
    }

    @FXML public void selected() {
        String id = userp.getId(list_result.getSelectionModel().getSelectedIndex());
        setImage(id);
        setContent(id);
        setOther(id);
    }

    private void setContent(String id){
        String con = userp.getContent(id);
        if(con.isEmpty()||con.trim().isEmpty()) con = userp.getDescription(id);
        if(con.isEmpty()||con.trim().isEmpty()) con = userp.getTitle(id);
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

    @FXML public void checkBox(){
        boolean clikecd = cb_offline.isSelected();
        limit.setDisable(clikecd);
        search_type.setDisable(clikecd);
    }

    private void listCheck(){
        boolean disable = list_result.getItems().isEmpty();
        list_result.setDisable(disable);
    }

}

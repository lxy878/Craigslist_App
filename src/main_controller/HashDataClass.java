package main_controller;

import java.io.*;
import java.util.*;

class HashDataClass{
    private Hashtable<String, Hashtable<String, String>> data;
    private Hashtable<String, Hashtable<String, HashSet<String>>> data_user;

    private final String[] keys_data = {"url", "description", "placename", "region", "image",
                                            "title", "type", "price", "content", "timestamp"};
    private final String[] keys_user = {"password", "url_collection"};

    HashDataClass(){
        data = new Hashtable<>();
        data_user = new Hashtable<>();
        loadData(FileProcessClass.storage_path+"data.txt");
        loadData_user(FileProcessClass.storage_path+"userData.txt");
    }

    private void addData(String key, Hashtable<String, String> info){
        if(exist_data(key)) return;
        data.put(key, info);
    }

    // load data from local storage
    private void loadData(String dataFile){
        hb_data(dataFile);
    }

    // rebuild data storage
    public void rebuild(String dataFile, String userFile){
        data.clear();
        data_user.clear();
        loadData(dataFile);
        loadData_user(userFile);
        saveData(FileProcessClass.storage_path+"data.txt");
        saveData_user(FileProcessClass.storage_path+"userData.txt");
        System.out.println("hashtable was rebuilt");
    }
    // add data from temp file to hashtable
    void hb_data(String inputFile){
        // store data base on the format
        try{
            BufferedReader reader = new BufferedReader(new FileReader(new File(inputFile)));
            String line;
            Hashtable<String, String> url_pack=null;
            String id = "";
            while((line = reader.readLine()) != null){
                String[] parts = line.split("=\"");
                // line skip
                if(parts.length < 2) continue;

                switch (parts[0]) {
                    case "image": {
                        if (exist_data(id)) continue;
                        String key = parts[0];
                        // value can be either a url or a local directory
                        String value = parts[1].substring(0, parts[1].length() - 1);
                        // value is a url, then download a image
                        if (value.contains("https://")) {
                            value = URLProcessClass.load_image(value, id);
                        }
                        url_pack.put(key, value);
                        break;
                    }
                    case "url_id":  // data reader begins with id
                        id = parts[1].replace("\"", "");
                        Hashtable<String, String> current = new Hashtable<>();
                        url_pack = current;
                        break;
                    case "timestamp": { // date reader ends with timestamp
                        String key = parts[0];
                        String value = parts[1].substring(0, parts[1].length() - 1);
                        url_pack.put(key, value);
                        lastCheck(url_pack);
                        addData(id, url_pack);
                        break;
                    }
                    default: {
                        String key = parts[0];
                        String value = parts[1].substring(0, parts[1].length() - 1);
                        url_pack.put(key, value);
                        break;
                    }
                }
            }
            reader.close();
        }catch (Exception e){
            System.out.println("save data in data process error: "+ e.fillInStackTrace());
        }
    }

    HashSet<String> get_urlset(String user){
        if(!data_user.containsKey(user)) return null;
        return data_user.get(user).get("url_collection");
    }

    // save data to the local storage
    void saveData(String path){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            for(String key : data.keySet()){
                writer.write("url_id=\""+key+"\"\n");
                Hashtable<String, String> subtable = data.get(key);
                for(String subkey : keys_data){
                    String value = subtable.get(subkey);
                    writer.write(subkey+"=\""+value+"\"\n");
                }
                writer.write("\n");
            }
            writer.close();
        }catch (Exception e){
            System.out.println("save to local in data process error");
        }
    }

    // check missing elements
    private void lastCheck(Hashtable<String, String> subtable){
        for(String key : keys_data){
            // find a missing element, then add it.
            if(!subtable.containsKey(key)) subtable.put(key,"");
        }
    }

    Hashtable<String, Hashtable<String, String>> getData(){
       return data;
    }

    private boolean exist_data(String id){
        return data.containsKey(id);
    }

    void print_data(){
        System.out.println("The size of Hash table: "+data.size());
        for(String key : data.keySet()){
            System.out.print(key+": ");
            Hashtable<String, String> subtable = data.get(key);
            for(String subkey : subtable.keySet()){
                String value =subtable.get(subkey);
                System.out.print("["+subkey+": "+value+"] ");
            }
            System.out.println(" ");
        }
    }

    boolean userExisted (String account){
        return data_user.containsKey(account);
    }

    boolean passwordCheck(String acc, String pw){
        return data_user.get(acc).get("password").contains(pw);
    }

    void addUser(String account, String password){
        HashSet<String> pwd = new HashSet<>();
        HashSet<String> urls = new HashSet<>();
        pwd.add(password);
        Hashtable<String, HashSet<String>> user_info = new Hashtable<>();

        user_info.put("password", pwd);
        user_info.put("url_collection", urls);
        data_user.put(account, user_info);
        saveData_user(FileProcessClass.storage_path+"userData.txt");
    }

    void saveData_user(String path){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            for(String account : data_user.keySet()){
                writer.write("account="+account+"\n");
                Hashtable<String, HashSet<String>> userInf =data_user.get(account);
                for(String element : keys_user){
                    String set = userInf.get(element).toString();
                    writer.write(element+"="+set+"\n");
                }
                writer.write("\n");
            }
            writer.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void loadData_user(String path){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            String line;
            Hashtable<String, HashSet<String>> user_info = null;
            while((line = reader.readLine()) != null){
                String[] parts = line.split("=");
                if(parts.length < 2) continue;
                if(line.contains("account")){
                    Hashtable<String, HashSet<String>> newUser = new Hashtable<>();
                    user_info = newUser;
                    data_user.put(parts[1], newUser);
                }else {
                    String fixedString = parts[1].substring(1,parts[1].length()-1);
                    HashSet<String> newSet;
                    if(!fixedString.isEmpty()){
                        String[] elements = fixedString.split(", ");
                        newSet = new HashSet<>(Arrays.asList(elements));
                    }else{
                        newSet = new HashSet<>();
                    }
                    user_info.put(parts[0], newSet);
                }
            }
            reader.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    void print_user(){
        for(String user : data_user.keySet()){
            System.out.print(user+": [");
            Hashtable<String, HashSet<String>> curr = data_user.get(user);
            for(String info : curr.keySet()) System.out.print(info+":"+ curr.get(info)+", ");
            System.out.println("]");
        }
    }
}

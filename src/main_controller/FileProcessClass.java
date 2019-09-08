
package main_controller;

import java.io.*;
import java.util.Hashtable;
import java.util.List;

public class FileProcessClass {
    //get the different kind of separators based on the current OS
    private static final String separator = System.getProperty("file.separator");
    //get current directory
    public static final String path = System.getProperty("user.dir")+separator;
    // the directory of image files
    public static final String image_path = path+"image"+separator;
    // the directory of user_data and info_data
    static final String storage_path = path+"storage"+separator;
    // the directory of temp_data
    static final String temp_path = path+"temp"+separator;

    private URLProcessClass urlp;
    private HashDataClass hb;

    // constructor
    FileProcessClass(){
        urlp = new URLProcessClass();
        hb = new HashDataClass();
    }
    // read lines from the given file name
    void fileReader(String name){
        try{
            BufferedReader fileReader = new BufferedReader(new FileReader(new File(path+name)));
            String line;
            // create target urls from input file and add to a set
            while((line = fileReader.readLine()) != null){
                fileProcessor(line);
            }
        }catch(Exception e){
            System.out.println("file process reader error");
        }
    }

    void fileProcessor(String input){
        //create target url
        String url = urlp.urlConstructor(input);
        // add to a set
        urlp.addTargetSet(url);
    }

    List<String> search(String user){
        // results/urls from target urls set
        return urlp.searchFrom(hb.get_urlset(user));
    }

    void saveData(){
        System.out.println("Saving data to hashtable");
        // read data from temp file and store data into hashtable
        hb.hb_data(temp_path+"temp.txt");
        // save data in hashtable to local storage
        hb.saveData(storage_path+"data.txt");
        hb.saveData_user(storage_path+"userData.txt");
        System.out.println("data saved to hashtable");
    }

    HashDataClass get_hd(){
        return hb;
    }

    // output data to the given file name
    void fileWriter(String outputName){
        try{
            // read from temp file
            BufferedReader reader = new BufferedReader(new FileReader(new File(temp_path+"temp.txt")));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path+outputName)));
            String line;
            // write to output file
            while((line=reader.readLine()) != null){
                if(line.contains("url=")|| line.contains("description=")
                        || line.contains("title=") || line.contains("placename=")
                        || line.contains("region")){
                    writer.write(line+"\n");
                }else if(line.contains("price=") ){
                    writer.write(line+"\n\n");
                }
            }
            writer.close();
            reader.close();
        }catch (Exception e){
            System.out.println("file writer in file process error");
        }
    }

    // read lines from the given file and return a hashtable of lines
    public static Hashtable<String,String> read_and_set(String filepath){
        Hashtable<String, String> table = new Hashtable<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
            String line;
            while((line=reader.readLine()) != null){
                int comma = line.indexOf(",");
                String data = line.substring(0,comma-1);
                String key = line.substring(comma+1);
                table.put(key, data);
            }
            reader.close();
        }catch (Exception e){
            System.out.println("read and set in FileProcess error");
        }
        return table;
    }

    void print_servers(){
        urlp.print_servers();
    }

    void print_searchType(){
        urlp.print_searchType();
    }

    void rebuild(){
        hb.rebuild(storage_path+"backup_data.txt", storage_path+"backup_user.txt");
    }
}

package main_controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class URLProcessClass {

    private HashSet<String> targetSet;
    private HashSet<String> resultSet;
    private Hashtable<String, String> serverTable;
    private Hashtable<String, String> searchTable;
    private String nextPage = "";
    private boolean hasNextPage = false;

    private String regex_url = "https:\\/\\/[a-zA-Z0-9+./&-?_]*";
    // Limte the number of results
    private int limited_number=-1;

    URLProcessClass(){
        targetSet = new HashSet<>();
        resultSet = new HashSet<>();
        // set servers and search options
        setOptions();
    }

    // create url
    String urlConstructor(String line){
        String[] terms = line.split(";");
        String url_base="", searchType="housing", search="";
        for(String term: terms){
            term = term.replace("; ","");
            if(term.contains("serverCity=")){
                term=term.replace("serverCity=","");
                // find out server by city name
                if(serverTable.containsKey(term)) url_base= serverTable.get(term);
                // otherwise, use the server of new york city
                else url_base= serverTable.get("new york");

            }else if(term.contains("searchType=")){
                term=term.replace("searchType=","");
                if(searchTable.containsKey(term)){
                    searchType = searchTable.get(term);
                }
            }else if(term.contains("search=")){
                    term=term.replace("search=","");
                    search = searchGenerator(term);
            }else if(term.contains("limit=")){
                term=term.replace("limit=","");
                limited_number = Integer.parseInt(term);
            }
        }
        return url_base+"/search/"+searchType+"?sort=rel&query="+search;
    }

    private String searchGenerator(String term){
        return term.replaceAll("\\+","%2B").replaceAll("/","-").replaceAll(" ","+");
    }

    void addTargetSet(String url){
        targetSet.add(url);
    }

    // level-1 open link: get resulted urls from urls in file
    List<String> searchFrom(HashSet<String> user) {
        for (String url : targetSet) {
            // download a result page
            String path_html = load_html(url);
            // collect links from result page
            resultedLinks(path_html);
            while (hasNextPage) {
                hasNextPage = false;
                path_html = load_html(nextPage);
                resultedLinks(path_html);
            }
            // show number of results
            System.out.println(resultSet.size()+" are found from "+url);
        }
        targetSet.clear();

        // retrieve elements from each resulted link and add to a temp file
        return getInfo(user);
    }

    // collect urls from the local file
    private void resultedLinks(String path){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            String line;
            // read html file
            while((line = reader.readLine()) != null) {
                if(resultSet.size() == limited_number){
                    break;
                }
                if (line.contains("class=\"result-title hdrlnk\"")) {
                    // fix the line to a url
                    String fixed_url = regexFix(line, regex_url);
                    //add them to resultset
                    resultSet.add(fixed_url);
                }
                // record more links when there are more than one pages
                if (line.contains("<link rel=\"next\"")) {
                    hasNextPage = true;
                    nextPage = regexFix(line, regex_url);
                }
            }
            reader.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    // level-2 open link: results/urls from target urls set to result set
    private List<String> getInfo(HashSet<String> user){
        String path_text = FileProcessClass.temp_path+"temp.txt";
        List<String> idList = new ArrayList<>();
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path_text)));
            for(String url : resultSet){
                // download html
                String path_html = load_html(url);
                // show results
                System.out.println(url);
                BufferedReader reader = new BufferedReader(new FileReader(new File(path_html)));
                String line, text, content="";
                // id and the current url
                String regex_id = "\\/[0-9]*.html";
                String id = regexFix(url, regex_id);
                id = id.replace(".html","").replace("/","");
                idList.add(id);
                if(user != null){
                    user.add(id);
                }
                writer.write("url_id=\""+id+"\"\n");
                writer.write("url=\""+url+"\"\n");
                // record elements from temp html to temp text file
                while ((line = reader.readLine()) != null){
                    // get id .... and image url
                    String regex_all = "content=\"[\"A-Za-z\\s(),.0-9!$-/?*+#@:×–';“”=~’（]+\"";
                    if(line.contains("class=\"price\"")){ 	// get price
                        String regex_price = "\\$[0-9.]+";
                        text = regexFix(line, regex_price);
                        text = "price=\""+text+"\"";
                        writer.write(text+"\n");
                    }else if(line.contains("<meta property=\"og:image\"")){ 	//get image
                        text = regexFix(line, regex_url);
                        text = "image=\""+text+"\"";
                        writer.write(text+"\n");
                    }else if(line.contains("<meta name=\"description\"")){	// get description
                        text = regexFix(line, regex_all);
                        text = "description=\""+text+"\"";
                        writer.write(text+"\n");
                    }else if(line.contains("<meta name=\"geo.placename\"")){	// get city
                        String regex_placename = "content=\"[A-Za-z,0-9\\s]+\"";
                        text = regexFix(line, regex_placename);
                        text = "placename=\""+text+"\"";
                        writer.write(text+"\n");
                    }else if(line.contains("<meta name=\"geo.region\"")){	// get state
                        String regex_region = "content=\"[A-Za-z-]+\"";
                        text = regexFix(line, regex_region);
                        text = "region=\""+text+"\"";
                        writer.write(text+"\n");
                    }else if(line.contains("<meta property=\"og:title\"")){	// get title
                        text = regexFix(line, regex_all);
                        text = "title=\""+text+"\"";
                        writer.write(text+"\n");
                    }else if(line.contains("<meta property=\"og:type\"")){	// get article type
                        String regex_type = "content=\"[a-z]+\"";
                        text = regexFix(line, regex_type);
                        text = "type=\""+text+"\"";
                        writer.write(text+"\n");
                    }else if(line.contains("<br>")){				// get main content from the page
                        //replace line skipping to "newLine"
                        String t = line.replaceAll("<br>", "[newLine]");
                        content += t+"[newLine]";
                    }
                }
                content=content.replaceAll("<a href=\"[_>/A-Za-z0-9\"\\s=-]+</a>","");
                writer.write("content=\""+content+"\"\n");
                String timestemp ="timestamp=\""+getTime()+"\"";
                writer.write(timestemp+"\n\n");
                reader.close();
            }
            resultSet.clear();
            writer.close();
        }catch (Exception e){
            System.out.println("url process writer error");
        }
        return idList;
    }

    private String regexFix(String line, String regex) {
        //replace special characters
        String fixed_line = line.replaceAll("amp;", "").replaceAll("&#39;", "'");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fixed_line);
        while (matcher.find()) {
            // avoid empty string
            if (matcher.group().length() != 0) {
                // remove white spaces
                String s = matcher.group().trim();
                s = s.replace("content=", "").replaceAll("\"","");
                return s;
            }
        }
        return "";
    }

    // download html form of url and return the local directory
    private String load_html(String url){
        String html_path = FileProcessClass.temp_path+"temp.html";
        try{
            URLConnection con = (new URL(url)).openConnection();
            con.setRequestProperty("Accept", "text/html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(html_path)));
            String line;
            while((line = reader.readLine()) != null){
                writer.write(line+"\n");
            }
            reader.close();
            writer.close();
        }catch (Exception e){
            System.out.println("retrieve html in url process error");
        }
        return html_path;
    }

    // download image and return the local directory
    static String load_image(String url, String id){
        String location = FileProcessClass.image_path+id+".jpg";
        try{
            URLConnection con = (new URL(url)).openConnection();
            InputStream inputStream = con.getInputStream();
            BufferedImage image = ImageIO.read(inputStream);
            FileOutputStream outputStream = new FileOutputStream(location);
            ImageIO.write(image, "jpg", outputStream);
            outputStream.close();
            inputStream.close();
        }catch (Exception e){
            System.out.println("load image in url process error");
        }
        return location;
    }

    // current time
    private String getTime(){
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss-a-MM-dd-yyyy");
        return ft.format(dNow);
    }
    // set options of servers and search types
    private void setOptions(){
        serverTable = FileProcessClass.read_and_set(FileProcessClass.path+"server.txt");
        searchTable = FileProcessClass.read_and_set(FileProcessClass.path+"searchType.txt");
    }

    void print_servers(){
        int n = 1;
        System.out.print("{");
        for(String key : serverTable.keySet()) {
            System.out.print(key+", ");
            if(n%10==0) System.out.println(" ");
            n++;
        }
        System.out.println("}");
    }

    void print_searchType(){
        int n = 1;
        System.out.print("{");
        for(String key : searchTable.keySet()) {
            System.out.print(key+", ");
            if(n%10==0) System.out.println(" ");
            n++;
        }
        System.out.println("}");
    }
}

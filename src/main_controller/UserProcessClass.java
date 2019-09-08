package main_controller;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class UserProcessClass {
    private final FileProcessClass filep = new FileProcessClass();
    private final static UserProcessClass userp = new UserProcessClass();
    private final HashDataClass hd = filep.get_hd();
    private String user="";
    private List<String> idList = new ArrayList<>();

    public static UserProcessClass getUserp(){
        return userp;
    }

    public boolean logIn(String account, String password){
        return accountExist(account) && passwordCheck(account, password);
    }

    public boolean accountExist(String account){
        return hd.userExisted(account);
    }

    private boolean passwordCheck(String account, String password){
        return hd.passwordCheck(account, password);
    }

    public void  addUser(String account, String password){
        hd.addUser(account, password);
    }

    public List<String> online(String input){
        filep.fileProcessor(input);
        List<String> list = filep.search(user);
        filep.saveData();
        return getTitleList(list);
    }

    // offline for users
    public List<String> offline(String url, String term){
        Hashtable<String, Hashtable<String, String>> data = hd.getData();
        List<String> results = new ArrayList<>();
        for(String key : data.keySet()){
            Hashtable<String, String> info = data.get(key);
            if(info.get("url").contains(url) && (info.get("title").contains(term)
                    || info.get("description").contains(term) || info.get("content").contains(term))){
                // add to id list
                results.add(key);
            }
        }
        return getTitleList(results);
    }

    // offline for admin
    public List<String> offline(String url, String term, String users){
        if(users.isEmpty()) return offline(url, term);
        String[] accounts = users.split(" ");
        List<String> list = new ArrayList<>();
        for(String acc : accounts) list.addAll(hd.get_urlset(acc));

        Hashtable<String, Hashtable<String, String>> data = hd.getData();
        HashSet<String> resultSet = new HashSet<>();
        for(String id : list){
            Hashtable<String, String> info = data.get(id);
            if(info.get("url").contains(url) && (info.get("title").contains(term)
                    || info.get("description").contains(term) || info.get("content").contains(term))){
                resultSet.add(id);
            }
        }
        return getTitleList(resultSet);
    }

    public void rebuildData(){
        filep.rebuild();
    }

    private List<String> getTitleList(HashSet<String> set){
        return getTitleList(new ArrayList<>(set));
    }

    public List<String> getTitleList(){
        return getTitleList(new ArrayList<>(hd.get_urlset(this.user)));
    }

    private List<String> getTitleList(List<String> idList){
        this.idList = idList;
        List<String> titleList = new ArrayList<>();
        for(String id : idList){
            String title = hd.getData().get(id).get("title");
            if(title.isEmpty()|| title.trim().isEmpty()) title = "No Title";
            titleList.add(title);
        }
        return titleList;
    }

    public String getId(int index){
        if(idList.isEmpty()) return "";
        return idList.get(index);
    }

    public void delete(String id){
        hd.get_urlset(this.user).remove(id);
    }

    public void modify(String id, String target, String value){
        hd.getData().get(id).replace(target, value);
    }

    public void setUser(String account){
        user = account;
    }

    public String getImagePath(String id){
        return hd.getData().get(id).get("image");
    }

    public String getUser(){
        return user;
    }

    public String getRegion(String id){
        return hd.getData().get(id).get("region");
    }

    public String getPlace(String id){
        return hd.getData().get(id).get("placename");
    }

    public String getPrice(String id){
        return hd.getData().get(id).get("price");
    }

    public String getContent(String id){
        String content = hd.getData().get(id).get("content");
        String[] lines = content.split("\\[newLine]");
        String fixedContent ="";
        for(String line : lines) if (!line.trim().isEmpty() && !line.isEmpty()) fixedContent += line + "\n";
        return fixedContent;
    }

    public String getDescription(String id){
        return hd.getData().get(id).get("description");
    }

    public String getTitle(String id){
        return hd.getData().get(id).get("title");
    }

}

package com.project.server;

import com.project.server.model.ClientModel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    private final String DATABASE_PATH = "src/main/resources/com/project/server/database.txt";
    private ArrayList<ClientModel> clients;

    public Database(){
        clients = new ArrayList<>();
        onLoad();
    }

    private void onLoad(){
        try{
            File file = new File(DATABASE_PATH);
            if (file.createNewFile())
                System.out.println("File created: " + file.getName());
            else
                System.out.println("File already exists.");

            FileWriter writer = new FileWriter(DATABASE_PATH, false);

            String json = loadUsers();

            writer.write(json);
            writer.close();
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    private String loadUsers(){
        StringBuilder json = new StringBuilder("[\n");
        try{
            HashMap<String, String> accounts = new HashMap<>(3);

            accounts.put("stefano.cipolletta@unito.it", "stefano.cipolletta");
            accounts.put("matteo.barone@unito.it", "matteo.barone");
            accounts.put("alessio.rosa@unito.it", "alessio.rosa");

            for(String email : accounts.keySet()){
                String password = accounts.get(email);
                json.append(String.format("\t{\n\t\t\"email\":\"%s\",\n\t\t\"password\":\"%s\"\n\t},\n", email, password));
            }
            json.append("]");

        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }

        return json.toString();
    }

}

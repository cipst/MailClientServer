package com.project.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import com.project.models.Email;

public class Database {

    private final String DATABASE_PATH = "src/main/resources/com/project/server/database.dat";
    private final String EMAIL_PATH = "src/main/resources/com/project/server/emails/";
    private HashMap<String, String> clientsConnected;

    public Database(){
        clientsConnected = new HashMap<>();
        onLoad();
//        Email email = new Email("stefano.cipolletta@unito.it", new ArrayList<>(){
//            {
//                add("alessio.rosa@unito.it");
//                add("matteo.barone@unito.it");
//            }
//        }, "Ciao", "Come stai?");
//        insertEmail(email);
    }

    private void onLoad(){
        try{
            File file = new File(DATABASE_PATH);
            if (file.createNewFile())
                System.out.println("File created: " + file.getName());
            else
                System.out.println("File already exists.");

            HashMap<String, String> accounts = new HashMap<>(3);

            accounts.put("stefano.cipolletta@unito.it", "stefano.cipolletta");
            accounts.put("matteo.barone@unito.it", "matteo.barone");
            accounts.put("alessio.rosa@unito.it", "alessio.rosa");

            ObjectOutputStream accountsOut = new ObjectOutputStream(new FileOutputStream(DATABASE_PATH));
            accountsOut.writeObject(accounts);
            accountsOut.close();
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

    public boolean userExist(String email) {
        boolean exist = false;
        ObjectInputStream accounts = null;

        try {
            accounts = new ObjectInputStream(new FileInputStream(DATABASE_PATH));
            HashMap<String, String> accountsMap = (HashMap<String, String>) accounts.readObject();

            if(accountsMap.containsKey(email))
                exist = true;

        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }finally {
            try{
                assert accounts != null;
                accounts.close();
            } catch (IOException e) {
                System.out.println("ERROR: " + e);
            }
        }

        return exist;
    }


    // TODO: Scrivere il metodo per inserire una mail nel file corrispondente
    public void insertEmail(Email email) {
        boolean allRecipientsExists = true;
        boolean currentRecipientExists = true;
        ArrayList<String> wrongRecipients = new ArrayList<>();

        // check all recipients must be valid
        for (String recipient : email.getRecipients()){
            currentRecipientExists = userExist(recipient);
            if(!currentRecipientExists){
                wrongRecipients.add(recipient);
            }

            allRecipientsExists &= currentRecipientExists;
        }

        if(!allRecipientsExists) throw new Error("One or more recipients doesn't exist: "+wrongRecipients);

        try{
            for (String recipient: email.getRecipients()) {
                // TODO: creare cartella dell'utente che riceve all'interno di src/main/resources/com/project/server/emails
                File accountsFile = new File(EMAIL_PATH+recipient);
                accountsFile.mkdirs();
                accountsFile.createNewFile();
            }
        }catch(Exception e){
            System.out.println("ERROR: " + e);
        }


        // TODO: creare file con la data dell'invio

        // TODO: scrivere l'email nel file
    }
}

package com.project.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.models.EmailSerializable;
import com.project.server.controller.LogController;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    private final String DATABASE_PATH = "src/main/resources/com/project/server/database.json";
    private final String EMAILS_PATH = "src/main/resources/com/project/server/emails";

    public Database() {
        onLoad();
    }

    private void onLoad() {
        try {
            File file = new File(DATABASE_PATH);
            if (file.createNewFile())
                System.out.println("File created: " + file.getName());
            else
                System.out.println("File already exists.");

            HashMap<String, String> accounts = new HashMap<>(3);

            accounts.put("stefano.cipolletta@unito.it", "stefano.cipolletta");
            accounts.put("matteo.barone@unito.it", "matteo.barone");
            accounts.put("alessio.rosa@unito.it", "alessio.rosa");

            Gson g = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(DATABASE_PATH);
            writer.write(g.toJson(accounts));
            writer.close();
        } catch (Exception e) {
            System.out.println("ERROR onLoad: " + e);
        }
    }

    public boolean userExist(String email) {
        boolean exist = false;

        try {
            Reader reader = new FileReader(DATABASE_PATH);
            Gson g = new Gson();

            HashMap<String, String> accountsMap = g.fromJson(reader, HashMap.class);
            reader.close();

            if (accountsMap.containsKey(email))
                exist = true;

        } catch (Exception e) {
            System.out.println("ERROR userExist: " + e);
        }

        return exist;
    }

    public boolean checkCredentials(String email, String password) {
        boolean areValid = false;

        try {
            Reader reader = new FileReader(DATABASE_PATH);
            Gson g = new Gson();

            HashMap<String, String> accountsMap = g.fromJson(reader, HashMap.class);
            reader.close();

            if (accountsMap.containsKey(email) && accountsMap.get(email).equals(password))
                areValid = true;

        } catch (Exception e) {
            System.out.println("ERROR login: " + e);
        }

        return areValid;
    }

    private void writeEmail(EmailSerializable email, String to) {
        String emailFileName = email.getDate().split(" ")[0];
        String emailFilePath = EMAILS_PATH + "/" + to + "/" + emailFileName + ".json";

        try {
            Reader reader = new FileReader(emailFilePath);
            Gson g = new GsonBuilder().setPrettyPrinting().create();

            ArrayList<EmailSerializable> emails = g.fromJson(reader, ArrayList.class);
            emails.add(email);
            reader.close();

            FileWriter writer = new FileWriter(emailFilePath);
            writer.write(g.toJson(emails));
            writer.close();
        } catch (Exception e) {
            System.out.println("ERROR write: " + e);
        }
    }

    private ArrayList<EmailSerializable> readEmailsByDate(String to, String date) {
        String emailFilePath = EMAILS_PATH + "/" + to + "/" + date + ".json";
        ArrayList<EmailSerializable> emails = null;

        try {
            Reader reader = new FileReader(emailFilePath);
            Gson g = new Gson();

            emails = g.fromJson(reader, ArrayList.class);
            reader.close();
        } catch (Exception e) {
            System.out.println("ERROR read: " + e);
        }

        return emails;
    }

    public HashMap<String, ArrayList<EmailSerializable>> readAllEmails(String to) {
        String emailFilePath = EMAILS_PATH + "/" + to + "/";
        HashMap<String, ArrayList<EmailSerializable>> emails = new HashMap<>();

        try {
            File emailFile = new File(emailFilePath);
            File[] files = emailFile.listFiles();

            assert files != null;
            for (File file : files) {
                String date = file.getName().substring(0, file.getName().length() - 4);
                emails.put(date, readEmailsByDate(to, date));
            }
        } catch (Exception e) {
            System.out.println("ERROR read: " + e);
        }

        return emails;
    }

    public void insertEmail(EmailSerializable email) {
        try {
            // make a copy of the recipients list and add the sender to save the email in his outbox
            ArrayList<String> accounts = new ArrayList<>(email.getRecipients());
            accounts.add(0, email.getSender());

            for (String account : accounts) {
                File accountsFile = new File(EMAILS_PATH + "/" + account);
                accountsFile.mkdirs();

                String emailFileName = email.getDate().split(" ")[0];
                String emailFilePath = EMAILS_PATH + "/" + account + "/" + emailFileName + ".json";

                File emailFile = new File(emailFilePath);
                if (emailFile.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(emailFilePath);
                    fileWriter.write("[]");
                    fileWriter.close();
                }

                writeEmail(email, account);
                if (account.equals(email.getSender()))
                    LogController.emailSent(email.getSender(), (ArrayList<String>) email.getRecipients());
                else
                    LogController.emailReceived(account);
            }
        } catch (Exception e) {
            System.out.println("ERROR insertEmail: " + e);

        }
    }
}

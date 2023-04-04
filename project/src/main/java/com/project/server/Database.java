package com.project.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
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

            for (String account : accounts.keySet()) {
                File accountsFile = new File(EMAILS_PATH + "/" + account);
                if (accountsFile.mkdirs()) {
                    String statsPath = EMAILS_PATH + "/" + account + "/" + "stats.json";

                    File stats = new File(statsPath);
                    stats.createNewFile();

                    HashMap<String, String> counter = new HashMap<>();
                    counter.put("emailReceived", "0");

                    FileWriter writerStats = new FileWriter(statsPath);
                    writerStats.write(g.toJson(counter));
                    writerStats.close();
                }
            }
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
        String emailFileName = email.getDate().split(" ")[0].replaceAll("/", "-");
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

    private ArrayList<EmailSerializable> readEmailsByDate(String address, String date) {
        String emailFilePath = EMAILS_PATH + "/" + address + "/" + date + ".json";
        ArrayList<EmailSerializable> emails = new ArrayList<>();

        try {
            Reader reader = new FileReader(emailFilePath);
            Gson g = new Gson();

            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            reader.close();

            for (int i = 0; i < array.size(); i++) {
                emails.add(g.fromJson(array.get(i), EmailSerializable.class));
            }

        } catch (Exception e) {
            System.out.println("ERROR read emails by date: " + e);
            e.printStackTrace();
        }

        return emails;
    }

    public ArrayList<EmailSerializable> readAllEmails(String address) {
        String emailFilePath = EMAILS_PATH + "/" + address + "/";
        ArrayList<EmailSerializable> emails = new ArrayList<>();

        try {
            File emailFile = new File(emailFilePath);
            File[] files = emailFile.listFiles();

            assert files != null;
            for (File file : files) {
                if (file.getName().compareTo("stats.json") != 0) {
                    String date = file.getName().substring(0, file.getName().length() - 5);
                    emails.addAll(readEmailsByDate(address, date));
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR read all emails: " + e);
        }

        return emails;
    }

    public int readStats(String address) {
        String statsPath = EMAILS_PATH + "/" + address + "/" + "stats.json";
        int id = 0;

        try {
            Reader reader = new FileReader(statsPath);
            Gson g = new Gson();

            HashMap<String, String> stats = g.fromJson(reader, HashMap.class);
            reader.close();

            id = Integer.parseInt(stats.get("emailReceived"));
        } catch (Exception e) {
            System.out.println("ERROR read stats: " + e);
        }

        return id;
    }

    private void incrementStats(String account) {
        String statsPath = EMAILS_PATH + "/" + account + "/" + "stats.json";

        try {
            Reader reader = new FileReader(statsPath);
            Gson g = new GsonBuilder().setPrettyPrinting().create();

            HashMap<String, String> stats = g.fromJson(reader, HashMap.class);
            reader.close();

            stats.put("emailReceived", String.valueOf(Integer.parseInt(stats.get("emailReceived")) + 1));

            FileWriter writer = new FileWriter(statsPath);
            writer.write(g.toJson(stats));
            writer.close();
        } catch (Exception e) {
            System.out.println("ERROR updateStats: " + e);
        }
    }

    private void decrementStats(String account) {
        String statsPath = EMAILS_PATH + "/" + account + "/" + "stats.json";

        try {
            Reader reader = new FileReader(statsPath);
            Gson g = new GsonBuilder().setPrettyPrinting().create();

            HashMap<String, String> stats = g.fromJson(reader, HashMap.class);
            reader.close();

            stats.put("emailReceived", String.valueOf(Integer.parseInt(stats.get("emailReceived")) - 1));

            FileWriter writer = new FileWriter(statsPath);
            writer.write(g.toJson(stats));
            writer.close();
        } catch (Exception e) {
            System.out.println("ERROR updateStats: " + e);
        }
    }

    public void insertEmail(EmailSerializable email) {
        try {
            //TODO: retrieve last email id from database and increment it
            // make a copy of the recipients list and add the sender to save the email in his outbox

            for (String account : email.getRecipients()) {
                email.setId(readStats(account) + 1);

                String emailFileName = email.getDate().split(" ")[0].replaceAll("/", "-");
                String emailFilePath = EMAILS_PATH + "/" + account + "/" + emailFileName + ".json";

                File emailFile = new File(emailFilePath);
                if (emailFile.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(emailFilePath);
                    fileWriter.write("[]");
                    fileWriter.close();
                }

                writeEmail(email, account);

                incrementStats(account);
                if (account.equals(email.getSender()))
                    LogController.emailSent(email.getSender(), email.getRecipients());
                else
                    LogController.emailReceived(account);
            }
        } catch (Exception e) {
            System.out.println("ERROR insertEmail: " + e);

        }
    }

    public void deleteEmail(EmailSerializable email, String account) {
        File accountsFile = new File(EMAILS_PATH + "/" + account);
        if (accountsFile.exists()) {
            String emailFileName = email.getDate().split(" ")[0].replaceAll("/", "-");
            String emailFilePath = EMAILS_PATH + "/" + account + "/" + emailFileName + ".json";

            try {
                ArrayList<EmailSerializable> emails = new ArrayList<>();
                Reader reader = new FileReader(emailFilePath);
                Gson g = new GsonBuilder().setPrettyPrinting().create();

                JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
                reader.close();

                // fill emails array with emails from file
                for (int i = 0; i < array.size(); i++) {
                    emails.add(g.fromJson(array.get(i), EmailSerializable.class));
                }

                System.out.println("emails size: " + emails.size());

                for (EmailSerializable e : emails) {
                    if (e.getId() == email.getId()) {
                        System.out.println("deleting email: " + e.getId());
                        emails.remove(e);
                        break;
                    }
                }

                FileWriter writer = new FileWriter(emailFilePath);
                writer.write(g.toJson(emails));
                writer.close();

//                decrementStats(account);
            } catch (Exception e) {
                System.out.println("ERROR delete: " + e);
            }
        } else{
            System.out.println("ERROR delete: account not found");
        }

    }

}

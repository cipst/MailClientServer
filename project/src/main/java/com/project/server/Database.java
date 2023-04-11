package com.project.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.project.models.Email;
import com.project.server.controller.LogController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {

    private final String DATABASE_PATH = "src/main/resources/com/project/server/database.json";
    private final String EMAILS_PATH = "src/main/resources/com/project/server/emails";

    private HashMap<String, ReentrantReadWriteLock> emailLocks;
    private HashMap<String, ReentrantReadWriteLock> statsLock;

    public Database() {
        emailLocks = new HashMap<>();
        statsLock = new HashMap<>();
        HashMap<String, String> accounts = accountsHashMap();

        onLoad(accounts);
    }

    private HashMap<String, String> accountsHashMap() {
        HashMap<String, String> accounts = new HashMap<>();

        accounts.put("stefano.cipolletta@unito.it", "stefano.cipolletta");
        accounts.put("matteo.barone@unito.it", "matteo.barone");
        accounts.put("alessio.rosa@unito.it", "alessio.rosa");

        for (String email : accounts.keySet()) {
            emailLocks.put(email, new ReentrantReadWriteLock());
            statsLock.put(email, new ReentrantReadWriteLock());
        }
        return accounts;
    }

    /**
     * Write the accounts HashMap to the database.json file
     * @param accounts HashMap with the accounts
     */
    private void onLoad(HashMap<String, String> accounts) {
        try {
            File file = new File(DATABASE_PATH);
            if (file.createNewFile()) {
                Gson g = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(DATABASE_PATH);
                writer.write(g.toJson(accounts));
                writer.close();

                initEmailsDirectoriesAndFiles(accounts, g);
            }
        } catch (Exception e) {
            System.out.println("ERROR onLoad: " + e);
        }
    }

    private void initEmailsDirectoriesAndFiles(HashMap<String, String> accounts, Gson g) throws IOException {
        for (String account : accounts.keySet()) {
            File accountsFile = new File(EMAILS_PATH + "/" + account);
            if (accountsFile.mkdirs()) {
                String statsPath = EMAILS_PATH + "/" + account + "/" + "stats.json";

                File stats = new File(statsPath);
                stats.createNewFile();

                HashMap<String, String> counter = new HashMap<>();
                counter.put("emailReceived", "-1");

                FileWriter writerStats = new FileWriter(statsPath);
                writerStats.write(g.toJson(counter));
                writerStats.close();
            }
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

    private boolean writeEmail(Email email, String address) {
        String emailFileName = email.getDate().split(" ")[0].replaceAll("/", "-");
        String emailFilePath = EMAILS_PATH + "/" + address + "/" + emailFileName + ".json";
        boolean isOperationSuccessful;

        try {
            Reader reader = new FileReader(emailFilePath);
            Gson g = new GsonBuilder().setPrettyPrinting().create();

            emailLocks.get(address).readLock().lock();
            ArrayList<Email> emails = g.fromJson(reader, ArrayList.class);
            isOperationSuccessful = emails.add(email);
            reader.close();
            emailLocks.get(address).readLock().unlock();

            emailLocks.get(address).writeLock().lock();
            FileWriter writer = new FileWriter(emailFilePath);
            writer.write(g.toJson(emails));
            writer.close();
            emailLocks.get(address).writeLock().unlock();
        } catch (Exception e) {
            System.out.println("ERROR write: " + e);
            isOperationSuccessful = false;
        } finally {
            if (emailLocks.get(address).isWriteLocked())
                emailLocks.get(address).writeLock().unlock();

            if (emailLocks.get(address).getReadLockCount() != 0)
                emailLocks.get(address).readLock().unlock();
        }

        return isOperationSuccessful;
    }

    private ArrayList<Email> readEmailsByDate(String address, String date) {
        String emailFilePath = EMAILS_PATH + "/" + address + "/" + date + ".json";
        ArrayList<Email> emails = new ArrayList<>();

        try {
            Reader reader = new FileReader(emailFilePath);
            Gson g = new Gson();

            emailLocks.get(address).readLock().lock();
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            reader.close();
            emailLocks.get(address).readLock().unlock();

            for (int i = 0; i < array.size(); i++) {
                emails.add(g.fromJson(array.get(i), Email.class));
            }

        } catch (Exception e) {
            System.out.println("ERROR read emails by date: " + e);
            e.printStackTrace();
        } finally {
            if (emailLocks.get(address).getReadLockCount() != 0)
                emailLocks.get(address).readLock().unlock();
        }

        return emails;
    }

    public ArrayList<Email> readAllEmails(String address) {
        String emailFilePath = EMAILS_PATH + "/" + address + "/";
        ArrayList<Email> emails = new ArrayList<>();

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
        int id = -1;

        try {
            Reader reader = new FileReader(statsPath);
            Gson g = new Gson();

            statsLock.get(address).readLock().lock();
            HashMap<String, String> stats = g.fromJson(reader, HashMap.class);
            reader.close();
            statsLock.get(address).readLock().unlock();

            id = Integer.parseInt(stats.get("emailReceived"));
        } catch (Exception e) {
            System.out.println("ERROR read stats: " + e);
        } finally {
            if (statsLock.get(address).getReadLockCount() != 0)
                statsLock.get(address).readLock().unlock();
        }

        return id;
    }

    private boolean incrementStats(String account) {
        String statsPath = EMAILS_PATH + "/" + account + "/" + "stats.json";
        boolean isOperationSuccessful = true;

        try {
            Reader reader = new FileReader(statsPath);
            Gson g = new GsonBuilder().setPrettyPrinting().create();

            statsLock.get(account).readLock().lock();
            HashMap<String, String> stats = g.fromJson(reader, HashMap.class);
            reader.close();
            statsLock.get(account).readLock().unlock();

            stats.put("emailReceived", String.valueOf(Integer.parseInt(stats.get("emailReceived")) + 1));

            statsLock.get(account).writeLock().lock();
            FileWriter writer = new FileWriter(statsPath);
            writer.write(g.toJson(stats));
            writer.close();
            statsLock.get(account).writeLock().unlock();
        } catch (Exception e) {
            System.out.println("ERROR updateStats: " + e);
            isOperationSuccessful = false;
        } finally {
            if (statsLock.get(account).isWriteLocked())
                statsLock.get(account).writeLock().unlock();

            if (statsLock.get(account).getReadLockCount() != 0)
                statsLock.get(account).readLock().unlock();
        }

        return isOperationSuccessful;
    }

    public boolean insertEmail(Email email) {
        boolean isOperationSuccessful = true;
        LogController.emailSent(email.getSender(), email.getRecipients());

        try {
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

                isOperationSuccessful &= writeEmail(email, account);

                isOperationSuccessful &= incrementStats(account);

                LogController.emailReceived(account);
            }
        } catch (Exception e) {
            System.out.println("ERROR insertEmail: " + e);
            isOperationSuccessful = false;
        }
        return isOperationSuccessful;
    }

    public boolean deleteEmail(Email email, String account) {
        File accountsFile = new File(EMAILS_PATH + "/" + account);
        boolean isOperationSuccessful = false;

        if (accountsFile.exists()) {
            String emailFileName = email.getDate().split(" ")[0].replaceAll("/", "-");
            String emailFilePath = EMAILS_PATH + "/" + account + "/" + emailFileName + ".json";

            try {
                ArrayList<Email> emails = new ArrayList<>();
                Reader reader = new FileReader(emailFilePath);
                Gson g = new GsonBuilder().setPrettyPrinting().create();

                JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
                reader.close();

                // fill emails array with emails from file
                for (int i = 0; i < array.size(); i++) {
                    emails.add(g.fromJson(array.get(i), Email.class));
                }

                for (Email e : emails) {
                    if (e.getId() == email.getId()) {
                        isOperationSuccessful = emails.remove(e);
                        break;
                    }
                }

                FileWriter writer = new FileWriter(emailFilePath);
                writer.write(g.toJson(emails));
                writer.close();
            } catch (Exception e) {
                System.out.println("ERROR delete: " + e);
                isOperationSuccessful = false;
            }
        } else {
            System.out.println("ERROR delete: account not found");
        }

        return isOperationSuccessful;
    }

}

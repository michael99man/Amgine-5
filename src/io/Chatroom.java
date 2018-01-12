package io;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import chat_objects.Message;
import chat_objects.Poller;
import chat_objects.User;
import cryptosystem.AES;
import cryptosystem.Crypto;
import gui.ChatWindow;

//keeps track of the current chatroom, including names, public keys and messages sent to/from
//holds encryption 
public class Chatroom {

    private ChatWindow parent;

    public ArrayList<User> users;

    public Crypto crypto;
    public User me;

    private Poller poller;
    private Thread thread;

    // this is MySQL's date format
    public static final java.text.SimpleDateFormat SDF = new java.text.SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private static final java.text.SimpleDateFormat SAFE = new java.text.SimpleDateFormat(
            "yyyy-MM-dd%20HH:mm:ss");

    private static final int THRESHOLD = 8;

    public Chatroom(String name, ChatWindow parent) {
        // accounts for different timezones!
        SDF.setTimeZone(TimeZone.getTimeZone("EST"));
        SAFE.setTimeZone(TimeZone.getTimeZone("EST"));

        // this object represents myself
        crypto = new Crypto();

        me = new User(name, crypto.getPublicKey());
        this.parent = parent;
        // in constructor, add self to users table!
        // send: Name, Public Key, Signed Name

        users = new ArrayList<User>();
        join();
        poller = new Poller(this);
        thread = new Thread(poller);
        thread.start();
        poller.startPolling();
    }

    // join chatroom by posting public key + signed name + name to the user
    // table
    private void join() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("name", me.name);

        String pk = Crypto.getPublicKeyString(crypto.getPublicKey());
        data.put("public_key", pk);

        String signed = crypto.sign(me.name);
        // signs a hash of my name
        data.put("signed_name", signed);

        Date d = new Date();
        data.put("date_joined", SDF.format(d));

        // posts my data to the server
        IO.post(data, "users");
    }

    // reads all users currently in the chatroom

    public void getUsers() {
        try {
            ArrayList<User> newUsers = new ArrayList<User>();
            JSONArray array = IO.get(null, "users");
            for (int i = 0; i < array.length(); i++) {
                JSONObject json;
                json = array.getJSONObject(i);
                String userName = json.getString("name");
                String pkHex = json.getString("public_key");
                String signed_name = json.getString("signed_name");
                PublicKey publicKey = Crypto.publicKeyFromString(pkHex);
                boolean verified = crypto.verify(signed_name, userName, publicKey);
                // if not myself...
                if (verified && !crypto.pkEqual(publicKey, crypto.getPublicKey())) {
                    newUsers.add(new User(userName, publicKey));
                }
            }

            // look for new members
            for (int i = users.size(); i < newUsers.size(); i++) {
                User u = newUsers.get(i);
                users.add(u);
                System.out.println("Added: " + u);
                parent.addUser(u);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(User target, String message) {
        PublicKey key = target.publicKey;

        SecretKey aesKey = AES.getSecretEncryptionKey();
        String aesC = AES.encryptText(message, aesKey);
        String keyC = crypto.encryptKey(aesKey, key);

        Date dt = new java.util.Date();

        String dateString = SDF.format(dt);
        Message m = new Message(target, message, aesC, keyC, dt, this);

        String dateTime = String.valueOf(dt.getTime());
        dateTime = dateTime.substring(0, dateTime.length() - 3);

        String signedDateString = crypto.sign(dateTime);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("recipient", target.name);
        // my public key in hex form
        map.put("sender", crypto.getPublicKeyString());
        map.put("ciphertext", aesC);
        map.put("encrypted_key", keyC);
        map.put("date", dateString);
        map.put("signed_date", signedDateString);

        IO.post(map, "messages");
        // Message m = new Message(target, key, message, crypto);
    }

    // called by Poller
    public synchronized void update(int tick) {
        if (tick % 3 == 0 || tick == 0) {
            // update users every 3 seconds
            getUsers();
        }
        // update messages every second
        getMessages();
    }

    public User find(PublicKey pk) {
        for (User user : users) {
            if (user.publicKey.equals(pk)) {
                return user;
            }
        }
        return null;
    }
    
    private synchronized void getMessages() {
        Date dt = new java.util.Date();
        String dateString = SAFE.format(dt);

        String query = "ABS(TIMESTAMPDIFF(SECOND,'" + dateString + "',date))";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(query, String.valueOf(THRESHOLD));
        params.put("recipient", me.name);
        try {
            JSONArray msg = IO.get(params, "messages");
            for (int i = msg.length()-1; i >= 0; i--) {
                Message message = new Message(msg.getJSONObject(i), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // for each of the messages, create Message instance

        // get all messages corresponding to my name/public key send within the
        // last few seconds
    }

}

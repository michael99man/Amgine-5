package chat_objects;

import java.math.BigInteger;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;

import org.json.JSONException;
import org.json.JSONObject;

import cryptosystem.AES;
import cryptosystem.Crypto;
import io.Chatroom;

public class Message {
    
    //only defined for a recieved message!
    private JSONObject json;
    
    //initialized by constructors
    public User sender;
    public User target;
    public String plaintext;
    private String ciphertext;
    private String keyC;
    private Date date;
    public boolean iSent;
    
    private static ArrayList<String> ciphertexts = new ArrayList<String>();
    
    
    //sending message to someone else
    public Message(User target, String plaintext, String ciphertext, String keyC, Date dt, Chatroom chat){
        iSent = true;
        sender = chat.me;
        this.target = target;
        this.plaintext = plaintext;
        this.ciphertext = ciphertext;
        this.keyC = keyC;
        date = dt;
        target.add(this);
    }
    
    //receiving message from other person
    public Message(JSONObject msg, Chatroom chat){
        iSent = false;
        json = msg;
        
        try {
            //this is a PublicKey in hex form
            String send = (String) json.get("sender");
            sender = chat.find(Crypto.publicKeyFromString(send)); 
            target = chat.me;
            if(sender != null){
                ciphertext = (String) json.get("ciphertext");
                
                if(ciphertexts.contains(ciphertext)){
                    System.out.println("Ignored dupe");
                    return;
                }
                ciphertexts.add(ciphertext);
                
                String dateString = (String) json.get("date");
                date = Chatroom.SDF.parse(dateString);
                keyC = (String) json.get("encrypted_key");
                String signed_date = (String) json.get("signed_date");
                String dateTime = String.valueOf(date.getTime());
                dateTime = dateTime.substring(0, dateTime.length() - 3);
                
                boolean verified = chat.crypto.verify(signed_date, dateTime, sender.publicKey);
                if(!verified){
                    System.out.println("Failed verification!");
                } else {
                    SecretKey secKey = chat.crypto.decryptKey(keyC);
                    plaintext = AES.decryptText(ciphertext, secKey);
                    //important: the msg is added to the SENDER's list
                    sender.add(this);
                }   
            } else {
                System.out.println("Couldn't find user: " + send + " in database");
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
    
    
    public JSONObject getJSON(){
        return json;
    }
    
    @Override
    public String toString(){
        return plaintext + " (Sender: " + sender.name + " Receiver: " + target.name + ")";
    }
    
    public boolean isEqual(Message m){
        return (this.plaintext.equals(m.plaintext) && this.date.equals(m.date) && (this.sender == m.sender));
    }
}

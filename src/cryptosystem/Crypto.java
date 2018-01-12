package cryptosystem;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

//manages encryption/decryption/signing
//instance holds public/private key pair for RSA, as well as AES secret key
public class Crypto {

    // encr
    private KeyPair key;
    private String publicKeyString;
    
    private static final int BIT_LENGTH = 1024;

    public Crypto() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");

            keyGen.initialize(BIT_LENGTH);
            key = keyGen.generateKeyPair();
            // aesKey = AESEncryption.getSecretEncryptionKey();
            publicKeyString = getPublicKeyString(key.getPublic());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // SENDING
    // encrypts the AES key with RSA and the
    public String encryptKey(SecretKey aesKey, PublicKey pk) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte[] cipherBytes = cipher.doFinal(aesKey.getEncoded());
            return bytesToHex(cipherBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // RECIEVING
    // decrypt the AES key with RSA
    public SecretKey decryptKey(String encKey) {
        try {
            byte[] bytes = hexToBytes(encKey);
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
            byte[] keyBytes = cipher.doFinal(bytes);
            SecretKey aesKey = new SecretKeySpec(keyBytes, 0, keyBytes.length,
                    "AES");
            return aesKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // getter methods
    public PublicKey getPublicKey() {
        return key.getPublic();
    }
    
    public String getPublicKeyString() {
        return publicKeyString;
    }

    // returns the public key in hex form
    public static String getPublicKeyString(PublicKey key) {
        KeyFactory fact;
        try {
            fact = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = fact.getKeySpec(key,
                    X509EncodedKeySpec.class);
            return bytesToHex(spec.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    // takes in a public key in hex form and returns a PublicKey object
    public static PublicKey publicKeyFromString(String pk) {
        KeyFactory fact;
        try {
            byte[] key = hexToBytes(pk);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
            fact = KeyFactory.getInstance("RSA");
            return fact.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    // signs a string with the private key (in hex)
    public String sign(String str) {
        byte[] data = str.getBytes();
        Signature signer;
        try {
            signer = Signature.getInstance("SHA1WithRSA");
            signer.initSign(key.getPrivate());
            signer.update(data);
            byte[] signature = signer.sign();
            return bytesToHex(signature);
        } catch (NoSuchAlgorithmException | SignatureException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    // verifies a signature given the plaintext
    //str: signature, message: plaintext, pk, public key of the signer
    public boolean verify(String str, String message, PublicKey pk) {
        byte[] data = hexToBytes(str);
        Signature signer;
        try {
            signer = Signature.getInstance("SHA1WithRSA");
            signer.initVerify(pk);
            byte[] messageBytes = message.getBytes();
            signer.update(messageBytes);
            return signer.verify(data);
        } catch (NoSuchAlgorithmException | SignatureException
                | InvalidKeyException e) {
            e.printStackTrace();
            // if it false it can't be verified!
            return false;
        }
    }
    
    /*public static void main(String[] args) throws ParseException{
        Crypto a = new Crypto();
        Date d = new Date();
        
        String dateTime1 = String.valueOf(d.getTime());
        dateTime1 = dateTime1.substring(0, dateTime1.length() - 3);
        System.out.println(dateTime1);
        String sign = a.sign(dateTime1);
        System.out.println(d.getTime());
        
        String dateString = Chatroom.SDF.format(d);
        
        Date dRec = Chatroom.SDF.parse(dateString);
        
        String plain = String.valueOf(dRec.getTime());
        plain = plain.substring(0, plain.length() - 3); 
        System.out.println(plain);
        System.out.println(dRec.getTime());
        
        Crypto b = new Crypto();
        System.out.println(b.verify(sign, plain, a.getPublicKey()));
    }*/

    /**
     * Convert a binary byte array into readable hex form
     * @param hash
     * @return String
     */
    public static String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }

    /**
     * Convert a hex string to a binary byte array
     * @param hex
     * @return byte[]
     */
    public static byte[] hexToBytes(String hex) {
        try {
            return Hex.decodeHex(hex.toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean pkEqual(PublicKey pk1, PublicKey pk2){
        return pk1.hashCode() == pk2.hashCode();
    }
}

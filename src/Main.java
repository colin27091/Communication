
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author c
 */
public class Main {
    
    
     public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
         
         
         
         
         
         
         try {
             String message = "Bonjour";
            System.out.println("Message : " + message);
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            SecretKey key = generator.generateKey();
            System.out.println("La clé a bien été generé");
             
             System.out.println("Création du cipher...");
             Cipher cipher = Cipher.getInstance("AES");
             byte[] toCrypt = message.getBytes();
             System.out.println("Avant cryptage(en byte[]) : "+ toCrypt);
             System.out.println("Preparation du cipher pour cryptage");
             cipher.init(Cipher.ENCRYPT_MODE, key);
             byte[] crypted = cipher.doFinal(toCrypt);
             System.out.println("Message crypté (en byte[]) :" + crypted);
             System.out.println("Message crypté : "+ new String(crypted));
             System.out.println("Preparation du cipher pour decryptage...");
             cipher.init(Cipher.DECRYPT_MODE, key);
             byte [] decrypted = cipher.doFinal(crypted);
             System.out.println("Message decrypté (en byte[]) : "+ decrypted);
             System.out.println("Message decrypté : "+ new String(decrypted));
             System.out.println("FIN");
             
             
             
         } catch (Exception ex) {
             System.out.println(ex);
         }
         
         
         
     }
    
    
    
    
}

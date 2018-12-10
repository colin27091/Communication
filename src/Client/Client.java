package Client;

import java.awt.RenderingHints.Key;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import serveur.Serveur;

/**
 *
 * @author c
 */
public class Client {
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        
        
        String serverName;
        int port;
        Socket socket;
        final BufferedReader in;//reception message du serveur
        final PrintWriter out;//envoie message au serveur
        final Scanner scan = new Scanner(System.in);//lecture clavier
        final Scanner word = new Scanner(System.in);//lecture clavier pour message
        KeyGenerator generator;//generateur de clé
        SecretKey key;//clé généré
        ObjectOutputStream outKey;//outter permettant d'écrire la clé dans un fichier
        
        
        
        try{
            
            System.out.print("Nom du serveur : ");//nom de serveur
            serverName = scan.nextLine();
            System.out.println();
            System.out.print("Port : ");//port
            port = scan.nextInt();
            System.out.println();
            socket = new Socket(serverName, port);//information serveur
            System.out.println("Connection etablished");
            
            
            //generation de la clé
            generator = KeyGenerator.getInstance("AES");//utilisation d'une clé symetrique AES
            key = generator.generateKey();
            //fin generation
            
            //exportation de la clé vers un fichier, ici  KeyFile.xx
            outKey = new ObjectOutputStream(new FileOutputStream("KeyFile.xx"));
            outKey.writeObject(key);
            outKey.close();
            //fin de l'exportation
            
            //definition des outils d'echange avec le serveur
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //fin definition
            
            
            //Création du processus d'envoi de message
            Thread send = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){ //boucle infini pour lecture de clavier et envoie en continu
                        String message = word.nextLine();//lecture du clavier, enregistrement du message
                        
                        if(message.equals("bye")){//si le client envoie bye
                            System.out.println("Deconnexion");
                            break;
                        }
                        
                        
                        try {
                            Cipher cipher;//outils de cryptage et de decryptage
                            cipher = Cipher.getInstance("AES");//clé symetrique AES
                            System.out.println(message);
                            byte[] toCrypt = message.getBytes();//transformation du message en byte[] avant cryptage
                            cipher.init(Cipher.ENCRYPT_MODE, key);//preparation du cipher pour crypter
                            byte[] crypted = cipher.doFinal(toCrypt);//byte[] crypté
                            System.out.println("( "+ toCrypt + " -> "+ crypted +" )");
                            out.println(crypted);//envoie du message crypté au serveur
                            out.flush();
                            
                            
                        } catch (Exception ex) {
                            System.out.println("Send error : "+ex);
                            break;
                        }
                        
                        
                        
                    }System.exit(-1);
                   
                }
            });
            send.start();
            
            //Creation du processus de reception de message
            Thread receive = new Thread(new Runnable() {
                @Override
                public void run() {
                    
                    while(true){
                        
                        
                        try{
                            
                            byte[] crypted = in.readLine().getBytes();
                            Cipher cipher = Cipher.getInstance("AES");
                            cipher.init(Cipher.DECRYPT_MODE, key);
                            byte[] uncrypted = cipher.doFinal(crypted);
                            String message = new String(uncrypted);
                            System.out.println("Serveur : " + message);

                        } catch (Exception ex){
                            System.out.println("Receive error : "+ex);
                            break;
                        }
                        
                        
                    }System.exit(-1);
                    
                }
            });
            receive.start();
            
            
            
        } catch (Exception ex){
            System.out.println(ex);
        } 
        
        
        
    }
    
}

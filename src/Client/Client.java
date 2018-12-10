package Client;

import java.awt.RenderingHints.Key;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
import javax.xml.bind.DatatypeConverter;

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
        final InputStream in;//reception message du serveur
        final OutputStream out;//envoie message au serveur
        final Scanner scan = new Scanner(System.in);//lecture clavier
        final Scanner word = new Scanner(System.in);//lecture clavier pour message
        
        SecretKey key;//clé généré
        ObjectInputStream inKey;//outter permettant de lire la cl� dans un fichier

        
        
        
        try{
            
            System.out.print("Nom du serveur : ");//nom de serveur
            serverName = scan.nextLine();
            System.out.println();
            System.out.print("Port : ");//port
            port = scan.nextInt();
            System.out.println();
            socket = new Socket(serverName, port);//information serveur
            System.out.println("Connection etablished");
            
            
          //lecture importation de la cl� depuis un fichier
            inKey = new ObjectInputStream(new FileInputStream("KeyFile.xx"));
            key = (SecretKey) inKey.readObject();//stockage de la cl�
            //fin d'importation
            System.out.println(key);
            
            //definition des outils d'echange avec le serveur
            out = socket.getOutputStream();
            in = socket.getInputStream();           
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
                            byte[] toCrypt = message.getBytes();//transformation du message en byte[] avant cryptage
                            cipher.init(Cipher.ENCRYPT_MODE, key);//preparation du cipher pour crypter
                            byte[] crypted = cipher.doFinal(toCrypt);//byte[] crypté
                            out.write(crypted.length);//envoie de la longueur du cryptage
                            out.write(crypted);//envoie du message crypté au serveur
                            out.flush();
                            System.out.println("( "+ new String(toCrypt) + " -> "+ new String(crypted) +" )");

                            
                            
                        } catch (Exception ex) {
                            System.out.println("Send error : "+ex);
                            break;
                        }
                        
                        
                        
                    }System.exit(0);
                   
                }
            });
            send.start();
            
            //Creation du processus de reception de message
            Thread receive = new Thread(new Runnable() {
                @Override
                public void run() {
                    
                    while(true){
 
                        try{
                            
                            int longueur = in.read();//reconstitution du byte[] crypté
                            byte[] crypted = new byte[longueur];
                            for(int i=0; i < longueur; i++){
                                in.read(crypted, i, 1);
                            }
                            
                            
                            
                            Cipher cipher = Cipher.getInstance("AES");
                            cipher.init(Cipher.DECRYPT_MODE, key);
                            byte[] uncrypted = cipher.doFinal(crypted);
                           System.out.println("( " + new String(crypted) + " -> " + new String(uncrypted) +" )");
                            String message = new String(uncrypted);
                            System.out.println("Serveur : " + message);

                        } catch (Exception ex){
                            System.out.println("Receive error : "+ex);
                            break;
                        }
                        
                        
                    }System.exit(0);
                    
                }
            });
            receive.start();
            
            
            
            
        } catch (Exception ex){
            System.out.println(ex);
        } 
        
        
        
    }
    
}

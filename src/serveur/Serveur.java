package serveur;

import java.awt.RenderingHints.Key;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author c
 */
public class Serveur {

    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        ServerSocket serverSocket;
        Socket socket;
        int port;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner scan = new Scanner(System.in);//lecture clavier
        final Scanner word = new Scanner(System.in);//lecture clavier pour message
        ObjectInputStream inKey;//outter permettant de lire la clé dans un fichier
        SecretKey key;
        
        try {
        	
            //debut mise en plase du server
            System.out.print("Port : ");
            port = scan.nextInt();//port demander � l'uilisateur
            System.out.println();
        	
            serverSocket = new ServerSocket(port);//ouverture du port choisi
            socket = serverSocket.accept();//accepte les connexions client
            //fin mise en place du server
                
            //connexion d'un client
            System.out.println("Connexion de "+ socket.getInetAddress());
                
            //lecture importation de la clé depuis un fichier
            inKey = new ObjectInputStream(new FileInputStream("KeyFile.xx"));
            key = (SecretKey) inKey.readObject();//stockage de la clé
            //fin d'importation
                
            System.out.println("Clé partagé");
                
                
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
                            
                            String crypted = in.readLine();
                            
                            Cipher cipher = Cipher.getInstance("AES");
                            cipher.init(Cipher.DECRYPT_MODE, key);
                            byte[] uncrypted = cipher.doFinal(crypted.getBytes());
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
            
            
            
           
        } catch (Exception ex) {
        	
        }
      
               
	      
    }
    
}

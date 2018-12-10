package serveur;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.crypto.KeyGenerator;

/**
 *
 * @author c
 */
public class Serveur {

    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        ServerSocket serverSocket;
        Socket socket;
        int port;
        final InputStream in;
        final OutputStream out;
        final Scanner scan = new Scanner(System.in);//lecture clavier
        final Scanner word = new Scanner(System.in);//lecture clavier pour message
        ObjectOutputStream outKey;//outter permettant d'Ã©crire la clÃ© dans un fichier
        SecretKey key;
        KeyGenerator generator;//generateur de clÃ©
        
        try {
        	
            //debut mise en plase du server
            System.out.print("Port : ");
            port = scan.nextInt();//port demandé � l'uilisateur
            System.out.println();
        	
            
            
            //generation de la clÃ©
            generator = KeyGenerator.getInstance("AES");//utilisation d'une clÃ© symetrique AES
            key = generator.generateKey();
            //fin generation
            
            
            System.out.println(key.toString());
            //exportation de la clÃ© vers un fichier, ici  KeyFile.xx
            outKey = new ObjectOutputStream(new FileOutputStream("KeyFile.xx"));
            outKey.writeObject(key);
            outKey.close();
            //fin de l'exportation
            
            //mise en place du serveur
            serverSocket = new ServerSocket(port);//ouverture du port choisi
            socket = serverSocket.accept();//accepte les connexions client
            //fin mise en place du server
            
            //connexion d'un client
            System.out.println("Connexion de "+ socket.getInetAddress());

            System.out.println("Clé partagé");
                
                
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
                            
                            int longueur = in.read();
                            
                            byte[] crypted = new byte[longueur];
                            
                            
                            for(int i=0; i < longueur; i++){
                                in.read(crypted, i, 1);
                            }
                            
                            
                            
                            Cipher cipher = Cipher.getInstance("AES");
                            
                            cipher.init(Cipher.DECRYPT_MODE, key);
                            
                            byte[] uncrypted = cipher.doFinal(crypted);
                            
                           System.out.println("( " + new String(crypted) + " -> " + new String(uncrypted) +" )");
                           
                            String message = new String(uncrypted);
                            System.out.println("Client : " + message);

                        } catch (Exception ex){
                            System.out.println("Receive error : "+ex);
                            break;
                        }
                        
                        
                    }System.exit(0);
                    
                }
            });
            receive.start();
            
           
           
        } catch (Exception ex) {
        	
        }
      
               
	      
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import serveur.Serveur;

/**
 *
 * @author c
 */
public class Client {
    
    public static void main(String[] args) {
        
        Socket socket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner scan = new Scanner(System.in);//lecture clavier
        
        
        try {
            
            System.out.print("Nom du serveur : ");
            socket = new Socket(scan.nextLine(), 4444);//information serveur
            
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            Thread send = new Thread(new Runnable() {
                String message;
                
                @Override
                public void run() {
                    
                    while(true){
                        message = scan.next();
                        out.println(message);
                        out.flush();
                    }
                }
            });
            send.start();
            
            
            
            Thread receive = new Thread(new Runnable() {
                String message;
                
                @Override
                public void run() {
                    while(true){
                        
                        try {
                            message = in.readLine();
                        } catch (IOException ex) {
                            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("Serveur : "+message);
                    }
                }
            });
            receive.start();
            
            
            
            
        } catch (IOException ex) {
            System.out.println("Impossible de se connecter au serveur");
        }
        
        
    }
    
}

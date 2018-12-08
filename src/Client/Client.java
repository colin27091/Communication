/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            socket = new Socket(scan.nextLine(), 4444);
            
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}

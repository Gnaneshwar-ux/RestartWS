/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package RestartApp;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 *
 * @author Gnaneshwar
 */
public class RestartApp {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
       System.setProperty("sun.java2d.uiScale", "1.5");

//        boolean b=true;
    System.out.println("Hello 1");
        String user = System.getProperty("user.name");
         String propPath = "C:/Users/"+user+"/Documents";
        File file = new File(propPath+"/cred.properties");
        System.out.println("Hello 2");
        
        SwingUtilities.invokeLater(new Runnable() {
    public void run() {
        RestartWindow rw = new RestartWindow();
        System.out.println("Hello 7");
        rw.setResizable(false);
        System.out.println("Hello 8");
        rw.setVisible(true);
        System.out.println("Hello 9");
// UI-related code goes here
    }
});
        
        
//        if (!file.exists()) {
//            b=false;
//        }
//        if(!RestartWebWorkspace.validate()){
//            b=false;
//        }
//        
//        if(!b)
//        {
//         rw.tabUpdate();
//        }
//        
//        rw.setVisible(true);
////        
    }
    
    
    
}

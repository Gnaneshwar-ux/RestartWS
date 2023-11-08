/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package RestartApp;

import java.awt.Image;
import java.awt.Toolkit;
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
       double scale = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0;
       
       System.out.println(scale);

        // Calculate the desired scale factor based on the display ratio
        double desiredScaleFactor = 1.5*(scale);

        // Set the "sun.java2d.uiScale" property
        System.setProperty("sun.java2d.uiScale", String.valueOf(desiredScaleFactor));

//        boolean b=true;
    
        String user = System.getProperty("user.name");
         String propPath = "C:/Users/"+user+"/Documents";
        File file = new File(propPath+"/cred.properties");
        
        
        SwingUtilities.invokeLater(new Runnable() {
    public void run() {
        RestartWindow rw = new RestartWindow();
        
        //rw.setResizable(false);
        
        rw.setVisible(true);
        
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package RestartApp;

import java.io.File;
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
        String user = System.getProperty("user.name");
         String propPath = "C:/Users/"+user+"/Documents";
        File file = new File(propPath+"/cred.properties");
        
        RestartWindow rw = new RestartWindow();
        rw.setResizable(false);
        rw.setVisible(true);
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

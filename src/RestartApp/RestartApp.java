/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package RestartApp;

import java.io.File;


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
        boolean b=true;
        String user = System.getProperty("user.name");
         String propPath = "C:/Users/"+user+"/Documents";
        File file = new File(propPath+"/cred.properties");
        
        new RestartWindow().setVisible(true);
        
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

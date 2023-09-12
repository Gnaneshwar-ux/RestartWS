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
    static Setup prev;
    public static void main(String[] args) {
        // TODO code application logic here
        boolean b=true;
        String user = System.getProperty("user.name");
         String propPath = "C:/Users/"+user+"/Documents";
        File file = new File(propPath+"/cred.properties");
        
        if (!file.exists()) {
            b=false;
        }
        if(!RestartWebWorkspace.validate()){
            b=false;
        }
        Setup s;
        if(!b)
        {
         s = new Setup();
         prev = s;
         s.setVisible(true);
        }
        else
        new RestartWindow().setVisible(true);
    }
    
    public static void disablePrev(){
        prev.setVisible(false);
    }
    
}

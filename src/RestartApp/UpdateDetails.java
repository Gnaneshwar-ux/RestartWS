/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestartApp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Gnaneshwar
 */
public class UpdateDetails {
    static String propPath = "";
    public static boolean execute(String pathJconfig,String pathWebWorkspace,String username,String password,String autoLogin) throws Exception{
        
        try
        {
            String user = System.getProperty("user.name");
            propPath = "C:/Users/"+user+"/Documents";
            Properties p = new Properties();
            FileWriter file = new FileWriter(propPath+"/cred.properties",true);
            p.setProperty("pathJconfig",pathJconfig);
            p.setProperty("pathWebWorkspace", pathWebWorkspace);
            p.setProperty("username",username);
            p.setProperty("password",password);
            p.setProperty("autoLogin", autoLogin);
            p.store(file,"user credentials for NMS");
            return true;
        }
        catch(IOException e){
            return false;
        }
    }
    
}

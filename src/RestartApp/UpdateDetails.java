/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestartApp;

import static RestartApp.RestartWebWorkspace.getValue;
import static RestartApp.RestartWebWorkspace.putValue;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Gnaneshwar
 */
public class UpdateDetails {
    static String propPath = "";
    public static boolean execute(String projectName,String pathJconfig,String pathWebWorkspace,String username,String password,String autoLogin) throws Exception{
        
        try
        {
            putValue("pathJconfig",pathJconfig);
            putValue("pathWebWorkspace", pathWebWorkspace);
            putValue("username",username);
            putValue("password",password);
            putValue("autoLogin", autoLogin);
            
            return true;
        }
        catch(IOException e){
            return false;
        }
    }
    public static boolean addProject(String projectName){
        try
        {
            if(getValue("Projects")==null || getValue("Projects").equals("") ){
                putValue("Projects", projectName);
            }
            else {
                 putValue("Projects", getValue("Projects")+" , "+projectName);
            }
            return true;
        }
        catch(IOException e){
            return false;
        }
    }
    
}

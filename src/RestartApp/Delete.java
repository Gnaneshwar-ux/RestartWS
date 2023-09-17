/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestartApp;

import static RestartApp.RestartWebWorkspace.getValue;
import static RestartApp.RestartWebWorkspace.removeValue;
import static RestartApp.RestartWebWorkspace.setTextArea;
import static RestartApp.RestartWebWorkspace.validateSetup;
import static RestartApp.Setup.tempFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author Gnaneshwar
 */
class Delete extends RestartWindow{
    static String tempFile = "Login.xml";
    static String CommandCode = "<Include name=\"AUTO_LOGIN_COMMANDS.inc\"/>";
    public static void execute() throws IOException {
        
        try{
        
        setTextArea("Delete process started ... ");
        
        if(!RestartWebWorkspace.validate()){
            setTextArea("Delete already completed or setup not performed ");
            return;
        }
        String pathJconfig = getValue("pathJconfig");
        String loginPath = pathJconfig +"/global/xml/" + tempFile;
        File tempfile = new File(tempFile);
        FileReader r = new FileReader(loginPath);
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempfile));
       
        BufferedReader reader = new BufferedReader(r);
        
        String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.contains(CommandCode)) {
                    // If the line doesn't contain the specified text, write it to the temporary file
                    
                    writer.write(line);
                    writer.newLine();
                }
            }
            
            r.close();
            reader.close();
            writer.close();
            
            setTextArea("Removed changes in login.xml ... ");
            
                File sourceFileLogin = new File(tempFile);
		

		String[] command = {"cmd", "/c", "copy", tempFile, loginPath};

                ProcessBuilder processBuilder = new ProcessBuilder(command);

                
                Process process = processBuilder.start();
                
                 BufferedReader reader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    setTextArea(line1);
                }

                int exitCode = process.waitFor();
                
                if(exitCode == 0){
                    setTextArea("Login.xml rollback done.");
                }
                else{
                    setTextArea("Login.xml rollback failed.");
                }

                
               File targetDirLogin = new File(pathJconfig + "/global/xml/AUTO_LOGIN_COMMANDS.inc");
                
                if(targetDirLogin.isFile()){
                    targetDirLogin.delete();
                    setTextArea("Deleted AUTO_LOGIN_COMMANDS.inc ... ");
                }
                
                targetDirLogin = new File(pathJconfig + "/java/src/custom/LoadCredentialsExternalCommand.java");
                
                if(targetDirLogin.isFile()){
                    targetDirLogin.delete();
                    setTextArea("Deleted LoadCredentialsExternalCommand.java ... ");
                }
                
                removeValue("pathJconfig");
                removeValue("pathWebWorkspace");
                removeValue("username");
                removeValue("password");
                removeValue("autoLogin");
                setTextArea("Removed data from cred file");
                
                setTextArea("Delete process completed.");
                
        }
        catch(Exception e){
            if(!validateSetup()){
                removeValue("pathJconfig");
                removeValue("pathWebWorkspace");
                removeValue("username");
                removeValue("password");
                removeValue("autoLogin");
                setTextArea("Removed data from cred file");
            }
            setTextArea("Delete process Failed.");
            
        }

    }
    
}

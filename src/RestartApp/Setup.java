package RestartApp;

import static RestartApp.RestartWebWorkspace.clear;
import static RestartApp.RestartWebWorkspace.setTextArea;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Setup {

	static String tempFile = "Login.xml";
	static String CommandCode = "<Include name=\"AUTO_LOGIN_COMMANDS.inc\"/> \n";
        static javax.swing.JTextArea send;

	public static boolean execute(String pathJconfig,String pathWebWorkspace,String username,String password,String autoLogin,javax.swing.JTextArea textf ) throws Exception {
            
            
                send=textf;
                clear();
                
                setTextArea("jconfig path : "+pathJconfig+"\n");
                setTextArea("WebWorkspace.exe path : "+pathWebWorkspace+"\n");
                setTextArea("username : "+username+"\n");
                setTextArea("Autologin : "+autoLogin+"\n");
		
		String user = System.getProperty("user.name");
                
                setTextArea("System user : "+user+"\n");

		String propPath = "C:/Users/" + user + "/Documents";

                if(!updateFile(pathJconfig + "/global/xml/", pathWebWorkspace+"/java/product/global/xml/"+tempFile)){
                    setTextArea("Update file failed");
                    return false;
                }

		Properties prop = new Properties();
		FileWriter credFile = new FileWriter(propPath + "/cred.properties", true);
                
                
		prop.setProperty("pathJconfig", pathJconfig);
		prop.setProperty("pathWebWorkspace", pathWebWorkspace);
		prop.setProperty("autoLogin", autoLogin);
		prop.setProperty("username", username);
		prop.setProperty("password", password);
		prop.store(credFile, "user credentials for NMS");
               return copyFiles(pathJconfig);
               
               
	}

	public static boolean updateFile(String pathLogin, String altPathLogin) throws InterruptedException  {

            try{
		File file = new File(pathLogin+tempFile);

		if (!file.exists()) {
			setTextArea("Login.xml is not found in project!\n");
                        
		
                file  = new File(altPathLogin);
                
                if(!file.exists()){
                    setTextArea("Login.xml is not found in product!\n");
                    return false;
                }
                    
                    }

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;

		boolean found = false;
		String text = CommandCode;
		String res = "";
		while ((line = br.readLine()) != null) {

			if (line.contains("windowOpened")) {
				found = true;
			}
                        if(line.contains("AUTO_LOGIN_COMMANDS.inc")){
                            return true;
                        }
			if (found && line.contains("/Perform")) {
				res += text;
			}
			res += line + "\n";
		}

		br.close();
		FileWriter fWriter = new FileWriter(tempFile);
		fWriter.write(res);

		fWriter.close();

		File sourceFileLogin = new File(tempFile);
		File targetDirLogin = new File(pathLogin);

		if (!targetDirLogin.exists()) {
			targetDirLogin.mkdirs();
		}

		String[] command = {"cmd", "/c", "copy", tempFile, pathLogin};

                ProcessBuilder processBuilder = new ProcessBuilder(command);

                
                Process process = processBuilder.start();
                
                 BufferedReader reader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    setTextArea(line1);
                }

                int exitCode = process.waitFor();
                
                if(exitCode == 0){
                    
                }
                else{
                    setTextArea("Login.xml copy failed.");
                }

                setTextArea("Login.xml file copied successfully!\n");
                return true;
            }
            catch(IOException e){
                setTextArea(e+"\n");
                return false;
            }
	}

	public static boolean copyFiles(String pathJconfig) throws Exception {

		// copying inc files
                try{
                   
		File sourceFileLogin = new File("AUTO_LOGIN_COMMANDS.inc");
		File targetDirLogin = new File(pathJconfig + "/global/xml/AUTO_LOGIN_COMMANDS.inc");

		if (!targetDirLogin.exists()) {
			targetDirLogin.mkdirs();
		}

		Files.copy(sourceFileLogin.toPath(), targetDirLogin.toPath(), StandardCopyOption.REPLACE_EXISTING);

		//System.out.println("Inc file copied successfully!");
                setTextArea("Inc file copied successfully!\n");

		// copying command files

		sourceFileLogin = new File("LoadCredentialsExternalCommand.java");
		targetDirLogin = new File(pathJconfig + "/java/src/custom/LoadCredentialsExternalCommand.java");

		if (!targetDirLogin.exists()) {
			targetDirLogin.mkdirs();
		}

		Files.copy(sourceFileLogin.toPath(), targetDirLogin.toPath(), StandardCopyOption.REPLACE_EXISTING);

		//System.out.println("Java command file copied successfully!");
                setTextArea("Java command file copied successfully!\n");
                return true;
                }
                catch(IOException e){
                    setTextArea(e+"\n");
                    return false;
                }
	}

}
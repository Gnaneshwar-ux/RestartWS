package RestartApp;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SetUpAutoRestart {

	static String tempFile = "Login.xml";
	static String CommandCode = "<Include name=\"AUTO_LOGIN_COMMANDS.inc\"/> \n";
        static javax.swing.JTextArea send;

	public static void execute(String pathJconfig,String pathWebWorkspace,String username,String password,String autoLogin,javax.swing.JTextArea textf ) throws Exception {
            
            
                send=textf;
                
                send.setText(send.getText()+"jconfig path : "+pathJconfig+"\n");
                send.setText(send.getText()+"WebWorkspace.exe path : "+pathWebWorkspace+"\n");
                send.setText(send.getText()+"username : "+username+"\n");
                send.setText(send.getText()+"Autologin : "+autoLogin+"\n");
		
		String user = System.getProperty("user.name");
                
                send.setText(send.getText()+"System user : "+user+"\n");

		String propPath = "C:/Users/" + user + "/Documents";

                if(!updateFile(pathJconfig + "/global/xml/" + tempFile)){
                    return;
                }

		Properties prop = new Properties();
		FileWriter credFile = new FileWriter(propPath + "/cred.properties", true);
                
                
		prop.setProperty("pathJconfig", pathJconfig);
		prop.setProperty("pathWebWorkspace", pathWebWorkspace);
		prop.setProperty("autoLogin", autoLogin);
		prop.setProperty("username", username);
		prop.setProperty("password", password);
		prop.store(credFile, "user credentials for NMS");
               copyFiles(pathJconfig);
               
	}

	public static boolean updateFile(String pathLogin)  {

            try{
		File file = new File(pathLogin);

		if (!file.exists()) {
			send.setText(send.getText()+"Login.xml is not found!\n");
                        return false;
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

		Files.copy(sourceFileLogin.toPath(), targetDirLogin.toPath(), StandardCopyOption.REPLACE_EXISTING);

                send.setText(send.getText()+"Login.xml file copied successfully!\n");
                return true;
            }
            catch(IOException e){
                send.setText(send.getText()+e+"\n");
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
                send.setText(send.getText()+"Inc file copied successfully!\n");

		// copying command files

		sourceFileLogin = new File("LoadCredentialsExternalCommand.java");
		targetDirLogin = new File(pathJconfig + "/java/src/custom/LoadCredentialsExternalCommand.java");

		if (!targetDirLogin.exists()) {
			targetDirLogin.mkdirs();
		}

		Files.copy(sourceFileLogin.toPath(), targetDirLogin.toPath(), StandardCopyOption.REPLACE_EXISTING);

		//System.out.println("Java command file copied successfully!");
                send.setText(send.getText()+"Java command file copied successfully!\n");
                return true;
                }
                catch(IOException e){
                    send.setText(send.getText()+e+"\n");
                    return false;
                }
	}

}
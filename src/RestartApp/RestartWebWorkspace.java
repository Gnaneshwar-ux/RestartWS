package RestartApp;

import java.io.*;
import java.util.*;

public class RestartWebWorkspace {
	
	static String propPath="";
	static javax.swing.JTextArea send;
   public static void execute(boolean start,boolean stop,javax.swing.JTextArea textA) throws Exception{
                send = textA;
		
		//Finding Most recent log file in OracleNMS.
		String user = System.getProperty("user.name");
		
		propPath = "C:/Users/"+user+"/Documents";
                
                if(stop)
                {
		
		File directory = new File("C:\\Users\\"+user+"\\AppData\\Local\\Temp\\OracleNMS");
		File[] files = directory.listFiles(File::isFile);
		long lastModifiedTime = Long.MIN_VALUE;
		File chosenFile = null;

		if (files != null)
		{
			for (File file : files)
			{
				if (file.lastModified() > lastModifiedTime)
				{
					chosenFile = file;
					lastModifiedTime = file.lastModified();
				}
			}
		}
		Process process;
		//Extracting PID from the log file.
		FileReader fr=new FileReader(chosenFile); 
		BufferedReader br=new BufferedReader(fr);   
		String pidLine="";
		String line;  
		while((line=br.readLine())!=null)  
		{  
			if(line.substring(0,3).equals("PID")){
				pidLine = line;
				break;
			}
		}  
		fr.close();  
		if(pidLine.length()>6)
		{
		String prevPID = pidLine.substring(6);
	
                
		//Closing already opened WebWorkspace
		 process = Runtime.getRuntime().exec("cmd /c " + "taskkill /F /pid "+prevPID+" && exit 0 || exit 1");
		
                 textA.setText(textA.getText()+"PID : "+prevPID+"\n");
                 
		if(process.waitFor() == 1){
			textA.setText(textA.getText()+"No opened webWorkspace found..\n");
		}
		else
			textA.setText(textA.getText()+"Closed webWorkspace...\n");
		}
		else{
			textA.setText(textA.getText()+"Closed webWorkspace...\n");
		}
                }
		
		//build command
                
		if(start)
                {
		
			String path="";
			//textA.setText(textA.getText()+"loop");
		if(getValue("pathJconfig")==null || getValue("pathJconfig").equals("")){
			
                        textA.setText(textA.getText()+"\nError*** Jconfig path not valid.\n");
			return;
			
		}
		else{
			path = getValue("pathJconfig");
		}
		
		 String commandArray[] = {"cmd.exe", " /c "," ant config && exit 0 || exit 1"};
		 try{
			 
	     ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "ant config && exit 0 || exit 1");
		 builder.directory(new File(path));
            // start the process
            Process process = builder.start();
		 
		 BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	   
		   String s = null;
		   String result="";
		   textA.setText(textA.getText()+"Build running... wait"+"\n");
			while ((s = stdInput.readLine()) != null) {
				result += s+"\n";
			}
		 if(process.waitFor()==1)
		 {
			 textA.setText(textA.getText()+result+"\n");
			 return;
			 
		 }
		 else{
			
			textA.setText(textA.getText()+"-----BUILD SUCCESSFUL.\n");
			
		 }
		 }
		 catch(IOException e)
		 {
                    textA.setText(textA.getText()+"Error** Invalid Jconfig path.\n");
                    textA.setText(textA.getText()+e+"\n");
                    return;
		 }
		
		
		
		
		
		//opening window
		
                 path="";
			
		if(getValue("pathWebWorkspace")==null || getValue("pathWebWorkspace").equals("")){
			
			
                        textA.setText(textA.getText()+"\nError*** WebWorkspace.exe path not valid.\n");
			return;
                        
			
		}
		else{
			path = getValue("pathWebWorkspace");
		}
		 String commandArray1[] = {"cmd.exe", "/c ","WebWorkspace.exe"};
		 try{
	         Process process = Runtime.getRuntime().exec(commandArray1,null,new File(path));
		 textA.setText(textA.getText()+"NEW WINDOW OPEN\n");
			
		 }
		 catch(IOException e)
		 {
                       
                       textA.setText(textA.getText()+"Error** Invalid WebWorkspace path\n");
                        textA.setText(textA.getText()+e+"\n");
		 }
                }
		
		
		
   }
   public static boolean validate(){
       String user = System.getProperty("user.name");
		
        propPath = "C:/Users/"+user+"/Documents";
       try
       {
        if(getValue("pathJconfig")==null||getValue("pathJconfig").length()==0){
            return false;
        }
        if(getValue("pathWebWorkspace")==null||getValue("pathWebWorkspace").length()==0){
            return false;
        }
        if(getValue("username")==null||getValue("username").length()==0){
            return false;
        }
        if(getValue("password")==null||getValue("password").length()==0){
            return false;
        }
        if(getValue("autoLogin")==null||getValue("autoLogin").length()==0){
            return false;
        }
        
        String pathLogin = getValue("pathJconfig") + "/global/xml/Login.xml";
        
        File file = new File(pathLogin);

        if (!file.exists()) {
               return false;
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        boolean b=true;
        while ((line = br.readLine()) != null) {
            if(line.contains("AUTO_LOGIN_COMMANDS.inc")){
                b= false;
            }
        }
        if(b)
            return false;
        File targetDirLogin = new File(getValue("pathJconfig") + "/java/src/custom/LoadCredentialsExternalCommand.java");
        if (!targetDirLogin.exists()) {
			return false;
	}
         targetDirLogin = new File(getValue("pathJconfig") + "/global/xml/AUTO_LOGIN_COMMANDS.inc");

        if (!targetDirLogin.exists()) {
                return false;
        }
        return true;
        
       }
       catch(IOException e){
           return false;
       }
   }
   public static String getValue(String name) throws IOException{
	   FileReader file = new FileReader(propPath+"/cred.properties");
	   Properties p = new Properties();
	   p.load(file);
	   file.close();
	   return p.getProperty(name);
	   
   }
    public static void putValue(String name,String value) throws IOException{
	   FileWriter file = new FileWriter(propPath+"/cred.properties",true);
	   Properties p = new Properties();
	   p.setProperty(name,value);
	   p.store(file,"user credentials for NMS");
	   file.close();
	   return ;
   }
}

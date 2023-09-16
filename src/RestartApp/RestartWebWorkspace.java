package RestartApp;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class RestartWebWorkspace {

    static String propPath = "";
    static javax.swing.JTextArea send;
    static Map<String,String> jpsMap;
    

    public static void execute(boolean start,boolean build, boolean stop, javax.swing.JTextArea textA) throws Exception {
        send = textA;

        //Finding Most recent log file in OracleNMS.
        String user = System.getProperty("user.name");

        propPath = "C:/Users/" + user + "/Documents";

       if(stop){
           setTextArea("stop");
           
           loadProcessMap();
           return;
       }
       if(build){
           build();
       }
       if(start){
           clear();
           //start();
       }
    }
    
    public static void stopProject(String p){
        
    }
    
    //Local copy launched "jps" gives as JWSLauncher
    //JNLP copy launched "jps" gives as Launcher
    public static void loadProcessMap(){
        setTextArea("Loading process list ...");
        jpsMap = new HashMap<>();
         try {
            Process process = Runtime.getRuntime().exec("jps");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    jpsMap.put(parts[0], parts[1]);
                }
            }
            process.waitFor();
             
        } catch (IOException | InterruptedException e) {
             setTextArea(">>> Exception while executing jsp command\n\n");
             setTextArea(e.toString());
        }
          setTextArea("Loading process list ... completed");
    }
    
    
    public static void getRunningProjectList(String p){
        File logs[] = getLogFiles("WebWorkspace");
        
        for(File log: logs){
            try {
                Map<String,String> mp = parseLog(log);
                
                if(mp.get("PROJECT").equals(p)){
                    
                }
                
            } catch (IOException ex) {
                
            }
            
        }
    }
    
    public static void isRunning(String PID){
        
    }
    
    public static void start()throws IOException{
        String path = "";

            if (getValue("pathWebWorkspace") == null || getValue("pathWebWorkspace").equals("")) {

                setTextArea( "\nError*** WebWorkspace.exe path not valid.\n");
                return;

            } else {
                path = getValue("pathWebWorkspace");
            }
            String commandArray1[] = {"cmd.exe", "/c ", "WebWorkspace.exe"};
            try {
                Process process = Runtime.getRuntime().exec(commandArray1, null, new File(path));
                setTextArea( "NEW WINDOW OPEN\n");

            } catch (IOException e) {

                setTextArea("Error** Invalid WebWorkspace path\n");
                setTextArea( e + "\n");
            }
    }
    
    public static void build() throws IOException, InterruptedException{
        String path = "";
            //textA.setText(textA.getText()+"loop");
            if (getValue("pathJconfig") == null || getValue("pathJconfig").equals("")) {

                setTextArea( "\nError*** Jconfig path not valid.\n");
                return;

            } else {
                path = getValue("pathJconfig");
            }

            
            try {

                ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "ant config && exit 0 || exit 1");
                builder.directory(new File(path));
                // start the process
                Process process = builder.start();

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String s = null;
                String result = "";
                setTextArea( "Build running... (ant config)" + "\n");
                while ((s = stdInput.readLine()) != null) {
                    result += s + "\n";
                    setTextArea( result + "\n");
                }
                if (process.waitFor() == 1) {
                    return;

                } else {

                    setTextArea( "-----BUILD SUCCESSFUL.\n");

                }
            } catch (IOException e) {
                setTextArea( "Error** Invalid Jconfig path.\n");
                setTextArea( e + "\n");
                return;
            }

    }
    
    public static void stop(String PID)throws IOException, InterruptedException{
        Process process = Runtime.getRuntime().exec("cmd /c " + "taskkill /F /pid " + PID + " && exit 0 || exit 1");

        setTextArea( "PID : " + PID + "\n");

        if (process.waitFor() == 1) {
            setTextArea("No opened webWorkspace found..\n");
        } else {
            setTextArea( "Closed webWorkspace...\n");
        }
    }
    
    public static File[] getLogFiles(String app){
        String user = System.getProperty("user.name");
        propPath = "C:/Users/" + user + "/Documents";
        
        File directory = new File("C:\\Users\\" + user + "\\AppData\\Local\\Temp\\OracleNMS");
            File[] logfiles = directory.listFiles((dir, name) -> name.contains(app) && new File(dir, name).isFile());
            long lastModifiedTime = Long.MIN_VALUE;
            File chosenFile = null;


            if (logfiles != null) {
                Arrays.sort(logfiles, new Comparator<File>() {
                    @Override
                    public int compare(File file1, File file2) {
                        long lastModified1 = file1.lastModified();
                        long lastModified2 = file2.lastModified();

                        if (lastModified1 < lastModified2) {
                            return 1; // For descending order
                        } else if (lastModified1 > lastModified2) {
                            return -1; // For descending order
                        } else {
                            return 0;
                        }
                    }
                });
            }
            else{
                JOptionPane.showMessageDialog(null, "Before using this application you must run the WebWorkspace atleast once manually.", "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return logfiles;
    }
    
    public static Map<String,String> parseLog(File file) throws FileNotFoundException, IOException{
        
        if(file == null)return null;
        
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String pidLine = "";
        String projectLine = "";
        String time="";
        String username = "";
        String line;
        boolean a=false,b=false,c=true,d=true;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("PID")) {
                pidLine = line;
                a=true;
            }
            if(line.startsWith("CLIENT_TOOL_PROJECT_NAME")){
                projectLine = line;
                b=true;
            }
            if(line.startsWith("CLIENT_TOOL_PROJECT_BUILD_DATE")){
                time = line;
                c=true;
            }
            if(line.startsWith("USERNAME")){
                username = line;
                d=true;
            }
            if(a && b && c && d){
                break;
            }
        }
        
        if(!(a && b)){
            return null;
        }
        
        String inputDateStr = time.split("=")[1].trim().replaceAll("\"", "");
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

        try {
            Date date = inputDateFormat.parse(inputDateStr);
            
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
            time = outputDateFormat.format(date);

        } catch (ParseException e) {
            time = "N/A";
        }
        
        Map<String,String> mp = new HashMap<>();
        
        mp.put("PID",pidLine.split("=")[1].trim() );
        mp.put("PROJECT", projectLine.split("=")[1].trim().replaceAll("\"", ""));
        mp.put("USER", username.split("=")[1].trim().replaceAll("\"", ""));
        mp.put("TIME", time);
        return mp;
    }

    public static boolean validate() {
        String user = System.getProperty("user.name");

        propPath = "C:/Users/" + user + "/Documents";
        try {
            if (getValue("pathJconfig") == null || getValue("pathJconfig").length() == 0) {
                return false;
            }
            if (getValue("pathWebWorkspace") == null || getValue("pathWebWorkspace").length() == 0) {
                return false;
            }
            if (getValue("username") == null || getValue("username").length() == 0) {
                return false;
            }
            if (getValue("password") == null || getValue("password").length() == 0) {
                return false;
            }
            if (getValue("autoLogin") == null || getValue("autoLogin").length() == 0) {
                return false;
            }

            String pathLogin = getValue("pathJconfig") + "/global/xml/Login.xml";

            File file = new File(pathLogin);

            if (!file.exists()) {
                return false;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean b = true;
            while ((line = br.readLine()) != null) {
                if (line.contains("AUTO_LOGIN_COMMANDS.inc")) {
                    b = false;
                }
            }
            if (b) {
                return false;
            }
            File targetDirLogin = new File(getValue("pathJconfig") + "/java/src/custom/LoadCredentialsExternalCommand.java");
            if (!targetDirLogin.exists()) {
                return false;
            }
            targetDirLogin = new File(getValue("pathJconfig") + "/global/xml/AUTO_LOGIN_COMMANDS.inc");

            if (!targetDirLogin.exists()) {
                return false;
            }
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public static String getValue(String name) throws IOException {
        FileReader file = new FileReader(propPath + "/cred.properties");
        Properties p = new Properties();
        p.load(file);
        file.close();
        return p.getProperty(name);

    }

    public static void putValue(String name, String value) throws IOException {
        FileWriter file = new FileWriter(propPath + "/cred.properties", true);
        Properties p = new Properties();
        p.setProperty(name, value);
        p.store(file, "user credentials for NMS");
        file.close();
        return;
    }
    
    public static void  setTextArea(String s){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                send.append(s+"\n");
            }
        }); // Handle any exceptions that may occur
    }
    
    public static void clear(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                send.setText("");
            }
        }); // Handle any exceptions that may occur
    }
}

package RestartApp;

import static RestartApp.Delete.tempFile;
import static RestartApp.UpdateDetails.addProject;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import static java.lang.System.exit;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class RestartWebWorkspace {

    static String propPath = "";
    static javax.swing.JTextArea send;
    static Map<String, String> jpsMap;
    static javax.swing.JComboBox combobox;
    static javax.swing.JComboBox buildbox;
    static javax.swing.JDialog jDialog1;
    static JProgressBar bar;
    static JButton buttonBuild;
    static JButton buttonRestart;
    static JButton buttonStart;
    static JButton buttonStop;
    static JButton buttonLog;

    public static void init(javax.swing.JTextArea textA, javax.swing.JComboBox boxA, javax.swing.JComboBox boxB, javax.swing.JDialog jDialog, JProgressBar bar1, JButton b5, JButton b1, JButton b9, JButton b2, JButton b8) {
        send = textA;
        combobox = boxA;
        buildbox = boxB;
        String user = System.getProperty("user.name");
        propPath = "C:/Users/" + user + "/Documents";
        jDialog1 = jDialog;
        bar = bar1;
        buttonStop = b5;
        buttonStart = b1;
        buttonBuild = b9;
        buttonRestart = b2;
        buttonLog = b8;
    }

    public static void execute(boolean start, boolean build, boolean stop) throws Exception {

        //Finding Most recent log file in OracleNMS.
        if (!RestartWindow.isSetuped) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(jDialog1, "Please setup the application with proper details.", "Warning", JOptionPane.WARNING_MESSAGE);
                    buttonStop.setEnabled(true);
                    buttonBuild.setEnabled(true);
                    buttonStart.setEnabled(true);
                    buttonRestart.setEnabled(true);
                }
            });
            return;
        }

        if (stop) {
            clear();
            setBar(0);
            loadProcessMap();
            setBar(40);
            setTextArea("STOP Process Begin...\n");
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    try {
            if (!stopProject(combobox.getSelectedItem().toString())) {
                buttonStop.setEnabled(true);
                buttonRestart.setEnabled(true);
                return;
            }
            buttonStop.setEnabled(true);
//                    } catch (IOException ex) {
//                        setTextArea("Failed with IOException");
//                        buttonStop.setEnabled(true);
//                    }
//                }
//            });

        }
        if (build) {
            if (!stop) {
                clear();
            }
            setBar(0);
            setTextArea("BUILD Process Begin...\n");
            build();
            buttonBuild.setEnabled(true);
        }
        if (start) {
            if (!build) {
                clear();
            }
            setTextArea("Start Process Begin...\n");
            setBar(0);
            start();
            buttonStart.setEnabled(true);
        }
        buttonRestart.setEnabled(true);
    }

    public static void viewLog() throws IOException {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                List<Map<String, String>> l = getRunningProcessList(combobox.getSelectedItem().toString(), false);

                if (l == null || l.isEmpty()) {
                    setTextArea("No logs found for the project.");
                    buttonLog.setEnabled(true);
                    return;
                }

                l = selectProcess(l);

                String user = System.getProperty("user.name");
                String pathLog = "C:\\Users\\" + user + "\\AppData\\Local\\Temp\\OracleNMS\\";
                if (l == null || l.isEmpty()) {
                    buttonLog.setEnabled(true);
                    return;
                }

                setTextArea(l.get(0).get("FILE"));

                String[] command = {"cmd", "/c", "notepad", pathLog + l.get(0).get("FILE")};

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                try {
                    Process process = processBuilder.start();

                } catch (Exception ex) {
                    setTextArea("Exception while opening log");
                    buttonLog.setEnabled(true);
                }
                buttonLog.setEnabled(true);
            }
        });

    }

    public static boolean stopProject(String project) throws IOException {
        List<Map<String, String>> processList = getRunningProcessList(project, true);

        setBar(60);

        if (processList == null || processList.isEmpty()) {
            setTextArea("No running processes found. - " + project);
            setBar(100);
            return true;
        } else {
            if (processList.size() == 1) {
                try {
                    stop(processList.get(0).get("PID"));
                    setTextArea("Successfully stoped process - " + processList.get(0).get("PID"));
                    setBar(100);
                    return true;
                } catch (InterruptedException ex) {
                    setTextArea("Failed to stop process - " + processList.get(0).get("PID"));
                    setBar(100);
                    return true;
                }
            } else {
                processList = selectProcess(processList);
                if (processList == null) {
                    setTextArea("No processes selected. - " + project);
                    setBar(100);

                    return false;
                }
                for (Map<String, String> process : processList) {
                    try {
                        stop(process.get("PID"));
                        setTextArea("Successfully stoped process - " + process.get("PID"));

                    } catch (InterruptedException ex) {
                        setTextArea("Failed to stop process - " + process.get("PID"));
                        setBar(100);
                        return true;
                    }
                }
                setBar(100);
                return true;
            }
        }
    }

    public static List<Map<String, String>> selectProcess(List<Map<String, String>> process) {
        String[][] rowData = new String[process.size()][4];

        for (int i = 0; i < process.size(); i++) {
            rowData[i][0] = i + 1 + "";
            //rowData[i][1] = process.get(i).get("USER");
            rowData[i][1] = process.get(i).get("LAUNCHER");
            rowData[i][2] = process.get(i).get("TIME");
            rowData[i][3] = process.get(i).get("PID");
        }

        //JTable table = new JTable(rowData, new Object[]{"S.NO","USER","TIMESTAMP","PID"});
        DefaultTableModel tableModel = new DefaultTableModel(rowData, new Object[]{"S.NO", "PROCESS", "MODIFIED TIME", "PID"});
        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Select from below list.");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        panel.add(label, BorderLayout.NORTH);

        JCheckBox filterCheckBox = new JCheckBox("Filter local processes.");
        panel.add(filterCheckBox, BorderLayout.SOUTH);

        // Create a JPanel to hold the table
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(380, 150)); // Set the preferred size

        int[] columnWidths = {50, 60, 120, 60}; // Widths for each column in pixels

        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(rowSorter);

        filterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filterCheckBox.isSelected()) {
                    // Enable filtering based on the launcher column (column index 2)
                    rowSorter.setRowFilter(RowFilter.regexFilter("Local", 1));
                } else {
                    // Disable filtering
                    rowSorter.setRowFilter(null);
                }
            }
        });
        rowSorter.setRowFilter(RowFilter.regexFilter("Local", 1));
        filterCheckBox.setSelected(true);

        // Show the JOptionPane with the JPanel as the message
        JFrame jf = new JFrame();
        jf.setAlwaysOnTop(true);
        int result = JOptionPane.showOptionDialog(
                jf,
                panel,
                "Select process",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null
        );

        // Check the user's choice
        if (result == JOptionPane.OK_OPTION) {
            int[] selectedRow = table.getSelectedRows();
            if (selectedRow.length != 0) {
                List<Map<String, String>> res = new ArrayList<>();
                for (int i : selectedRow) {
                    res.add(process.get(i));
                }
                return res;

            } else {
                setTextArea("Please select processes to continue");
            }
        } else {
            setTextArea("Cancelled process.");
        }
        return null;

    }

    //Local copy launched "jps" gives as JWSLauncher
    //JNLP copy launched "jps" gives as Launcher
    public static void loadProcessMap() {
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

    public static List<Map<String, String>> getRunningProcessList(String project, boolean running) {

        File logs[] = getLogFiles("WebWorkspace");
        List<Map<String, String>> projects = new ArrayList<>();

        if (logs == null || logs.length == 0) {
            return null;
        }

        for (File log : logs) {
            try {
                Map<String, String> mp = parseLog(log);
                if (mp != null && !mp.isEmpty() && mp.get("PROJECT").equals(project) && (!running || jpsMap.containsKey(mp.get("PID")))) {

                    projects.add(mp);

                }
            } catch (IOException ex) {
                return projects;
            }
        }

        return projects;
    }

    public static void refreshProjects(Set<String> projectList) {
        clear();
        setTextArea("Reloading projects from logs..");
        File[] files = getLogFiles("");
        if (files == null || files.length == 0) {
            setTextArea("No projects found in the system.");
            return;
        }
        for (File f : files) {
            try {
                Map<String, String> m = parseLog(f);
                if (m != null) {
                    if (!projectList.contains(m.get("PROJECT"))) {
                        addProject(m.get("PROJECT"));
                        projectList.add(m.get("PROJECT"));
                        setTextArea("added project - " + m.get("PROJECT"));
                    }
                }
            } catch (IOException ex) {
                setTextArea("Reloading exited with exception - " + ex.toString());
            }
        }
        setTextArea("Reload completed successfully");
    }

    public static void start() throws IOException, InterruptedException {
        String path = "";
        setBar(10);
        if (getValue("pathWebWorkspace") == null || getValue("pathWebWorkspace").equals("")) {

            setTextArea("\nError*** WebWorkspace.exe path not valid.\n");
            setBar(100);
            return;

        } else {
            path = getValue("pathWebWorkspace");
            setBar(30);
        }
        String commandArray1[] = {"cmd.exe", "/c ", "WebWorkspace.exe"};
        try {
            File f = new File(path + "/WebWorkspace.exe");
            if (!f.exists()) {
                setTextArea("workspace path is incorrect.\n");
                setBar(100);
                return;
            }
            Process process = Runtime.getRuntime().exec(commandArray1, null, new File(path));

            setTextArea("WINDOW OPEN DONE\n");
            setBar(100);

        } catch (IOException e) {

            setTextArea("Error** Invalid WebWorkspace path\n");
            setTextArea(e + "\n");
            setBar(100);
        }
    }

    public static void build() throws IOException, InterruptedException {
        String path = "";
        setBar(10);
        //textA.setText(textA.getText()+"loop");
        if (getValue("pathJconfig") == null || getValue("pathJconfig").equals("")) {

            setTextArea("\nError*** Jconfig path not valid.\n");
            setBar(100);
            return;

        } else {
            path = getValue("pathJconfig");
            setBar(20);
        }

        try {

            String type = "";

            if (buildbox.getSelectedItem().toString().equals("Ant config")) {
                type = "ant config";
            } else {
                type = "ant clean config";
            }

            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", type + " && exit 0 || exit 1");
            builder.directory(new File(path));
            // start the process
            Process process = builder.start();
            setBar(40);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String s = null;
            String result = "";
            setTextArea("Build running... (" + type + ")" + "\n");

            setBar(50);
            while ((s = stdInput.readLine()) != null) {
                result += s + "\n";
                setTextArea(s);
            }
            setBar(70);
            if (process.waitFor() == 1) {
                setTextArea("-----BUILD Failed.\n");
                setBar(100);
                return;

            } else {

                setTextArea("-----BUILD SUCCESSFUL.\n");
                setBar(100);
            }
        } catch (IOException e) {
            setTextArea("Error** Invalid Jconfig path.\n");
            setTextArea(e + "\n");
            setBar(100);
            return;
        }

    }

    public static void stop(String PID) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("cmd /c " + "taskkill /F /pid " + PID + " && exit 0 || exit 1");

        setTextArea("PID : " + PID + "\n");

        if (process.waitFor() == 1) {
            setTextArea("No opened webWorkspace found..\n");
        } else {
            setTextArea("Closed webWorkspace...\n");
        }
    }

    public static File[] getLogFiles(String app) {
        String user = System.getProperty("user.name");
        propPath = "C:/Users/" + user + "/Documents";
        File directory = null;
        try {
            directory = new File("C:\\Users\\" + user + "\\AppData\\Local\\Temp\\OracleNMS");
        } catch (Exception e) {
            setTextArea(e.toString());

        }
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
        } else {
            JOptionPane.showMessageDialog(null, "Before using this application you must run the WebWorkspace atleast once manually.", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return logfiles;
    }

    public static Map<String, String> parseLog(File file) throws FileNotFoundException, IOException {

        if (file == null) {
            return null;
        }

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String pidLine = "";
        String projectLine = "";
        String launcher = "";

        String username = "";
        String line;
        boolean a = false, b = false, c = false, d = false;
        while ((line = br.readLine()) != null) {

            //System.out.println(line);
            if (line.startsWith("PID")) {
                pidLine = line;
                a = true;
            }
            if (line.startsWith("CLIENT_TOOL_PROJECT_NAME")) {
                projectLine = line;
                b = true;
            }

            if (line.startsWith("USERNAME")) {

                username = line;
                d = true;
            }

            if (line.contains("/version.xml")) {
                if (line.contains("AppData") && line.contains(".nms")) {
                    launcher = "JNLP";
                    c = true;
                } else {
                    launcher = "Local";
                    c = true;
                }
            }
            if (a && b && c && d) {
                break;
            }
        }

        if (!(a && b)) {
            return null;
        }
        String time;
        long lastModifiedTimestamp = file.lastModified();
        Date lastModifiedDate = new Date(lastModifiedTimestamp);

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
        time = outputDateFormat.format(lastModifiedDate).replaceAll("am", "AM").replaceAll("pm", "PM");

        Map<String, String> mp = new HashMap<>();

        mp.put("PID", pidLine.split("=")[1].trim());
        mp.put("PROJECT", projectLine.split("=")[1].trim().replaceAll("\"", ""));
        mp.put("USER", username.equals("") ? "N/A" : username.split("=")[1].trim().replaceAll("\"", ""));
        mp.put("TIME", time);
        mp.put("FILE", file.getName());
        mp.put("LAUNCHER", launcher);
        return mp;
    }

    public static boolean validate() {

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

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public static boolean validateSetup() {

        try {

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
        if (!name.equals("Projects")) {
            name = combobox.getSelectedItem().toString() + "_" + name;
        }
        
        String filePath = propPath + "/cred.properties"; // Replace with the full path to your properties file

        File file = new File(filePath);
        //setTextArea("getvalue method");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                setTextArea("cred.properties file creation failed.");
                // Handle the exception as needed
            }
        }
        
        FileReader filer = new FileReader(propPath + "/cred.properties");
        
        
        Properties p = new Properties();
        p.load(filer);
        filer.close();
        return p.getProperty(name);

    }

    public static void putValue(String name, String value) throws IOException {

        if (!(name.equals("Projects") || name.equals("selectedProject"))) {
            name = combobox.getSelectedItem().toString() + "_" + name;
        }

        String filePath = propPath + "/cred.properties"; // Replace with the full path to your properties file

        File file = new File(filePath);
        //setTextArea("putvalue method");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                setTextArea("cred.properties file creation failed.");
                // Handle the exception as needed
            }
        }

        // Load existing properties from the file
        Properties p = new Properties();
        FileReader fileReader = new FileReader(filePath);
        p.load(fileReader);
        fileReader.close();

        // Remove the property if it exists
        p.remove(name);

        // Set the new property
        p.setProperty(name, value);

        // Save the modified properties back to the file
        FileWriter fileWriter = new FileWriter(filePath);
        p.store(fileWriter, "user credentials for NMS");
        fileWriter.close();
    }

    public static void removeValue(String name) throws IOException {

        if (!(name.equals("Projects") || name.equals("selectedProject"))) {
            name = combobox.getSelectedItem().toString() + "_" + name;
        }

        String filePath = propPath + "/cred.properties"; // Replace with the full path to your properties file

        // Load existing properties from the file
        Properties p = new Properties();
        FileReader fileReader = new FileReader(filePath);
        p.load(fileReader);
        fileReader.close();

        // Remove the property if it exists
        p.remove(name);

        // Save the modified properties back to the file
        FileWriter fileWriter = new FileWriter(filePath);
        p.store(fileWriter, "user credentials for NMS");
        fileWriter.close();
    }

    public static void setTextArea(String s) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                send.append(s + "\n");
            }
        }); // Handle any exceptions that may occur
    }

    public static void clear() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                send.setText("");
            }
        }); // Handle any exceptions that may occur
    }

    public static void setBar(int i) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                bar.setValue(i);
            }
        });
    }
}

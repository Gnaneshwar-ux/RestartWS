
Recommendation:
- Ensure you are using Java version 17.

Prerequisites:
- Before using our tool, make sure to open the web workspace at least once for the specific project you intend to work with.

Setup Process (one-time setup for each project):
1. Open the "RestartApp.jar" file.
2. Click the "Reload" button. This will populate the list of available projects based on log files.
3. Select your desired project from the dropdown menu. Fill in the following details:
   - Example: For the project "OPAL"
   - Jconfig Path: C:\NMS projects\OPAL\jconfig
   - WebWorkspace.exe Path: C:\Oracle NMS\opal\
     (Note: Do not include "WebWorkspace.exe" in the path)
   - Username: nms5
   - Password: Oracle1234
   - Autologin: Configure this based on your specific needs. The provided credentials are used for the autologin process.
4. Click the "Setup" button. This action will update the necessary files and modify "login.xml" to ensure the tool functions correctly.
5. The setup process is now complete.

Restart:
  - The "Restart" button performs the following operations:
  - Closes the currently open web workspace for the selected project.
  - Executes either "ant config" or "ant clean config" based on the user's selection from the dropdown menu.
  - Opens a new web workspace for the selected project.

Start:
  - Clicking the "Start" button launches a new web workspace for the selected project.

Stop:
  - The "Stop" button closes the currently open web workspace for the selected project.

Build:
  - The "Build" button executes "ant config" or "ant clean config" based on the user's selection.

Delete:
  - This option is used to revert all file changes made by the tool for the autologin implementation in the selected project. It will not delete any project files or impact the product code in any way.
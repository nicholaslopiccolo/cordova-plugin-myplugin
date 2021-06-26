package cordova.plugin.myplugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

//import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
//import org.json.JSONObject;

import jdk.internal.net.http.common.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class Myplugin extends CordovaPlugin {
    private static final  String ECHO = "echo";
    private static final String NEW_TASK = "newTask";
    private static final String DEL_TASK = "delTask";

    private ArrayList <Timer> alt = new ArrayList<>();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ECHO)) {
            String message = args.getString(0);
            this.echo(message, callbackContext);
            return true;
        } 
        else if (action.equals(NEW_TASK)) {
            this.newTask(callbackContext);
            return true;
        } 
        else if (action.equals(DEL_TASK)) {
            this.delTask(callbackContext);
            return true;
        }
        Log.i("tag","no function called");
        return false;
    }

    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            Log.i("tag","LOG: " + message);
            callbackContext.success("LOLLO: " + message); // la callback SUCCESS via javascript
        } else {
            Log.i("tag","error echo");
            callbackContext.error("Expected one non-empty string argument."); // la callback ERROR via javascript
        }
    }

    private void newTask(CallbackContext callbackContext) {
        try {
            Timer t = new Timer();
            TimerTask ts = new TimerTask() {
                @Override
                public void run() {
                    Log.i("tag", "A Kiss every 5 seconds");
                    //System.out.println("A Kiss every 5 seconds");
                }
            };

            t.scheduleAtFixedRate(ts, 0, 5000);

            alt.add(t);
            callbackContext.success("created task"); // la callback SUCCESS via javascript
        } catch (Exception e) {
            Log.i("tag","error in new task");
            callbackContext.error("error: "+e.getMessage()); // la callback ERROR via javascript

        }
    }

    private void delTask(CallbackContext callbackContext) {
        try {
            alt.remove(0);
            Log.i("tag","task removed");
            callbackContext.success("removed task"); // la callback SUCCESS via javascript
        } catch (Exception e) {
            Log.i("tag","error in del task");
            callbackContext.error("error: "+e.getMessage()); // la callback ERROR via javascript

        }
    }
}
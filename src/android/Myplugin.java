package cordova.plugin.myplugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

//import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
//import org.json.JSONObject;

import android.util.Log;
/**
 * This class echoes a string called from JavaScript.
 */
public class Myplugin extends CordovaPlugin {
    private static final String ECHO = "echo";
    private static final String NEW_TASK = "newTask";
    private static final String DEL_TASK = "delTask";

    private ArrayList < Timer > alt = new ArrayList < > ();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        try {
            if (action.equals(ECHO)) {
                String message = args.getString(0);
                this.echo(message, callbackContext);
                return true;
            } else if (action.equals(NEW_TASK)) {
                JSONObject obj = args.getJSONObject(0);
                String phone = obj.getString("phone");
                String message = obj.getString("message");
                int nsec = obj.getInt("nsec");
                this.newTask(phone, message, nsec, callbackContext);
                return true;
            } else if (action.equals(DEL_TASK)) {
                JSONObject obj = args.getJSONObject(0);
                int index = obj.getInt("index");
                this.delTask(index, callbackContext);
                return true;
            }
            return false;
        } catch (JSONException e) {
            Log.e("tag", e.getMessage());
            return false;
        }
    }

    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message); // la callback SUCCESS via javascript
        } else {
            Log.e("tag", "error echo");
            callbackContext.error("Expected one non-empty string argument."); // la callback ERROR via javascript
        }
    }

    private void newTask(String phone, String message, int nsec, CallbackContext callbackContext) {
        try {
            Timer t = new Timer();
            TimerTask ts = new TimerTask() {
                @Override
                public void run() {
                    Log.i("tag", message);
                    //System.out.println("A Kiss every 5 seconds");
                }
            };

            t.scheduleAtFixedRate(ts, 0, nsec);

            alt.add(t);
            callbackContext.success("created task"); // la callback SUCCESS via javascript
        } catch (Exception e) {
            Log.i("tag", "error in new task");
            callbackContext.error("error: " + e.getMessage()); // la callback ERROR via javascript

        }
    }

    private void delTask(int index, CallbackContext callbackContext) {
        try {
            Timer t = alt.get(index);
            t.cancel();
            t.purge();

            alt.remove(index);

            Log.i("tag", "task removed");
            callbackContext.success("removed task"); // la callback SUCCESS via javascript
        } catch (Exception e) {
            Log.e("tag", "error in del task");
            callbackContext.error("error: " + e.getMessage()); // la callback ERROR via javascript

        }
    }
}
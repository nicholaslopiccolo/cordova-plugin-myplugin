package cordova.plugin.myplugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class Myplugin extends CordovaPlugin {
    private final String COOL_METHOD = "coolMethod";
    private final String ECHO = "echo";


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(COOL_METHOD)) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if(action.equals(ECHO)){
            String message = args.getString(0);
            this.echo(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message); // chiami la callback SUCCESS via javascript
        } else {
            callbackContext.error("Expected one non-empty string argument."); // chiami la callback ERROR via javascript
        }
    }

    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message); // la callback SUCCESS via javascript
        } else {
            callbackContext.error("Expected one non-empty string argument."); // la callback ERROR via javascript
        }
    }
}

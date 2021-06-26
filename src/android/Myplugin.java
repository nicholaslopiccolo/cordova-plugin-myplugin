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


// Imports for SMS
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import org.apache.cordova.PluginResult;

/**
 * Class for schedule tasks.
 */
public class Myplugin extends CordovaPlugin {
    private static final String ECHO = "echo";
    private static final String NEW_TASK = "newTask";
    private static final String DEL_TASK = "delTask";
    private ArrayList < Timer > alt = new ArrayList < > ();


    // SMS state
    private static final String INTENT_FILTER_SMS_SENT = "SMS_SENT";

    //private static final int SEND_SMS_REQ_CODE = 0;
	//private static final int REQUEST_PERMISSION_REQ_CODE = 1;
	private CallbackContext callbackContext;
	private JSONArray args;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
		this.args = args;
        
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
                    send(callbackContext,phone,message);
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

    private boolean sendSMS(String phoneNumber,String message) {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					//parsing arguments
                    /*
					String separator = ";";
					if (android.os.Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
						// See http://stackoverflow.com/questions/18974898/send-sms-through-intent-to-multiple-phone-numbers/18975676#18975676
						separator = ",";
					}*/
					//String phoneNumber = args.getJSONArray(0).join(separator).replace("\"", "");
					String method = "";//args.getString(2);
					/*boolean replaceLineBreaks = Boolean.parseBoolean(args.getString(3));

					// replacing \n by new line if the parameter replaceLineBreaks is set to true
					if (replaceLineBreaks) {
						message = message.replace("\\n", System.getProperty("line.separator"));
					}
					if (!checkSupport()) {
						callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SMS not supported on this platform"));
						return;
					}*/
					if (method.equalsIgnoreCase("INTENT")) {
						invokeSMSIntent(phoneNumber, message);
						// always passes success back to the app
						callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
					} else {
						send(callbackContext, phoneNumber, message);
					}
					return;
				} catch (Exception ex) {
                    Log.e("tag",ex.getMessage());
					//callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
				}
			}
		});
		return true;
	}

    private boolean checkSupport() {
		Activity ctx = this.cordova.getActivity();
		return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

	@SuppressLint("NewApi")
	private void invokeSMSIntent(String phoneNumber, String message) {
		Intent sendIntent;
		if ("".equals(phoneNumber) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this.cordova.getActivity());

			sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/plain");
			sendIntent.putExtra(Intent.EXTRA_TEXT, message);

			if (defaultSmsPackageName != null) {
				sendIntent.setPackage(defaultSmsPackageName);
			}
		} else {
			sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("sms_body", message);
			// See http://stackoverflow.com/questions/7242190/sending-sms-using-intent-does-not-add-recipients-on-some-devices
			sendIntent.putExtra("address", phoneNumber);
			sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
		}
		this.cordova.getActivity().startActivity(sendIntent);
	}

	private void send(final CallbackContext callbackContext, String phoneNumber, String message) {
		SmsManager manager = SmsManager.getDefault();
		final ArrayList<String> parts = manager.divideMessage(message);

        Log.i("tag","1 messaggio inviato: "+message);

		// by creating this broadcast receiver we can check whether or not the SMS was sent
		final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

			boolean anyError = false; //use to detect if one of the parts failed
			int partsCount = parts.size(); //number of parts to send

			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case SmsManager.STATUS_ON_ICC_SENT:
				case Activity.RESULT_OK:
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				case SmsManager.RESULT_ERROR_NO_SERVICE:
				case SmsManager.RESULT_ERROR_NULL_PDU:
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					anyError = true;
					break;
				}
				// trigger the callback only when all the parts have been sent
				partsCount--;
				/*if (partsCount == 0) {
					if (anyError) {
						callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
					} else {
						callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
					}
					cordova.getActivity().unregisterReceiver(this);
				}*/
			}
		};

        Log.i("tag","2 messaggio inviato: "+message);

        // randomize the intent filter action to avoid using the same receiver
		String intentFilterAction = INTENT_FILTER_SMS_SENT + java.util.UUID.randomUUID().toString();
		this.cordova.getActivity().registerReceiver(broadcastReceiver, new IntentFilter(intentFilterAction));

        Log.i("tag","3 messaggio inviato: "+message);

		PendingIntent sentIntent = PendingIntent.getBroadcast(this.cordova.getActivity(), 0, new Intent(intentFilterAction), 0);

        Log.i("tag","4 messaggio inviato: "+message);


		// depending on the number of parts we send a text message or multi parts
		if (parts.size() > 1) {
			ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
			for (int i = 0; i < parts.size(); i++) {
				sentIntents.add(sentIntent);
			}
			manager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, null);
		}
		else {
            Log.i("tag","5 messaggio inviato: "+message);
			manager.sendTextMessage(phoneNumber, null, message, sentIntent, null);
            Log.i("tag","6 messaggio inviato: "+message);

		}
	}
}
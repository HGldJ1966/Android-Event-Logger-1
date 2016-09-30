package com.arkada38.eventlogger.Controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.arkada38.eventlogger.Model.Settings;
import com.arkada38.eventlogger.R;

import java.util.List;

public class Activation implements BillingProcessor.IBillingHandler {
    public static BillingProcessor bp;
    public static Activation a;
    static String tag = "EventLogger";

    public Activation() {
        bp = new BillingProcessor(Settings.activity, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzIBv+altmZVFoaFrp+tw9wuVueyVHPxVnCYYbtPCAtAcpEy2uZtCs3daukTkwSesCHcUQoS2YCmj5OD9ySVib//GWs/wfl5ouP/3eUgEHel6cvvdklkOidEeyxrvFwWKPvg1e5do3ppAPfR9Y5kAW0j7pbOCNxNBXpvVxbfD55hGl1jyku63pyFxBAkDU6D8kHscEDzYkAGZiM81+2AMsrJm9lS2oGXICvD911n6+MdShE5ccokmoGAt3oPIHvwX6+BJcviWT2TqODmC7f8RGGDIzh79/AoUM1ToHuYoUa21mWL+kmZVTMWgeHvpDFGxdlVK2620ZS4DddPV7PQQcQIDAQAB", this);
    }

    @Override
    public void onBillingInitialized() {
        Log.d(tag, "BillingProcessor was initialized and it's ready to purchase");
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(tag, "PRODUCT ID " + productId + " was successfully purchased");
        Settings.setAccess(true);
        Toast.makeText(Settings.activity, Settings.activity.getString(R.string.successfully_purchased), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d(tag, "onBillingError");
        Toast.makeText(Settings.activity, Settings.activity.getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(tag, "onPurchaseHistoryRestored");
    }

    public static void initMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.activity);

        dialogBuilder.setTitle(Settings.activity.getString(R.string.activation_title));
        dialogBuilder.setMessage(Settings.activity.getString(R.string.activation_message));
        dialogBuilder.setPositiveButton(Settings.activity.getString(R.string.buy), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                boolean isAvailable = BillingProcessor.isIabServiceAvailable(Settings.activity);
                if(isAvailable) {
                    Settings.waitingPayment = true;
                    bp.purchase(Settings.activity, "a");
                }
                else
                    Toast.makeText(Settings.activity, Settings.activity.getString(R.string.gp_error), Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton(Settings.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(Settings.activity, Settings.activity.getString(R.string.activation_title), Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public static void restored() {
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(Settings.activity);
        if(isAvailable) {
            bp.loadOwnedPurchasesFromGoogle();

            List listOwnedProducts = bp.listOwnedProducts();
            Log.d(tag, "listOwnedProducts " + listOwnedProducts.size());

            if (bp.isPurchased("a")) {
                Log.d(tag, "Item purchased");
                Settings.setAccess(true);
            }
            else {
                Log.d(tag, "Item not purchased");
                Settings.setAccess(false);
            }
        }
        else
            Toast.makeText(Settings.activity, Settings.activity.getString(R.string.gp_error), Toast.LENGTH_SHORT).show();
    }
}

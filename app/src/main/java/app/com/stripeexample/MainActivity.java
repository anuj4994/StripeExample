package app.com.stripeexample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;

import cz.msebera.android.httpclient.entity.mime.Header;

public class MainActivity extends AppCompatActivity {

    Button btnSave, btnPay;
    CardInputWidget mCardInputWidget;
    Card cardToSave;
    Context mContext;
    Stripe stripe;
    Token mToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        btnSave = (Button) findViewById(R.id.btnSaveCard);
        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setVisibility(View.GONE);
        mContext = getApplicationContext();
        stripe = new Stripe(mContext, "pk_test_ogTRhfvXntPKIP9BBHdqUGof");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardToSave = mCardInputWidget.getCard();
                if (cardToSave == null) {
                    Toast.makeText(MainActivity.this,"Invalid Card Data",Toast.LENGTH_LONG).show();
                }else {
                    cardToSave.setName("Customer Name");
                    cardToSave.setAddressZip("12345");
                    validateCard();
                }

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params  = new RequestParams();
                    params.put("method", "charge");
                    params.put("description", "Test");
                    params.put("source", mToken.getId());
                    params.put("amount", "1000");
                    client.post("http://54.70.113.238:7002/makePayment/",params ,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            System.out.println(Arrays.toString(headers) + " :::: " + statusCode);
                            Toast.makeText(MainActivity.this,"Payment Successful ",Toast.LENGTH_LONG).show();
                            btnPay.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            System.out.println(Arrays.toString(headers) + " :::: " + statusCode);
                            Toast.makeText(MainActivity.this,"Payment Declined ",Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void validateCard(){
        if (cardToSave.validateCard()) {

            final boolean[] valid = {false};
            stripe.createToken(
                    cardToSave,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            System.out.println(token.getCard());
                            mToken = token;
                            btnPay.setVisibility(View.VISIBLE);
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(MainActivity.this,
                                    "Error",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

        }
    }

    //http://54.70.113.238/stripe/charge.php


}

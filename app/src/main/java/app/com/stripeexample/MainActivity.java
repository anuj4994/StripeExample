package app.com.stripeexample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    Button btnGenerateToken, btnPay, btnSaveCustomer, btnPayWithCustomer, btnCreateConnectClient,btnPayWithConnect;
    EditText txtFirstName, txtLastName, txtAddressLine1, txtAddressLine2, txtCity, txtState, txtZip, txtEmail;
    CardInputWidget mCardInputWidget;
    Card cardToSave;
    Context mContext;
    Stripe stripe;
    Token mToken;
    String customerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        btnGenerateToken = (Button) findViewById(R.id.btnGenerateCard);
        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setVisibility(View.GONE);
        btnSaveCustomer = (Button) findViewById(R.id.btnSaveCustomer);
        btnSaveCustomer.setVisibility(View.GONE);
        btnPayWithCustomer = (Button) findViewById(R.id.btnPayWithCustomer);
        btnPayWithCustomer.setVisibility(View.GONE);
        btnCreateConnectClient = (Button) findViewById(R.id.btnCreateConnectClient);
        btnPayWithConnect = (Button) findViewById(R.id.btnPayWithConnect);

        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtAddressLine1 = (EditText) findViewById(R.id.txtAddressLine1);
        txtAddressLine2 = (EditText) findViewById(R.id.txtAddressLine2);
        txtCity = (EditText) findViewById(R.id.txtCity);
        txtState = (EditText) findViewById(R.id.txtState);
        txtZip = (EditText) findViewById(R.id.txtZip);
        txtEmail = (EditText) findViewById(R.id.txtEmail);

        mContext = getApplicationContext();
//        pk_test_ogTRhfvXntPKIP9BBHdqUGof
        stripe = new Stripe(mContext, "pk_test_ogTRhfvXntPKIP9BBHdqUGof");

        btnGenerateToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardToSave = mCardInputWidget.getCard();
                if (cardToSave == null) {
                    Toast.makeText(MainActivity.this,"Invalid Card Data",Toast.LENGTH_LONG).show();
                }else {
                    cardToSave.setName(txtFirstName.getText().toString() + txtLastName.getText().toString());
                    cardToSave.setAddressLine1(txtAddressLine1.getText().toString());
                    cardToSave.setAddressLine2(txtAddressLine2.getText().toString());
                    cardToSave.setAddressCity(txtCity.getText().toString());
                    cardToSave.setAddressState(txtState.getText().toString());
                    cardToSave.setAddressCountry("United States of America");
                    cardToSave.setAddressZip(txtZip.getText().toString());
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

        btnPayWithCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params  = new RequestParams();
                    params.put("method", "charge");
                    params.put("description", "Test");
                    params.put("customerId", customerId);
                    params.put("amount", "1500");
                    client.post("http://54.70.113.238:7002/payWithCustomer/",params ,new AsyncHttpResponseHandler() {
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

        btnSaveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params  = new RequestParams();
                    params.put("email", txtEmail.getText().toString());
                    params.put("source", mToken.getId());
                    client.post("http://54.70.113.238:7002/saveCustomer/",params ,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            Toast.makeText(MainActivity.this,"Payment Successful ",Toast.LENGTH_LONG).show();
                            try {
                                //TODO Store this customer ID
                                customerId = new String(responseBody, "UTF-8");
                                System.out.println(customerId);
                                btnPayWithCustomer.setVisibility(View.VISIBLE);

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
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

        btnCreateConnectClient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params  = new RequestParams();
                params.put("email", txtEmail.getText().toString());
                client.post("http://54.70.113.238:7002/createConnectAccount/",params ,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        Toast.makeText(MainActivity.this,"Payment Successful ",Toast.LENGTH_LONG).show();
                        try {
                            //TODO Store this connect account object
                            String connectAccount = new String(responseBody, "UTF-8");
                            System.out.println(connectAccount);
//                            btnPayWithCustomer.setVisibility(View.VISIBLE);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        System.out.println(Arrays.toString(headers) + " :::: " + statusCode);
                        Toast.makeText(MainActivity.this,"Payment Declined ",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnPayWithConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params  = new RequestParams();
                params.put("account", "acct_1BPjfgEtS9fhKI6U");
                params.put("amount", "1000");
                params.put("source", mToken.getId());
                client.post("http://54.70.113.238:7002/connectCharge/",params ,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        Toast.makeText(MainActivity.this,"Payment Successful ",Toast.LENGTH_LONG).show();
                        try {
                            //TODO Store this connect account object
                            String connectAccount = new String(responseBody, "UTF-8");
                            System.out.println(connectAccount);
//                            btnPayWithCustomer.setVisibility(View.VISIBLE);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        System.out.println(Arrays.toString(headers) + " :::: " + statusCode);
                        Toast.makeText(MainActivity.this,"Payment Declined ",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }

    public void validateCard(){
        if (cardToSave.validateCard()) {

            stripe.createToken(
                    cardToSave,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            System.out.println(token.getCard());
                            mToken = token;
                            btnPay.setVisibility(View.VISIBLE);
                            btnSaveCustomer.setVisibility(View.VISIBLE);
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


}

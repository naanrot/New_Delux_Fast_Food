package com.example.newdeluxfastfood.screens.place_new_order_screen.payment_screen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.newdeluxfastfood.databinding.ActivityPaymentOptionsBinding;
import com.example.newdeluxfastfood.custom_loading_screen.PaytmCustomLoadingDialog;
import com.example.newdeluxfastfood.screens.place_new_order_screen.payment_screen.Paytm.PaytmPayMethod;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PaymentOptions extends AppCompatActivity implements PaytmPayMethod.Connector {
    private static final String TAG = "PaymentOptions";
    private ActivityPaymentOptionsBinding binding;
    private int price;
    private String response;
    private PaytmPayMethod PPM;
    private FirebaseAuth auth =  FirebaseAuth.getInstance();
    private ArrayList<String> orderItems = new ArrayList<>();

    //Making dialog static to use it in this class as well as in interface making only one common
    //instance of the dialog
    private static PaytmCustomLoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentOptionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent passedIntent = getIntent();
        price = passedIntent.getIntExtra("Price", -1);
        orderItems = passedIntent.getStringArrayListExtra("orderItems");

        binding.priceTextView.setText("Passed price: "+price);

        dialog = new PaytmCustomLoadingDialog(PaymentOptions.this);

        binding.paytmOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.showLoadingDialog();
                PPM = new PaytmPayMethod(PaymentOptions.this, ""+price, auth.getUid(), orderItems);
                PPM.generateChecksum();
                //Toast.makeText(PaymentOptions.this, "Paytm option", Toast.LENGTH_SHORT).show();
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
    }

    @Override
    public void getResponse(String response) {
        this.response = response;
        dialog.dismissLoadingDialog();
        Log.d(TAG, "getResponse: "+response);
    }
}
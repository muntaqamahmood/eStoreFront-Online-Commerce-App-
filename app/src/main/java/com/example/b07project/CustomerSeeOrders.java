package com.example.b07project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerSeeOrders extends AppCompatActivity {

    private Customer customer;
    private ListView lstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_see_orders);
        customer = (Customer) getIntent().getSerializableExtra("account");
        //set up the listView and adapter
        lstView = findViewById(R.id.lstOrders);
        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lstView.setAdapter(ordersAdapter);

        //get orders from the database
        String customerName = customer.getUsername();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customers").child(customerName).child("Orders");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("B07 Project", "Couldn't get data", task.getException());
                }
                else{
                    for(DataSnapshot child : task.getResult().getChildren()) {
                        CustomerOrder order = child.getValue(CustomerOrder.class);

                        //make sure it's not an empty order
                        if (order.items != null) {
                            if (order.items != null) {
                                String line =  formatOrder(order);
                                ordersAdapter.add(line);
                            }
                        }
                    }
                }
            }
        });
    }

    private String formatOrder(CustomerOrder order){
        String output = "\nOrderNumber: " + order.orderNumber + "\n" + "From: " + order.storeOwner + '\n';
        output += "\nItems: \n";
        for (Product p: order.items){
            output += p.toString() + '\n';
        }
        output += "\n";
        output+= "Completed: " + order.completed + "\n";
        return output;
    }
}
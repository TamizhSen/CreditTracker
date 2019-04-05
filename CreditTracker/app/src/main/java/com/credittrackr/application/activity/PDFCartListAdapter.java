package com.credittrackr.application.activity;

/**
 * Created by ravi on 26/09/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.credittrackr.application.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PDFCartListAdapter extends RecyclerView.Adapter<PDFCartListAdapter.MyViewHolder> {
    private Context context;
    private List<PDFCardItem> cartList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cardNumberT, expirationDateT;
        public TextView interestRateT;
        public TextView maximumLimitT;
        // public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            cardNumberT = (TextView) view.findViewById(R.id.cardNumber);
            expirationDateT = (TextView) view.findViewById(R.id.expirationDate);
            interestRateT = (TextView) view.findViewById(R.id.interestRate);
            maximumLimitT = (TextView) view.findViewById(R.id.maximumLimit);
            // thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);
            viewForeground = (RelativeLayout) view.findViewById(R.id.view_foreground);
        }
    }


    public PDFCartListAdapter(Context context, List<PDFCardItem> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdfcart_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PDFCardItem item = cartList.get(position);
        holder.cardNumberT.setText("Card No: " + item.getCardNumber());
        holder.expirationDateT.setText("Payment Date: " + item.getExpirationDate());
        holder.interestRateT.setText("Interest Rate: " + item.getInterestRate());
        holder.maximumLimitT.setText("Maximum Limit: " + item.getMaximumLimit());
        // holder.viewForeground
        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the 'context' here
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("STATEMENT - #### "+ item.getCardNumber().substring(item.getCardNumber().length() - 4));
                String alert5 = "Total Credit Limit: "+ item.getMaximumLimit();
                String alert4 = "available Limit: " + item.getAvailableLimit();
                String alert1 = "Minimum Due: " + item.getMinimumLimit();
                String alert2 = "Due Date: "+ item.getExpirationDate();
                String alert3 = "interest: "+ item.getInterestRate();
                String alert6 = "You have to pay interest "+ item.getMinimumLimit() + "If you pay after due date";
                alertDialog.setMessage(alert5 +"\n"+ alert4 +"\n"+ alert1 +"\n"+ alert2 +"\n"+ alert3 +"\n"+ alert6);
                //alertDialog.setMessage("Minimum   ");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });


        //Glide.with(context)
          //      .load(item.getThumbnail())
            //    .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(PDFCardItem item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}

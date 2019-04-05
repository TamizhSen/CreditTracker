package com.credittrackr.application.activity;

/**
 * Created by ravi on 26/09/17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.DateFormat;
import java.text.ParseException;

import com.credittrackr.application.R;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {
    private Context context;
    private List<CardItem> cartList;
    private String finalDay = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cardNumberT, cardTypeT, expirationDateT;
        public TextView bankT, interestRateT, startDateT;
        public TextView endDateT, maximumLimitT;
        // public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            cardNumberT = (TextView) view.findViewById(R.id.cardNumber);
            cardTypeT = (TextView) view.findViewById(R.id.cardType);
            expirationDateT = (TextView) view.findViewById(R.id.expirationDate);
            bankT = (TextView) view.findViewById(R.id.bank);
            interestRateT = (TextView) view.findViewById(R.id.startDate);
            startDateT = (TextView) view.findViewById(R.id.startDate);
            endDateT = (TextView) view.findViewById(R.id.endDate);
            maximumLimitT = (TextView) view.findViewById(R.id.maximumLimit);
            // thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);
            viewForeground = (RelativeLayout) view.findViewById(R.id.view_foreground);
        }
    }


    public CartListAdapter(Context context, List<CardItem> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CardItem item = cartList.get(position);
        String input = item.getCardNumber();

        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ");
            }

            result.append(input.charAt(i));
        }
        holder.cardNumberT.setText("Card No: " + result);
        holder.cardTypeT.setText("Card Type: " + item.getCardType());
        holder.expirationDateT.setText("Payment Date: " + item.getExpirationDate());
        holder.bankT.setText("Bank: " + item.getBank());
        holder.interestRateT.setText("Interest Rate: " + item.getInterestRate());
        holder.startDateT.setText("Start Date: " + item.getStartDate());
        holder.endDateT.setText("End Date: " + item.getEndDate());
        holder.maximumLimitT.setText("Maximum Limit: " + item.getMaximumLimit());
        // holder.viewForeground
        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float minimumDue = 0;
                if (item.getTotalExpense() > 0) {
                    minimumDue = 10;
                }
                try {
                    String input_date=item.getExpirationDate();
                    SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
                    Date dt1=format1.parse(input_date);
                    DateFormat format2=new SimpleDateFormat("MMMM");
                    DateFormat format3=new SimpleDateFormat("dd");
                    finalDay =format2.format(dt1) + " " + format3.format(dt1);

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                float usedAmount = item.getTotalExpense() - item.getTotalPaid();
                //pass the 'context' here
                float intrestAmount = ((usedAmount * Float.parseFloat(item.getInterestRate())) / 100);
                float totalDueAmount = intrestAmount + item.getTotalExpense();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("STATEMENT - #### "+ result.toString().substring(result.length() - 4));
                String alert5 = "Total Credit Limit: $"+ item.getMaximumLimit();
                String alert4 = "Used: $" + usedAmount;
                String alert1 = "Minimum Due: $" + minimumDue;
                String alert2 = "Due Date: "+ finalDay;
                String alert3 = "interest: "+ item.getInterestRate();
                String alert6 = "You have to pay interest $"+ totalDueAmount + "If you pay after due date";
                alertDialog.setMessage(alert5 +"\n"+ alert4 +"\n"+ alert1 +"\n"+ alert2 +"\n"+ alert3 +"\n"+ alert6);
                //alertDialog.setMessage("Minimum   ");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                /* alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // DO SOMETHING HERE

                    }
                }); */

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

    public void restoreItem(CardItem item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}

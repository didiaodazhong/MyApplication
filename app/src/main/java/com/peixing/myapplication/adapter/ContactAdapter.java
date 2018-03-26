package com.peixing.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peixing.myapplication.R;
import com.peixing.myapplication.bean.ContactInfo;

import java.util.ArrayList;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ContactInfo> contactInfos;

    public ContactAdapter(Context context, ArrayList<ContactInfo> contactInfos) {
        this.context = context;
        this.contactInfos = contactInfos;
    }

    public void setContactInfos(ArrayList<ContactInfo> contactInfos) {
        this.contactInfos = contactInfos;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder itemViewHolder, int position) {

        //Here you can fill your row view
        ContactInfo contactInfo = contactInfos.get(position);
        itemViewHolder.txtTop.setText(contactInfo.getContactName() + " : " + contactInfo.getContactNumber());
        itemViewHolder.imgAvatar.setImageBitmap(contactInfo.getPhoto());
    }

    @Override
    public int getItemCount() {

        return contactInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTop;
        private ImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar);
            txtTop = (TextView) itemView.findViewById(R.id.txt_top);

        }
    }
}

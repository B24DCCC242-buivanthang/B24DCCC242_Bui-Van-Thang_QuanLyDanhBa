package com.example.th2_danh_ba;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactClickListener {
        void onContactClick(Contact contact, int position);
    }

    private final ArrayList<Contact> contactList;
    private final OnContactClickListener listener;

    public ContactAdapter(ArrayList<Contact> contactList, OnContactClickListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact, position, listener);
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout layoutAvatar;
        private final TextView tvAvatarLetter;
        private final TextView tvContactName;
        private final TextView tvContactPhone;
        private final TextView tvContactGroup;
        private final ImageView ivFavoriteStar;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutAvatar = itemView.findViewById(R.id.layoutAvatar);
            tvAvatarLetter = itemView.findViewById(R.id.tvAvatarLetter);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            tvContactGroup = itemView.findViewById(R.id.tvContactGroup);
            ivFavoriteStar = itemView.findViewById(R.id.ivFavoriteStar);
        }

        public void bind(Contact contact, int position, OnContactClickListener listener) {
            tvContactName.setText(contact.getName());
            tvContactPhone.setText(contact.getPhone());
            tvContactGroup.setText(contact.getGroup());
            tvAvatarLetter.setText(contact.getFirstLetter());

            // Thiết lập màu nền động cho avatar tròn
            GradientDrawable avatarBg = new GradientDrawable();
            avatarBg.setShape(GradientDrawable.OVAL);
            avatarBg.setColor(contact.getAvatarColor());
            layoutAvatar.setBackground(avatarBg);

            // Ẩn/hiện dấu sao yêu thích
            if (contact.isFavorite()) {
                ivFavoriteStar.setVisibility(View.VISIBLE);
            } else {
                ivFavoriteStar.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContactClick(contact, position);
                }
            });
        }
    }
}

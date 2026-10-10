package com.codefest.practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codefest.practice.database.CodefestDatabase;
import com.codefest.practice.database.Product;
import com.codefest.practice.database.ProductDao;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {

    private final Context context;
    private final List<Product> products;
    private final OnItemClickListener listener;

    public ProductsAdapter(Context context, List<Product> products, OnItemClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        Product product = products.get(position);
        holder.button.setOnClickListener(v -> listener.onItemClick(product));
        if (product.getImage() == null) {
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        } else {
            Glide.with(context).load(product.getImage()).into(holder.image);
        }
        holder.name.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView button;
        ImageView image;
        TextView name;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.button = itemView.findViewById(R.id.button);
            this.image = itemView.findViewById(R.id.image);
            this.name = itemView.findViewById(R.id.name);
        }
    }
}

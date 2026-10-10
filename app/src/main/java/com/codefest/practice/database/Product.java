package com.codefest.practice.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.*;

import java.io.File;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "name")
    public String name;

    @Ignore
    public Product(String name) {
        this.name = name;
    }

    public Product() {
    }

    public int getUid() {
        return uid;
    }

    public Bitmap getImage() {
        return convertImageToBitmap(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Bitmap convertImageToBitmap(String image) {
        if (image == null) {
            return null;
        }
        return BitmapFactory.decodeFile(new File(image).getAbsolutePath());
    }
}

package com.codefest.practice.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products WHERE uid = :uid")
    LiveData<Product> getById(int uid);

    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM products WHERE name LIKE :name LIMIT 20")
    LiveData<List<Product>> findByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("DELETE from products")
    void deleteAll();
}

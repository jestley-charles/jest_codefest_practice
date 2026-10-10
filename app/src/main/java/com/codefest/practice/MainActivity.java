package com.codefest.practice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codefest.practice.database.CodefestDatabase;
import com.codefest.practice.database.Product;
import com.codefest.practice.database.ProductDao;
import com.google.android.material.search.SearchView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProductDao productDao;

    ActivityResultLauncher<Intent> startUploadIntent;
    Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productDao = CodefestDatabase.getInstance(this).productDao();

        setupRecyclerViews();
        setupSearch(findViewById(R.id.search));

        startUploadIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data == null) return; // No image selected
                    handleSelectedImageUri(data.getData());
                });

    }

    private void performItemClick(Product product) {
        selectedProduct = product;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/jpg", "image/png", "image/webp"});
        startUploadIntent.launch(intent);
    }

    private void handleSelectedImageUri(Uri selectedImageUri) {
        if (selectedImageUri == null) return;
        new Handler().postDelayed(() -> {
            try {

                Bitmap bitmap;
                try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
                File pictures = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Codefest");
                if (!pictures.exists()) pictures.mkdirs();
                File imageFile = new File(pictures, "img_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                selectedProduct.setImage(imageFile.getAbsolutePath());
                out.close();

                // Update database
                new Thread(() -> productDao.insert(selectedProduct)).start();

            } catch (IOException e) {
                Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
            }
        }, 0);
    }

    private void setupRecyclerViews() {
        RecyclerView bestSellers = findViewById(R.id.bestSellers);
        bestSellers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        RecyclerView products = findViewById(R.id.products);

        /** How to use DAOs:
         * If we want to use DAOs (database services),
         * we need to use it inside a separate thread.
         *
         * Note: An error will occur if we didn't use it inside a separate thread:
         **/
        new Thread(() -> {
            productDao.deleteAll();
            List<Product> sampleProducts = new ArrayList<>();
            sampleProducts.add(new Product("product name test 1"));
            sampleProducts.add(new Product("product name test 2"));
            sampleProducts.add(new Product("product name test 3"));
            sampleProducts.add(new Product("product name test 4"));
            sampleProducts.add(new Product("product name test 5"));
            sampleProducts.add(new Product("product name test 6"));
            productDao.insertAll(sampleProducts);
        }).start();

        /** Except when using the LiveData,
         * We can run it in our main thread.
         *
         * Note: An error will occur if LiveData has been used in a separate thread:
         **/
        LiveData<List<Product>> productLiveData = productDao.getAll();
        productLiveData.observe(this, productsList -> {
            ProductsAdapter productsAdapter = new ProductsAdapter(this, productsList, this::performItemClick);
            bestSellers.setAdapter(productsAdapter);
            products.setAdapter(productsAdapter);
        });
    }

    private void setupSearch(SearchView search) {
        RecyclerView searchResults = findViewById(R.id.searchResults);

        // OnEditorAction Listener (when the user clicks the search button after typing)
        search.getEditText().setOnEditorActionListener((a, b, c) -> {
            String query = search.getText().toString().trim().toLowerCase();
            if (query.isEmpty()) {
                searchResults.setAdapter(new ProductsAdapter(this, new ArrayList<>(), null));
            } else {
                // Important to add "%" to search like a regex
                productDao.findByName("%" + query + "%").observe(this, products -> {
                    searchResults.setAdapter(new ProductsAdapter(this, products, this::performItemClick));
                });
            }
            return true;
        });
    }
}
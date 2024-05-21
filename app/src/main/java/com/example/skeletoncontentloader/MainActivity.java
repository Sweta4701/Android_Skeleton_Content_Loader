package com.example.skeletoncontentloader;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout dataLayout;
    LinearLayout shimmerContainer;
    GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shimmerFrameLayout = findViewById(R.id.shimmer_view);
        dataLayout = findViewById(R.id.data_view);
        shimmerContainer = findViewById(R.id.shimmer_container);
        gridView = findViewById(R.id.grid_view);

        shimmerFrameLayout.startShimmer();

        fetchImageUrls();
    }

    private void fetchImageUrls() {
        Log.i(TAG, "fetchImageUrls: ");
        ApiService apiService = Retrofit.getInstance().apiService();
        Call<ImageResponse> call = apiService.getImagesUrls();
        Log.d(TAG, "Fetching image URLs from API: " + call.request().url());

        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                Log.d(TAG, "API call response received");
                if (response.isSuccessful() && response.body() != null) {
                    ImageResponse imagesResponse = response.body();
                    List<String> imageUrls = imagesResponse.getImages();
                    Log.d(TAG, "Image URLs fetched:");
                    for (String imageUrl : imageUrls) {
                        Log.d(TAG, imageUrl);
                    }
                    setupShimmerPlaceholders(imageUrls.size());
                    loadImages(imageUrls);
                } else {
                    Log.e(TAG, "Failed to fetch image URLs: " + response.message() + " (Code: " + response.code() + ")");
                    showError();
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                showError();
            }
        });
    }

    private void setupShimmerPlaceholders(int count) {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < count; i++) {
            View placeholder = inflater.inflate(R.layout.item_placeholder, shimmerContainer, false);
            shimmerContainer.addView(placeholder);
        }
    }

    private void loadImages(List<String> imageUrls) {
        new Handler().postDelayed(() -> {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);

            dataLayout.setVisibility(View.VISIBLE);

            gridView.setAdapter(new GalleryAdapter(MainActivity.this, imageUrls));
        }, 3000); // 3 seconds delay to simulate loading
    }

    private void showError() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this, "Failed to load images", Toast.LENGTH_SHORT).show();
    }
}
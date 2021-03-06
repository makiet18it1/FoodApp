package com.example.learnapi;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnapi.Model.PostModel;
import com.example.learnapi.Model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Home extends Fragment {


    private ArrayList<Integer> pkPost = new ArrayList<>();
    private ArrayList<String> namePost = new ArrayList<>();
    private ArrayList<String> authorPost = new ArrayList<>();
    private ArrayList<String> authorText = new ArrayList<>();
    private ArrayList<Integer> authorID = new ArrayList<>();
    private ArrayList<String> imgPost = new ArrayList<>();
    private RecyclerView recyclerView;
    ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.post_list, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_post_list);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        if (InternetUtil.isInternetOnline(getActivity())) {
            ClearList();
            showAllPosts();
        }


        getActivity().setTitle("Cook-Store");

        return rootView;


    }

    private void showAllPosts() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PostApi.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostApi postApi = retrofit.create(PostApi.class);
        Call<List<PostModel>> call = postApi.getListPost();

        call.enqueue(new Callback<List<PostModel>>() {
            @Override
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        List<PostModel> postList = response.body();

                        for (PostModel h : postList) {


                            Integer au_id = h.getAuthor();
                            authorID.add(au_id);

                            Integer cat_id = h.getId();
                            pkPost.add(cat_id);

                            String cat_name = h.getCaption();
                            namePost.add(cat_name);

                            String cat_author = h.getUsername();
                            authorPost.add(cat_author);

                            String cat_img = h.getImage();
                            imgPost.add(cat_img);


                        }

                        initRecyclerView();

                    }

                } else {
                    Log.d("fail", "fail");
                }
            }

            @Override
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("fail", t.getMessage() == null ? "" : t.getMessage());
            }

        });

    }


    private void initRecyclerView() {
        Log.d("Home", "initRecyclerView: init recyclerview.");
        RecyclerHomeList adapter = new RecyclerHomeList(pkPost, namePost, authorPost, authorID, imgPost, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    public void ClearList() {
        pkPost.clear();
        namePost.clear();

        RecyclerHomeList adapter = new RecyclerHomeList(pkPost, namePost, authorPost, authorID, imgPost, getActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }


}
package com.alpharelevant.idarenow;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.alpharelevant.idarenow.data.Adapters.SearchViewAdapter;
import com.alpharelevant.idarenow.data.models.CustomModel.UserResult;
import com.alpharelevant.idarenow.data.models.User_Accounts;
import com.alpharelevant.idarenow.data.remote.APIServices;
import com.alpharelevant.idarenow.data.remote.RetrofitClient;
import com.alpharelevant.idarenow.data.utils.Session;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainNavSearchFragment extends Fragment {

    Session session;

    FloatingActionButton floatingActionButton;
    public MainNavSearchFragment() {

        // Required empty public constructor

    }


    ListView lv;
    SearchView sv;
    ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session = new Session(getContext());
        // Inflate the layout for this fragment
        try {


            View v = inflater.inflate(R.layout.fragment_main_nav_search, container, false);
            lv = (ListView) v.findViewById(R.id.listviewSearch);
            sv = (SearchView) v.findViewById(R.id.searchViewUser);


            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (s.length() >= 3) {
                        Log.d("search", "onQueryTextChange: " + s);
                        RetrofitClient.apiServices().getSearchResults(s, String.valueOf(session.getUserID())).enqueue(new Callback<List<UserResult>>() {
                            @Override
                            public void onResponse(Call<List<UserResult>> call, Response<List<UserResult>> response) {

                                List<UserResult> ua = response.body();
                                SearchViewAdapter sva = new SearchViewAdapter(getActivity(), ua);
                                lv.setAdapter(sva);

                            }

                            @Override
                            public void onFailure(Call<List<UserResult>> call, Throwable t) {

                            }
                        });
                    }
                    return false;
                }
            });

            return v;
        }
        catch (Exception e)
        {
            return inflater.inflate(R.layout.fragment_main_nav_search, container, false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }


    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}

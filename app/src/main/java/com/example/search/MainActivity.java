package com.example.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.search.Retrofit.ISuggestAPI;
import com.example.search.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    MaterialSearchBar materialSearchBar;
    RadioButton rdi_youtube, rdi_google;
    ISuggestAPI myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<String> suggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        myAPI = RetrofitClient.getInstance().create(ISuggestAPI.class);
        rdi_youtube = (RadioButton)findViewById(R.id.rdi_youtube);
        rdi_google = (RadioButton)findViewById(R.id.rdi_google);
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.search_bar);

        //Searchbar init
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Toast.makeText(MainActivity.this, text.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Set suggest list load from api
                getSuggestions(charSequence.toString(),
                        "chrome",
                        "en",
                        rdi_youtube.isChecked()?"yt":"");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void save(){
        System.out.println("Agrege esto");
        System.out.println("Update this");
    }

    public void algo(){
        
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void getSuggestions(String query, String client, String language, String restrict) {
        if (!TextUtils.isEmpty(restrict))//youtube
        {
            compositeDisposable.add(
                    myAPI.getSuggestFromYoutube(query,client,language,restrict)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            //here we retrieve JSON Array
                            if (suggestions.size()>0)suggestions.clear();
                            JSONArray mainJson = new JSONArray(s);
                            //Here we will use GSON convert Json Array to objet
                            suggestions = new Gson().fromJson(mainJson.getString(1),
                                    new TypeToken<List<String>>(){}.getType());
                            //update
                            materialSearchBar.updateLastSuggestions(suggestions);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }
        else
        {
            compositeDisposable.add(
                    myAPI.getSuggestFromGoogle(query,client,language)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            //here we retrieve JSON Array
                            if (suggestions.size()>0)suggestions.clear();
                            JSONArray mainJson = new JSONArray(s);
                            //Here we will use GSON convert Json Array to objet
                            suggestions = new Gson().fromJson(mainJson.getString(1),
                                    new TypeToken<List<String>>(){}.getType());
                            //update
                            materialSearchBar.updateLastSuggestions(suggestions);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }
    }
}

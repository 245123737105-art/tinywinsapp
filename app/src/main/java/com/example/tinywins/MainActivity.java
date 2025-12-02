package com.example.tinywins;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private Button buttonAdd, buttonClear;
    private TextView scoreNumber, doneCount, totalCount;
    private RecyclerView recycler;

    private WinsAdapter adapter;
    private List<WinItem> items = new ArrayList<>();

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    private static final String PREFS = "tiny_prefs";
    private static final String KEY_ITEMS = "items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        inputText = findViewById(R.id.inputText);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonClear = findViewById(R.id.buttonClear);
        scoreNumber = findViewById(R.id.scoreNumber);
        doneCount = findViewById(R.id.doneCount);
        totalCount = findViewById(R.id.totalCount);
        recycler = findViewById(R.id.recycler);

        adapter = new WinsAdapter(items, new WinsAdapter.Listener() {
            @Override
            public void onToggle(WinItem item) {
                save();
                updateCounts();
            }

            @Override
            public void onDelete(WinItem item) {
                deleteItem(item);
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        load();
        updateCounts();

        buttonAdd.setOnClickListener(v -> addItem());

        inputText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                addItem();
                return true;
            }
            return false;
        });

        buttonClear.setOnClickListener(v -> {
            items.clear();
            save();
            adapter.notifyDataSetChanged();
            updateCounts();
        });
    }

    private void addItem() {
        String text = inputText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        WinItem newItem = new WinItem(UUID.randomUUID().toString(), text, true);
        items.add(0, newItem);

        adapter.notifyDataSetChanged();
        save();
        updateCounts();

        inputText.setText("");
    }

    private void deleteItem(WinItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Delete this win?")
                .setPositiveButton("Delete", (d, i) -> {
                    items.remove(item);
                    adapter.notifyDataSetChanged();
                    save();
                    updateCounts();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateCounts() {
        int done = 0;

        for (WinItem w : items)
            if (w.isDone()) done++;

        scoreNumber.setText(String.valueOf(done));
        doneCount.setText(done + " done");
        totalCount.setText(items.size() + " total");
    }

    private void save() {
        prefs.edit().putString(KEY_ITEMS, gson.toJson(items)).apply();
    }

    private void load() {
        String json = prefs.getString(KEY_ITEMS, null);
        if (json != null) {
            Type type = new TypeToken<List<WinItem>>(){}.getType();
            items = gson.fromJson(json, type);
        }
        adapter.setList(items);
    }
}

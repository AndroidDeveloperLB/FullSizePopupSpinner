package com.lb.full_size_popup_spinner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.lb.full_size_popup_spinner.lib.FullSizePopupSpinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int[] ITEMS_TEXTS_RES_IDS = {R.string.item_1, R.string.item_2, R.string.item_3};
    private static final int[] ITEMS_ICONS_RES_IDS = {android.R.drawable.sym_def_app_icon, android.R.drawable.sym_def_app_icon, android.R.drawable.sym_def_app_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FullSizePopupSpinner fullSizePopupSpinner = (FullSizePopupSpinner) findViewById(R.id.spinner);
        fullSizePopupSpinner.setItems(ITEMS_TEXTS_RES_IDS, ITEMS_ICONS_RES_IDS);
        fullSizePopupSpinner.setSelectedItemPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        String url = null;
        switch (item.getItemId()) {
            case R.id.menuItem_all_my_apps:
                url = "https://play.google.com/store/apps/developer?id=AndroidDeveloperLB";
                break;
            case R.id.menuItem_all_my_repositories:
                url = "https://github.com/AndroidDeveloperLB";
                break;
            case R.id.menuItem_current_repository_website:
                url = "https://github.com/AndroidDeveloperLB/FullSizePopupSpinner";
                break;
        }
        if (url == null)
            return true;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(intent);
        return true;
    }
}

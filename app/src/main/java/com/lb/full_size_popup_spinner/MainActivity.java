package com.lb.full_size_popup_spinner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lb.full_size_popup_spinner.lib.FullSizePopupSpinner;

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
}

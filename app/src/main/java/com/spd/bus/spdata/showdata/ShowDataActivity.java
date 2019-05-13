package com.spd.bus.spdata.showdata;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.spd.bus.R;
import com.spd.bus.spdata.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class ShowDataActivity extends MVPBaseActivity<ShowDataContract.View, ShowDataPresenter>
        implements ShowDataContract.View ,AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
    }

    private void initRV() {
//        RecyclerView recyclerView = findViewById(R.id.rv_content);
//        mAdapter = new RVAdapter(R.layout.item_info, mList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

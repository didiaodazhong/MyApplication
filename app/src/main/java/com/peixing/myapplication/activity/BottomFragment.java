package com.peixing.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.peixing.myapplication.R;
import com.peixing.myapplication.adapter.PathAdapter;

/**
 * Created by xuxiaohe on 2018/4/2.
 */

@SuppressLint("ValidFragment")
public class BottomFragment extends BottomSheetDialogFragment {
    private Context context;
    private RecyclerView recyclerProduct;
    private PathAdapter pathAdapter;

    public BottomFragment(Context context, PathAdapter pathAdapter) {
        this.context = context;
        this.pathAdapter = pathAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.MMTheme_DataSheet);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_product_choose_dialog);
        recyclerProduct = (RecyclerView) dialog.findViewById(R.id.recycler_product);

        recyclerProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerProduct.setHasFixedSize(true);
        recyclerProduct.setAdapter(pathAdapter);
        return dialog;
    }
}

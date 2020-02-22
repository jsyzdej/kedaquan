package com.yangs.kedaquan.find;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yangs.kedaquan.R;

import java.util.List;

/**
 * Created by yangs on 2017/7/30.
 */

public class FindMainAdapter extends RecyclerView.Adapter<FindMainAdapter.ViewHolder> {
    private List<String> list;

    public FindMainAdapter(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item.setText(list.get(position));
        switch (position) {
            case 0:
                holder.iv.setImageResource(R.drawable.cjjd);
                break;
            case 1:
                holder.iv.setImageResource(R.drawable.kdxl);
                break;
            case 2:
                holder.iv.setImageResource(R.drawable.tycj);
                break;
            case 3:
                holder.iv.setImageResource(R.drawable.tysk);
                break;
            case 4:
                holder.iv.setImageResource(R.drawable.zccq);
                break;
            case 5:
                holder.iv.setImageResource(R.drawable.syxt);
                break;
            case 6:
                holder.iv.setImageResource(R.drawable.kjs);
                break;
            case 7:
                holder.iv.setImageResource(R.drawable.yjpj);
                break;
            case 8:
                holder.iv.setImageResource(R.drawable.tsjy);
                break;
            case 9:
                holder.iv.setImageResource(R.drawable.qzjw);
                break;
            case 10:
                holder.iv.setImageResource(R.drawable.alxt);
                break;
            case 11:
                holder.iv.setImageResource(R.drawable.bsdt);
                break;
            case 12:
                holder.iv.setImageResource(R.drawable.bysj);
                break;
            case 13:
                holder.iv.setImageResource(R.drawable.xslw);
                break;
            case 14:
                holder.iv.setImageResource(R.drawable.dhlb);
                break;
            case 15:case 16:
                holder.iv.setImageResource(R.drawable.slj);
                break;
            case 17:
                holder.iv.setImageResource(R.drawable.ssgj);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item;
        private ImageView iv;

        private ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.find_item_tv);
            iv = view.findViewById(R.id.find_item_iv);
        }
    }
}
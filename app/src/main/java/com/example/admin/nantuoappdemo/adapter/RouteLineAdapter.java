package com.example.admin.nantuoappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.example.admin.nantuoappdemo.R;

import java.util.List;

/**
 * 路线选择适配器
 */
public class RouteLineAdapter extends BaseAdapter {

    private List<? extends RouteLine> routeLines;
    private LayoutInflater layoutInflater;
    private Type mtype;

    public RouteLineAdapter(Context context, List<? extends RouteLine> routeLines, Type type) {
        this.routeLines = routeLines;
        layoutInflater = LayoutInflater.from(context);
        mtype = type;
    }

    @Override
    public int getCount() {
        return routeLines.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NodeViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_transit_item, null);
            holder = new NodeViewHolder();
            holder.name = convertView.findViewById(R.id.transitName);
            holder.lightNum = convertView.findViewById(R.id.lightNum);
            holder.dis = convertView.findViewById(R.id.dis);
            convertView.setTag(holder);
        } else {
            holder = (NodeViewHolder) convertView.getTag();
        }

        switch (mtype) {
            case TRANSIT_ROUTE:
            case WALKING_ROUTE:
            case BIKING_ROUTE:
                holder.name.setText(R.string.lx + (position + 1));
                int time = routeLines.get(position).getDuration();
                if (time / 3600 == 0) {
                    holder.lightNum.setText(R.string.dy + time / 60 + R.string.fen);
                } else {
                    holder.lightNum.setText(R.string.dy + time / 3600 + R.string.hour + (time % 3600) / 60 + R.string.fen);
                }
                holder.dis.setText(R.string.jl + routeLines.get(position).getDistance() + "米");
                break;

            case DRIVING_ROUTE:
                DrivingRouteLine drivingRouteLine = (DrivingRouteLine) routeLines.get(position);
                holder.name.setText(R.string.xl + (position + 1));
                int dura = drivingRouteLine.getDuration();
                String durastr = "";
                if (dura / 3600 == 0) {
                    durastr = "大约需要：" + dura / 60 + R.string.fen;
                } else {
                    durastr = "大约需要：" + dura / 3600 + R.string.hour + (dura % 3600) / 60 + R.string.fen;
                }
                holder.lightNum.setText(R.string.hlh + drivingRouteLine.getLightNum());
                holder.dis.setText(R.string.yd + drivingRouteLine.getCongestionDistance() + R.string.mi + "\r\n" + durastr + "\r\n距离大约是：" + drivingRouteLine.getDistance() + R.string.mi);
                break;
            case MASS_TRANSIT_ROUTE:
                MassTransitRouteLine massTransitRouteLine = (MassTransitRouteLine) routeLines.get(position);
                holder.name.setText(R.string.xl + (position + 1));
                holder.lightNum.setText(R.string.yj + massTransitRouteLine.getArriveTime());
                holder.dis.setText(R.string.price + massTransitRouteLine.getPrice() + "元");
                break;

            default:
                break;
        }
        return convertView;
    }

    private class NodeViewHolder {
        private TextView name;
        private TextView lightNum;
        private TextView dis;
    }

    public enum Type {
        MASS_TRANSIT_ROUTE, // 综合交通
        TRANSIT_ROUTE, // 公交
        DRIVING_ROUTE, // 驾车
        WALKING_ROUTE, // 步行
        BIKING_ROUTE // 骑行
    }
}

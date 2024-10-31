package com.example.bookingflight.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.activity.DetailActivity;
import com.example.bookingflight.model.Result;

import java.io.Serializable;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> implements Parcelable {
    private List<Result> resultList;
    private Context mContext ;
    private Result selectedFlight;
    private ResultAdapter resultAdapter;

    protected ResultAdapter(Parcel in) {
        resultList = in.createTypedArrayList(Result.CREATOR);
        selectedFlight = in.readParcelable(Result.class.getClassLoader());
        resultAdapter = in.readParcelable(ResultAdapter.class.getClassLoader());
    }

    public static final Creator<ResultAdapter> CREATOR = new Creator<ResultAdapter>() {
        @Override
        public ResultAdapter createFromParcel(Parcel in) {
            return new ResultAdapter(in);
        }

        @Override
        public ResultAdapter[] newArray(int size) {
            return new ResultAdapter[size];
        }
    };

    public Result getSelectedFlight() {
        return selectedFlight;
    }
    public void setSelectedFlight(Result selectedFlight) {
        this.selectedFlight = selectedFlight;
        notifyDataSetChanged();
    }
    public void setContext(Context context) {
        this.mContext = context;
    }
    public ResultAdapter(Context context ,List<Result> resultList) {
        this.mContext = context ;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        final  Result result = resultList.get(position);
        holder.bind(result);
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickGoToDetail(result);
            }
        });
    }

    private void onClickGoToDetail(Result result) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("object_flight", result);
        mContext.startActivity(intent);
    }
    public void release(){
        mContext = null ;
    }

    @Override
    public int getItemCount() {
        return resultList != null ? resultList.size() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeTypedList(resultList);
        dest.writeParcelable(selectedFlight, flags);
        dest.writeParcelable(resultAdapter, flags);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        private TextView placeNameTextView, placeNameTextView2;
        private RelativeLayout layoutItem ;
        private TextView descriptionTextView,descriptionTextView2;
        private TextView giaveTextView, maCB , gioBayTextView;
        private TextView ngayDi, ngayDen, txtSoLuongCon;
        private ImageView imageStatus;


        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            placeNameTextView = itemView.findViewById(R.id.placeNameTextView);
            placeNameTextView2 = itemView.findViewById(R.id.placeNameTextView2);
            maCB = itemView.findViewById(R.id.maCB);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            descriptionTextView2 = itemView.findViewById(R.id.descriptionTextView2);
            ngayDi = itemView.findViewById(R.id.ngayDi);
            ngayDen = itemView.findViewById(R.id.ngayDen);
            gioBayTextView = itemView.findViewById(R.id.gioBayTextView);
            txtSoLuongCon = itemView.findViewById(R.id.txtSoLuongCon);

        }

        public void bind(Result result) {
            maCB.setText("Mã Chuyến Bay: " + result.getMaCB());
            gioBayTextView.setText("Giờ bay: " + result.getGioBay());
            ngayDi.setText(result.getNgayDi());
            ngayDen.setText(result.getNgayDen());
            placeNameTextView.setText(result.getDiaDiemDi());
            placeNameTextView2.setText(result.getDiaDiemDen());
            CardView cardView = itemView.findViewById(R.id.cvAirlines);

            // Tính tổng số lượng vé
            String soLuongConString = result.getSoLuongCon(); // Giả định rằng bạn có phương thức này
            if (soLuongConString != null && !soLuongConString.isEmpty()) {
                String[] soLuongArray = soLuongConString.split(",\\s*");
                int totalSoLuongCon = 0;

                for (String s : soLuongArray) {
                    totalSoLuongCon += Integer.parseInt(s.trim());
                }
                if(totalSoLuongCon > 0){
                    // Hiển thị tổng số lượng vé
                    txtSoLuongCon.setText(String.valueOf(totalSoLuongCon)); // Giả định bạn có một TextView để hiển thị
                    Log.d("SoLuongCon", "Tổng số lượng còn: " + totalSoLuongCon);
                    cardView.setVisibility(View.VISIBLE);
                }else{
                    cardView.setVisibility(View.GONE);
                }

            } else {
                txtSoLuongCon.setText("Không có thông tin số lượng");
            }
        }
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
        notifyDataSetChanged();
    }
}
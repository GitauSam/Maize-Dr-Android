package org.tensorflow.demo.userinterface.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.tensorflow.demo.R;
import org.tensorflow.demo.models.UserNotice;

import java.util.List;

public class UserNoticeAdapter extends RecyclerView.Adapter<UserNoticeAdapter.MyViewHolder>{

    private Context context;
    public static List<UserNotice> userNoticeList;

    public UserNoticeAdapter(Context context, List<UserNotice> userNoticeList) {
        this.context = context;
        this.userNoticeList = userNoticeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.user_notice_layout, parent, false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView firstName, secondName, message;
        ImageView noticeImage;
        private MyViewHolder(View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.text_view_user_notice_first_name);
            secondName = itemView.findViewById(R.id.text_view_user_notice_second_name);
            message = itemView.findViewById(R.id.text_view_user_notice_message);
            noticeImage = itemView.findViewById(R.id.image_view_user_notice_image);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserNotice userNotice = userNoticeList.get(position);
        holder.firstName.setText(userNotice.getFirstName());
        holder.secondName.setText(userNotice.getLastName());
        holder.message.setText(userNotice.getNotice());

        //holder.noticeImage.setImageDrawable(context.getResources().getDrawable(userNotice.getImage()));
        Glide.with(context).load(userNotice.getImageURL()).into(holder.noticeImage);
    }

    @Override
    public int getItemCount() {
        return userNoticeList.size();
    }
}

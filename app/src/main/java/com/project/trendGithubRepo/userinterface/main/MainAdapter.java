package com.project.trendGithubRepo.userinterface.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.project.trendGithubRepo.R;
import com.project.trendGithubRepo.data.model.ItemModel;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static List<ItemModel> mData = new ArrayList<>();

    public MainAdapter(){}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item,parent,false);

        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder mViewHolder, int position) {
        ItemModel data = mData.get(position);
        String mCount = (data.getStar_count() >= 1000 ? data.getStar_count()/1000+"k" : String.valueOf(data.getStar_count()));
        String mForks = (data.getForks_count() >= 1000 ? data.getForks_count()/1000+"k" : String.valueOf(data.getForks_count()));

        if(data.getOwners() != null) mViewHolder.setUsername(data.getOwners().getName());
        if(data.getName()!= null) mViewHolder.setTitle(data.getName());
        if(data.getDescription()!= null) mViewHolder.setDescription(data.getDescription());
        if (data.getOwners() != null) mViewHolder.setAvatar(data.getOwners().getAvatar_url());
        if (data.getLanguage() != null) mViewHolder.setLanguage(data.getLanguage());
        if (data.getLicenses() != null) mViewHolder.setLicense(data.getLicenses().getName());
        mViewHolder.setStarCount(mCount);
        mViewHolder.setForksCount(mForks);
        mViewHolder.setSubItemVisiblity(data.isSubItemVisible());

        mViewHolder.itemView.setOnClickListener(v -> {
            for (int x = 0; x < mData.size(); x++) {
                if (x == position) {
                    boolean expanded = data.isSubItemVisible();
                    data.setSubItemVisibility(!expanded);
                    continue;
                }
                mData.get(x).setSubItemVisibility(false);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return (mData == null ? 0 : mData.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mUsername;
        private final TextView mTitle;
        private final TextView mDescription;
        private final TextView mStar;
        private final TextView mLang;
        private final ImageView mAvatatImg;
        private final TextView mForkValue;
        private final TextView mLicenseView;

        private final LinearLayout mSubItem;

        public ViewHolder(@NonNull final View mView) {
            super(mView);

            mView.setOnClickListener(view -> MainAdapter.copyToClip(view.getContext(),getAdapterPosition()));

            mAvatatImg = mView.findViewById(R.id.avatarImg);
            mUsername = mView.findViewById(R.id.username);
            mTitle = mView.findViewById(R.id.title);

            mDescription = mView.findViewById(R.id.detail);
            mLang = mView.findViewById(R.id.language);
            mStar = mView.findViewById(R.id.stars_value);
            mForkValue = mView.findViewById(R.id.fork_value);

            mLicenseView = mView.findViewById(R.id.licenseView);

            mSubItem = mView.findViewById(R.id.sub_item);
        }

        public void setUsername(String username) {
            mUsername.setText(username);
        }

        public void setTitle(String title) {
            mTitle.setText(title);
        }

        public void setDescription(String description) {
            mDescription.setText(description);
        }

        public void setStarCount(String count) {
            mStar.setText(count);
        }

        public void setLanguage(String language) {
            mLang.setText(language);
        }

        public void setAvatar(String avatar) {
            Picasso.get().load(avatar).into(mAvatatImg);
        }

        public void setForksCount(String forkValue) {
            mForkValue.setText(forkValue);
        }

        public void setLicense(String license) {
            mLicenseView.setText(license);
        }

        public void setSubItemVisiblity(boolean isVisible) {
            mSubItem.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    public static void copyToClip(Context context, int position) {
        ClipboardManager clipboard = (ClipboardManager)  context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Git URL", mData.get(position).getClone_url());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }

        Toast.makeText(context,"Link Copied to the Clipboard",Toast.LENGTH_SHORT).show();
    }

    public void clearData() {
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(List<ItemModel> mData){
        this.mData.addAll(mData);
        notifyDataSetChanged();
    }

}

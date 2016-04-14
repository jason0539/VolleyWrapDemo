package com.lzh.volleywrapdemo.ui;

import com.lzh.volleywrap.baseframe.utils.MLog;
import com.lzh.volleywrap.baseframe.utils.ScreenUtils;
import com.lzh.volleywrap.middleframe.ImageLoaderWrapper;
import com.lzh.volleywrapdemo.R;
import com.lzh.volleywrapdemo.utils.DemoConstant;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * liuzhenhui 16/2/23.下午8:34
 */
public class ImageDemoActivity extends Activity{
    private static final String TAG = ImageDemoActivity.class.getSimpleName();

    private int mCellImageWidth;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedemo);
        mCellImageWidth = ScreenUtils.getScreenWidth(this) / 8;
        GridViewAdpter adpter = new GridViewAdpter(this, DemoConstant.PHOTOS);
        mGridView = ((GridView) findViewById(R.id.gv_imagelist));
        mGridView.setAdapter(adpter);
    }

    class GridViewAdpter extends BaseAdapter {
        LayoutInflater mInflater;
        String[] mUrlArray;
        int mWidth;

        public GridViewAdpter(Context context, String[] urlArray) {
            mInflater = LayoutInflater.from(context);
            mUrlArray = urlArray;
            mWidth = ScreenUtils.getScreenWidth(context) / 3;
        }

        @Override
        public int getCount() {
            return mUrlArray.length;
        }

        @Override
        public Object getItem(int position) {
            return mUrlArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemViewTag viewTag;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gridview_item, null);
                viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.iv_grid_image));
                AbsListView.LayoutParams params = (AbsListView.LayoutParams) convertView.getLayoutParams();
                if (params == null) {
                    params = new AbsListView.LayoutParams(mWidth, mWidth);
                }
                convertView.setLayoutParams(params);
                convertView.setTag(viewTag);
            } else {
                viewTag = (ItemViewTag) convertView.getTag();
                viewTag.mImageView.setImageResource(R.mipmap.ic_launcher);
            }
            MLog.d("width = " + mCellImageWidth + ",height = " + mCellImageWidth);
            ImageLoaderWrapper.getInstance()
                    .displayImage(mUrlArray[position], viewTag.mImageView, mCellImageWidth, mCellImageWidth);
            return convertView;
        }

        class ItemViewTag {
            protected ImageView mImageView;

            public ItemViewTag(ImageView imageView) {
                this.mImageView = imageView;
            }
        }
    }
}

package com.softstao.softstaolibrary.library.widget.cyckeView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.softstao.softstaolibrary.R;

import java.util.ArrayList;

/**
 * 广告图片自动轮播控件</br>
 *
 * <pre>
 *   集合ViewPager和指示器的一个轮播控件，主要用于一般常见的广告图片轮播，具有自动轮播和手动轮播功能
 *   使用：只需在xml文件中使用{@code <com.minking.imagecycleview.ImageCycleView/>} ，
 *   然后在页面中调用  {@link #setImageResources(ArrayList, ImageCycleViewListener) }即可!
 *
 *   另外提供{@link #startImageCycle() } \ {@link #pushImageCycle() }两种方法，用于在Activity不可见之时节省资源；
 *   因为自动轮播需要进行控制，有利于内存管理
 * </pre>
 *
 */
public class ImageCycleView extends LinearLayout {


    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 图片轮播视图
     */
    private ViewPager mBannerPager = null;

    /**
     * 滚动图片视图适配器
     */
    private PagerAdapter mAdvAdapter;

    /**
     * 图片轮播指示器控件
     */
    private ViewGroup mGroup;

    /**
     * 图片轮播指示器-个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示器-视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */
    private int mImageIndex = 0;

    /**
     * 手机密度
     */
    private float mScale;


    private ArrayList<BasePic> data = new ArrayList<>();
    /**
     * @param context
     */
    public ImageCycleView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ImageCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        LayoutInflater.from(context).inflate(R.layout.view_banner_content, this);
        mBannerPager = (ViewPager) findViewById(R.id.pager_banner);
        mBannerPager.setOnPageChangeListener(new GuidePageChangeListener());
        mBannerPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageTimerTask();
                        break;
                    default:
                        // 停止图片滚动
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视图
        mGroup = (ViewGroup) findViewById(R.id.viewGroup);
    }

    /**
     * 装填图片数据

     * @param imageCycleViewListener
     */
    public void setImageResources(ArrayList<BasePic> infoList, ImageCycleViewListener imageCycleViewListener) {
        data=infoList;
        setData();
        mAdvAdapter = new ImageCycleAdapter(mContext, imageCycleViewListener);
        mBannerPager.setAdapter(mAdvAdapter);
        startImageTimerTask();
    }

    public void setData(){
        // 清除所有子视图
        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = data.size();
        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(mContext);
            int imageParams = (int) (mScale * 10 + 0.5f);// XP与DP转换，适应不同分辨率
            int imagePadding = (int) (mScale * 5 + 0.5f);
            LayoutParams layout = new LayoutParams(imageParams,imageParams);

            layout.setMargins(3, 0, 3, 0);
            mImageView.setLayoutParams(layout);
            mImageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                mImageViews[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
            mGroup.addView(mImageViews[i]);
        }
    }

    /**
     * 开始轮播(手动控制自动轮播与否，便于资源控制)
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播——用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 开始图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片每3秒滚动一次
//        mHandler.postDelayed(mImageTimerTask, 3000);
        mHandler.sendEmptyMessageDelayed(100,3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        mHandler.removeMessages(100);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){
                if (mImageViews != null) {
                    // 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
                    mImageIndex++;
                    mBannerPager.setCurrentItem(mImageIndex);
//                    mBannerPager.setPageTransformer(true,new ZoomOutPageTransformer());
                }
            }
        }
    };


    /**
     * 轮播图片状态监听器
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE)
                startImageTimerTask(); // 开始下次计时
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            startImageTimerTask();
        }

        @Override
        public void onPageSelected(int index) {
            index = index % mImageViews.length;
            mImageViews[index].setBackgroundResource(R.mipmap.page_indicator_focused);
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                }
            }
            startImageTimerTask();
        }

    }

    private class ImageCycleAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private ArrayList<ImageView> mImageViewCacheList;

        /**
         * 图片资源列表
         */
//        private ArrayList<BasePic> mAdList = new ArrayList<BasePic>();

        /**
         * 广告图片点击监听器
         */
        private ImageCycleViewListener mImageCycleViewListener;

        private Context mContext;

        public ImageCycleAdapter(Context context, ImageCycleViewListener imageCycleViewListener) {
            mContext = context;
            mImageCycleViewListener = imageCycleViewListener;
            mImageViewCacheList = new ArrayList();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE/2;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container,int position) {
            position=position % data.size();
            String imageUrl = data.get(position).getPic();
            ImageView imageView ;
            if (mImageViewCacheList.isEmpty()) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            // 设置图片点击监听
            int finalPosition = position;
            imageView.setOnClickListener(v -> {
                mImageCycleViewListener.onImageClick(data.get(finalPosition), finalPosition, v);
            });
            container.addView(imageView);
            mImageCycleViewListener.displayImage(imageUrl, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);
            mImageViewCacheList.add(view);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            setData();
        }
    }

    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    public interface ImageCycleViewListener {

        /**
         * 加载图片资源
         *
         * @param imageURL
         * @param imageView
         */
        void displayImage(String imageURL, ImageView imageView);

        /**
         * 单击图片事件
         *

         * @param imageView
         */
        void onImageClick(Object info, int position, View imageView);
    }

    public void setGroupGravity(int gravity){
        ((LinearLayout)mGroup).setGravity(gravity);
    }

    public PagerAdapter getmAdvAdapter() {
        return mAdvAdapter;
    }

    public void setmAdvAdapter(PagerAdapter mAdvAdapter) {
        this.mAdvAdapter = mAdvAdapter;
    }
}

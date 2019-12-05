package com.andrew.bcamerchantservice.ui.mainforum.story;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryFragment extends Fragment implements IStoryView, StoriesProgressView.StoriesListener, View.OnClickListener {

    public static final String GET_MID = "get_mid";
    public static final String GET_CURRENT_POSITION = "get_current_position";
    public static final String GET_MAX_STORY = "get_max_story";


    private View v;
    private ImageView image_story;
    private Context mContext;
    private StoriesProgressView storiesProgressView;

    private IStoryPresenter presenter;

    private List<Merchant.MerchantStory> merchantStoryList;

    private long pressTime;
    private int story_counter, max_story;

    //    current_position,
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_story, container, false);
        initVar();
        return v;
    }

    @Override
    public void onResume() {
//        initVar();
//        storiesProgressView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            long limit = 500L;
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
//                    mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    private void initVar() {
        mContext = v.getContext();

        story_counter = 0;

        View reverse = v.findViewById(R.id.reverse_story);
        View skip = v.findViewById(R.id.skip_story);

        image_story = v.findViewById(R.id.image_story);
        storiesProgressView = v.findViewById(R.id.progress_story);

        merchantStoryList = new ArrayList<>();

        presenter = new StoryPresenter(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString(GET_MID) != null) {
                String MID = bundle.getString(GET_MID);
//                current_position = bundle.getInt(GET_CURRENT_POSITION);
                max_story = bundle.getInt(GET_MAX_STORY);
                presenter.onClickStory(MID);
            }
        }

        reverse.setOnClickListener(this);
        skip.setOnClickListener(this);
        skip.setOnTouchListener(onTouchListener);
        reverse.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onLoadStory(List<Merchant.MerchantStory> merchantStoryList) {
        this.merchantStoryList = merchantStoryList;
        storiesProgressView.setStoriesCount(merchantStoryList.size());
        storiesProgressView.setStoryDuration(Constant.DURATION_STORY);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.setSaveFromParentEnabled(true);
        Picasso.get()
                .load(merchantStoryList.get(0).getStory_picture())
                .into(image_story);
        storiesProgressView.startStories(0);
    }

    @Override
    public void onNext() {
        if (story_counter < merchantStoryList.size() - 1)
            Picasso.get()
                    .load(merchantStoryList.get(++story_counter).getStory_picture())
                    .into(image_story);
        else {
            storiesProgressView.startStories(story_counter);
            storiesProgressView.pause();
            if (MainForum.story_pager.getCurrentItem() < max_story - 1) {
                MainForum.story_pager.setCurrentItem(MainForum.story_pager.getCurrentItem() + 1);
            } else if (MainForum.story_pager.getCurrentItem() == max_story - 1) {
                MainForum.story_pager.setVisibility(View.GONE);
            }
            onDestroy();
        }
    }

    @Override
    public void onPrev() {
        if (story_counter > 0)
            Picasso.get()
                    .load(merchantStoryList.get(--story_counter).getStory_picture())
                    .into(image_story);
        else {
            storiesProgressView.startStories(story_counter);
            storiesProgressView.pause();
            onDestroy();
//            story_counter = 0;
            if (MainForum.story_pager.getCurrentItem() > 0)
                MainForum.story_pager.setCurrentItem(MainForum.story_pager.getCurrentItem() - 1);
            else {
                MainForum.story_pager.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onComplete() {
        if (story_counter == merchantStoryList.size() - 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    storiesProgressView.startStories(story_counter);
                    storiesProgressView.pause();
                    if (MainForum.story_pager.getCurrentItem() < max_story - 1) {
                        MainForum.story_pager.setCurrentItem(MainForum.story_pager.getCurrentItem() + 1);
                    } else if (MainForum.story_pager.getCurrentItem() == max_story - 1) {
                        MainForum.story_pager.setVisibility(View.GONE);
                        onDestroy();
                    }
                }
            }, 7000);
        }
    }

    @Override
    public void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reverse_story:
                storiesProgressView.reverse();
                break;
            case R.id.skip_story:
                storiesProgressView.skip();
                break;
        }
    }
}

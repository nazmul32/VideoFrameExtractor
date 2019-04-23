package com.demo.videoeditor.trim;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.demo.videoeditor.customview.DisabledScrollViewPager;
import com.demo.videoeditor.R;
import com.demo.videoeditor.viewmodel.AdvancedToMainViewModel;
import com.demo.videoeditor.viewmodel.MainToAdvancedViewModel;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrimFragment.OnTrimFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrimFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrimFragment extends Fragment {
    private Disposable disposable = null;
    private MainToAdvancedViewModel mainToAdvancedViewModel;
    private AdvancedToMainViewModel advancedToMainViewModel;
    private int totalDuration;
    private TextView tvSimple;
    private TextView tvAdvanced;
    private int stopPosition = 0;
    private VideoView trimVideoView;
    private OnTrimFragmentInteractionListener mListener;

    public TrimFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TrimFragment newInstance() {
        TrimFragment fragment = new TrimFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trim, container, false);
        trimVideoView = view.findViewById(R.id.video_view_trim);
        ImageView ivPlayVideo = view.findViewById(R.id.play_video);
        ivPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!trimVideoView.isPlaying()) {
                    trimVideoView.seekTo(stopPosition);
                    trimVideoView.start();
                    mainToAdvancedViewModel.select(-1);
                    updateViewModel(true);
                } else {
                    trimVideoView.pause();
                    stopPosition = trimVideoView.getCurrentPosition();
                    updateViewModel(false);
                }
            }
        });
        trimVideoView.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.sample_video));
        trimVideoView.seekTo(1);

        trimVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                totalDuration = trimVideoView.getDuration() / 1000;

                final DisabledScrollViewPager viewPagerTrim = view.findViewById(R.id.viewpager_trim);
                viewPagerTrim.setPagingEnabled(false);
                EditModeFragmentPagerAdapter editModeFragmentPagerAdapter = new EditModeFragmentPagerAdapter(
                        getActivity().getSupportFragmentManager());
                viewPagerTrim.setAdapter(editModeFragmentPagerAdapter);
                viewPagerTrim.setCurrentItem(0);
                viewPagerTrim.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    // This method will be invoked when a new page becomes selected.
                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0) {
                            tvSimple.setTextColor(getResources().getColor(R.color.menu_item_selected_color));
                            tvAdvanced.setTextColor(getResources().getColor(R.color.bottom_tab_gray_color));
                        } else {
                            tvSimple.setTextColor(getResources().getColor(R.color.bottom_tab_gray_color));
                            tvAdvanced.setTextColor(getResources().getColor(R.color.menu_item_selected_color));
                        }
                    }

                    // This method will be invoked when the current page is scrolled
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        // Code goes here
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        // Code goes here
                    }
                });

                tvSimple = view.findViewById(R.id.trim_simple);
                tvAdvanced = view.findViewById(R.id.trim_advanced);
                tvSimple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewPagerTrim.getCurrentItem() == 1) {
                            viewPagerTrim.setCurrentItem(0);
                        }
                    }
                });
                tvAdvanced.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewPagerTrim.getCurrentItem() == 0) {
                            viewPagerTrim.setCurrentItem(1);
                        }
                    }
                });

                mainToAdvancedViewModel = ViewModelProviders.of(getActivity()).get(MainToAdvancedViewModel.class);
                advancedToMainViewModel = ViewModelProviders.of(getActivity()).get(AdvancedToMainViewModel.class);

                advancedToMainViewModel.getSelected().observe(TrimFragment.this, new android.arch.lifecycle.Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer integer) {
                        stopPosition = (integer + 5) * 1000;
                        trimVideoView.seekTo(stopPosition);
                        if (trimVideoView.isPlaying()) {
                            trimVideoView.pause();
                        }
                    }
                });
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTrimFragmentInteraction(uri);
        }
    }

    private void updateViewModel(final boolean runningFlag) {
        if (!runningFlag && disposable != null) {
            disposable.dispose();
        }

        Observable<Integer> localObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                while (trimVideoView.isPlaying()) {
                    emitter.onNext((trimVideoView.getCurrentPosition() / 1000) + 2);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        localObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (!runningFlag) {
                            d.dispose();
                        }

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.v("onNext", "integer: " + integer);
                        mainToAdvancedViewModel.select(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        stopPosition = trimVideoView.getCurrentPosition();
        trimVideoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTrimFragmentInteractionListener) {
            mListener = (OnTrimFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTrimFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTrimFragmentInteraction(Uri uri);
    }

    private class EditModeFragmentPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

        public EditModeFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TrimSimpleFragment.newInstance("TrimSimpleFragment", "0");
                case 1:
                    return TrimAdvancedFragment.newInstance("TrimAdvancedFragment",
                            "android.resource://" + getActivity().getPackageName() + "/" + R.raw.sample_video, totalDuration);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}

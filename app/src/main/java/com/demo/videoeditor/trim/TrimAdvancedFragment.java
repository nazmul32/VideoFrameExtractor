package com.demo.videoeditor.trim;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.demo.videoeditor.R;
import com.demo.videoeditor.adapter.TrimAdvancedAdapter;
import com.demo.videoeditor.customview.SpeedyLinearLayoutManager;
import com.demo.videoeditor.util.ImageUtils;
import com.demo.videoeditor.viewmodel.AdvancedToMainViewModel;
import com.demo.videoeditor.viewmodel.MainToAdvancedViewModel;

import java.util.ArrayList;

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
 * {@link TrimAdvancedFragment.OnTrimAdvancedFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrimAdvancedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrimAdvancedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean startButtonPressed = false;
    private boolean endButtonPressed = false;
    private int runningValue = 0;
    private int widthInPixel;
    private Disposable disposable = null;
    private MainToAdvancedViewModel mainToAdvancedViewModel;
    private AdvancedToMainViewModel advancedToMainViewModel;
    private int totalDuration;
    private OnTrimAdvancedFragmentInteractionListener mListener;
    private ImageView ivTrimSelectionStart;
    private ImageView ivTrimSelectionEnd;

    public TrimAdvancedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrimAdvancedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrimAdvancedFragment newInstance(String param1, String param2, int duration) {
        TrimAdvancedFragment fragment = new TrimAdvancedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            totalDuration = getArguments().getInt(ARG_PARAM3);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trim_advanced, container, false);
        widthInPixel = ImageUtils.getScreeWidth(getActivity()) / 2;

        final RecyclerView trimAdvancedRecyclerView = view.findViewById(R.id.trim_recycler_view);
        trimAdvancedRecyclerView.setHasFixedSize(true);
        final SpeedyLinearLayoutManager layoutManager = new SpeedyLinearLayoutManager(getActivity(), SpeedyLinearLayoutManager.HORIZONTAL, false);
        trimAdvancedRecyclerView.setLayoutManager(layoutManager);
        trimAdvancedRecyclerView.setItemViewCacheSize(20);
        trimAdvancedRecyclerView.setDrawingCacheEnabled(true);
        trimAdvancedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        final TrimAdvancedAdapter mAdapter = new TrimAdvancedAdapter(getActivity());
        trimAdvancedRecyclerView.setAdapter(mAdapter);

        ivTrimSelectionStart = view.findViewById(R.id.iv_trim_start_here);
        ivTrimSelectionEnd = view.findViewById(R.id.iv_trim_end_here);

        ivTrimSelectionStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimAdvancedRecyclerView.stopScroll();
                runningValue = 1;
                startButtonPressed  = true;
            }
        });

        ivTrimSelectionEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimAdvancedRecyclerView.stopScroll();
                runningValue = 1;
                endButtonPressed = true;
            }
        });

        loadVideoFrames(mAdapter);
        initViewModel(trimAdvancedRecyclerView);
        setTouchListenerToRecyclerView(trimAdvancedRecyclerView);
        processRecyclerViewScroll(trimAdvancedRecyclerView, layoutManager, mAdapter);

        return view;
    }


    private void initViewModel(final RecyclerView trimAdvancedRecyclerView) {
        mainToAdvancedViewModel = ViewModelProviders.of(getActivity()).get(MainToAdvancedViewModel.class);
        advancedToMainViewModel = ViewModelProviders.of(getActivity()).get(AdvancedToMainViewModel.class);

        mainToAdvancedViewModel.getSelected().observe(this, new android.arch.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == -1) {
                    runningValue = integer;
                    return;
                }
                trimAdvancedRecyclerView.smoothScrollToPosition(integer);
            }
        });
    }

    private void setTouchListenerToRecyclerView(final RecyclerView trimAdvancedRecyclerView) {
        trimAdvancedRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                trimAdvancedRecyclerView.stopScroll();
                runningValue = 1;
                return false;
            }
        });
    }

    private void processRecyclerViewScroll(RecyclerView trimAdvancedRecyclerView,
                                           final SpeedyLinearLayoutManager layoutManager,
                                           final TrimAdvancedAdapter mAdapter ) {
        trimAdvancedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int sum = 0;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && (startButtonPressed || endButtonPressed)) {
                    int i = layoutManager.findFirstCompletelyVisibleItemPosition();
                    int j = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (i == j) {
                        if (startButtonPressed) {
                            startButtonPressed = false;
                            ivTrimSelectionStart.setEnabled(false);
                            mAdapter.markFrame(i, true);
                        } else if (endButtonPressed) {
                            endButtonPressed = false;
                            ivTrimSelectionEnd.setEnabled(false);
                            mAdapter.markFrame(i, false);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                sum += dx;
                if (runningValue == 1) {
                    advancedToMainViewModel.select(sum / widthInPixel);
                }
            }
        });

    }
    private void loadVideoFrames(final TrimAdvancedAdapter mAdapter) {
        Observable<ArrayList<Bitmap>> localObservable = Observable.create(new ObservableOnSubscribe<ArrayList<Bitmap>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Bitmap>> emitter) {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(getActivity(), Uri.parse(mParam2));
                ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
                int start = 0;
                while (start <= totalDuration) {
                    bitmapArrayList.add(ImageUtils.getCompressedBitmap(mediaMetadataRetriever.getFrameAtTime(start * 1000000)));
                    if (start % 4 == 3) {
                        emitter.onNext(bitmapArrayList);
                        bitmapArrayList = new ArrayList<>();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    start++;
                }
                emitter.onNext(bitmapArrayList);
            }
        });

        localObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Bitmap>>(){
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(ArrayList<Bitmap> bitmapArrayList) {
                        mAdapter.updateDataSet(bitmapArrayList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTrimAdvancedFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTrimAdvancedFragmentInteractionListener) {
            mListener = (OnTrimAdvancedFragmentInteractionListener) context;
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
    public interface OnTrimAdvancedFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTrimAdvancedFragmentInteraction(Uri uri);
    }
}

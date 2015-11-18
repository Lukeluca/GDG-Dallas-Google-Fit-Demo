package com.gdgdallas.googlefitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.gdgdallas.googlefitdemo.data.BurpeeLog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BurpeeFragmentListener} interface
 * to handle interaction events.
 * Use the {@link BurpeeCounterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BurpeeCounterFragment extends android.support.v4.app.Fragment {

    private BurpeeFragmentListener mListener;

    private DatePicker mDatePicker;
    private EditText mCounter;
    private Button mBtnSubmit;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BurpeeCounterFragment.
     */
    public static BurpeeCounterFragment newInstance() {
        BurpeeCounterFragment fragment = new BurpeeCounterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BurpeeCounterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_burpee_counter, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BurpeeFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BurpeeFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View thisView = getView();

        mCounter = (EditText) thisView.findViewById(R.id.editText);
        mBtnSubmit = (Button) thisView.findViewById(R.id.button);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Editable mCounterText = mCounter.getText();
                final Integer count = Integer.parseInt(mCounterText.toString());
                BurpeeLog burpeeLog = new BurpeeLog(System.currentTimeMillis(), count);
                onSubmitCount(burpeeLog);
            }
        });

    }

    private void onSubmitCount(BurpeeLog burpeeLog) {
        if (mListener != null) {
            mListener.onBurpeeLogSaved(burpeeLog);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface BurpeeFragmentListener {
        public void onBurpeeLogSaved(BurpeeLog burpeeLog);
    }

}

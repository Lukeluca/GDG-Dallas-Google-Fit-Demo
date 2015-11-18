package com.gdgdallas.googlefitdemo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.gdgdallas.googlefitdemo.data.CupcakeLog;
import com.gdgdallas.googlefitdemo.data.DonutLog;
import com.gdgdallas.googlefitdemo.data.SweetLog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SweetsFragmentListener} interface
 * to handle interaction events.
 * Use the {@link SweetsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SweetsFragment extends android.support.v4.app.Fragment {

    private SweetsFragmentListener mListener;
    private DatePicker mDatePicker;
    private EditText mCupcakes;
    private EditText mDonuts;
    private Button mBtnSubmit;
    private TextView mHistory;
    private SweetLog mSweetsHistory;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SweetsFragment.
     */
    public static SweetsFragment newInstance() {
        SweetsFragment fragment = new SweetsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SweetsFragment() {
        // Required empty public constructor
    }

    public void setHistory(SweetLog sweetLog) {
        mSweetsHistory = sweetLog;
        if (mSweetsHistory != null) {
            updateHistory();
        }
    }

    private void updateHistory() {
        StringBuilder sb = new StringBuilder();

        if (mSweetsHistory != null) {
            sb.append("Last 7 Days Data:\n");
            sb.append("\nCupcakes:" + String.valueOf(mSweetsHistory.getCupcakeLog().getCount()));
            sb.append("\nDonuts:" + String.valueOf(mSweetsHistory.getDonutLog().getCount()));
        }

        mHistory.setText(sb.toString());
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
        return inflater.inflate(R.layout.fragment_sweets, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SweetsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SweetsFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View thisView = getView();
        mCupcakes = (EditText) thisView.findViewById(R.id.etCupcakes);
        mDonuts = (EditText) thisView.findViewById(R.id.etDonut);
        mBtnSubmit = (Button) thisView.findViewById(R.id.button);
        mHistory = (TextView) thisView.findViewById(R.id.tvHistory);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int cupcakeCount = getEditTextValue(mCupcakes);
                final int donutCount = getEditTextValue(mDonuts);

                CupcakeLog cupcakes = new CupcakeLog(System.currentTimeMillis(), cupcakeCount);
                DonutLog donutLog = new DonutLog(System.currentTimeMillis(), donutCount);

                onSubmitCount(cupcakes, donutLog);
            }

            private int getEditTextValue(EditText e) {
                final Editable ed = e.getText();
                final String s = ed.toString();
                if (TextUtils.isEmpty(s)) {
                    return 0;
                } else {
                    if (!TextUtils.isDigitsOnly(s)) {
                        return 0;
                    } else {
                        return Integer.parseInt(s);
                    }
                }

            }
        });

        updateHistory();

    }

    private void onSubmitCount(CupcakeLog cupcakeLog, DonutLog donutLog) {
        if (mListener != null) {
            mListener.onSweetsLogSaveClick(cupcakeLog, donutLog);
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
    public interface SweetsFragmentListener {
        public void onSweetsLogSaveClick(CupcakeLog cupcakeLog, DonutLog donutLog);
    }

}

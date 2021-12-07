package cn.wch.blecomm;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReceiveMsgFragment extends Fragment {

    ViewModeWithLiveData ReceiveMsgFrag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receivemsg , container, false);



        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        //TextView TV = getView().findViewById(R.id.ChgOverCur);


        ReceiveMsgFrag = new ViewModelProvider(requireActivity()).get(ViewModeWithLiveData.class);

        ((TextView) getView().findViewById(R.id.receive_msg)).setMovementMethod(ScrollingMovementMethod.getInstance());

        ((TextView) getView().findViewById(R.id.receive_msg)).setClickable(true);
        ((TextView) getView().findViewById(R.id.receive_msg)).setEnabled(true);
        ((TextView) getView().findViewById(R.id.receive_msg)).setVerticalScrollBarEnabled(true);

        ReceiveMsgFrag.getChangeFlg().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {


                for (int i = 0; i < 3; i++)
                {
                    ((TextView) getView().findViewById(R.id.receive_msg)).append("\n");
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss", Locale.getDefault());
                long timegettime = System.currentTimeMillis();
                String time = sdf.format(timegettime);
                ((TextView) getView().findViewById(R.id.receive_msg)).append(time + ": ");
                ((TextView) getView().findViewById(R.id.receive_msg)).append("\n");




                ((TextView) getView().findViewById(R.id.receive_msg)).append(ReceiveMsgFrag.ReceiveDataToString);


                int offset = (((TextView) getView().findViewById(R.id.receive_msg))).getLineCount() * (((TextView) getView().findViewById(R.id.receive_msg))).getLineHeight();
                //int maxHeight = usbReadValue.getMaxHeight();
                int height = (((TextView) getView().findViewById(R.id.receive_msg))).getHeight();
                //USBLog.d("offset: "+offset+"  maxHeight: "+maxHeight+" height: "+height);
                if (offset > height) {
                    //USBLog.d("scroll: "+(offset - usbReadValue.getHeight() + usbReadValue.getLineHeight()));
                    (((TextView) getView().findViewById(R.id.receive_msg))).scrollTo(0, offset - (((TextView) getView().findViewById(R.id.receive_msg))).getHeight() + (((TextView) getView().findViewById(R.id.receive_msg))).getLineHeight());

                }
            }


        }

        );

    }
}

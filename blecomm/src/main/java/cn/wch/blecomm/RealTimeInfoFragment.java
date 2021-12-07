package cn.wch.blecomm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class RealTimeInfoFragment extends Fragment {

    ViewModeWithLiveData RecRealTimeFrag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtimeinfo, container, false);

        RecRealTimeFrag  = new ViewModelProvider(requireActivity()).get(ViewModeWithLiveData.class);
        RecRealTimeFrag.getChangeFlg().observe(getViewLifecycleOwner(),  new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                ((TextView)view.findViewById(R.id.Cell1)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[0]);
                ((TextView)view.findViewById(R.id.Cell1)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell2)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[1]);
                ((TextView)view.findViewById(R.id.Cell2)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell3)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[2]);
                ((TextView)view.findViewById(R.id.Cell3)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell4)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[3]);
                ((TextView)view.findViewById(R.id.Cell4)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell5)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[4]);
                ((TextView)view.findViewById(R.id.Cell5)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell6)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[5]);
                ((TextView)view.findViewById(R.id.Cell6)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell7)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[6]);
                ((TextView)view.findViewById(R.id.Cell7)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell8)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[7]);
                ((TextView)view.findViewById(R.id.Cell8)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell9)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[8]);
                ((TextView)view.findViewById(R.id.Cell9)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Cell10)).setText("" + RecRealTimeFrag.RealTimeInfo.cells_voltage[9]);
                ((TextView)view.findViewById(R.id.Cell10)).setTextColor(Color.BLUE);

                //Real Time Data
                ((TextView)view.findViewById(R.id.SocValueNum)).setText("SOC: " + RecRealTimeFrag.RealTimeInfo.asoc);
                //((TextView)ActUsed.findViewById(R.id.SocValueNum)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.Voltage)).setText("" + RecRealTimeFrag.RealTimeInfo.bat_voltage);
                ((TextView)view.findViewById(R.id.Voltage)).setTextColor(Color.BLUE);

                if (RecRealTimeFrag.RealTimeInfo.charge_current >= RecRealTimeFrag.RealTimeInfo.bat_discharge_total_current)
                {
                    ((TextView)view.findViewById(R.id.Current)).setText("" + RecRealTimeFrag.RealTimeInfo.charge_current);
                    ((TextView)view.findViewById(R.id.Current)).setTextColor(Color.BLUE);
                }
                else if (RecRealTimeFrag.RealTimeInfo.charge_current < RecRealTimeFrag.RealTimeInfo.bat_discharge_total_current)
                {
                    ((TextView)view.findViewById(R.id.Current)).setText("" + RecRealTimeFrag.RealTimeInfo.bat_discharge_total_current);
                    ((TextView)view.findViewById(R.id.Current)).setTextColor(Color.BLUE);
                }

                ((TextView)view.findViewById(R.id.Capacity)).setText("" + RecRealTimeFrag.RealTimeInfo.remain_capcity);
                ((TextView)view.findViewById(R.id.Capacity)).setTextColor(Color.BLUE);
                //((TextView)ActUsed.findViewById(R.id.SocData)).setText("" + RealTimeInfo.asoc);
                //((TextView)ActUsed.findViewById(R.id.SocData)).setTextColor(Color.BLUE);
                ((TextView)view.findViewById(R.id.SohData)).setText("" + RecRealTimeFrag.RealTimeInfo.soh);
                ((TextView)view.findViewById(R.id.SohData)).setTextColor(Color.BLUE);

                ((ProgressBar)view.findViewById(R.id.SocBar)).setProgress(RecRealTimeFrag.RealTimeInfo.asoc);

            }
        });

        return view;
    }
}

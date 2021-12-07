package cn.wch.blecomm;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.reactivex.annotations.Nullable;

public class ProtectionInfoFragment extends Fragment {

    int test = 0;
    ViewModeWithLiveData ProtectionInfoFrag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_protectioninfo, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //This is test code for pressing the textview
        TextView TV = getView().findViewById(R.id.ChgOverCur);
        TV.setEnabled(true);
        TV.setClickable(true);
        TV.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                TV.setText("" + test);
                if (test % 2 == 0)
                {
                    TV.setBackgroundColor(Color.RED);
                }
                else
                {
                    TV.setBackgroundColor(Color.GREEN);
                }

                test = test + 1;
            }
        });

        //Start to show the protection information
        ProtectionInfoFrag = new ViewModelProvider(requireActivity()).get(ViewModeWithLiveData.class);
        ProtectionInfoFrag.getChangeFlg().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onChanged(Integer integer) {

                //start to judge if there is any protection happened
                if (ProtectionInfoFrag.RealTimeInfo.protect_status != 0)
                {
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_OVER_CURRENT) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgOverCur)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.ChgOverCur)).setTextColor(Color.WHITE);

                    }

                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_TIME_OUT) != 0)
                    {
                        //add in future
                    }

                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_SINGLE_CELL_OVER_VOL) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgCellOverVol)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.ChgCellOverVol)).setTextColor(Color.WHITE);

                    }

                    //
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_OVER_TEMP) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgOverTemp)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.ChgOverTemp)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_UNDER_TEMP) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgUnderTemp)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.ChgUnderTemp)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_OVER_CURRENT) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgOverCur)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.DisChgOverCur)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_UNDER_VOL_LOCK) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgUnderVol)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.DisChgUnderVol)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_CELL_UNDER_VOL) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgCellUnderVol)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.DisChgCellUnderVol)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_OVER_TEMP) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgOverTemp)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.DisChgOverTemp)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_UNDER_TEMP) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgUnderTemp)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.DisChgUnderTemp)).setTextColor(Color.WHITE);
                          }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PRTECT_MASK_GND_BRUSH_NOLOAD) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.FlBrushNoload)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.FlBrushNoload)).setTextColor(Color.WHITE);
                          }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MAIN_DISCHARGE_NOLOAD) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.MainDshNoLoad)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.MainDshNoLoad)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MOS_OVER_TEMP) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.MosOverTemp)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.MosOverTemp)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CELLS_IMBALANCE) != 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MAIN_BRUSH_SCD) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.MainBrushSCD)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.MainBrushSCD)).setTextColor(Color.WHITE);
                        }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_GND_BRUSH_OVER_CURRENT) != 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_FLOOR_BRUSH_SCD) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.FlBrushSCD)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.FlBrushSCD)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MOTOR_STALL) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.MotorStall)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.MotorStall)).setTextColor(Color.WHITE);
                         }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_NTC_ERROR) != 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_GND_BRUSH_OVER_CURRENT) != 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_OVER_TEMP) != 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_CELL_OVER_VOL) != 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_CELL_UNDER_VOL) != 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_CHARGE_OVER_CURRENT) != 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgSecOverCur)).setBackgroundColor(Color.RED);
                        ((TextView)getView().findViewById(R.id.ChgSecOverCur)).setTextColor(Color.WHITE);
                          }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_DISCHARGE_OVER_CURRENT) != 0)
                    {
                        //add in future
                    }
                }


                //when there is no protection, need reset the color
                {
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_OVER_CURRENT) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgOverCur)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.ChgOverCur)).setTextColor(R.color.colorPrimary);
                    }


                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_TIME_OUT) == 0)
                    {
                        //add in future
                    }

                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_SINGLE_CELL_OVER_VOL) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgCellOverVol)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.ChgCellOverVol)).setTextColor(R.color.colorPrimary);
                    }

                    //
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_OVER_TEMP) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgOverTemp)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.ChgOverTemp)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CHARGE_UNDER_TEMP) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgUnderTemp)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.ChgUnderTemp)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_OVER_CURRENT) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgOverCur)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.DisChgOverCur)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_UNDER_VOL_LOCK) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgUnderVol)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.DisChgUnderVol)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_CELL_UNDER_VOL) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgCellUnderVol)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.DisChgCellUnderVol)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_OVER_TEMP) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgOverTemp)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.DisChgOverTemp)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_DISCHARGE_UNDER_TEMP) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.DisChgUnderTemp)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.DisChgUnderTemp)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PRTECT_MASK_GND_BRUSH_NOLOAD) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.FlBrushNoload)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.FlBrushNoload)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MAIN_DISCHARGE_NOLOAD) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.MainDshNoLoad)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.MainDshNoLoad)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MOS_OVER_TEMP) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.MosOverTemp)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.MosOverTemp)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_CELLS_IMBALANCE) == 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MAIN_BRUSH_SCD) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.MainBrushSCD)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.MainBrushSCD)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_GND_BRUSH_OVER_CURRENT) == 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_FLOOR_BRUSH_SCD) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.FlBrushSCD)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.FlBrushSCD)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_MOTOR_STALL) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.MotorStall)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.MotorStall)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_MASK_NTC_ERROR) == 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_GND_BRUSH_OVER_CURRENT) == 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_OVER_TEMP) == 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_CELL_OVER_VOL) == 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_CELL_UNDER_VOL) == 0)
                    {
                        //add in future
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_CHARGE_OVER_CURRENT) == 0)
                    {
                        ((TextView)getView().findViewById(R.id.ChgSecOverCur)).setBackgroundResource(R.drawable.tv_round);
                        ((TextView)getView().findViewById(R.id.ChgSecOverCur)).setTextColor(R.color.colorPrimary);
                    }
                    if ((ProtectionInfoFrag.RealTimeInfo.protect_status & ProtectionInfoFrag.PROTECT_2ND_MASK_DISCHARGE_OVER_CURRENT) == 0)
                    {
                        //add in future
                    }
                }

            }
        });

    }
}

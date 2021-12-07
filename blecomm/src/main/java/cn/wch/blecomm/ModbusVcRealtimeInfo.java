package cn.wch.blecomm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Timer;
import java.util.TimerTask;

class RealData{
    public short bms_work_state;
    public int bat_voltage;  //public short[] bat_voltage = new short[2]; the value is an 32bit value
    public int charge_port_voltage; //public short[] charge_port_voltage = new short[2];
    public int p_voltage; //public short[] p_voltage = new short[2];
    public int floor_brush_voltage;//public short[] floor_brush_voltage = new short[2];
    public short[] reserve1 = new short[3];
    public int bat_discharge_total_current; //public short[] bat_discharge_total_current = new short[2];
    public int main_brush_current;//public short[] main_brush_current = new short[2];
    public int floor_brush_current;//public short[] floor_brush_current = new short[2];
    int charge_current; //public short[] charge_current = new short[2];
    public short[] reserve2 = new short[2];
    public short[] cells_voltage = new short[10];
    public short cell_voltage_difference;
    public short max_cell_temp;
    public short min_cell_temp;
    public short[] cell_ntc_Temp = new short[3];
    public short[] BmsBoardNtcTemp = new short[2];
    public short[] reserve3 = new short[4];
    public int protect_status; //public short protect_status[2];
    public short io_status;
    public short afe_status;
    public char[] sn = new char[32];
    public char[] date = new char[14];
    public short sw_version;
    public short hd_version;
    public short asoc;
    public short rsoc;
    public short usoc;
    public short soh;
    public int act_capcity;//public short[] act_capcity = new short[2];
    public int remain_capcity; //public short[] remain_capcity = new short[2];
}

public class ModbusVcRealtimeInfo {

    //Define the protection bit value for different protection
    final int PROTECT_MASK_CHARGE_OVER_CURRENT		=		0X00000001;
    final int PROTECT_MASK_CHARGE_TIME_OUT			=		0X00000002;
    final int PROTECT_MASK_CHARGE_SINGLE_CELL_OVER_VOL	=	0X00000004;
    final int PROTECT_MASK_CHARGE_OVER_TEMP			=		0X00000008;
    final int PROTECT_MASK_CHARGE_UNDER_TEMP			=		0X00000010;
    final int PROTECT_MASK_DISCHARGE_OVER_CURRENT		=	0X00000020;
    final int PROTECT_MASK_DISCHARGE_UNDER_VOL_LOCK		=	0X00000040;
    final int PROTECT_MASK_DISCHARGE_CELL_UNDER_VOL		=	0X00000080;
    final int PROTECT_MASK_DISCHARGE_OVER_TEMP		=		0X00000100;
    final int PROTECT_MASK_DISCHARGE_UNDER_TEMP		=		0X00000200;
    final int PRTECT_MASK_GND_BRUSH_NOLOAD			=		0X00000400;
    final int PROTECT_MASK_MAIN_DISCHARGE_NOLOAD	=		0X00000800;
    final int PROTECT_MASK_MOS_OVER_TEMP			=			0X00001000;
    final int PROTECT_MASK_CELLS_IMBALANCE			=			0X00002000;
    final int PROTECT_MASK_MAIN_BRUSH_SCD			=			0X00004000;
    final int PROTECT_MASK_GND_BRUSH_OVER_CURRENT	=		0X00008000;
    final int PROTECT_MASK_FLOOR_BRUSH_SCD			=		0X00010000;
    final int PROTECT_MASK_MOTOR_STALL				=		0X00020000;
    final int PROTECT_MASK_NTC_ERROR				=			0X00040000;

    /* 2ND Protect*/

    final int PROTECT_2ND_MASK_GND_BRUSH_OVER_CURRENT	=	0X04000000;
    final int PROTECT_2ND_MASK_OVER_TEMP				=		0X08000000;
    final int PROTECT_2ND_MASK_CELL_OVER_VOL			=		0X10000000;
    final int PROTECT_2ND_MASK_CELL_UNDER_VOL		=		0X20000000;
    final int PROTECT_2ND_MASK_CHARGE_OVER_CURRENT	=		0X40000000;
    final int PROTECT_2ND_MASK_DISCHARGE_OVER_CURRENT	=	0X80000000;



    RealData RealTimeInfo = new RealData();
    byte[] RecBytes = new byte[200];
    int bytelength;
    Timer Rectimer = null;
    TimerTask Rectimertask = null;
    Activity ActUsed;
    String ReceiveDataToString = "";


    public ModbusVcRealtimeInfo(Activity AC)
    {
        RealTimeInfo.bms_work_state = 0;
        for (int i = 0; i < RealTimeInfo.cells_voltage.length; i++ )
        {
            RealTimeInfo.cells_voltage[i] = 0;
        }

        RealTimeInfo.bat_voltage = 0;

        RealTimeInfo.cell_voltage_difference = 0;
        RealTimeInfo.hd_version = 0;
        RealTimeInfo.sw_version = 0;
        RealTimeInfo.asoc = 0;

        for (int i = 0; i < RecBytes.length; i++)
        {
            RecBytes[i] = 0;
        }
        ActUsed = AC;


    }

    public short Trans2bytetoShort(byte[] Temp, int pos)
    {
        byte high = Temp[pos + 1];
        byte low = Temp[pos];
        short value = (short) (((high & 0xFF) << 8) | (low & 0xFF));
        return value;
    };

    public int Trans4bytetoInt(byte[] Temp, int pos)
    {
        byte byte1 = Temp[pos + 3];
        byte byte2 = Temp[pos + 2];
        byte byte3 = Temp[pos + 1];
        byte byte4 = Temp[pos];

        int value = (((byte1 & 0xFF) << 24) | ((byte2 & 0xFF) << 16) | ((byte3 & 0xFF) << 8) | (byte4 & 0xFF));

        return value;
    };

    public void ResetModbusVcRealtimeInfo()
    {
        //initialize the receive data based on modbus protocol
        RealTimeInfo.bms_work_state = 0;
        for (int i = 0; i < RealTimeInfo.cells_voltage.length; i++ )
        {
            RealTimeInfo.cells_voltage[i] = 0;
        }

        RealTimeInfo.bat_voltage = 0;

        RealTimeInfo.cell_voltage_difference = 0;
        RealTimeInfo.hd_version = 0;
        RealTimeInfo.sw_version = 0;
        RealTimeInfo.asoc = 0;

        //initialize the RecBytes array
        for (int i = 0; i < RecBytes.length; i++)
        {
            RecBytes[i] = 0;
        }
        bytelength = 0;//Every time, the byte array will start from 0 position
        ReceiveDataToString = "";// Every time, the data to show need initialize to ""

        //initialize the Rectimer

         if (Rectimer == null || Rectimertask == null)
        {
            Rectimer = new Timer();
            Rectimertask = new TimerTask() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {

                    //when rectimer time out, RecBytes array receive all bytes
                    byte[] Temp = new byte[162];
                    System.arraycopy(RecBytes, 3, Temp, 0, 162);


                    if (RealTimeInfo != null && ActUsed != null)
                    {
                        RealTimeInfo.bms_work_state = Trans2bytetoShort(Temp, 0);

                        for (int i = 0; i < RealTimeInfo.cells_voltage.length; i++)
                        {
                            RealTimeInfo.cells_voltage[i] = Trans2bytetoShort(Temp, 44 + (i * 2));
                            ReceiveDataToString = ReceiveDataToString + "Cell" + i + ": " + RealTimeInfo.cells_voltage[i] + "mv |";
                        }

                        for (int j = 0; j < 3; j++)
                        {
                            RealTimeInfo.cell_ntc_Temp[j] = Trans2bytetoShort(Temp, 70 + (j * 2));
                            ReceiveDataToString = ReceiveDataToString + " *** cell_ntc_Temp" + j + ": " + RealTimeInfo.cell_ntc_Temp[j];
                        }
                        for (int k = 0; k < 2; k++)
                        {
                            RealTimeInfo.BmsBoardNtcTemp[k] = Trans2bytetoShort(Temp, 76 + (k * 2));
                            ReceiveDataToString = ReceiveDataToString + " *** BmsBoardNtcTemp" + k + ": " + RealTimeInfo.BmsBoardNtcTemp[k];
                        }

                        RealTimeInfo.bat_voltage = Trans4bytetoInt(Temp, 2);
                        ReceiveDataToString = ReceiveDataToString + " *** bat voltage: " + RealTimeInfo.bat_voltage + "mv";

                        RealTimeInfo.bat_discharge_total_current = Trans4bytetoInt(Temp, 24);
                        ReceiveDataToString = ReceiveDataToString + " *** DisCharge Current: " + RealTimeInfo.bat_discharge_total_current + "mA";

                        RealTimeInfo.charge_current = Trans4bytetoInt(Temp, 36);
                        ReceiveDataToString = ReceiveDataToString + " *** charge_current: " + RealTimeInfo.charge_current + "mA";


                        RealTimeInfo.protect_status = Trans4bytetoInt(Temp, 88);


                        RealTimeInfo.sw_version = Trans2bytetoShort(Temp, 142);
                        ReceiveDataToString = ReceiveDataToString + " *** sw_version: " + RealTimeInfo.sw_version;

                        RealTimeInfo.hd_version = Trans2bytetoShort(Temp, 144);
                        ReceiveDataToString = ReceiveDataToString + " *** hd_version: " + RealTimeInfo.hd_version;

                        RealTimeInfo.asoc = Trans2bytetoShort(Temp, 146);
                        ReceiveDataToString = ReceiveDataToString + " *** asoc: " + RealTimeInfo.asoc;

                        RealTimeInfo.soh = Trans2bytetoShort(Temp, 152);
                        ReceiveDataToString = ReceiveDataToString + " *** soh: " + RealTimeInfo.soh;

                        RealTimeInfo.act_capcity = Trans4bytetoInt(Temp, 154);
                        ReceiveDataToString = ReceiveDataToString + " *** act_capcity: " + RealTimeInfo.act_capcity;


                        RealTimeInfo.remain_capcity = Trans4bytetoInt(Temp, 158);
                        ReceiveDataToString = ReceiveDataToString + " *** remain_capcity: " + RealTimeInfo.remain_capcity;


                        {
                            ((TextView)ActUsed.findViewById(R.id.Cell1)).setText("" + RealTimeInfo.cells_voltage[0]);
                            ((TextView)ActUsed.findViewById(R.id.Cell1)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell2)).setText("" + RealTimeInfo.cells_voltage[1]);
                            ((TextView)ActUsed.findViewById(R.id.Cell2)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell3)).setText("" + RealTimeInfo.cells_voltage[2]);
                            ((TextView)ActUsed.findViewById(R.id.Cell3)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell4)).setText("" + RealTimeInfo.cells_voltage[3]);
                            ((TextView)ActUsed.findViewById(R.id.Cell4)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell5)).setText("" + RealTimeInfo.cells_voltage[4]);
                            ((TextView)ActUsed.findViewById(R.id.Cell5)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell6)).setText("" + RealTimeInfo.cells_voltage[5]);
                            ((TextView)ActUsed.findViewById(R.id.Cell6)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell7)).setText("" + RealTimeInfo.cells_voltage[6]);
                            ((TextView)ActUsed.findViewById(R.id.Cell7)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell8)).setText("" + RealTimeInfo.cells_voltage[7]);
                            ((TextView)ActUsed.findViewById(R.id.Cell8)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell9)).setText("" + RealTimeInfo.cells_voltage[8]);
                            ((TextView)ActUsed.findViewById(R.id.Cell9)).setTextColor(Color.BLUE);
                            ((TextView)ActUsed.findViewById(R.id.Cell10)).setText("" + RealTimeInfo.cells_voltage[9]);
                            ((TextView)ActUsed.findViewById(R.id.Cell10)).setTextColor(Color.BLUE);

                        }
                        ((TextView)ActUsed.findViewById(R.id.SocValueNum)).setText("SOC: " + RealTimeInfo.asoc);
                        //((TextView)ActUsed.findViewById(R.id.SocValueNum)).setTextColor(Color.BLUE);
                        ((TextView)ActUsed.findViewById(R.id.Voltage)).setText("" + RealTimeInfo.bat_voltage);
                        ((TextView)ActUsed.findViewById(R.id.Voltage)).setTextColor(Color.BLUE);

                        if (RealTimeInfo.charge_current >= RealTimeInfo.bat_discharge_total_current)
                        {
                            ((TextView)ActUsed.findViewById(R.id.Current)).setText("" + RealTimeInfo.charge_current);
                            ((TextView)ActUsed.findViewById(R.id.Current)).setTextColor(Color.BLUE);
                        }
                        else if (RealTimeInfo.charge_current < RealTimeInfo.bat_discharge_total_current)
                        {
                            ((TextView)ActUsed.findViewById(R.id.Current)).setText("" + RealTimeInfo.bat_discharge_total_current);
                            ((TextView)ActUsed.findViewById(R.id.Current)).setTextColor(Color.BLUE);
                        }

                        ((TextView)ActUsed.findViewById(R.id.Capacity)).setText("" + RealTimeInfo.remain_capcity);
                        ((TextView)ActUsed.findViewById(R.id.Capacity)).setTextColor(Color.BLUE);
                        //((TextView)ActUsed.findViewById(R.id.SocData)).setText("" + RealTimeInfo.asoc);
                        //((TextView)ActUsed.findViewById(R.id.SocData)).setTextColor(Color.BLUE);
                        ((TextView)ActUsed.findViewById(R.id.SohData)).setText("" + RealTimeInfo.soh);
                        ((TextView)ActUsed.findViewById(R.id.SohData)).setTextColor(Color.BLUE);

                        ((ProgressBar)ActUsed.findViewById(R.id.SocBar)).setProgress(RealTimeInfo.asoc);

                        //start to judge if there is any protection happened
                        if (RealTimeInfo.protect_status == 0)
                        {
                            ((TextView)ActUsed.findViewById(R.id.SystemStatus)).setText("ALL OK");
                            ((TextView)ActUsed.findViewById(R.id.SystemStatus)).setBackgroundColor(Color.GREEN);
                            ((TextView)ActUsed.findViewById(R.id.SystemStatus)).setTextColor(Color.WHITE);
                        }
                        else
                        {
                            ((TextView)ActUsed.findViewById(R.id.SystemStatus)).setText("Fault");
                            ((TextView)ActUsed.findViewById(R.id.SystemStatus)).setBackgroundColor(Color.RED);
                            ((TextView)ActUsed.findViewById(R.id.SystemStatus)).setTextColor(Color.WHITE);

                            //start to judge which protection happen
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_OVER_CURRENT) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgOverCur)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.ChgOverCur)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_CHARGE_OVER_CURRENT";
                            }

                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_TIME_OUT) != 0)
                            {
                                //add in future
                            }

                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_SINGLE_CELL_OVER_VOL) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgCellOverVol)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.ChgCellOverVol)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_CHARGE_SINGLE_CELL_OVER_VOL";
                            }

                            //
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_OVER_TEMP) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgOverTemp)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.ChgOverTemp)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_CHARGE_OVER_TEMP";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_UNDER_TEMP) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgUnderTemp)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.ChgUnderTemp)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_CHARGE_UNDER_TEMP";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_OVER_CURRENT) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverCur)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverCur)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_DISCHARGE_OVER_CURRENT";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_UNDER_VOL_LOCK) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderVol)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderVol)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_DISCHARGE_UNDER_VOL_LOCK";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_CELL_UNDER_VOL) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgCellUnderVol)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.DisChgCellUnderVol)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_DISCHARGE_CELL_UNDER_VOL";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_OVER_TEMP) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverTemp)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverTemp)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_DISCHARGE_OVER_TEMP";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_UNDER_TEMP) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderTemp)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderTemp)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_DISCHARGE_UNDER_TEMP";
                            }
                            if ((RealTimeInfo.protect_status & PRTECT_MASK_GND_BRUSH_NOLOAD) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.FlBrushNoload)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.FlBrushNoload)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PRTECT_MASK_GND_BRUSH_NOLOAD";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MAIN_DISCHARGE_NOLOAD) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MainDshNoLoad)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.MainDshNoLoad)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_MAIN_DISCHARGE_NOLOAD";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MOS_OVER_TEMP) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MosOverTemp)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.MosOverTemp)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_MOS_OVER_TEMP";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CELLS_IMBALANCE) != 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MAIN_BRUSH_SCD) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MainBrushSCD)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.MainBrushSCD)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_MAIN_BRUSH_SCD";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_GND_BRUSH_OVER_CURRENT) != 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_FLOOR_BRUSH_SCD) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.FlBrushSCD)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.FlBrushSCD)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_FLOOR_BRUSH_SCD";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MOTOR_STALL) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MotorStall)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.MotorStall)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_MASK_MOTOR_STALL";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_NTC_ERROR) != 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_GND_BRUSH_OVER_CURRENT) != 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_OVER_TEMP) != 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_CELL_OVER_VOL) != 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_CELL_UNDER_VOL) != 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_CHARGE_OVER_CURRENT) != 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgSecOverCur)).setBackgroundColor(Color.RED);
                                ((TextView)ActUsed.findViewById(R.id.ChgSecOverCur)).setTextColor(Color.WHITE);
                                ReceiveDataToString = ReceiveDataToString + " *** PROTECT_2ND_MASK_CHARGE_OVER_CURRENT";
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_DISCHARGE_OVER_CURRENT) != 0)
                            {
                                //add in future
                            }
                        }

                        //when there is no protection, need reset the color

                        {
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_OVER_CURRENT) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgOverCur)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.ChgOverCur)).setTextColor(R.color.colorPrimary);
                            }


                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_TIME_OUT) == 0)
                            {
                                //add in future
                            }

                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_SINGLE_CELL_OVER_VOL) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgCellOverVol)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.ChgCellOverVol)).setTextColor(R.color.colorPrimary);
                            }

                            //
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_OVER_TEMP) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgOverTemp)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.ChgOverTemp)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CHARGE_UNDER_TEMP) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgUnderTemp)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.ChgUnderTemp)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_OVER_CURRENT) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverCur)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverCur)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_UNDER_VOL_LOCK) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderVol)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderVol)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_CELL_UNDER_VOL) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgCellUnderVol)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.DisChgCellUnderVol)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_OVER_TEMP) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverTemp)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.DisChgOverTemp)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_DISCHARGE_UNDER_TEMP) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderTemp)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.DisChgUnderTemp)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PRTECT_MASK_GND_BRUSH_NOLOAD) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.FlBrushNoload)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.FlBrushNoload)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MAIN_DISCHARGE_NOLOAD) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MainDshNoLoad)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.MainDshNoLoad)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MOS_OVER_TEMP) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MosOverTemp)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.MosOverTemp)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_CELLS_IMBALANCE) == 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MAIN_BRUSH_SCD) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MainBrushSCD)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.MainBrushSCD)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_GND_BRUSH_OVER_CURRENT) == 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_FLOOR_BRUSH_SCD) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.FlBrushSCD)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.FlBrushSCD)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_MOTOR_STALL) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.MotorStall)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.MotorStall)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_MASK_NTC_ERROR) == 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_GND_BRUSH_OVER_CURRENT) == 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_OVER_TEMP) == 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_CELL_OVER_VOL) == 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_CELL_UNDER_VOL) == 0)
                            {
                                //add in future
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_CHARGE_OVER_CURRENT) == 0)
                            {
                                ((TextView)ActUsed.findViewById(R.id.ChgSecOverCur)).setBackgroundResource(R.drawable.tv_round);
                                ((TextView)ActUsed.findViewById(R.id.ChgSecOverCur)).setTextColor(R.color.colorPrimary);
                            }
                            if ((RealTimeInfo.protect_status & PROTECT_2ND_MASK_DISCHARGE_OVER_CURRENT) == 0)
                            {
                                //add in future
                            }
                        }

                        //Start to show the message in text view
                        ((TextView)ActUsed.findViewById(R.id.receive_data)).append(ReceiveDataToString);
                        int offset = ((TextView)ActUsed.findViewById(R.id.receive_data)).getLineCount() * ((TextView)ActUsed.findViewById(R.id.receive_data)).getLineHeight();
                        //int maxHeight = usbReadValue.getMaxHeight();
                        int height = ((TextView)ActUsed.findViewById(R.id.receive_data)).getHeight();
                        //USBLog.d("offset: "+offset+"  maxHeight: "+maxHeight+" height: "+height);
                        if (offset > height) {
                            //USBLog.d("scroll: "+(offset - usbReadValue.getHeight() + usbReadValue.getLineHeight()));
                            ((TextView)ActUsed.findViewById(R.id.receive_data)).scrollTo(0, offset - ((TextView)ActUsed.findViewById(R.id.receive_data)).getHeight() + ((TextView)ActUsed.findViewById(R.id.receive_data)).getLineHeight());
                        }


                    }





                    /*
                    Object obj = null;
                    ByteArrayInputStream bin = null;
                    ObjectInputStream ois = null;
                    try {
                        bin = new ByteArrayInputStream(Temp);
                        ois = new ObjectInputStream(bin);
                        obj = ois.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    */

                    ((Button)ActUsed.findViewById(R.id.write)).setClickable(true);

                    if (Rectimer != null && Rectimertask !=null)
                    {
                        Rectimer.cancel();
                        Rectimer = null;
                        Rectimertask.cancel();
                        Rectimertask = null;
                    }

                }
            };

            if (Rectimer != null && Rectimertask !=null)
            {
                Rectimer.schedule(Rectimertask,300);//200毫秒后停止
                ((Button)ActUsed.findViewById(R.id.write)).setClickable(false);
            }

        }
         else
         {
             //如果当上一个定时器还没有释放，就进行了新的modbus命令，需要进行容错处理
         }



    }

}


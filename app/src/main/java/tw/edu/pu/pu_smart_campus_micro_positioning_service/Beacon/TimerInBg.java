package tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class TimerInBg{
    private boolean TimerStarted;
    private CountDownTimer countDownTimer;
    private long TimeLeft;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String str = bundle.getString("Key");


        }
    };

    public TimerInBg(Boolean start, int Sec) {
        TimeLeft = Sec;
        TimerStarted = start;
    }

    public void Timer(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(TimeLeft, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        TimeLeft = millisUntilFinished;
                    }

                    @Override
                    public void onFinish() {
                        String str = "請到安全通道方能使用此功能";

                    }
                }.start();
            }
        };
        if(TimerStarted){
            runnable.run();
        }
        else {
            try {
                runnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDownTimer.cancel();
        }
    }
}
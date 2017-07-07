package com.appjam.assist.assist.team;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appjam.assist.assist.BaseActivity;
import com.appjam.assist.assist.R;
import com.appjam.assist.assist.model.response.ResultMessage;
import com.appjam.assist.assist.network.ApplicationController;
import com.appjam.assist.assist.network.NetworkService;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gominju on 2017. 6. 27..
 */

public class AddScheduleFragment extends Fragment {
    private View v;
    private Button done;
    private TimePicker timePicker;
    private EditText et_place, et_other, et_content;
    private String where, who, when, what;
    private String date;
    private TextView record_date;
    private NetworkService networkService;
    private int team_id;
    private LinearLayout layout;

    public AddScheduleFragment newInstance(String input_date) {
        Bundle args = new Bundle();
        args.putSerializable("date", input_date);

        AddScheduleFragment fragment = new AddScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_add_schedule, container, false);
        BaseActivity.setGlobalFont(getContext(), getActivity().getWindow().getDecorView());

        init();

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }

        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        team_id = preferences.getInt("team_id", 0);

        date = getArguments().getString("date");
        when = date.substring(0, 4) + "년 " + date.substring(5, 7) + "월 " + date.substring(8, 10) + "일";
        record_date.setText(when);


        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO 빈 내용 처리하기
                where = et_place.getText().toString();
                who = et_other.getText().toString();
                what = et_content.getText().toString();
                if (where.equals("")) {
                    Toast.makeText(getContext(), "장소를 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else if (who.equals("")) {
                    Toast.makeText(getContext(), "상대팀을 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else if (what.equals("")) {
                    Toast.makeText(getContext(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    saveSchedule();
                }

            }
        });
        return v;
    }

    private void saveSchedule() {
        networkService = ApplicationController.getInstance().getNetworkService();

        Schedule schedule = new Schedule();
        schedule.setGame_dt(date + " " + timePicker.getCurrentHour() + "시 " + timePicker.getCurrentMinute() + "분");
        schedule.setAgainst_team(who);
        schedule.setPlace(where);
        schedule.setMessage(what);

        Call<ResultMessage> result = networkService.addSchedule(team_id, schedule);
        result.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(Call<ResultMessage> call, Response<ResultMessage> response) {
                if (response.isSuccessful()) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new ScheduleFragment());
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onFailure(Call<ResultMessage> call, Throwable t) {
            }
        });

    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void init() {
        done = (Button) v.findViewById(R.id.btn_done);
        timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        et_place = (EditText) v.findViewById(R.id.et_place);
        et_other = (EditText) v.findViewById(R.id.et_other);
        et_content = (EditText) v.findViewById(R.id.et_content);
        record_date = (TextView) v.findViewById(R.id.tv_date);

        Calendar rightNow = Calendar.getInstance();// 날짜 불러오는 함수
        int cur_minite = rightNow.get(Calendar.MINUTE);
        int cur_hour = rightNow.get(Calendar.HOUR);
        timePicker.setCurrentHour(cur_hour);
        timePicker.setCurrentMinute(cur_minite);
        layout = (LinearLayout)v.findViewById(R.id.lin);
    }
}

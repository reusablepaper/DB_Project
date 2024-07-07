package org.techtown.databasetest;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ResultListFragment extends Fragment {
    private ListView listView;
    String collage = "";
    String building = "";
    String dayOfWeek = "";
    String startTime = "";
    String endTime = "";
    String timeSlotId = "";
    String activityName="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_list, container, false);



        // ListView 초기화
        listView = view.findViewById(R.id.ListView);

        ArrayList<String> dataList = new ArrayList<>();
        ArrayList<String> timeSlotIds = new ArrayList<>();
        ArrayList<Integer> secIds = new ArrayList<>();
        ArrayList<Integer> usedRoomIds = new ArrayList<>();



        // adapter 생성
        ArrayAdapter<String> adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataList);

        //searchResult->this 데이터 받아오기
        getBundle();
        if(startTime.length()==4)
            startTime="0"+startTime;
        if(endTime.length()==4)
            endTime="0"+endTime;

        getTimeSlotId(timeSlotIds);

        getSecId(timeSlotIds,secIds);//schedule : day_of_week, time_slot_id 를 포함하는 sec_id


        if(!timeSlotIds.isEmpty()) {

            getUsedRoomId(secIds, usedRoomIds); //section : sec_id -> usedRoomIds


           getUnusedRoomName(usedRoomIds, dataList); //collage&building – usedRoomIds -> room_id

        }
        if(activityName.equals("NearActivity")){
            //여기에 nearactivity에게 dataList가 isempty인지 아닌지 전달하는 코드 작성

        }
        listView.setAdapter(adapter);
        return view;


    }


    private void getTimeSlotId(ArrayList<String> timeSlotIds) {
        Cursor cursor = MainActivity.database.rawQuery(
                "SELECT time_slot_id FROM time_slot WHERE start_time >=? AND start_time < ? ",
                new String[]{startTime, endTime});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 데이터 처리
                timeSlotId = cursor.getString(cursor.getColumnIndex("time_slot_id"));

                timeSlotIds.add(timeSlotId);
            }

            cursor.close();
        }
    }

    private void getSecId(ArrayList<String> timeSlotIds, ArrayList<Integer> secIds) {
        StringBuilder timeSlotIdString = new StringBuilder();
        for (int i = 0; i < timeSlotIds.size(); i++) {
            timeSlotIdString.append("'");
            timeSlotIdString.append(timeSlotIds.get(i));
            timeSlotIdString.append("'");
            if (i < timeSlotIds.size() - 1) {
                timeSlotIdString.append(",");
            }
        }

        Cursor cursor = MainActivity.database.rawQuery(
                "SELECT DISTINCT sec_id FROM schedule WHERE day_of_week = ? AND time_slot_id IN (" + timeSlotIdString.toString() + ")",
                new String[]{dayOfWeek});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int secId = cursor.getInt(cursor.getColumnIndex("sec_id"));
                secIds.add(secId);
            }
            cursor.close();
        }

    }
    private void getUsedRoomId(ArrayList<Integer> secIds, ArrayList<Integer> usedRoomIds) {
        for (int secId : secIds) {
            Cursor cursor = MainActivity.database.rawQuery(
                    "SELECT room_id FROM section WHERE sec_id = ?",
                    new String[]{String.valueOf(secId)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int roomId = cursor.getInt(cursor.getColumnIndex("room_id"));
                    usedRoomIds.add(roomId);
                }
                cursor.close();
            }
        }
    }

    private void getUnusedRoomName(ArrayList<Integer> usedRoomIds, ArrayList<String> dataList) {
        // 해당 요일과 시간에 사용되는 room_id 가져오기
        Set<Integer> usedRoomIdSet = new HashSet<>(usedRoomIds);

        // 선택한 collage와 building을 가지고 있는 room_id와 room_name 추출
        Cursor cursor = MainActivity.database.rawQuery(
                "SELECT room_name, room_id  FROM classroom WHERE collage=? AND building=? ",
                new String[]{collage, building});


        if (cursor != null) {
            while (cursor.moveToNext()) {
                int roomId = cursor.getInt(cursor.getColumnIndex("room_id"));
                String roomName = cursor.getString(cursor.getColumnIndex("room_name"));
                // usedRoomIds와 겹치지 않는 경우 dataList에 추가
                if (!usedRoomIdSet.contains(roomId)) {
                    dataList.add(collage + building + " " + roomName);
                }
            }
            cursor.close();
        }
    }


    private void getBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            collage = bundle.getString("collage");  //단대
            building = bundle.getString("building");    //건물
            dayOfWeek = bundle.getString("dayOfWeek");//요일
            startTime = bundle.getString("startTime");//시작 시간
            endTime = bundle.getString("endTime");//종료 시간
            activityName=bundle.getString("activityName");

        }

    }



}

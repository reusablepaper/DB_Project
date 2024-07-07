package org.techtown.databasetest;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {
    private Spinner collageSpinner;//단대 드롭다운
    private TextView collageTextView;//단대 드롭다운 결과
    String collageText;
    private TextView buildingTextView;//호관 드롭다운 결과
    private Spinner buildingSpinner;//호관 드롭다운
    String buildingText;

    EditText startTimeEditText;
    EditText endTimeEditText;
    Switch startTimeExactSwitch;
    Switch endTimeExactSwitch;

    Date selectedDate;


    Button dateButton;
    Button nextButton;
    Button backButton;

    SimpleDateFormat dateFormat=MainActivity.dateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        collageSpinner = (Spinner) findViewById(R.id.collageSpinner);
        collageTextView = (TextView) findViewById(R.id.tv_test);
        buildingSpinner = (Spinner) findViewById(R.id.buildingSpinner);
        buildingTextView = (TextView) findViewById(R.id.tv_test2);

        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backToMainButton);
        dateButton=findViewById(R.id.dateButton);

        startTimeEditText = findViewById(R.id.StartTimeEditText);
        startTimeExactSwitch=findViewById(R.id.StartTimeExactSwitch);
        startTimeExactSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!startTimeExactSwitch.isChecked())
                    startTimeExactSwitch.setText(":30");
                else startTimeExactSwitch.setText(":00");
            }
        });


        endTimeEditText=findViewById(R.id.EndTimeEditText);
        endTimeExactSwitch=findViewById(R.id.EndTimeExactSwitch);
        endTimeExactSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!endTimeExactSwitch.isChecked())
                    endTimeExactSwitch.setText(":30");
                else endTimeExactSwitch.setText(":00");
            }

        });

        collageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collageText =parent.getItemAtPosition(position).toString();
                collageTextView.setText(collageText);
                ArrayAdapter<CharSequence> adapter;
                if (collageText.equals("간호대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.간호대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("공과대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.공대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("농업생명과학대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.농생대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("사범대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.사범대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("사회과학대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.사과대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("상과대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.상대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("생활과학대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.생과대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("예술대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.예대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("의과대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.의대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("인문대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.인문대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("자연과학대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.자과대, android.R.layout.simple_spinner_item);
                } else if (collageText.equals("치과대학")) {
                    adapter = ArrayAdapter.createFromResource(SearchActivity.this,
                            R.array.치대, android.R.layout.simple_spinner_item);
                } else {
                    adapter = null;
                }

                if (adapter != null) {
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    buildingSpinner.setAdapter(adapter);
                } else {
                    buildingSpinner.setAdapter(null);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                collageTextView.setText("단과 대학을 선택해 주세요");
            }
        });

        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                buildingText =parent.getItemAtPosition(position).toString();
                buildingTextView.setText(buildingText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                buildingTextView.setText("호관을 선택해 주세요");
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dayOfWeek=getDayOfWeek(selectedDate);

                String startTime= startTimeEditText.getText().toString();
                String endTime=endTimeEditText.getText().toString();

                if(dayOfWeek.equals("일"))
                    toast("일요일은 검색이 불가능합니다");

                else if(startTime.isEmpty())
                    toast("시작 시간을 입력하세요");

                else if(Integer.parseInt(startTime)<8 || Integer.parseInt(startTime)>21)
                    toast("유효한 시작 시간을 입력하세요");

                else if(endTime.isEmpty())
                    toast("종료 시작을 입력하세요.");

                else if(Integer.parseInt(endTime)<8 || Integer.parseInt(endTime)>21)
                    toast("유효한 종료 시간을 입력하세요");

                else if(Integer.parseInt(startTime)>Integer.parseInt((endTime)))
                    toast("시작 시간과 종료 시간을 확인해 주세요");

                else {

                    startTime=startTime+(startTimeExactSwitch.isChecked()?":00":":30");
                    endTime=endTime+(endTimeExactSwitch.isChecked()?":00":":30");

                    Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                    intent.putExtra("collage", collageText); //단대
                    intent.putExtra("building", buildingText); //건물
                    intent.putExtra("dayOfWeek", dayOfWeek); //요일
                    intent.putExtra("startTime", startTime); //시작 시간
                    intent.putExtra("endTime",endTime); //종료 시간
                    intent.putExtra("date",selectedDate);
                    startActivity(intent);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Date curDate = new Date();
        setSelectedDate(curDate);

    }

    private void toast(String data) {
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }

    private String getDayOfWeek(Date selectedDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "일";
            case Calendar.MONDAY:
                return "월";
            case Calendar.TUESDAY:
                return "화";
            case Calendar.WEDNESDAY:
                return "수";
            case Calendar.THURSDAY:
                return "목";
            case Calendar.FRIDAY:
                return "금";
            case Calendar.SATURDAY:
                return "토";
            default:
                return "오류";
        }
    }
    private void showDateDialog() {
        String birthDay = dateButton.getText().toString();

        Calendar calendar = Calendar.getInstance();
        Date newDay = new Date();
        try {
            newDay = dateFormat.parse(birthDay);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        calendar.setTime(newDay);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener,  year, month, day);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(Calendar.YEAR, year);
            selectedCalendar.set(Calendar.MONTH, monthOfYear);
            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            Date curDate = selectedCalendar.getTime();
            setSelectedDate(curDate);
        }
    };

    private void setSelectedDate(Date today) {
        selectedDate = today;

        String selectedDateStr = dateFormat.format(today);
        dateButton.setText(selectedDateStr);
    }


}
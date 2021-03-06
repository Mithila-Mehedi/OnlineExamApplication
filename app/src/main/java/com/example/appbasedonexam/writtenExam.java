package com.example.appbasedonexam;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class writtenExam extends AppCompatActivity {

    TextView question, timer;
    EditText answer;
    Button submit,back,next;
    DatabaseReference databaseReference;
    int total = 0;
    private Object student;
    ImageView calculator;
    private String roll, courseNo, date, year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_written_exam);

        roll = getIntent().getStringExtra("roll");
        courseNo = getIntent().getStringExtra("courseNo");
        date = getIntent().getStringExtra("date");
        year = getIntent().getStringExtra("year");
        databaseReference = FirebaseDatabase.getInstance().getReference();


        calculator=findViewById(R.id.calculator);
        question = findViewById(R.id.question1);
        timer = findViewById(R.id.timerText);
        answer = findViewById(R.id.answer);
        submit = findViewById(R.id.submit1);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        back=findViewById(R.id.back);

        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(writtenExam.this,ScientificCalculator.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(false);
            }
        });
        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(true);
            }
        });

        updateQuestion(true);
        ReverseTimer(30, timer);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

    }

    private void upload() {
        databaseReference.child("Written Question").child("Sheet1").child(String.valueOf(total)).child(roll).setValue(answer.getText().toString());
    }

    public void saveData() {
        String name = answer.getText().toString().trim();
        student student = new student(name);
        databaseReference.child(roll).setValue(student);
        databaseReference.child(courseNo).setValue(student);
        databaseReference.child(year).setValue(student);
        databaseReference.child(date).setValue(student);
        updateQuestion(true);
    }


    public void updateQuestion(final boolean value) {
        if (total != 0) {
            upload();
        }
        answer.setText("");
        System.out.println(total);
        if (total > 5) {
            Intent i = new Intent(writtenExam.this, ResultActivity.class);
            startActivity(i);
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Written Question").child("Sheet1").child(String.valueOf(total));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String string = dataSnapshot.child("question").getValue().toString();
                        System.out.println(string);
                        if (question!= null) {
                            question.setText(string);
                            if (value) total++;
                            else total--;
                        } else {
                            System.out.println("total=" + total);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void ReverseTimer(int seconds, final TextView tv) {
        new CountDownTimer(seconds* 1000 + 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds=(int) (millisUntilFinished/1000);
                int minutes= seconds%60;
                tv.setText(String.format("%02d",minutes)
                        + ":" + String.format("%02d",seconds));
            }

            @Override
            public void onFinish() {
                tv.setText("completed");
                Intent myintent = new Intent(writtenExam.this, complete.class);
                myintent.putExtra("total", String.valueOf(total));
                startActivity(myintent);

            }
        }.start();
    }
}
package com.lrt.molky.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.lrt.molky.model.capitales.GamePreference;
import com.lrt.molky.view.AlertDialogBScore;
import com.lrt.molky.view.AlertDialogHelp;
import com.lrt.molky.R;

// TODO Changer le nom a arcadeGame
// TODO Changer le logo de l'appli

public class MainActivity extends AppCompatActivity {

    private Button m_btnCapitales;
    private Button m_btnPositions;

    private CheckedTextView m_chkAfrique;
    private CheckedTextView m_chkAmerique;
    private CheckedTextView m_chkAsie;
    private CheckedTextView m_chkEurope;

    private CheckedTextView m_chkDiff1;
    private CheckedTextView m_chkDiff2;
    private CheckedTextView m_chkDiff3;

    private GamePreference m_gamePreference;

    private SharedPreferences m_sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_sharedPreferences = getPreferences(MODE_PRIVATE);

        m_btnCapitales = findViewById(R.id.activity_main_capitales);
        m_btnPositions = findViewById(R.id.activity_main_positions);
        m_chkAfrique = findViewById(R.id.activity_main_afrique);
        m_chkAmerique =  findViewById(R.id.activity_main_amerique);
        m_chkAsie = findViewById(R.id.activity_main_asie);
        m_chkEurope = findViewById(R.id.activity_main_europe);
        m_chkDiff1 = findViewById(R.id.activity_main_diff_1);
        m_chkDiff2 = findViewById(R.id.activity_main_diff_2);
        m_chkDiff3 = findViewById(R.id.activity_main_diff_3);

        //m_btnCapitales.setEnabled(m_txtName.getText().toString().length() !=0);
        //m_btnPositions.setEnabled(m_txtName.getText().toString().length() !=0);

        m_chkAfrique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_chkAfrique.isChecked()){
                    m_chkAfrique.setChecked(false);
                }else{
                    m_chkAfrique.setChecked(true);
                }
            }
        });

        m_chkAmerique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_chkAmerique.isChecked()){
                    m_chkAmerique.setChecked(false);
                }else{
                    m_chkAmerique.setChecked(true);
                }
            }
        });

        m_chkAsie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_chkAsie.isChecked()){
                    m_chkAsie.setChecked(false);
                }else{
                    m_chkAsie.setChecked(true);
                }
            }
        });

        m_chkEurope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_chkEurope.isChecked()){
                    m_chkEurope.setChecked(false);
                }else{
                    m_chkEurope.setChecked(true);
                }
            }
        });

        m_chkDiff1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_chkDiff1.isChecked()) {
                    m_chkDiff1.setChecked(true);
                    m_chkDiff2.setChecked(false);
                    m_chkDiff3.setChecked(false);
                }
            }
        });

        m_chkDiff2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_chkDiff2.isChecked()) {
                    m_chkDiff2.setChecked(true);
                    m_chkDiff1.setChecked(false);
                    m_chkDiff3.setChecked(false);
                }
            }
        });

        m_chkDiff3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_chkDiff3.isChecked()) {
                    m_chkDiff3.setChecked(true);
                    m_chkDiff2.setChecked(false);
                    m_chkDiff1.setChecked(false);
                }
            }
        });


        m_btnCapitales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_chkAfrique.isChecked()|| m_chkAmerique.isChecked()||
                        m_chkAsie.isChecked()|| m_chkEurope.isChecked()) {

                    m_gamePreference = new GamePreference();
                    m_gamePreference.setAfrique(m_chkAfrique.isChecked());
                    m_gamePreference.setAmerique(m_chkAmerique.isChecked());
                    m_gamePreference.setAsie(m_chkAsie.isChecked());
                    m_gamePreference.setEurope(m_chkEurope.isChecked());
                    if (m_chkDiff1.isChecked()) {
                        m_gamePreference.setDifficulty(1);
                    } else if (m_chkDiff2.isChecked()) {
                        m_gamePreference.setDifficulty(2);
                    } else {
                        m_gamePreference.setDifficulty(3);
                    }

                    Intent w_capitalesActivity = new Intent(MainActivity.this, CapitalesActivity.class);
                    w_capitalesActivity.putExtra("GamePreference", m_gamePreference);
                    startActivity(w_capitalesActivity);
                }
            }
        });


        m_btnPositions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_chkAfrique.isChecked()|| m_chkAmerique.isChecked()||
                        m_chkAsie.isChecked()|| m_chkEurope.isChecked()) {

                    m_gamePreference = new GamePreference();
                    m_gamePreference.setAfrique(m_chkAfrique.isChecked());
                    m_gamePreference.setAmerique(m_chkAmerique.isChecked());
                    m_gamePreference.setAsie(m_chkAsie.isChecked());
                    m_gamePreference.setEurope(m_chkEurope.isChecked());
                    if (m_chkDiff1.isChecked()) {
                        m_gamePreference.setDifficulty(1);
                    } else if (m_chkDiff2.isChecked()) {
                        m_gamePreference.setDifficulty(2);
                    } else {
                        m_gamePreference.setDifficulty(3);
                    }

                    Intent w_mapActivity = new Intent(MainActivity.this, MapActivity.class);
                    w_mapActivity.putExtra("GamePreference", m_gamePreference);
                    startActivity(w_mapActivity);
                }
            }
        });

        Button w_main2048 = findViewById(R.id.activity_main_2048);
        w_main2048.setEnabled(true);
        w_main2048.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent w_2048Activity = new Intent(MainActivity.this, C2048Activity.class);
                startActivity(w_2048Activity);
            }
        });


        Button w_mainLabyrinthe = findViewById(R.id.activity_main_labyrinthe);
        w_mainLabyrinthe.setEnabled(true);
        w_mainLabyrinthe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent w_labyrintheNiveaux = new Intent(MainActivity.this, LabyrintheNiveauxActivity.class);
                startActivity(w_labyrintheNiveaux);
            }
        });

        Button w_mainLab2048 = findViewById(R.id.activity_main_lab2048);
        w_mainLab2048.setEnabled(true);
        w_mainLab2048.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent w_mainLab2048 = new Intent(MainActivity.this, Lab2048Activity.class);
                startActivity(w_mainLab2048);
            }
        });

        Button w_mainMolky = findViewById(R.id.activity_main_molky);
        w_mainMolky.setEnabled(true);
        w_mainMolky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent w_molkyActivity = new Intent(MainActivity.this, MolkyActivity.class);
                startActivity(w_molkyActivity);
            }
        });
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_BScoreCap){
            AlertDialogBScore alertBScoreCap = new AlertDialogBScore(this,0);
            alertBScoreCap.show();

        }else if(item.getItemId() == R.id.menu_BScorePos){
            AlertDialogBScore alertBScoreCap = new AlertDialogBScore(this,1);
            alertBScoreCap.show();

        }else if(item.getItemId() == R.id.menu_help){
            AlertDialogHelp alertHelp = new AlertDialogHelp(this);
            alertHelp.show();
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

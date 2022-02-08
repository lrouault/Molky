package com.lrt.molky.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lrt.molky.R;
import com.lrt.molky.common.CommonMolkyEnum;
import com.lrt.molky.model.molky.MolkyGamePreference;
import com.lrt.molky.model.molky.MolkyJoueurBank;
import com.lrt.molky.model.molky.MolkyJoueurData;

import java.util.ArrayList;

public class MolkyActivity extends AppCompatActivity  {
    private static final String TAG = "MolkyActivity";

    // Sauvegarde du plateau a la fermeture de l'app
    private SharedPreferences m_sharedPreferences;

    // Model
    private MolkyJoueurBank m_joueurBank, m_previousJoueurBank, m_previousJoueurBank2;
    private MolkyGamePreference m_gamePref;

    // Gestion des deplacements des tuiles
    private GestureDetector m_gestureDetector;

    // Plateau de jeu (0123 / 4567 / 891011 / 12131415 )
    private TextView[] m_molkyBtn = new TextView[13];

    // Layout et boutons actionnables
    private Menu m_optionMenu;
    private TextView m_txtScore;
    private Button m_btnClear;
    private Button m_btnAddPlayer;
    private Button m_btnRestart;
    private Button m_btnUndo;

    private int m_nbUndo = 0;
    private int m_nbUndoMax = 2;


    /** onCreate()
     * Instanciation du jeu
     * Listener sur le layout
     * Button listener
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiation du plateau avec celle sauvegardee si existante
        m_sharedPreferences = getPreferences(MODE_PRIVATE);
        String w_strGamePref = m_sharedPreferences.getString("Molky_gamePref","");
        String w_strJoueurBank = m_sharedPreferences.getString("Molky_joueurBank","");
        Log.d(TAG,"GamePref   sauvegarde : "+w_strGamePref);
        Log.d(TAG,"JoueurBank sauvegarde : "+w_strJoueurBank);

        m_gamePref =  new MolkyGamePreference();
        if (w_strGamePref!="") {
            m_gamePref.parseString(w_strGamePref);
        }

        //TODO Serialize joueur bank
        m_joueurBank = new MolkyJoueurBank(m_gamePref);
        if (w_strJoueurBank!="") {
            m_joueurBank.parseString(w_strJoueurBank);
        }
        m_previousJoueurBank = new MolkyJoueurBank(m_joueurBank);



        setContentView(R.layout.activity_molky);

        // Instantiation du plateau avec celle sauvegardee si existante
        m_sharedPreferences = getPreferences(MODE_PRIVATE);

        // Cablage de la vue
        m_txtScore = findViewById(R.id.activity_molky_txtScore);
        m_btnClear = findViewById(R.id.activity_molky_btnClear);
        m_btnAddPlayer = findViewById(R.id.activity_molky_btnAddPlayer);
        m_btnRestart = findViewById(R.id.activity_molky_btnRestart);
        m_btnUndo = findViewById(R.id.activity_molky_btnUndo);
        m_btnUndo.setText("UNDO ("+m_nbUndo+")");

        int i = 0;
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn00);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn01);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn02);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn03);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn04);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn05);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn06);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn07);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn08);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn09);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn10);
        m_molkyBtn[i++] = findViewById(R.id.activity_molky_btn11);
        m_molkyBtn[i]   = findViewById(R.id.activity_molky_btn12);

        for (int w_i = 0; w_i < 13; w_i++) {
            final int finalW_i = w_i;
            Log.e(TAG, "onCreate: valeur de ai_lancer : "+finalW_i );
            m_molkyBtn[w_i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_joueurBank.getSomeoneHasWon()) {
                        _showDialogRestart(true);
                    } else {
                        _svgPreviousState();
                        m_joueurBank.addLancer(finalW_i);
                        _majAffichage();
                        if (m_joueurBank.getSomeoneHasWon()) {
                            _showDialogRestart(true);
                        }
                    }
                }
            });

        }

        // AddPlayer
        m_btnAddPlayer.setEnabled(true);
        m_btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDialogAddPlayer();
            }
        });

        // Clear
        m_btnClear.setEnabled(true);
        m_btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDialogClear();
            }
        });

        // Restart
        m_btnRestart = findViewById(R.id.activity_molky_btnRestart);
        m_btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDialogRestart(false);
            }
        });

        // Undo
        m_btnUndo.setEnabled(true);
        m_btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: UNDO" );
                m_joueurBank = m_previousJoueurBank;
                m_previousJoueurBank = m_previousJoueurBank2;
                if (m_nbUndo > 0) {
                    m_nbUndo--;
                }
                m_btnUndo.setText("UNDO ("+m_nbUndo+")");
                _majAffichage();
            }
        });

        _majAffichage();
    } // END onCreate()

    private void _showDialogAddPlayer() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);
        w_text.setText("Nom du joueur ?");

        final EditText w_edit =new EditText(this);
        w_edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        w_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                w_edit.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(w_edit, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        w_edit.requestFocus();

        final AlertDialog.Builder w_alert = new AlertDialog.Builder(this);
        LinearLayout w_linear=new LinearLayout(this);
        w_linear.setOrientation(LinearLayout.VERTICAL);
        w_linear.addView(w_text);
        w_linear.addView(w_edit);

        w_alert.setTitle("Add player");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                _svgPreviousState();
                m_joueurBank.addJoueur(w_edit.getText().toString());
                _majAffichage();
            }
        });
        w_alert.show();
    }

    private void _showDialogRestart(boolean ai_hasWon) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        _svgPreviousState();
                        m_joueurBank.restart();
                        _majAffichage();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (ai_hasWon) {
            builder.setMessage("Someone has won. Do you want to restart?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } else {
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    private void _showDialogClear() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        _svgPreviousState();
                        m_joueurBank.clearJoueurs();
                        _majAffichage();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void _svgPreviousState() {
        m_previousJoueurBank2 = new MolkyJoueurBank(m_previousJoueurBank);
        m_previousJoueurBank = new MolkyJoueurBank(m_joueurBank);
        if (m_nbUndo < m_nbUndoMax) {
            m_nbUndo++;
        }
        m_btnUndo.setText("UNDO ("+m_nbUndo+")");
    }

    private void _majAffichage() {
        Log.e(TAG, "_majAffichage: " );
        ArrayList<MolkyJoueurData> w_data = m_joueurBank.getData();

        LinearLayout w_scoreLayout = (LinearLayout) findViewById(R.id.activity_molky_scoreLayout);
        w_scoreLayout.removeAllViewsInLayout();

        for (MolkyJoueurData w_joueur : w_data) {
            View w_molky_score = getLayoutInflater()
                    .inflate(R.layout.molky_score, w_scoreLayout, false);
            // Nom
            TextView w_txtName = w_molky_score.findViewById(R.id.molky_score_name);
            w_txtName.setText(w_joueur.m_name);
            if (w_joueur.m_myTurn) {
                w_txtName.setTextColor(getResources().getColor(R.color.txt2048_OK));
            }
            // Score
            TextView w_txtActualScore = w_molky_score.findViewById(R.id.molky_score_actualScore);
            w_txtActualScore.setText(""+w_joueur.m_actualScore);
            // Score
            TextView w_txtPreviousScore = w_molky_score.findViewById(R.id.molky_score_previousScore);
            w_txtPreviousScore.setText(""+w_joueur.m_previousScore);
            // Position
            TextView w_txtPosition = w_molky_score.findViewById(R.id.molky_score_position);
            w_txtPosition.setText(""+w_joueur.m_position);

            if ((w_joueur.m_nbZero > 0)
                    || ((m_gamePref.m_nbZeroMax > 1)
                    && ((m_gamePref.m_nbZeroMax - w_joueur.m_nbZero) == 1))) {
                TextView w_txtNbZero = w_molky_score.findViewById(R.id.molky_score_nbZero);
                w_txtNbZero.setText(w_joueur.m_nbZero+"/"+m_gamePref.m_nbZeroMax);
            }
            if (  (    (m_gamePref.m_nbZeroMax == 2)
                    && (w_joueur.m_nbZero == 1))
                ||(    (w_joueur.m_nbZero >= 1))
                    && (m_gamePref.m_nbZeroMax > 2)
                    && (m_gamePref.m_nbZeroMax-w_joueur.m_nbZero <= 2)) {
                TextView w_txtFirstMiss = w_molky_score.findViewById(R.id.molky_score_firstMiss);
                w_txtFirstMiss.setBackgroundResource(R.drawable.redcross);
            }
            if (w_joueur.m_nbZero >= 1
                    && (m_gamePref.m_nbZeroMax >= 3)
                    && ((m_gamePref.m_nbZeroMax - w_joueur.m_nbZero) <= 1)) {
                TextView w_txtFirstMiss = w_molky_score.findViewById(R.id.molky_score_firstMiss);
                w_txtFirstMiss.setBackgroundResource(R.drawable.redcross);
                TextView w_txtSecondMiss = w_molky_score.findViewById(R.id.molky_score_secondMiss);
                w_txtSecondMiss.setBackgroundResource(R.drawable.redcross);
            }
            w_scoreLayout.addView(w_molky_score);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_molky_option, menu);
        this.m_optionMenu = menu;
        _majTxtOptionMenu();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_MolkyOptionScoreFinal:
                _showDialogScoreFinal();
                break;
            case R.id.menu_MolkyOptionScoreFinalAction:
                _showDialogScoreFinalAction();
                break;
            case R.id.menu_MolkyOptionNbZero:
                _showDialogNbZero();
                break;
            case R.id.menu_MolkyOptionNbZeroAction:
                _showDialogNbZeroAction();
                break;
            case R.id.menu_MolkyOptionChuteEgalite:
                _showDialogEquality();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void _showDialogScoreFinal() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(9);
        w_seek.setProgress(m_gamePref.m_finalScore/10 - 1);
        w_text.setText("Apres validation "+ m_gamePref.m_finalScore +" (defaut : "+ MolkyGamePreference.C_DEFAULT_FINALSCORE +")");
        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                w_text.setText("Apres validation :"+ ((progress+1)*10)+" (defaut : "+ MolkyGamePreference.C_DEFAULT_FINALSCORE +")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final AlertDialog.Builder w_alert = new AlertDialog.Builder(this);
        LinearLayout w_linear=new LinearLayout(this);
        w_linear.setOrientation(LinearLayout.VERTICAL);
        w_linear.addView(w_seek);
        w_linear.addView(w_text);

        w_alert.setTitle("Choix du score final :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                m_gamePref.m_finalScore = ((w_seek.getProgress()+1)*10);
                _majTxtOptionMenuFinalScore();
            }
        });
        w_alert.show();
    }

    private void _showDialogScoreFinalAction() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(2);
        w_text.setText("Apres validation : "+m_gamePref.m_passScore+" (defaut : "+ MolkyGamePreference.C_DEFAULT_PASSSCORE +")");
        w_seek.setProgress(m_gamePref.m_passScore.ordinal());

        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                w_text.setText("Apres validation : "+CommonMolkyEnum.PassFinalScoreEnum.values()[progress].toString()+" (defaut : "+ MolkyGamePreference.C_DEFAULT_PASSSCORE +")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final AlertDialog.Builder w_alert = new AlertDialog.Builder(this);
        LinearLayout w_linear=new LinearLayout(this);
        w_linear.setOrientation(LinearLayout.VERTICAL);
        w_linear.addView(w_seek);
        w_linear.addView(w_text);

        w_alert.setTitle("Action sur score trop grand :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                MenuItem w_passScoreItem = m_optionMenu.findItem(R.id.menu_MolkyOptionScoreFinalAction);
                m_gamePref.m_passScore = CommonMolkyEnum.PassFinalScoreEnum.values()[w_seek.getProgress()];
                _majTxtOptionMenuPassScore();
            }
        });
        w_alert.show();
    }

    private void _showDialogNbZero() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(5);
        w_seek.setProgress(m_gamePref.m_nbZeroMax);
        w_text.setText("Apres validation "+ m_gamePref.m_nbZeroMax +" (defaut : "+ MolkyGamePreference.C_DEFAULT_NBZEROMAX +")");
        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                w_text.setText("Apres validation : "+ progress +" (defaut : "+ MolkyGamePreference.C_DEFAULT_NBZEROMAX +")");
                if (progress == 0) {
                    w_text.setText("Apres validation : RAS (defaut : "+ MolkyGamePreference.C_DEFAULT_NBZEROMAX +")");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final AlertDialog.Builder w_alert = new AlertDialog.Builder(this);
        LinearLayout w_linear=new LinearLayout(this);
        w_linear.setOrientation(LinearLayout.VERTICAL);
        w_linear.addView(w_seek);
        w_linear.addView(w_text);

        w_alert.setTitle("Choix du nombre de zero avant action :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                m_gamePref.m_nbZeroMax = w_seek.getProgress();
                m_gamePref.m_isFallingOnZeroActivated = (0 != m_gamePref.m_nbZeroMax);
                _majTxtOptionMenuNbZeroMax();
            }
        });
        w_alert.show();
    }

    private void _showDialogNbZeroAction() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(2);

        w_text.setText("Apres validation : "+m_gamePref.m_passZero+" (defaut : "+ MolkyGamePreference.C_DEFAULT_PASSSZERO +")");
        w_seek.setProgress(m_gamePref.m_passZero.ordinal());

        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                w_text.setText("Apres validation : "+CommonMolkyEnum.PassNbZeroMaxEnum.values()[progress]+" (defaut : "+ MolkyGamePreference.C_DEFAULT_PASSSZERO +")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final AlertDialog.Builder w_alert = new AlertDialog.Builder(this);
        LinearLayout w_linear=new LinearLayout(this);
        w_linear.setOrientation(LinearLayout.VERTICAL);
        w_linear.addView(w_seek);
        w_linear.addView(w_text);

        w_alert.setTitle("Action sur trop de ratés :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                m_gamePref.m_passZero = CommonMolkyEnum.PassNbZeroMaxEnum.values()[w_seek.getProgress()];
                _majTxtOptionMenuNbZeroAction();
            }
        });
        w_alert.show();
    }

    private void _showDialogEquality() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(1);
        w_text.setText("Chute si égalité : "+m_gamePref.m_isFallingOnEqualityActivated+" (defaut : "+ MolkyGamePreference.C_DEFAULT_ISFALLINGEQUALITY +")");
        w_seek.setProgress(m_gamePref.m_isFallingOnEqualityActivated? 1 : 0);

        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                boolean w_tmp = (0 != progress);
                w_text.setText("Apres validation : "+ w_tmp +" (defaut : "+ MolkyGamePreference.C_DEFAULT_ISFALLINGEQUALITY +")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final AlertDialog.Builder w_alert = new AlertDialog.Builder(this);
        LinearLayout w_linear=new LinearLayout(this);
        w_linear.setOrientation(LinearLayout.VERTICAL);
        w_linear.addView(w_seek);
        w_linear.addView(w_text);

        w_alert.setTitle("Chute si on atteint un score adverse :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok", (dialog, id) -> {
            m_gamePref.m_isFallingOnEqualityActivated = (0 != w_seek.getProgress());
            _majTxtOptionMenuEquality();
        });
        w_alert.show();
    }

    private void _majTxtOptionMenu() {
        _majTxtOptionMenuFinalScore();
        _majTxtOptionMenuPassScore();
        _majTxtOptionMenuNbZeroMax();
        _majTxtOptionMenuNbZeroAction();
        _majTxtOptionMenuEquality();
    }

    private void _majTxtOptionMenuFinalScore() {
        MenuItem w_finalScoreItem = m_optionMenu.findItem(R.id.menu_MolkyOptionScoreFinal);
        w_finalScoreItem.setTitle("Score final : "+m_gamePref.m_finalScore);
    }

    private void _majTxtOptionMenuPassScore() {
        MenuItem w_passScoreItem = m_optionMenu.findItem(R.id.menu_MolkyOptionScoreFinalAction);
        w_passScoreItem.setTitle("-- Action si depasse : " + m_gamePref.m_passScore.toString());
    }

    private void _majTxtOptionMenuNbZeroMax() {
        MenuItem w_nbZeroMaxItem = m_optionMenu.findItem(R.id.menu_MolkyOptionNbZero);
        if (m_gamePref.m_isFallingOnZeroActivated) {
            w_nbZeroMaxItem.setTitle("Nombre de zero max : "+m_gamePref.m_nbZeroMax);
        } else {
            w_nbZeroMaxItem.setTitle("Nombre de zero max : RAS");
        }
    }

    private void _majTxtOptionMenuNbZeroAction() {
        MenuItem w_nbZeroActionItem = m_optionMenu.findItem(R.id.menu_MolkyOptionNbZeroAction);
        w_nbZeroActionItem.setTitle("-- Action si depasse : " + m_gamePref.m_passZero.toString());
    }

    private void _majTxtOptionMenuEquality() {
        MenuItem w_equalityItem = m_optionMenu.findItem(R.id.menu_MolkyOptionChuteEgalite);
        w_equalityItem.setTitle("Chutes si egalite : " + m_gamePref.m_isFallingOnEqualityActivated.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_sharedPreferences.edit().putString("Molky_gamePref", m_gamePref.toString()).apply();
        m_sharedPreferences.edit().putString("Molky_joueurBank", m_joueurBank.toString()).apply();
    }
}

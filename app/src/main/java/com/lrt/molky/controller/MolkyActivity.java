package com.lrt.molky.controller;

import android.app.AlertDialog;
import android.content.ClipData;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lrt.molky.R;
import com.lrt.molky.common.CommonEnum;
import com.lrt.molky.common.CommonMolkyEnum;
import com.lrt.molky.model.Game2048;
import com.lrt.molky.model.molky.MolkyGamePreference;
import com.lrt.molky.model.molky.MolkyJoueurBank;
import com.lrt.molky.model.molky.MolkyJoueurData;
import com.lrt.molky.view.OnSwipeListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Math.pow;

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

        m_gamePref = new MolkyGamePreference();
        m_joueurBank = new MolkyJoueurBank(m_gamePref);
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

            if (w_joueur.m_nbZero == 1) {
                TextView w_txtFirstMiss = w_molky_score.findViewById(R.id.molky_score_firstMiss);
                w_txtFirstMiss.setBackgroundResource(R.drawable.redcross);
            } else if (w_joueur.m_nbZero == 2) {
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
        w_text.setText("Apres validation "+ m_gamePref.m_finalScore +" (defaut : 40)");
        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                w_text.setText("Apres validation :"+ ((progress+1)*10)+" (defaut : 40)");
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
                MenuItem w_finalScoreItem = m_optionMenu.findItem(R.id.menu_MolkyOptionScoreFinal);
                w_finalScoreItem.setTitle("Score final : "+m_gamePref.m_finalScore);
            }
        });
        w_alert.show();
    }

    private void _showDialogScoreFinalAction() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(2);
        if (m_gamePref.m_passScore == CommonMolkyEnum.PassFinalScoreEnum.E_ZERO) {
            w_text.setText("Apres validation : ZERO (defaut : ZERO)");
            w_seek.setProgress(0);
        }else if (m_gamePref.m_passScore == CommonMolkyEnum.PassFinalScoreEnum.E_HALF) {
            w_text.setText("Apres validation : HALF (defaut : HALF)");
            w_seek.setProgress(1);
        }else {
            w_text.setText("Apres validation : WON (defaut : WON)");
            w_seek.setProgress(2);
        }
        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    w_text.setText("Apres validation : ZERO (defaut : ZERO)");
                }else if (progress == 1) {
                    w_text.setText("Apres validation : HALF (defaut : ZERO)");
                }else {
                    w_text.setText("Apres validation : WON (defaut : ZERO)");
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

        w_alert.setTitle("Action sur score trop grand :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                MenuItem w_passScoreItem = m_optionMenu.findItem(R.id.menu_MolkyOptionScoreFinalAction);
                if (w_seek.getProgress() == 0) {
                    w_passScoreItem.setTitle("-- Action si depasse : ZERO");
                    m_gamePref.m_passScore = CommonMolkyEnum.PassFinalScoreEnum.E_ZERO;
                }else if (w_seek.getProgress() == 1) {
                    w_passScoreItem.setTitle("-- Action si depasse : HALF");
                    m_gamePref.m_passScore = CommonMolkyEnum.PassFinalScoreEnum.E_HALF;
                }else {
                    w_passScoreItem.setTitle("-- Action si depasse : WON");
                    m_gamePref.m_passScore = CommonMolkyEnum.PassFinalScoreEnum.E_WON;
                }
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
        w_text.setText("Apres validation "+ m_gamePref.m_nbZeroMax +" (defaut : 40)");
        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                w_text.setText("Apres validation : "+ progress +" (defaut : 3)");
                if (progress == 0) {
                    w_text.setText("Apres validation : RAS (defaut : 3)");
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
                MenuItem w_nbZeroMaxItem = m_optionMenu.findItem(R.id.menu_MolkyOptionNbZero);
                w_nbZeroMaxItem.setTitle("Nombre de zero max : 3"+m_gamePref.m_nbZeroMax);
                if (0 == w_seek.getProgress()) {
                    m_gamePref.m_isFallingOnZeroActivated = false;
                    w_nbZeroMaxItem.setTitle("Nombre de zero max : RAS");
                } else {
                    m_gamePref.m_isFallingOnZeroActivated = true;
                    w_nbZeroMaxItem.setTitle("Nombre de zero max : "+m_gamePref.m_nbZeroMax);
                }
            }
        });
        w_alert.show();
    }

    private void _showDialogNbZeroAction() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(2);
        if (m_gamePref.m_passZero == CommonMolkyEnum.PassNbZeroMaxEnum.E_ZERO) {
            w_text.setText("Apres validation : ZERO (defaut : ZERO)");
            w_seek.setProgress(0);
        }else if (m_gamePref.m_passZero == CommonMolkyEnum.PassNbZeroMaxEnum.E_LAST) {
            w_text.setText("Apres validation : LAST (defaut : ZERO)");
            w_seek.setProgress(1);
        }else {
            w_text.setText("Apres validation : HALF (defaut : ZERO)");
            w_seek.setProgress(2);
        }
        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    w_text.setText("Apres validation : ZERO (defaut : ZERO)");
                }else if (progress == 1) {
                    w_text.setText("Apres validation : LAST (defaut : ZERO)");
                }else {
                    w_text.setText("Apres validation : HALF (defaut : ZERO)");
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

        w_alert.setTitle("Action sur trop de ratés :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {

                MenuItem w_nbZeroActionItem = m_optionMenu.findItem(R.id.menu_MolkyOptionNbZeroAction);
                if (w_seek.getProgress() == 0) {
                    w_nbZeroActionItem.setTitle("-- Action si depasse : ZERO");
                    m_gamePref.m_passZero = CommonMolkyEnum.PassNbZeroMaxEnum.E_ZERO;
                }else if (w_seek.getProgress() == 1) {
                    w_nbZeroActionItem.setTitle("-- Action si depasse : LAST");
                    m_gamePref.m_passZero = CommonMolkyEnum.PassNbZeroMaxEnum.E_LAST;
                }else {
                    w_nbZeroActionItem.setTitle("-- Action si depasse : HALF");
                    m_gamePref.m_passZero = CommonMolkyEnum.PassNbZeroMaxEnum.E_HALF;
                }
            }
        });
        w_alert.show();
    }

    private void _showDialogEquality() {
        final TextView w_text=new TextView(this);
        w_text.setPadding(10, 10, 10, 10);

        final SeekBar w_seek=new SeekBar(this);
        w_seek.setMax(1);
        if (m_gamePref.m_isFallingOnEqualityActivated) {
            w_text.setText("Chute si égalité : OUI (defaut : OUI)");
            w_seek.setProgress(1);
        } else {
            w_text.setText("Chute si égalité : NON (defaut : OUI)");
            w_seek.setProgress(0);
        }
        w_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    w_text.setText("Apres validation : NON (defaut : OUI)");
                }else {
                    w_text.setText("Apres validation : OUI (defaut : OUI)");
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

        w_alert.setTitle("Chute si on atteint un score adverse :");
        w_alert.setView(w_linear);

        w_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                MenuItem w_equalityItem = m_optionMenu.findItem(R.id.menu_MolkyOptionChuteEgalite);
                if (w_seek.getProgress() == 0) {
                    w_equalityItem.setTitle("Chutes si egalite : NON");
                    m_gamePref.m_isFallingOnEqualityActivated = false;
                } else {
                    w_equalityItem.setTitle("Chutes si egalite : OUI");
                    m_gamePref.m_isFallingOnEqualityActivated = true;
                }
            }
        });
        w_alert.show();
    }
}

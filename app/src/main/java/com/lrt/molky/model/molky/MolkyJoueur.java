package com.lrt.molky.model.molky;


import java.util.ArrayList;

public class MolkyJoueur {
    //private static final String TAG = "MolkyJoueur";
    private static MolkyGamePreference m_gamePref = null; // only modified by model

    private final String m_name;
    private ArrayList<Integer> m_score = new ArrayList<>();
    private Integer m_nbZero = 0;


    private  Boolean m_isDownForOther = false;
    private boolean m_hasWon = false;

    private boolean m_hasToGetLastScore = false;

    public MolkyJoueur(String ai_name, MolkyGamePreference ai_gamePref) {
        m_name = ai_name;
        m_gamePref = ai_gamePref;
        clearScore();
    }

    public MolkyJoueur(MolkyJoueur ai_copy) {
        m_name = ai_copy.m_name;
        m_nbZero = ai_copy.m_nbZero;
        m_isDownForOther = ai_copy.m_isDownForOther;
        m_hasWon = ai_copy.m_hasWon;
        m_score = new ArrayList<>(ai_copy.m_score);
    }

    public void newLancer(Integer ai_lancer) {
        m_isDownForOther = false;
        if ((ai_lancer == 0) && (m_gamePref.m_nbZeroMax != 0)) {
            m_nbZero += 1;
            if (m_nbZero == m_gamePref.m_nbZeroMax) {
                switch (m_gamePref.m_passZero) {
                    case E_HALF:
                        halfScore();
                        break;
                    case E_LAST:
                        m_hasToGetLastScore = true;
                        break;
                    case E_ZERO:
                        clearScore();
                        break;
                    // case E_HALF gere par au MolkyJoueurBank
                }
            }
        } else {
            // Reinitialisation du nombre de zero
            m_nbZero = 0;
            // Ajout du nouveau score
            m_score.add(m_score.get(m_score.size()-1) + ai_lancer);

            // A-t-il depassé le score maximal ?
            if (getLastScore() > m_gamePref.m_finalScore) {
                switch (m_gamePref.m_passScore){
                    case E_WON:
                        m_hasWon = true;
                        break;
                    case E_HALF:
                        halfScore();
                        break;
                    case E_ZERO:
                        clearScore();
                        break;
                }
            // A-t-il gagne ?
            } else if (getLastScore() == m_gamePref.m_finalScore)  {
                m_hasWon = true;
            // Peut-il faire tomber les autres ?
            } else if (m_gamePref.m_isFallingOnEqualityActivated) {
                m_isDownForOther = true;
            }
        }
    }

    public void otherScore(Integer ai_otherScore) {
        if (m_score.size() > 1) {
            int w_size = m_score.size()-1;
            if (m_score.get(w_size).equals(ai_otherScore)) {
                m_score.remove(w_size);
            }
        }
    }

    public int getLastScore() {
        if (!m_score.isEmpty()) {
            return m_score.get(m_score.size() -1);
        }
        return 0;
    }

    public int getPreviousScore() {
        if (m_score.size() > 1) {
            return m_score.get(m_score.size() -2);
        }
        return m_score.get(0);
    }

    public String getText() {
        String w_text = m_name + " -- "+ getLastScore()  +"\n";
        w_text += "--> Previous : "+ getPreviousScore()  +" NbZero : "+  m_nbZero +"\n";

        return w_text;
    }

    public void clearScore() {
        m_score.clear();
        m_score.add(0);
        m_nbZero = 0;
        m_hasWon = false;
    }

    public void halfScore() {
        int w_halfScore = m_gamePref.m_finalScore/2;
        m_score.clear();
        m_score.add(w_halfScore);
    }

    public void setScoreDown(int ai_score) {
        m_score.clear();
        m_score.add(ai_score);
        m_hasToGetLastScore = false;
    }

    public Boolean getIsDownForOther() {
        return m_isDownForOther;
    }
    public Boolean getHasWon() {
        return m_hasWon;
    }
    public String getName() {
        return m_name;
    }
    public Integer getNbZero() {
        return m_nbZero;
    }
    public boolean hasToGetLastScore() {
        return m_hasToGetLastScore;
    }
}
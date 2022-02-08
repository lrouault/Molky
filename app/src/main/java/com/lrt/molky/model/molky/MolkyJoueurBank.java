package com.lrt.molky.model.molky;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

public class MolkyJoueurBank {
    private static final String TAG = "MolkyJoueurBank";

    ArrayList<MolkyJoueur> m_listJoueurs = new ArrayList<>();
    int m_nextPlayer = 0;
    private Boolean m_someoneHasWon = false;
    public MolkyGamePreference m_gamePref;

    public MolkyJoueurBank(MolkyGamePreference ai_gamePref) {
        m_gamePref = ai_gamePref;
    }

    public MolkyJoueurBank(@NonNull MolkyJoueurBank ai_joueurBank) {
        m_gamePref = ai_joueurBank.m_gamePref;
        m_nextPlayer = ai_joueurBank.m_nextPlayer;
        m_someoneHasWon = ai_joueurBank.m_someoneHasWon;
        m_listJoueurs = new ArrayList<>();
        for (MolkyJoueur w_molkyJoueur : ai_joueurBank.m_listJoueurs) {
            Log.e(TAG, "MolkyJoueurBank: copie de joueur " );
            m_listJoueurs.add(new MolkyJoueur(w_molkyJoueur));
        }
    }

    public void addJoueur(String ai_name) {
        m_listJoueurs.add(new MolkyJoueur(ai_name, m_gamePref));
    }

    public void clearJoueurs() {
        m_listJoueurs.clear();
        m_someoneHasWon = false;
    }

    public void addLancer(Integer ai_score) {
        if (!m_listJoueurs.isEmpty()) {
            // Nouveau lancer
            m_listJoueurs.get(m_nextPlayer).newLancer(ai_score);
            // A-t-il gagne ?
            m_someoneHasWon = m_listJoueurs.get(m_nextPlayer).getHasWon();
            // Doit-il prendre le score minimal de la partie ?
            if (m_listJoueurs.get(m_nextPlayer).hasToGetLastScore()) {
                int w_min =m_gamePref.m_finalScore;
                for (int w_joueur = 0; w_joueur < m_listJoueurs.size(); w_joueur++) {
                    if (m_nextPlayer != w_joueur) {
                        if (m_listJoueurs.get(w_joueur).getLastScore() < w_min) {
                            w_min = m_listJoueurs.get(w_joueur).getLastScore();
                        }
                    }
                }
                m_listJoueurs.get(m_nextPlayer).setScoreDown(w_min);
            }

            // A-t-il atteint le score d'un autre joueur
            // OUI => Descente des autres joueurs (selon les parametres)
            if(m_gamePref.m_isFallingOnEqualityActivated){
                if (m_listJoueurs.get(m_nextPlayer).getIsDownForOther()) {
                    Integer w_score = m_listJoueurs.get(m_nextPlayer).getLastScore();
                    for (int w_joueur = 0; w_joueur < m_listJoueurs.size(); w_joueur++) {
                        if (m_nextPlayer != w_joueur) {
                            m_listJoueurs.get(w_joueur).otherScore(w_score);
                        }
                    }
                }
            }
            _nextPlayer();
        }
    }

    public void restart() {
        if (!m_listJoueurs.isEmpty()) {
            for (int w_joueur = 0; w_joueur < m_listJoueurs.size(); w_joueur++) {
                m_listJoueurs.get(w_joueur).clearScore();
            }
        }
        m_someoneHasWon = false;
    }

    public String getText() {
        StringBuilder w_out = new StringBuilder();
        if (!m_listJoueurs.isEmpty()) {
            for (int w_joueur = 0; w_joueur < m_listJoueurs.size(); w_joueur++) {
                if (w_joueur == m_nextPlayer) {
                    w_out.append("=>");
                }
                w_out.append(m_listJoueurs.get(w_joueur).getText()).append("\n");
            }
        }
        return w_out.toString();
    }

    public ArrayList<MolkyJoueurData> getData() {
        ArrayList<MolkyJoueurData> w_data;
        w_data = new ArrayList<>();

        if (!m_listJoueurs.isEmpty()) {
            for (int w_joueur = 0; w_joueur < m_listJoueurs.size(); w_joueur++) {
                MolkyJoueurData w_tmp = new MolkyJoueurData(
                        m_listJoueurs.get(w_joueur).getName(),
                        1,
                        m_listJoueurs.get(w_joueur).getLastScore(),
                        m_listJoueurs.get(w_joueur).getPreviousScore(),
                        m_listJoueurs.get(w_joueur).getNbZero(),
                        w_joueur == m_nextPlayer);
                w_data.add(w_tmp);
            }
        }
        _setPosition(w_data);
        return w_data;
    }

    private void _setPosition(ArrayList<MolkyJoueurData> ai_list) {
        ArrayList<Integer> w_position = new ArrayList<>();
        for (MolkyJoueurData w_joueur : ai_list) {
            if (!w_position.contains(w_joueur.m_actualScore)) {
                w_position.add(w_joueur.m_actualScore);
            }
        }
        Collections.sort(w_position);
        for (MolkyJoueurData w_joueur : ai_list) {
            w_joueur.m_position = w_position.size() - w_position.indexOf(w_joueur.m_actualScore);
        }
    }

    private void _nextPlayer() {
        if (m_nextPlayer < m_listJoueurs.size()-1) {
            m_nextPlayer +=1;
        } else {
            m_nextPlayer = 0;
        }
    }

    public Boolean getSomeoneHasWon() {
        return m_someoneHasWon;
    }

    @NonNull
    public String toString() {
        return "";
    }

    public void parseString(String ai_str) {
        //TODO
    }
}

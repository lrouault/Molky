package com.lrt.molky.model.molky;

import android.util.Log;

import androidx.annotation.NonNull;

import com.lrt.molky.common.CommonMolkyEnum;

import java.util.StringTokenizer;

public class MolkyGamePreference {
    private static final String TAG = "MolkyGamePreference";

    public static final CommonMolkyEnum.PassFinalScoreEnum C_DEFAULT_PASSSCORE = CommonMolkyEnum.PassFinalScoreEnum.E_HALF;
    public static final boolean C_DEFAULT_ISFALLINGEQUALITY = true;
    public static final int C_DEFAULT_FINALSCORE = 40;
    public static final boolean C_DEFAULT_ISFALLINGZERO = true;
    public static final int C_DEFAULT_NBZEROMAX = 3;
    public static final CommonMolkyEnum.PassNbZeroMaxEnum C_DEFAULT_PASSSZERO = CommonMolkyEnum.PassNbZeroMaxEnum.E_ZERO;

    public CommonMolkyEnum.PassFinalScoreEnum m_passScore = C_DEFAULT_PASSSCORE;
    public Boolean m_isFallingOnEqualityActivated = C_DEFAULT_ISFALLINGEQUALITY;
    public int m_finalScore = C_DEFAULT_FINALSCORE;
    public Boolean m_isFallingOnZeroActivated = C_DEFAULT_ISFALLINGZERO;
    public int m_nbZeroMax = C_DEFAULT_NBZEROMAX;
    public CommonMolkyEnum.PassNbZeroMaxEnum m_passZero = C_DEFAULT_PASSSZERO;


    public MolkyGamePreference() {}

    // Création d'un string contenant les données de la classe
    @NonNull
    public String toString() {
        return m_passScore.ordinal() + "," +
                m_isFallingOnEqualityActivated.toString() + "," +
                m_finalScore + "," +
                m_isFallingOnZeroActivated.toString() + "," +
                m_nbZeroMax + "," +
                m_passZero.ordinal();
    }

    // Création d'un string contenant les données de la classe
    public void parseString(String ai_serialize) {
        try {
            StringTokenizer st = new StringTokenizer(ai_serialize, ",");
            m_passScore = CommonMolkyEnum.PassFinalScoreEnum.values()[Integer.parseInt(st.nextToken())];
            m_isFallingOnEqualityActivated = Boolean.parseBoolean(st.nextToken());
            m_finalScore = Integer.parseInt(st.nextToken());
            m_isFallingOnZeroActivated = Boolean.parseBoolean(st.nextToken());
            m_nbZeroMax = Integer.parseInt(st.nextToken());
            m_passZero = CommonMolkyEnum.PassNbZeroMaxEnum.values()[Integer.parseInt(st.nextToken())];
        } catch (Exception e) {
            Log.e(TAG,"appel de _majSavedPlateau");
        }
    }
}

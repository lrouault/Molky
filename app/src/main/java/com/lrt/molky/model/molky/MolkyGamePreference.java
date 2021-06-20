package com.lrt.molky.model.molky;

import com.lrt.molky.common.CommonMolkyEnum;

public class MolkyGamePreference {
    public CommonMolkyEnum.PassFinalScoreEnum m_passScore = CommonMolkyEnum.PassFinalScoreEnum.E_HALF;
    public Boolean m_isFallingOnEqualityActivated = true;
    public int m_finalScore = 40;
    public Boolean m_isFallingOnZeroActivated = true;
    public int m_nbZeroMax = 3;
    public CommonMolkyEnum.PassNbZeroMaxEnum m_passZero = CommonMolkyEnum.PassNbZeroMaxEnum.E_ZERO;
}

package com.zcbspay.platform.channel.simulation.withholding.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zcbspay.platform.channel.simulation.withholding.dao.TxnsWithholdingDAO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.PojoTxnsWithholding;
import com.zcbspay.platform.channel.simulation.withholding.service.TxnsWithholdingService;
@Service("txnsWithholdingService")
public class TxnsWithholdingServiceImpl implements TxnsWithholdingService{

    @Autowired
    private TxnsWithholdingDAO txnsWithholdingDAO;
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void saveWithholdingLog(PojoTxnsWithholding withholding) {
            txnsWithholdingDAO.saveEntity(withholding);
    }
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void updateRealNameResult(PojoTxnsWithholding withholding) {
    	txnsWithholdingDAO.updateRealNameResult(withholding);
    }
    
    
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void updateWhithholding(PojoTxnsWithholding withholding){
        txnsWithholdingDAO.update(withholding);
    }
    
    @Transactional(readOnly=true)
    public PojoTxnsWithholding getWithholdingBySerialNo(String serialno){
        return txnsWithholdingDAO.getWithholdingBySerialNo(serialno);
    }
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void updateWithholdingLogError(PojoTxnsWithholding withholding) {
    	txnsWithholdingDAO.updateWithholdingLogError(withholding);
    }
    
    
}

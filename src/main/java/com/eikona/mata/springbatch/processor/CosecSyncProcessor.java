package com.eikona.mata.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Transaction;
@Component
public class CosecSyncProcessor implements ItemProcessor<Transaction, Transaction>{
	@Override
    public Transaction process(Transaction trans) throws Exception {
       Transaction transaction = new Transaction();
       transaction.setId(trans.getId());
       System.out.println("inside processor " + transaction.toString());
        return transaction;
    }
}

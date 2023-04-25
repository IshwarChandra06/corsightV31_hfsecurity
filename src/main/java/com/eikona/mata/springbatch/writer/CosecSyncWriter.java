package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;

import static com.eikona.mata.constants.NumberConstants.*;
import static com.eikona.mata.constants.ApplicationConstants.*;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.util.CosecTransactionUtil;
@Component
public class CosecSyncWriter implements ItemWriter<Transaction> {
	//@Autowired
	private TransactionRepository transactionRepository;
	
	//@Autowired
	private CosecTransactionUtil cosecTransactionUtil;
	
	
	@Autowired
	public CosecSyncWriter(CosecTransactionUtil cosecTransactionUtil,TransactionRepository transactionRepository) {
		super();
		this.cosecTransactionUtil = cosecTransactionUtil;
		this.transactionRepository=transactionRepository;
	}
	
	@Override
	public void write(List<? extends Transaction> transactions) throws Exception {
		List<Transaction> transactionList=new ArrayList<Transaction>();
		for(Transaction transaction : transactions) {
			Transaction trans=transactionRepository.findById(transaction.getId()).get();
			
			String response=cosecTransactionUtil.transactionSendToCosec(trans);
			
			String[] parts = response.split(DELIMITER_COLON);
			String message=parts[ZERO];
			if(SUCCESS.equalsIgnoreCase(message)) {
				trans.setSync(true);
				trans.setFailed(false);
			}else {
				trans.setSync(false);
				trans.setFailed(true);
			}
			transactionList.add(trans);
		}
		transactionRepository.saveAll(transactionList);
	}
}

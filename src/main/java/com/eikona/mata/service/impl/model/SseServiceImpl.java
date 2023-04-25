package com.eikona.mata.service.impl.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.util.ImageProcessingUtil;

@Service
public class SseServiceImpl {
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;

	// Save all emitters from different connections, to send message to all
	private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	
	@Autowired
	private TransactionRepository eventRepository;

	public SseEmitter registerClient() {
		SseEmitter emitter = new SseEmitter();

		// Add client specific new emitter in global list
		emitters.add(emitter);

		// On Client connection completion, unregister client specific emitter
		emitter.onCompletion(() -> emitters.remove(emitter));

		// On Client connection timeout, unregister and mark complete client specific emitter
		emitter.onTimeout(() -> {
			emitter.complete();
			emitters.remove(emitter);
		});
		
		return emitter;
	}

	public void process(String message, String user) throws IOException {
		
		List<Transaction> eventList = (List<Transaction>) eventRepository.findAll();
		for(Transaction event: eventList) {
			byte[] image = imageProcessingUtil.searchTransactionImage(event);
			event.setCropImageByte(image);
			sendEventToClients(event);
		}
		
	}
	
	public void processEvent(Transaction event) throws IOException {
			sendEventToClients(event);
		
	}


	public void sendEventToClients(Transaction event) {
		// Track which events could not be sent
		List<SseEmitter> deadEmitters = new ArrayList<>();
		// Send to all registered clients
		emitters.forEach(emitter -> {
			try {
				//emitter.send(org.json.JSONObject.valueToString(event));
				emitter.send(event);
			} catch (Exception e) {
				// record failed ones
				deadEmitters.add(emitter);
			}
		});
		// remove the failed one, otherwise it will keep on waiting for client connection
		emitters.remove(deadEmitters);
	}
}

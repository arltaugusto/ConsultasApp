package com.project.consultas.notifications;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.project.consultas.dto.SubscriptionDTO;
import com.project.consultas.entities.User;

@Service

public class FCMService {
    private static final String UNABLE_TO_BUILD_MESSAGE = "Unable to build message";
	private Logger logger = LoggerFactory.getLogger(FCMService.class);
    
    @Async
    public void deviceSuscriptionManager(BiConsumer<String, String> consumer, SubscriptionDTO subscriptionDto) {
    	subscriptionDto.getSubscriptionList().forEach(topic -> consumer.accept(subscriptionDto.getUserDeviceToken(), topic));
    }
    
    @Async
    public void subscribeToTopic(String deviceToken, String topic) {
    	FirebaseMessaging.getInstance().subscribeToTopicAsync(Arrays.asList(deviceToken), topic);
    }
    
    @Async
    public void unsubscribeFromTopic(String deviceToken, String topic) {
    	FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(Arrays.asList(deviceToken), topic);
    }
    
    @Async
    public <T extends User>void sendMassiveMessages(Function<Set<T>, List<PushNotificationMessage>> function, Set<T> users) {
    	List<PushNotificationMessage> pushNotificationMessages = function.apply(users);
    	pushNotificationMessages.parallelStream().forEach(t -> {
			try {
				t.buildMessage();
				sendMessageToToken(t);
			} catch (IOException e) {
				logger.info(UNABLE_TO_BUILD_MESSAGE, e);
			}
		});
    }
    
    @Async
    public void sendMessageWithData(PushNotificationMessage request) {
    	try {
	    	request.buildMessage();
	        Message message = getPreconfiguredMessageWithData(request);
	        String info = String.format("Sent message with data. Topic: %s", request.getTopic());
	        sendAndGetResponse(message, info);
    	} catch(Exception e) {
    		logger.error(UNABLE_TO_BUILD_MESSAGE, e);
    	}
    }

    @Async
    public void sendMessageToToken(PushNotificationMessage request) {
    	try {
			request.buildMessage();
    		Message message = getPreconfiguredMessageToToken(request);
	        String info = String.format("Sent message to token. Device token: \"%s\", \" ", request.getDestinationToken());
	        sendAndGetResponse(message, info);
		} catch (IOException e) {
			logger.error(UNABLE_TO_BUILD_MESSAGE);
		}
  
    }

    private void sendAndGetResponse(Message message, String info) {
        try {
        	String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            logger.info(info);
        } catch(Exception e) {
        	logger.error("Unable to send push notifications", e);
        }
    }

    private AndroidConfig getAndroidConfig(PushNotificationMessage request) {
    	return AndroidConfig.builder()
            .setCollapseKey(request.getTopic())
            .setPriority(AndroidConfig.Priority.HIGH)
            .putAllData(request.getData())
            .setNotification(AndroidNotification.builder()
                .setColor(NotificationParameter.COLOR.getValue())
        		.setSound(NotificationParameter.SOUND.getValue())
                .setIcon("ic_stat_name")
                .setTag(request.getTopic())
                .build()).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }

    private Message getPreconfiguredMessageToToken(PushNotificationMessage request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getDestinationToken())
                .build();
    }

    private Message getPreconfiguredMessageWithData(PushNotificationMessage request) {
        return getPreconfiguredMessageBuilder(request).setTopic(request.getTopic())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationMessage request) {
        AndroidConfig androidConfig = getAndroidConfig(request);
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                        new Notification(request.getTitle(), request.getMessage()));
    }
}

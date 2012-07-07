/**
 * 
 */
package com.richitec.providerDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.transmission.NotificationProgressListener;
import javapns.notification.transmission.NotificationThread;
import javapns.notification.transmission.NotificationThreads;

/**
 * @author chelsea zhai
 * 
 */
public class APNSProvider {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// certificate name and path
		String keystore = "ref/walkwork_APNS_privateKey.p12";
		// certificate password
		String password = "ivyinfo123";
		// iphone device token
		String deviceToken = "9d643f1a59a2d43a6dfbedf36255975082a64ff150761bc56adfd07b5f8eb49d";

		// production flag: true for distribution and false for development
		boolean production = false;
		// provider push message thread number
		int threadNumber = 10;

		try {
			// connect to apple notification push server
			AppleNotificationServer server = new AppleNotificationServerBasicImpl(
					keystore, password, production);

			// devices payload list
			List<PayloadPerDevice> devicesPayloadList = new ArrayList<PayloadPerDevice>();

			// create and init payload
			PushNotificationPayload payload = new PushNotificationPayload();
			payload.addAlert("Ares 邀请您加入会议（会议号：" + Math.abs(new Random().nextInt()) + "）");
			payload.addBadge(1);
			payload.addSound("default");

			// set device payload
			PayloadPerDevice devicePayload = new PayloadPerDevice(payload,
					deviceToken);

			// add device payload to list
			devicesPayloadList.add(devicePayload);

			// create and init push notices threads
			NotificationThreads pushNoticesThreads = new NotificationThreads(
					server, devicesPayloadList, threadNumber);
			// add listener
			pushNoticesThreads.setListener(new NotificationProgressListener() {

				@Override
				public void eventThreadStarted(
						NotificationThread notificationThread) {
					System.out.println("[EVENT]: thread #"
							+ notificationThread.getThreadNumber()
							+ " started with "
							+ " devices beginning at message id #"
							+ notificationThread.getFirstMessageIdentifier());
				}

				@Override
				public void eventThreadFinished(
						NotificationThread notificationThread) {
					System.out.println("[EVENT]: thread #"
							+ notificationThread.getThreadNumber()
							+ " finished: pushed messages #"
							+ notificationThread.getFirstMessageIdentifier()
							+ " to "
							+ notificationThread.getLastMessageIdentifier()
							+ " toward " + " devices");
				}

				@Override
				public void eventCriticalException(
						NotificationThread notificationThread,
						Exception exception) {
					System.out.println("[EVENT]: critical exception occurred: "
							+ exception);
				}

				@Override
				public void eventConnectionRestarted(
						NotificationThread notificationThread) {
					System.out
							.println("[EVENT]: connection restarted in thread #"
									+ notificationThread.getThreadNumber()
									+ " because it reached "
									+ notificationThread
											.getMaxNotificationsPerConnection()
									+ " notifications per connection");
				}

				@Override
				public void eventAllThreadsStarted(
						NotificationThreads notificationThreads) {
					System.out.println("[EVENT]: all threads started: "
							+ notificationThreads.getThreads().size());
				}

				@Override
				public void eventAllThreadsFinished(
						NotificationThreads notificationThreads) {
					System.out.println("[EVENT]: all threads finished: "
							+ notificationThreads.getThreads().size());
				}
			});
			// start thread
			pushNoticesThreads.start();
			// wait for all threads
			pushNoticesThreads.waitForAllThreads();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

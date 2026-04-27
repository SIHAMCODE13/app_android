package com.carrental.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.carrental.R;
import com.carrental.activities.DashboardActivity;
import com.carrental.activities.ReservationListActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "car_rental_channel";
    private static final String CHANNEL_NAME = "Car Rental Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications pour les réservations et paiements";
    private static final int NOTIFICATION_ID = 100;

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Notification pour nouvelle réservation
    public void showReservationCreatedNotification(String clientName, String carName, String dateDebut, String dateFin) {
        Intent intent = new Intent(context, ReservationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nouvelle réservation")
                .setContentText(clientName + " a réservé " + carName + " du " + dateDebut + " au " + dateFin)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(clientName + " a réservé " + carName + " du " + dateDebut + " au " + dateFin))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Notification pour réservation annulée
    public void showReservationCancelledNotification(String clientName, String carName) {
        Intent intent = new Intent(context, ReservationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Réservation annulée")
                .setContentText("La réservation de " + clientName + " pour " + carName + " a été annulée")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("La réservation de " + clientName + " pour " + carName + " a été annulée"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Notification pour rappel de réservation (1 jour avant)
    public void showReservationReminderNotification(String clientName, String carName, String dateDebut) {
        Intent intent = new Intent(context, ReservationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_week)
                .setContentTitle("Rappel de réservation")
                .setContentText("Rappel: " + clientName + " a réservé " + carName + " à partir du " + dateDebut)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Rappel: " + clientName + " a réservé " + carName + " à partir du " + dateDebut))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Notification pour paiement effectué
    public void showPaymentReceivedNotification(String clientName, double amount, String carName) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_sort_by_size)
                .setContentTitle("Paiement reçu")
                .setContentText(clientName + " a payé " + String.format("%.2f", amount) + " DT pour " + carName)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(clientName + " a payé " + String.format("%.2f", amount) + " DT pour " + carName))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Notification pour client (sa propre réservation)
    public void showClientReservationNotification(String carName, String dateDebut, String dateFin) {
        Intent intent = new Intent(context, ReservationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Réservation confirmée")
                .setContentText("Votre réservation pour " + carName + " du " + dateDebut + " au " + dateFin + " a été confirmée")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Votre réservation pour " + carName + " du " + dateDebut + " au " + dateFin + " a été confirmée"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Notification pour annulation par client
    public void showClientCancellationNotification(String carName) {
        Intent intent = new Intent(context, ReservationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Réservation annulée")
                .setContentText("Votre réservation pour " + carName + " a été annulée")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Votre réservation pour " + carName + " a été annulée"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Notification de bienvenue
    public void showWelcomeNotification(String username, String role) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String message = role.equals("Client") ?
                "Bienvenue ! Vous pouvez maintenant réserver des voitures." :
                "Bienvenue ! Vous pouvez gérer les réservations, les clients et les voitures.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Bienvenue " + username + " !")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
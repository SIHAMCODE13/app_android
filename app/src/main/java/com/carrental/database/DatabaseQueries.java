package com.carrental.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.carrental.models.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseQueries {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseQueries(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // User queries
    public boolean validateUser(String username, String password) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COL_USER_ID},
                DatabaseHelper.COL_USER_USERNAME + "=? AND " + DatabaseHelper.COL_USER_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public User getUser(String username) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COL_USER_ID, DatabaseHelper.COL_USER_USERNAME,
                        DatabaseHelper.COL_USER_PASSWORD, DatabaseHelper.COL_USER_ROLE},
                DatabaseHelper.COL_USER_USERNAME + "=?",
                new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserById(int id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COL_USER_ID, DatabaseHelper.COL_USER_USERNAME,
                        DatabaseHelper.COL_USER_PASSWORD, DatabaseHelper.COL_USER_ROLE},
                DatabaseHelper.COL_USER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean registerUser(String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_USERNAME, username);
        values.put(DatabaseHelper.COL_USER_PASSWORD, password);
        values.put(DatabaseHelper.COL_USER_ROLE, role);

        long result = database.insert(DatabaseHelper.TABLE_USER, null, values);
        return result != -1;
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COL_USER_PASSWORD, user.getPassword());
        
        return database.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_USER_ID + "=?", new String[]{String.valueOf(user.getId())});
    }

    public int getLastInsertedUserId() {
        Cursor cursor = database.rawQuery("SELECT last_insert_rowid()", null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    // Car queries
    public long addCar(Car car) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CAR_MARQUE, car.getMarque());
        values.put(DatabaseHelper.COL_CAR_MODELE, car.getModele());
        values.put(DatabaseHelper.COL_CAR_ANNEE, car.getAnnee());
        values.put(DatabaseHelper.COL_CAR_PRIX_JOUR, car.getPrixJour());
        values.put(DatabaseHelper.COL_CAR_DISPONIBLE, car.isDisponible() ? 1 : 0);
        values.put(DatabaseHelper.COL_CAR_IMAGE, car.getImage());

        return database.insert(DatabaseHelper.TABLE_CAR, null, values);
    }

    public int updateCar(Car car) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CAR_MARQUE, car.getMarque());
        values.put(DatabaseHelper.COL_CAR_MODELE, car.getModele());
        values.put(DatabaseHelper.COL_CAR_ANNEE, car.getAnnee());
        values.put(DatabaseHelper.COL_CAR_PRIX_JOUR, car.getPrixJour());
        values.put(DatabaseHelper.COL_CAR_DISPONIBLE, car.isDisponible() ? 1 : 0);
        values.put(DatabaseHelper.COL_CAR_IMAGE, car.getImage());

        return database.update(DatabaseHelper.TABLE_CAR, values,
                DatabaseHelper.COL_CAR_ID + "=?", new String[]{String.valueOf(car.getId())});
    }

    public int deleteCar(int carId) {
        return database.delete(DatabaseHelper.TABLE_CAR,
                DatabaseHelper.COL_CAR_ID + "=?", new String[]{String.valueOf(carId)});
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CAR, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Car car = new Car(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MARQUE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MODELE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_ANNEE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_PRIX_JOUR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_DISPONIBLE)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_IMAGE))
                );
                cars.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cars;
    }

    public Car getCar(int carId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_CAR, null,
                DatabaseHelper.COL_CAR_ID + "=?", new String[]{String.valueOf(carId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Car car = new Car(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MARQUE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MODELE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_ANNEE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_PRIX_JOUR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_DISPONIBLE)) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_IMAGE))
            );
            cursor.close();
            return car;
        }
        return null;
    }

    // FIX BUG 2: Nouvelle méthode pour vérifier s'il existe une réservation ACTIVE ou PAID pour une voiture
    public boolean hasActiveReservations(int carId) {
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_RESERVATION +
                " WHERE " + DatabaseHelper.COL_RES_CAR_ID + " = ? " +
                " AND (" + DatabaseHelper.COL_RES_STATUT + " = 'ACTIVE' OR " + DatabaseHelper.COL_RES_STATUT + " = 'PAID')";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(carId)});
        boolean hasActive = false;
        if (cursor != null && cursor.moveToFirst()) {
            hasActive = cursor.getInt(0) > 0;
            cursor.close();
        }
        return hasActive;
    }

    // FIX BUG 1 & 2: isCarAvailable amélioré (vérifie les chevauchements et permet d'exclure une réservation)
    public boolean isCarAvailable(int carId, String startDate, String endDate, int excludeResId) {
        // Logique de chevauchement : (DebutExistante <= FinDemandee) ET (FinExistante >= DebutDemandee)
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_RESERVATION +
                " WHERE " + DatabaseHelper.COL_RES_CAR_ID + " = ? " +
                " AND " + DatabaseHelper.COL_RES_ID + " != ? " +
                " AND (" + DatabaseHelper.COL_RES_STATUT + " = 'ACTIVE' OR " + DatabaseHelper.COL_RES_STATUT + " = 'PAID') " +
                " AND (" + DatabaseHelper.COL_RES_DATE_DEBUT + " <= ? AND " + 
                DatabaseHelper.COL_RES_DATE_FIN + " >= ?)";

        Cursor cursor = database.rawQuery(query, new String[]{
                String.valueOf(carId),
                String.valueOf(excludeResId),
                endDate,
                startDate
        });

        boolean available = true;
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                available = false;
            }
            cursor.close();
        }
        return available;
    }

    // Client queries
    public long addClient(Client client) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CLIENT_NOM, client.getNom());
        values.put(DatabaseHelper.COL_CLIENT_PRENOM, client.getPrenom());
        values.put(DatabaseHelper.COL_CLIENT_EMAIL, client.getEmail());
        values.put(DatabaseHelper.COL_CLIENT_TELEPHONE, client.getTelephone());
        values.put(DatabaseHelper.COL_CLIENT_USER_ID, client.getUserId());
        values.put(DatabaseHelper.COL_CLIENT_SOLDE, client.getSolde());

        return database.insert(DatabaseHelper.TABLE_CLIENT, null, values);
    }

    public int updateClient(Client client) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CLIENT_NOM, client.getNom());
        values.put(DatabaseHelper.COL_CLIENT_PRENOM, client.getPrenom());
        values.put(DatabaseHelper.COL_CLIENT_EMAIL, client.getEmail());
        values.put(DatabaseHelper.COL_CLIENT_TELEPHONE, client.getTelephone());
        values.put(DatabaseHelper.COL_CLIENT_SOLDE, client.getSolde());

        return database.update(DatabaseHelper.TABLE_CLIENT, values,
                DatabaseHelper.COL_CLIENT_ID + "=?", new String[]{String.valueOf(client.getId())});
    }

    public int deleteClient(int clientId) {
        return database.delete(DatabaseHelper.TABLE_CLIENT,
                DatabaseHelper.COL_CLIENT_ID + "=?", new String[]{String.valueOf(clientId)});
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CLIENT, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Client client = new Client(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_NOM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_PRENOM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_TELEPHONE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_USER_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_SOLDE))
                );
                clients.add(client);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clients;
    }

    public Client getClient(int clientId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_CLIENT, null,
                DatabaseHelper.COL_CLIENT_ID + "=?", new String[]{String.valueOf(clientId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Client client = new Client(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_NOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_PRENOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_TELEPHONE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_USER_ID)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_SOLDE))
            );
            cursor.close();
            return client;
        }
        return null;
    }

    public Client getClientByUserId(int userId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_CLIENT, null,
                DatabaseHelper.COL_CLIENT_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Client client = new Client(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_NOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_PRENOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_TELEPHONE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_USER_ID)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_SOLDE))
            );
            cursor.close();
            return client;
        }
        return null;
    }

    // Reservation queries
    // FIX BUG 2: SUPPRIMER la mise à jour manuelle de COL_CAR_DISPONIBLE
    public long addReservation(Reservation reservation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RES_CLIENT_ID, reservation.getClientId());
        values.put(DatabaseHelper.COL_RES_CAR_ID, reservation.getCarId());
        values.put(DatabaseHelper.COL_RES_DATE_DEBUT, reservation.getDateDebut());
        values.put(DatabaseHelper.COL_RES_DATE_FIN, reservation.getDateFin());
        values.put(DatabaseHelper.COL_RES_PRIX_TOTAL, reservation.getPrixTotal());
        values.put(DatabaseHelper.COL_RES_STATUT, "ACTIVE");

        return database.insert(DatabaseHelper.TABLE_RESERVATION, null, values);
    }

    public int updateReservation(Reservation reservation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RES_CLIENT_ID, reservation.getClientId());
        values.put(DatabaseHelper.COL_RES_CAR_ID, reservation.getCarId());
        values.put(DatabaseHelper.COL_RES_DATE_DEBUT, reservation.getDateDebut());
        values.put(DatabaseHelper.COL_RES_DATE_FIN, reservation.getDateFin());
        values.put(DatabaseHelper.COL_RES_PRIX_TOTAL, reservation.getPrixTotal());
        values.put(DatabaseHelper.COL_RES_STATUT, reservation.getStatut());

        return database.update(DatabaseHelper.TABLE_RESERVATION, values,
                DatabaseHelper.COL_RES_ID + "=?", new String[]{String.valueOf(reservation.getId())});
    }

    // FIX BUG 2: SUPPRIMER la remise à 1 de COL_CAR_DISPONIBLE
    public int cancelReservation(int reservationId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RES_STATUT, "CANCELLED");

        return database.update(DatabaseHelper.TABLE_RESERVATION, values,
                DatabaseHelper.COL_RES_ID + "=?", new String[]{String.valueOf(reservationId)});
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, c." + DatabaseHelper.COL_CLIENT_NOM + ", c." + DatabaseHelper.COL_CLIENT_PRENOM +
                ", v." + DatabaseHelper.COL_CAR_MARQUE + ", v." + DatabaseHelper.COL_CAR_MODELE +
                " FROM " + DatabaseHelper.TABLE_RESERVATION + " r" +
                " JOIN " + DatabaseHelper.TABLE_CLIENT + " c ON r." + DatabaseHelper.COL_RES_CLIENT_ID + " = c." + DatabaseHelper.COL_CLIENT_ID +
                " JOIN " + DatabaseHelper.TABLE_CAR + " v ON r." + DatabaseHelper.COL_RES_CAR_ID + " = v." + DatabaseHelper.COL_CAR_ID;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_CLIENT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_CAR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_DATE_DEBUT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_DATE_FIN)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_PRIX_TOTAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_STATUT))
                );
                reservation.setClientName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_PRENOM)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_NOM)));
                reservation.setCarName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MARQUE)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MODELE)));
                reservations.add(reservation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }

    public List<Reservation> getClientReservations(int clientId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, c." + DatabaseHelper.COL_CLIENT_NOM + ", c." + DatabaseHelper.COL_CLIENT_PRENOM +
                ", v." + DatabaseHelper.COL_CAR_MARQUE + ", v." + DatabaseHelper.COL_CAR_MODELE +
                " FROM " + DatabaseHelper.TABLE_RESERVATION + " r" +
                " JOIN " + DatabaseHelper.TABLE_CLIENT + " c ON r." + DatabaseHelper.COL_RES_CLIENT_ID + " = c." + DatabaseHelper.COL_CLIENT_ID +
                " JOIN " + DatabaseHelper.TABLE_CAR + " v ON r." + DatabaseHelper.COL_RES_CAR_ID + " = v." + DatabaseHelper.COL_CAR_ID +
                " WHERE r." + DatabaseHelper.COL_RES_CLIENT_ID + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(clientId)});

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_CLIENT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_CAR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_DATE_DEBUT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_DATE_FIN)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_PRIX_TOTAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RES_STATUT))
                );
                reservation.setClientName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_PRENOM)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_NOM)));
                reservation.setCarName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MARQUE)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MODELE)));
                reservations.add(reservation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }

    // Payment queries
    public String processPayment(int reservationId, int clientId, double amount) {
        database.beginTransaction();
        try {
            // Check balance
            Cursor cursor = database.query(DatabaseHelper.TABLE_CLIENT,
                    new String[]{DatabaseHelper.COL_CLIENT_SOLDE},
                    DatabaseHelper.COL_CLIENT_ID + "=?", new String[]{String.valueOf(clientId)},
                    null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                double solde = cursor.getDouble(0);
                cursor.close();
                
                if (solde < amount) {
                    return "Solde insuffisant";
                }
                
                // Deduct balance
                ContentValues clientValues = new ContentValues();
                clientValues.put(DatabaseHelper.COL_CLIENT_SOLDE, solde - amount);
                database.update(DatabaseHelper.TABLE_CLIENT, clientValues,
                        DatabaseHelper.COL_CLIENT_ID + "=?", new String[]{String.valueOf(clientId)});
                
                // Add payment record
                ContentValues payValues = new ContentValues();
                payValues.put(DatabaseHelper.COL_PAY_RES_ID, reservationId);
                payValues.put(DatabaseHelper.COL_PAY_CLIENT_ID, clientId);
                payValues.put(DatabaseHelper.COL_PAY_AMOUNT, amount);
                payValues.put(DatabaseHelper.COL_PAY_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                database.insert(DatabaseHelper.TABLE_PAYMENT, null, payValues);
                
                // Update reservation status
                ContentValues resValues = new ContentValues();
                resValues.put(DatabaseHelper.COL_RES_STATUT, "PAID");
                database.update(DatabaseHelper.TABLE_RESERVATION, resValues,
                        DatabaseHelper.COL_RES_ID + "=?", new String[]{String.valueOf(reservationId)});
                
                database.setTransactionSuccessful();
                return "SUCCESS";
            }
            return "Client non trouvé";
        } catch (Exception e) {
            return "Erreur lors du paiement: " + e.getMessage();
        } finally {
            database.endTransaction();
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT p.*, c." + DatabaseHelper.COL_CLIENT_NOM + ", c." + DatabaseHelper.COL_CLIENT_PRENOM +
                ", v." + DatabaseHelper.COL_CAR_MARQUE + ", v." + DatabaseHelper.COL_CAR_MODELE +
                " FROM " + DatabaseHelper.TABLE_PAYMENT + " p" +
                " JOIN " + DatabaseHelper.TABLE_CLIENT + " c ON p." + DatabaseHelper.COL_PAY_CLIENT_ID + " = c." + DatabaseHelper.COL_CLIENT_ID +
                " JOIN " + DatabaseHelper.TABLE_RESERVATION + " r ON p." + DatabaseHelper.COL_PAY_RES_ID + " = r." + DatabaseHelper.COL_RES_ID +
                " JOIN " + DatabaseHelper.TABLE_CAR + " v ON r." + DatabaseHelper.COL_RES_CAR_ID + " = v." + DatabaseHelper.COL_CAR_ID;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Payment payment = new Payment(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_RES_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_CLIENT_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_DATE))
                );
                payment.setClientName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_PRENOM)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_NOM)));
                payment.setCarName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MARQUE)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MODELE)));
                payments.add(payment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return payments;
    }

    // FIX BUG 3: Nouvelle méthode pour récupérer les paiements d'un client spécifique
    public List<Payment> getPaymentsByClient(int clientId) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT p.*, c." + DatabaseHelper.COL_CLIENT_NOM + ", c." + DatabaseHelper.COL_CLIENT_PRENOM +
                ", v." + DatabaseHelper.COL_CAR_MARQUE + ", v." + DatabaseHelper.COL_CAR_MODELE +
                " FROM " + DatabaseHelper.TABLE_PAYMENT + " p" +
                " JOIN " + DatabaseHelper.TABLE_CLIENT + " c ON p." + DatabaseHelper.COL_PAY_CLIENT_ID + " = c." + DatabaseHelper.COL_CLIENT_ID +
                " JOIN " + DatabaseHelper.TABLE_RESERVATION + " r ON p." + DatabaseHelper.COL_PAY_RES_ID + " = r." + DatabaseHelper.COL_RES_ID +
                " JOIN " + DatabaseHelper.TABLE_CAR + " v ON r." + DatabaseHelper.COL_RES_CAR_ID + " = v." + DatabaseHelper.COL_CAR_ID +
                " WHERE p." + DatabaseHelper.COL_PAY_CLIENT_ID + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(clientId)});

        if (cursor.moveToFirst()) {
            do {
                Payment payment = new Payment(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_RES_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_CLIENT_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAY_DATE))
                );
                payment.setClientName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_PRENOM)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLIENT_NOM)));
                payment.setCarName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MARQUE)) + " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAR_MODELE)));
                payments.add(payment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return payments;
    }
}
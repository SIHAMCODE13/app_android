package com.carrental.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.carrental.models.*;
import java.util.ArrayList;
import java.util.List;

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

    public boolean registerUser(String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_USERNAME, username);
        values.put(DatabaseHelper.COL_USER_PASSWORD, password);
        values.put(DatabaseHelper.COL_USER_ROLE, role);

        long result = database.insert(DatabaseHelper.TABLE_USER, null, values);
        return result != -1;
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

        return database.insert(DatabaseHelper.TABLE_CAR, null, values);
    }

    public int updateCar(Car car) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CAR_MARQUE, car.getMarque());
        values.put(DatabaseHelper.COL_CAR_MODELE, car.getModele());
        values.put(DatabaseHelper.COL_CAR_ANNEE, car.getAnnee());
        values.put(DatabaseHelper.COL_CAR_PRIX_JOUR, car.getPrixJour());
        values.put(DatabaseHelper.COL_CAR_DISPONIBLE, car.isDisponible() ? 1 : 0);

        return database.update(DatabaseHelper.TABLE_CAR, values,
                DatabaseHelper.COL_CAR_ID + "=?", new String[]{String.valueOf(car.getId())});
    }

    public int deleteCar(int carId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_RESERVATION,
                new String[]{DatabaseHelper.COL_RES_ID},
                DatabaseHelper.COL_RES_CAR_ID + "=? AND " + DatabaseHelper.COL_RES_STATUT + "='ACTIVE'",
                new String[]{String.valueOf(carId)}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return -1;
        }
        cursor.close();

        return database.delete(DatabaseHelper.TABLE_CAR,
                DatabaseHelper.COL_CAR_ID + "=?", new String[]{String.valueOf(carId)});
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CAR, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Car car = new Car(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CAR_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MARQUE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MODELE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CAR_ANNEE)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_CAR_PRIX_JOUR)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CAR_DISPONIBLE)) == 1
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
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CAR_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MARQUE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MODELE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CAR_ANNEE)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_CAR_PRIX_JOUR)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CAR_DISPONIBLE)) == 1
            );
            cursor.close();
            return car;
        }
        return null;
    }

    // Client queries
    public long addClient(Client client) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CLIENT_NOM, client.getNom());
        values.put(DatabaseHelper.COL_CLIENT_PRENOM, client.getPrenom());
        values.put(DatabaseHelper.COL_CLIENT_EMAIL, client.getEmail());
        values.put(DatabaseHelper.COL_CLIENT_TELEPHONE, client.getTelephone());
        values.put(DatabaseHelper.COL_CLIENT_USER_ID, client.getUserId());

        return database.insert(DatabaseHelper.TABLE_CLIENT, null, values);
    }

    public int updateClient(Client client) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CLIENT_NOM, client.getNom());
        values.put(DatabaseHelper.COL_CLIENT_PRENOM, client.getPrenom());
        values.put(DatabaseHelper.COL_CLIENT_EMAIL, client.getEmail());
        values.put(DatabaseHelper.COL_CLIENT_TELEPHONE, client.getTelephone());

        return database.update(DatabaseHelper.TABLE_CLIENT, values,
                DatabaseHelper.COL_CLIENT_ID + "=?", new String[]{String.valueOf(client.getId())});
    }

    public int deleteClient(int clientId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_RESERVATION,
                new String[]{DatabaseHelper.COL_RES_ID},
                DatabaseHelper.COL_RES_CLIENT_ID + "=? AND " + DatabaseHelper.COL_RES_STATUT + "='ACTIVE'",
                new String[]{String.valueOf(clientId)}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return -1;
        }
        cursor.close();

        return database.delete(DatabaseHelper.TABLE_CLIENT,
                DatabaseHelper.COL_CLIENT_ID + "=?", new String[]{String.valueOf(clientId)});
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CLIENT, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Client client = new Client(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_NOM)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_PRENOM)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_TELEPHONE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_USER_ID))
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
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_NOM)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_PRENOM)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_TELEPHONE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_USER_ID))
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
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_NOM)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_PRENOM)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_TELEPHONE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_USER_ID))
            );
            cursor.close();
            return client;
        }
        return null;
    }

    // Reservation queries
    public long addReservation(Reservation reservation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RES_CLIENT_ID, reservation.getClientId());
        values.put(DatabaseHelper.COL_RES_CAR_ID, reservation.getCarId());
        values.put(DatabaseHelper.COL_RES_DATE_DEBUT, reservation.getDateDebut());
        values.put(DatabaseHelper.COL_RES_DATE_FIN, reservation.getDateFin());
        values.put(DatabaseHelper.COL_RES_PRIX_TOTAL, reservation.getPrixTotal());
        values.put(DatabaseHelper.COL_RES_STATUT, reservation.getStatut());

        long result = database.insert(DatabaseHelper.TABLE_RESERVATION, null, values);

        if (result != -1) {
            ContentValues carValues = new ContentValues();
            carValues.put(DatabaseHelper.COL_CAR_DISPONIBLE, 0);
            database.update(DatabaseHelper.TABLE_CAR, carValues,
                    DatabaseHelper.COL_CAR_ID + "=?", new String[]{String.valueOf(reservation.getCarId())});
        }

        return result;
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

    public int cancelReservation(int reservationId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RES_STATUT, "CANCELLED");

        Cursor cursor = database.query(DatabaseHelper.TABLE_RESERVATION,
                new String[]{DatabaseHelper.COL_RES_CAR_ID},
                DatabaseHelper.COL_RES_ID + "=?", new String[]{String.valueOf(reservationId)},
                null, null, null);

        int carId = -1;
        if (cursor.moveToFirst()) {
            carId = cursor.getInt(0);
        }
        cursor.close();

        int result = database.update(DatabaseHelper.TABLE_RESERVATION, values,
                DatabaseHelper.COL_RES_ID + "=?", new String[]{String.valueOf(reservationId)});

        if (result > 0 && carId != -1) {
            Cursor checkCursor = database.query(DatabaseHelper.TABLE_RESERVATION,
                    new String[]{DatabaseHelper.COL_RES_ID},
                    DatabaseHelper.COL_RES_CAR_ID + "=? AND " + DatabaseHelper.COL_RES_STATUT + "='ACTIVE'",
                    new String[]{String.valueOf(carId)}, null, null, null);

            if (checkCursor.getCount() == 0) {
                ContentValues carValues = new ContentValues();
                carValues.put(DatabaseHelper.COL_CAR_DISPONIBLE, 1);
                database.update(DatabaseHelper.TABLE_CAR, carValues,
                        DatabaseHelper.COL_CAR_ID + "=?", new String[]{String.valueOf(carId)});
            }
            checkCursor.close();
        }

        return result;
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
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_RES_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_RES_CLIENT_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_RES_CAR_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_RES_DATE_DEBUT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_RES_DATE_FIN)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_RES_PRIX_TOTAL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_RES_STATUT))
                );
                reservation.setClientName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_PRENOM)) + " " +
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_NOM)));
                reservation.setCarName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MARQUE)) + " " +
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MODELE)));
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
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_RES_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_RES_CLIENT_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_RES_CAR_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_RES_DATE_DEBUT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_RES_DATE_FIN)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_RES_PRIX_TOTAL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_RES_STATUT))
                );
                reservation.setClientName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_PRENOM)) + " " +
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CLIENT_NOM)));
                reservation.setCarName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MARQUE)) + " " +
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CAR_MODELE)));
                reservations.add(reservation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }
}
package com.carrental.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "car_rental.db";
    private static final int DATABASE_VERSION = 4;

    // Table names
    public static final String TABLE_USER = "Utilisateur";
    public static final String TABLE_CAR = "Voiture";
    public static final String TABLE_CLIENT = "Client";
    public static final String TABLE_RESERVATION = "Reservation";
    public static final String TABLE_PAYMENT = "Payment";

    // User table columns
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role";

    // Car table columns
    public static final String COL_CAR_ID = "id";
    public static final String COL_CAR_MARQUE = "marque";
    public static final String COL_CAR_MODELE = "modele";
    public static final String COL_CAR_ANNEE = "annee";
    public static final String COL_CAR_PRIX_JOUR = "prix_jour";
    public static final String COL_CAR_DISPONIBLE = "disponible";

    // Client table columns
    public static final String COL_CLIENT_ID = "id";
    public static final String COL_CLIENT_NOM = "nom";
    public static final String COL_CLIENT_PRENOM = "prenom";
    public static final String COL_CLIENT_EMAIL = "email";
    public static final String COL_CLIENT_TELEPHONE = "telephone";
    public static final String COL_CLIENT_USER_ID = "user_id";
    public static final String COL_CLIENT_SOLDE = "solde";

    // Reservation table columns
    public static final String COL_RES_ID = "id";
    public static final String COL_RES_CLIENT_ID = "client_id";
    public static final String COL_RES_CAR_ID = "voiture_id";
    public static final String COL_RES_DATE_DEBUT = "date_debut";
    public static final String COL_RES_DATE_FIN = "date_fin";
    public static final String COL_RES_PRIX_TOTAL = "prix_total";
    public static final String COL_RES_STATUT = "statut";

    // Payment table columns
    public static final String COL_PAY_ID = "id";
    public static final String COL_PAY_RES_ID = "reservation_id";
    public static final String COL_PAY_CLIENT_ID = "client_id";
    public static final String COL_PAY_AMOUNT = "amount";
    public static final String COL_PAY_DATE = "payment_date";

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USER_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COL_USER_PASSWORD + " TEXT NOT NULL, " +
                    COL_USER_ROLE + " TEXT NOT NULL)";

    private static final String CREATE_CAR_TABLE =
            "CREATE TABLE " + TABLE_CAR + " (" +
                    COL_CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CAR_MARQUE + " TEXT NOT NULL, " +
                    COL_CAR_MODELE + " TEXT NOT NULL, " +
                    COL_CAR_ANNEE + " INTEGER NOT NULL, " +
                    COL_CAR_PRIX_JOUR + " REAL NOT NULL, " +
                    COL_CAR_DISPONIBLE + " INTEGER DEFAULT 1)";

    private static final String CREATE_CLIENT_TABLE =
            "CREATE TABLE " + TABLE_CLIENT + " (" +
                    COL_CLIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CLIENT_NOM + " TEXT NOT NULL, " +
                    COL_CLIENT_PRENOM + " TEXT NOT NULL, " +
                    COL_CLIENT_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COL_CLIENT_TELEPHONE + " TEXT NOT NULL, " +
                    COL_CLIENT_USER_ID + " INTEGER UNIQUE, " +
                    COL_CLIENT_SOLDE + " REAL DEFAULT 0, " +
                    "FOREIGN KEY(" + COL_CLIENT_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "))";

    private static final String CREATE_RESERVATION_TABLE =
            "CREATE TABLE " + TABLE_RESERVATION + " (" +
                    COL_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RES_CLIENT_ID + " INTEGER NOT NULL, " +
                    COL_RES_CAR_ID + " INTEGER NOT NULL, " +
                    COL_RES_DATE_DEBUT + " TEXT NOT NULL, " +
                    COL_RES_DATE_FIN + " TEXT NOT NULL, " +
                    COL_RES_PRIX_TOTAL + " REAL NOT NULL, " +
                    COL_RES_STATUT + " TEXT DEFAULT 'ACTIVE', " +
                    "FOREIGN KEY(" + COL_RES_CLIENT_ID + ") REFERENCES " + TABLE_CLIENT + "(" + COL_CLIENT_ID + "), " +
                    "FOREIGN KEY(" + COL_RES_CAR_ID + ") REFERENCES " + TABLE_CAR + "(" + COL_CAR_ID + "))";

    private static final String CREATE_PAYMENT_TABLE =
            "CREATE TABLE " + TABLE_PAYMENT + " (" +
                    COL_PAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PAY_RES_ID + " INTEGER NOT NULL, " +
                    COL_PAY_CLIENT_ID + " INTEGER NOT NULL, " +
                    COL_PAY_AMOUNT + " REAL NOT NULL, " +
                    COL_PAY_DATE + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COL_PAY_RES_ID + ") REFERENCES " + TABLE_RESERVATION + "(" + COL_RES_ID + "), " +
                    "FOREIGN KEY(" + COL_PAY_CLIENT_ID + ") REFERENCES " + TABLE_CLIENT + "(" + COL_CLIENT_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CAR_TABLE);
        db.execSQL(CREATE_CLIENT_TABLE);
        db.execSQL(CREATE_RESERVATION_TABLE);
        db.execSQL(CREATE_PAYMENT_TABLE);

        // Insert admin user
        ContentValues values = new ContentValues();
        values.put(COL_USER_USERNAME, "admin");
        values.put(COL_USER_PASSWORD, "admin123");
        values.put(COL_USER_ROLE, "Administrateur");
        long adminId = db.insert(TABLE_USER, null, values);

        // Insert employee user
        values = new ContentValues();
        values.put(COL_USER_USERNAME, "employee");
        values.put(COL_USER_PASSWORD, "employee123");
        values.put(COL_USER_ROLE, "Employé");
        long employeeId = db.insert(TABLE_USER, null, values);

        // Insert client user
        values = new ContentValues();
        values.put(COL_USER_USERNAME, "client1");
        values.put(COL_USER_PASSWORD, "client123");
        values.put(COL_USER_ROLE, "Client");
        long clientUserId = db.insert(TABLE_USER, null, values);

        // Insert client details
        values = new ContentValues();
        values.put(COL_CLIENT_NOM, "Martin");
        values.put(COL_CLIENT_PRENOM, "Sophie");
        values.put(COL_CLIENT_EMAIL, "sophie.martin@email.com");
        values.put(COL_CLIENT_TELEPHONE, "0612345678");
        values.put(COL_CLIENT_USER_ID, clientUserId);
        values.put(COL_CLIENT_SOLDE, 1000.0); // Sample balance
        long clientId = db.insert(TABLE_CLIENT, null, values);

        // Insert sample cars
        values = new ContentValues();
        values.put(COL_CAR_MARQUE, "Renault");
        values.put(COL_CAR_MODELE, "Clio");
        values.put(COL_CAR_ANNEE, 2022);
        values.put(COL_CAR_PRIX_JOUR, 50.0);
        values.put(COL_CAR_DISPONIBLE, 1);
        long carId1 = db.insert(TABLE_CAR, null, values);

        values = new ContentValues();
        values.put(COL_CAR_MARQUE, "Peugeot");
        values.put(COL_CAR_MODELE, "208");
        values.put(COL_CAR_ANNEE, 2023);
        values.put(COL_CAR_PRIX_JOUR, 60.0);
        values.put(COL_CAR_DISPONIBLE, 1);
        long carId2 = db.insert(TABLE_CAR, null, values);

        // Insert sample reservation
        values = new ContentValues();
        values.put(COL_RES_CLIENT_ID, clientId);
        values.put(COL_RES_CAR_ID, carId1);
        values.put(COL_RES_DATE_DEBUT, "2024-01-15");
        values.put(COL_RES_DATE_FIN, "2024-01-20");
        values.put(COL_RES_PRIX_TOTAL, 250.0);
        values.put(COL_RES_STATUT, "ACTIVE");
        db.insert(TABLE_RESERVATION, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_CLIENT + " ADD COLUMN " + COL_CLIENT_SOLDE + " REAL DEFAULT 0");
            db.execSQL(CREATE_PAYMENT_TABLE);
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            onCreate(db);
        }
    }
}
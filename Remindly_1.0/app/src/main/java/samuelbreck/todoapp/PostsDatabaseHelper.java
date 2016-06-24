package samuelbreck.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PostsDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "postsDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TASKS = "tasks";

    // Tasks Table Columns
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_USERNAME = "userName";
    private static final String KEY_TASK_DATE = "dueDate";
    private static final String KEY_TASK_PRIORITY = "priority";
    private static final String KEY_TASK_NOTE = "taskNote";

    private static PostsDatabaseHelper sInstance;

    public static synchronized PostsDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new PostsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private PostsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS +
                "(" +
                KEY_TASK_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TASK_USERNAME + " TEXT, " + // Define a foreign key
                KEY_TASK_DATE + " TEXT, " +
                KEY_TASK_PRIORITY + " TEXT, " +
                KEY_TASK_NOTE + " TEXT" +
                ")";

        db.execSQL(CREATE_TASKS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

            onCreate(db);
        }
    }

    // Inset a task into the database
    public void addTask(Task task) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();


        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();

        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).

            long userId = addOrUpdateTask(task);

            ContentValues values = new ContentValues();

            values.put(KEY_TASK_ID, userId);

            values.put(KEY_TASK_USERNAME, task.taskName);
            values.put(KEY_TASK_DATE, task.dueDate);
            values.put(KEY_TASK_PRIORITY, task.priority);
            values.put(KEY_TASK_NOTE, task.taskNote);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TASKS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            System.err.println("Error while trying to add task to database");
        } finally {
            db.endTransaction();
        }
    }

    // Insert or update a task in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // task already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addOrUpdateTask(Task task) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            // reenter the new info and see if there is a new user!
            values.put(KEY_TASK_USERNAME, task.taskName);
            values.put(KEY_TASK_DATE, task.dueDate);
            values.put(KEY_TASK_PRIORITY, task.priority);
            values.put(KEY_TASK_NOTE, task.taskNote);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_TASKS, values, KEY_TASK_USERNAME + "= ?", new String[]{task.taskName});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_TASK_ID, TABLE_TASKS, KEY_TASK_USERNAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(task.taskName)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(TABLE_TASKS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            System.err.println("Error while trying to add or update task");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    // Get all tasks in the database
    public List<Task> getAllTasks() {

        List<Task> tasks = new ArrayList<Task>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String TASKS_SELECT_QUERY = "SELECT * FROM " + TABLE_TASKS;

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Task newTask = new Task();
                    newTask.taskName = cursor.getString(cursor.getColumnIndex(KEY_TASK_USERNAME));
                    newTask.dueDate = cursor.getString(cursor.getColumnIndex(KEY_TASK_DATE));
                    newTask.priority = cursor.getString(cursor.getColumnIndex(KEY_TASK_PRIORITY));
                    newTask.taskNote = cursor.getString(cursor.getColumnIndex(KEY_TASK_NOTE));

                    tasks.add(newTask);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            System.err.println("Error while trying to get tasks from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tasks;
    }
}
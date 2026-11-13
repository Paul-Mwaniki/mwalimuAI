package ke.co.mwalimuai.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ke.co.mwalimuai.utils.JsonImporter;

@Database(entities = {CurriculumEntity.class, ActivityEntity.class, LessonEntity.class}, version = 1, exportSchema = false)
public abstract class MwalimuDatabase extends RoomDatabase {

    public abstract CurriculumDao curriculumDao();

    private static volatile MwalimuDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MwalimuDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (MwalimuDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MwalimuDatabase.class,
                            "mwalimu_ai_database"
                    )
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Prepopulate DB from assets asynchronously on first creation
                            JsonImporter.populateDatabaseIfEmpty(context.getApplicationContext(), INSTANCE, null);
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}

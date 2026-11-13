package ke.co.mwalimuai.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CurriculumDao {

    // --- Curriculum Queries ---
    @Query("SELECT * FROM curriculum_table ORDER BY strand_code, sub_strand_code")
    List<CurriculumEntity> getAllCurriculum();

    @Query("SELECT DISTINCT strand FROM curriculum_table ORDER BY strand_code")
    List<String> getDistinctStrands();

    @Query("SELECT * FROM curriculum_table WHERE strand = :strand ORDER BY sub_strand_code")
    List<CurriculumEntity> getCurriculumByStrand(String strand);

    @Query("SELECT * FROM curriculum_table WHERE curriculum_id = :curriculumId LIMIT 1")
    CurriculumEntity getCurriculumById(String curriculumId);

    @Query("SELECT * FROM curriculum_table WHERE sub_strand = :subStrand LIMIT 1")
    CurriculumEntity getCurriculumBySubStrand(String subStrand);

    @Query("SELECT COUNT(*) FROM curriculum_table")
    int getCurriculumCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCurriculumList(List<CurriculumEntity> curriculumList);

    // --- Activity Queries ---
    @Query("SELECT * FROM activity_table ORDER BY activity_id")
    List<ActivityEntity> getAllActivities();

    @Query("SELECT * FROM activity_table WHERE topic = :topic OR curriculum_id = :curriculumId")
    List<ActivityEntity> getActivitiesByTopicOrCurriculum(String topic, String curriculumId);

    @Query("SELECT * FROM activity_table WHERE activity_id = :activityId LIMIT 1")
    ActivityEntity getActivityById(String activityId);

    @Query("SELECT COUNT(*) FROM activity_table")
    int getActivityCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivities(List<ActivityEntity> activities);

    // --- Lesson Plan Queries ---
    @Query("SELECT * FROM lesson_table ORDER BY createdTimestamp DESC")
    LiveData<List<LessonEntity>> getAllLessonsLiveData();

    @Query("SELECT * FROM lesson_table ORDER BY createdTimestamp DESC")
    List<LessonEntity> getAllLessons();

    @Query("SELECT * FROM lesson_table WHERE id = :id LIMIT 1")
    LessonEntity getLessonById(long id);

    @Query("SELECT COUNT(*) FROM lesson_table")
    int getLessonCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLesson(LessonEntity lesson);

    @Update
    void updateLesson(LessonEntity lesson);

    @Delete
    void deleteLesson(LessonEntity lesson);

    @Query("DELETE FROM lesson_table WHERE id = :id")
    void deleteLessonById(long id);
}

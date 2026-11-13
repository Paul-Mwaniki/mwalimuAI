package ke.co.mwalimuai.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity representing a generated or saved CBC Lesson Plan.
 */
@Entity(tableName = "lesson_table")
public class LessonEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String lessonId = "";
    private String curriculumId = "";
    private int grade = 6;
    private String learningArea = "Mathematics";
    private String strand = "";
    private String subStrand = "";
    private String lessonTitle = "";
    private int durationMinutes = 40;
    private int classSize = 40;
    private String learningObjective = "";
    private String introduction = "";
    private String mainActivities = ""; // Formatted text or JSON
    private String lessonDevelopment = "";
    private String assessmentMethods = "";
    private String reflectionQuestion = "";
    private String resourcesNeeded = "";
    private String largeClassAdaptation = "";
    private String inclusionNotes = "";
    private long createdTimestamp = System.currentTimeMillis();

    public LessonEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(String curriculumId) {
        this.curriculumId = curriculumId;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getLearningArea() {
        return learningArea;
    }

    public void setLearningArea(String learningArea) {
        this.learningArea = learningArea;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getSubStrand() {
        return subStrand;
    }

    public void setSubStrand(String subStrand) {
        this.subStrand = subStrand;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getClassSize() {
        return classSize;
    }

    public void setClassSize(int classSize) {
        this.classSize = classSize;
    }

    public String getLearningObjective() {
        return learningObjective;
    }

    public void setLearningObjective(String learningObjective) {
        this.learningObjective = learningObjective;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getMainActivities() {
        return mainActivities;
    }

    public void setMainActivities(String mainActivities) {
        this.mainActivities = mainActivities;
    }

    public String getLessonDevelopment() {
        return lessonDevelopment;
    }

    public void setLessonDevelopment(String lessonDevelopment) {
        this.lessonDevelopment = lessonDevelopment;
    }

    public String getAssessmentMethods() {
        return assessmentMethods;
    }

    public void setAssessmentMethods(String assessmentMethods) {
        this.assessmentMethods = assessmentMethods;
    }

    public String getReflectionQuestion() {
        return reflectionQuestion;
    }

    public void setReflectionQuestion(String reflectionQuestion) {
        this.reflectionQuestion = reflectionQuestion;
    }

    public String getResourcesNeeded() {
        return resourcesNeeded;
    }

    public void setResourcesNeeded(String resourcesNeeded) {
        this.resourcesNeeded = resourcesNeeded;
    }

    public String getLargeClassAdaptation() {
        return largeClassAdaptation;
    }

    public void setLargeClassAdaptation(String largeClassAdaptation) {
        this.largeClassAdaptation = largeClassAdaptation;
    }

    public String getInclusionNotes() {
        return inclusionNotes;
    }

    public void setInclusionNotes(String inclusionNotes) {
        this.inclusionNotes = inclusionNotes;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}

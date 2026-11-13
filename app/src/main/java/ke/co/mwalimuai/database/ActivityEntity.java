package ke.co.mwalimuai.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Room Entity representing a CBC learning activity.
 */
@Entity(tableName = "activity_table")
public class ActivityEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "activity_id")
    @SerializedName("activity_id")
    private String activityId = "";

    @ColumnInfo(name = "curriculum_id")
    @SerializedName("curriculum_id")
    private String curriculumId = "";

    @ColumnInfo(name = "grade")
    @SerializedName("grade")
    private int grade = 6;

    @ColumnInfo(name = "topic")
    @SerializedName("topic")
    private String topic = "";

    @ColumnInfo(name = "activity_title")
    @SerializedName("activity_title")
    private String activityTitle = "";

    @ColumnInfo(name = "strategy")
    @SerializedName("strategy")
    private String strategy = "";

    @ColumnInfo(name = "objective_focus")
    @SerializedName("objective_focus")
    private String objectiveFocus = "";

    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String description = "";

    @ColumnInfo(name = "teacher_preparation")
    @SerializedName("teacher_preparation")
    private String teacherPreparation = "";

    @ColumnInfo(name = "learner_steps")
    @SerializedName("learner_steps")
    private String learnerSteps = "";

    @ColumnInfo(name = "assessment_method")
    @SerializedName("assessment_method")
    private String assessmentMethod = "";

    @ColumnInfo(name = "duration_minutes")
    @SerializedName("duration_minutes")
    private int durationMinutes = 10;

    @ColumnInfo(name = "class_size_min")
    @SerializedName("class_size_min")
    private int classSizeMin = 10;

    @ColumnInfo(name = "class_size_max")
    @SerializedName("class_size_max")
    private int classSizeMax = 60;

    @ColumnInfo(name = "resources")
    @SerializedName("resources")
    private String resources = "";

    @ColumnInfo(name = "internet_required")
    @SerializedName("internet_required")
    private boolean internetRequired = false;

    @ColumnInfo(name = "electricity_required")
    @SerializedName("electricity_required")
    private boolean electricityRequired = false;

    @ColumnInfo(name = "grouping")
    @SerializedName("grouping")
    private String grouping = "";

    @ColumnInfo(name = "differentiation_support")
    @SerializedName("differentiation_support")
    private String differentiationSupport = "";

    @ColumnInfo(name = "differentiation_extension")
    @SerializedName("differentiation_extension")
    private String differentiationExtension = "";

    @ColumnInfo(name = "inclusion")
    @SerializedName("inclusion")
    private String inclusion = "";

    @ColumnInfo(name = "competency_focus")
    @SerializedName("competency_focus")
    private String competencyFocus = "";

    @ColumnInfo(name = "local_context")
    @SerializedName("local_context")
    private String localContext = "";

    // Transient or cached TinyML predicted suitability score (1.0 to 5.0)
    @Ignore
    private float aiSuitabilityScore = 0.0f;

    public ActivityEntity() {
    }

    @NonNull
    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(@NonNull String activityId) {
        this.activityId = activityId;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getObjectiveFocus() {
        return objectiveFocus;
    }

    public void setObjectiveFocus(String objectiveFocus) {
        this.objectiveFocus = objectiveFocus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherPreparation() {
        return teacherPreparation;
    }

    public void setTeacherPreparation(String teacherPreparation) {
        this.teacherPreparation = teacherPreparation;
    }

    public String getLearnerSteps() {
        return learnerSteps;
    }

    public void setLearnerSteps(String learnerSteps) {
        this.learnerSteps = learnerSteps;
    }

    public String getAssessmentMethod() {
        return assessmentMethod;
    }

    public void setAssessmentMethod(String assessmentMethod) {
        this.assessmentMethod = assessmentMethod;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getClassSizeMin() {
        return classSizeMin;
    }

    public void setClassSizeMin(int classSizeMin) {
        this.classSizeMin = classSizeMin;
    }

    public int getClassSizeMax() {
        return classSizeMax;
    }

    public void setClassSizeMax(int classSizeMax) {
        this.classSizeMax = classSizeMax;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public boolean isInternetRequired() {
        return internetRequired;
    }

    public void setInternetRequired(boolean internetRequired) {
        this.internetRequired = internetRequired;
    }

    public boolean isElectricityRequired() {
        return electricityRequired;
    }

    public void setElectricityRequired(boolean electricityRequired) {
        this.electricityRequired = electricityRequired;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public String getDifferentiationSupport() {
        return differentiationSupport;
    }

    public void setDifferentiationSupport(String differentiationSupport) {
        this.differentiationSupport = differentiationSupport;
    }

    public String getDifferentiationExtension() {
        return differentiationExtension;
    }

    public void setDifferentiationExtension(String differentiationExtension) {
        this.differentiationExtension = differentiationExtension;
    }

    public String getInclusion() {
        return inclusion;
    }

    public void setInclusion(String inclusion) {
        this.inclusion = inclusion;
    }

    public String getCompetencyFocus() {
        return competencyFocus;
    }

    public void setCompetencyFocus(String competencyFocus) {
        this.competencyFocus = competencyFocus;
    }

    public String getLocalContext() {
        return localContext;
    }

    public void setLocalContext(String localContext) {
        this.localContext = localContext;
    }

    public float getAiSuitabilityScore() {
        return aiSuitabilityScore;
    }

    public void setAiSuitabilityScore(float aiSuitabilityScore) {
        this.aiSuitabilityScore = aiSuitabilityScore;
    }
}

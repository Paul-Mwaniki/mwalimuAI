package ke.co.mwalimuai.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Room Entity representing a Grade 6 Mathematics curriculum sub-strand from KICD.
 */
@Entity(tableName = "curriculum_table")
public class CurriculumEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "curriculum_id")
    @SerializedName("curriculum_id")
    private String curriculumId = "";

    @ColumnInfo(name = "grade")
    @SerializedName("grade")
    private int grade = 6;

    @ColumnInfo(name = "learning_area")
    @SerializedName("learning_area")
    private String learningArea = "Mathematics";

    @ColumnInfo(name = "strand_code")
    @SerializedName("strand_code")
    private String strandCode = "";

    @ColumnInfo(name = "strand")
    @SerializedName("strand")
    private String strand = "";

    @ColumnInfo(name = "sub_strand_code")
    @SerializedName("sub_strand_code")
    private String subStrandCode = "";

    @ColumnInfo(name = "sub_strand")
    @SerializedName("sub_strand")
    private String subStrand = "";

    @ColumnInfo(name = "lessons_allocated")
    @SerializedName("lessons_allocated")
    private int lessonsAllocated = 0;

    @ColumnInfo(name = "curriculum_aligned_focus")
    @SerializedName("curriculum_aligned_focus")
    private String curriculumAlignedFocus = "";

    @ColumnInfo(name = "source_pdf_pages")
    @SerializedName("source_pdf_pages")
    private String sourcePdfPages = "";

    @ColumnInfo(name = "source_document")
    @SerializedName("source_document")
    private String sourceDocument = "";

    @ColumnInfo(name = "local_resources")
    @SerializedName("local_resources")
    private String localResources = "";

    @ColumnInfo(name = "kenyan_context")
    @SerializedName("kenyan_context")
    private String kenyanContext = "";

    public CurriculumEntity() {
    }

    @NonNull
    public String getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(@NonNull String curriculumId) {
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

    public String getStrandCode() {
        return strandCode;
    }

    public void setStrandCode(String strandCode) {
        this.strandCode = strandCode;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getSubStrandCode() {
        return subStrandCode;
    }

    public void setSubStrandCode(String subStrandCode) {
        this.subStrandCode = subStrandCode;
    }

    public String getSubStrand() {
        return subStrand;
    }

    public void setSubStrand(String subStrand) {
        this.subStrand = subStrand;
    }

    public int getLessonsAllocated() {
        return lessonsAllocated;
    }

    public void setLessonsAllocated(int lessonsAllocated) {
        this.lessonsAllocated = lessonsAllocated;
    }

    public String getCurriculumAlignedFocus() {
        return curriculumAlignedFocus;
    }

    public void setCurriculumAlignedFocus(String curriculumAlignedFocus) {
        this.curriculumAlignedFocus = curriculumAlignedFocus;
    }

    public String getSourcePdfPages() {
        return sourcePdfPages;
    }

    public void setSourcePdfPages(String sourcePdfPages) {
        this.sourcePdfPages = sourcePdfPages;
    }

    public String getSourceDocument() {
        return sourceDocument;
    }

    public void setSourceDocument(String sourceDocument) {
        this.sourceDocument = sourceDocument;
    }

    public String getLocalResources() {
        return localResources;
    }

    public void setLocalResources(String localResources) {
        this.localResources = localResources;
    }

    public String getKenyanContext() {
        return kenyanContext;
    }

    public void setKenyanContext(String kenyanContext) {
        this.kenyanContext = kenyanContext;
    }
}

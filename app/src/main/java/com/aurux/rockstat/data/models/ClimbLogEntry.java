package com.aurux.rockstat.data.models;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "climb_log_entries")
public class ClimbLogEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "timestamp")
    private long timestamp;
    @ColumnInfo(name = "climbing_type")
    private String climbingType;
    @ColumnInfo(name = "route_name")
    private String routeName;
    @ColumnInfo(name = "grade")
    private String grade;
    @ColumnInfo(name = "attempts")
    private int attempts;
    @ColumnInfo(name = "completed")
    private boolean completed;
    @ColumnInfo(name = "comment")
    private String comment;
    @ColumnInfo(name = "rating")
    private int rating;
    @ColumnInfo(name = "location")
    private String selectedPlace;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getClimbingType() {
        return climbingType;
    }

    public void setClimbingType(String climbingType) {
        this.climbingType = climbingType;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(String selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    @Override
    public String toString() {
        return "ClimbLogEntry{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", climbingType='" + climbingType + '\'' +
                ", routeName='" + routeName + '\'' +
                ", grade='" + grade + '\'' +
                ", attempts=" + attempts +
                ", completed=" + completed +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", selectedPlace='" + selectedPlace + '\'' +
                '}';
    }




}

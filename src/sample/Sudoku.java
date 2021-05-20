package sample;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.Date;


public class Sudoku {
    private final ObjectProperty<LocalDateTime> startTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final IntegerProperty spentTime;
    private final StringProperty answer;
    private final StringProperty problem;

    // 디폴트 생성자
    public Sudoku() {
        this(null, 0);
    }

    // 데이터를 초기화하는 생성자
    public Sudoku(Date startTime, int spentTime) {
        this.startTime = new SimpleObjectProperty(startTime);
        this.spentTime = new SimpleIntegerProperty(spentTime);

        // 테스트를 위해 초기화하는 더미 데이터
        this.endTime = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.of(2019, 2, 21, 12, 25, 3333));
        this.answer = new SimpleStringProperty("answer");
        this.problem = new SimpleStringProperty("question");
    }

    public LocalDateTime getStartTime() {
        return startTime.get();
    }

    public ObjectProperty<LocalDateTime> startTimeProperty() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime.set(startTime);
    }

    public LocalDateTime getEndTime() {
        return endTime.get();
    }

    public ObjectProperty<LocalDateTime> endTimeProperty() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime.set(endTime);
    }

    public int getSpentTime() {
        return spentTime.get();
    }

    public IntegerProperty spentTimeProperty() {
        return spentTime;
    }

    public void setSpentTime(int spentTime) {
        this.spentTime.set(spentTime);
    }

    public String getAnswer() {
        return answer.get();
    }

    public StringProperty answerProperty() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer.set(answer);
    }

    public String getProblem() {
        return problem.get();
    }

    public StringProperty problemProperty() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem.set(problem);
    }
}
package sample;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Sudoku {
    private final IntegerProperty id;
    private final StringProperty nickname;
    private final StringProperty startTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final IntegerProperty spentTime;
    private final StringProperty answer;
    private final StringProperty problem;

    private static final String DATE_FORMATTER = "yyyy년 MM월 dd일 HH시 mm분 ss초";

    // 디폴트 생성자
    public Sudoku(StringProperty problem) {
        this(0, null, null, null, 0, null, null);
    }

    // 데이터를 초기화하는 생성자
    public Sudoku(int id, String nickname, LocalDateTime startTime, LocalDateTime endTime, int spentTime, String problem, String answer) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String formatStartTime = startTime.format(formatter);

        this.id = new SimpleIntegerProperty(id);
        this.nickname = new SimpleStringProperty(nickname);
        this.startTime = new SimpleStringProperty(formatStartTime);
        this.spentTime = new SimpleIntegerProperty(spentTime);

        // 테스트를 위해 초기화하는 더미 데이터
        this.endTime = new SimpleObjectProperty<LocalDateTime>(endTime);
        this.answer = new SimpleStringProperty(answer);
        this.problem = new SimpleStringProperty(problem);
    }

    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public IntegerProperty getId() {
        return id;
    }

    public String getStartTime() {
        return startTime.get();
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public void setStartTime(String startTime) {
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

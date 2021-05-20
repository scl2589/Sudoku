package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import static javafx.scene.control.ButtonType.OK;

public class Controller implements Initializable {

    @FXML private GridPane gp_sudoku_pane;
    @FXML private Button btn_generate;
    @FXML private Button btn_confirm;
    @FXML private Button btn_answer;
    @FXML private Button btn_delete;
    @FXML private Label timer_label;
    @FXML private TableView<Sudoku> sudokuTable;
    @FXML private TableColumn<Sudoku, String> startTimeColumn;
    @FXML private TableColumn<Sudoku, Integer> spentTimeColumn;


    private ArrayList<ArrayList<TextField>> arr;
    private ArrayList<Integer> removedArr;
    private ArrayList<Integer> allElements;
    private ArrayList<ArrayList<Integer>> answer;
    private StringBuilder question;

    private int count;
    private Timeline timeline;
    private Date start_time;
    private Date end_time;
    private StringBuilder sudokuAnswer;

    HashSet<Integer>[] rows = new HashSet[9];
    HashSet<Integer>[] cols = new HashSet[9];
    HashSet<Integer>[] box = new HashSet[9];

    HashSet<Integer>[] checkRows = new HashSet[9];
    HashSet<Integer>[] checkCols = new HashSet[9];
    HashSet<Integer>[] checkBox = new HashSet[9];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        arr = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            ArrayList<TextField> row = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                TextField tf = new TextField("");

                // text field 의 크기와 정렬 조정
                tf.setPrefSize(50, 55);
                tf.setAlignment(Pos.CENTER);

                // sudoku 처럼 보이기 위해 각 row/col 값을 계산해 outline 생성
                if (i % 3 == 2 && j % 3 == 2) {
                    tf.setStyle("-fx-border-width: 0 2 2 0; -fx-border-color: #364f6b;");
                } else if (i % 3 == 2 && j == 0) {
                    tf.setStyle("-fx-border-width: 0 0 2 2; -fx-border-color: #364f6b;");
                } else if (i == 0 && j ==0) {
                    tf.setStyle("-fx-border-width: 2 0 0 2; -fx-border-color: #364f6b;");
                } else if (i == 0 && j % 3 == 2) {
                    tf.setStyle("-fx-border-width: 2 2 0 0; -fx-border-color: #364f6b;");
                } else if (i % 3 == 2) {
                    tf.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: #364f6b;");
                } else if (j % 3 == 2) {
                    tf.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: #364f6b;");
                } else if ( j == 0) {
                    tf.setStyle("-fx-border-width: 0 0 0 2; -fx-border-color: #364f6b;");
                } else if ( i == 0) {
                    tf.setStyle("-fx-border-width: 2 0 0 0; -fx-border-color: #364f6b;");
                }

                row.add(tf);
                gp_sudoku_pane.add(row.get(row.size()-1), j, i);
            }
            arr.add(row);

        }
        timer_label.setStyle("-fx-font-size: 1.5em;");

        // 테이블 초기화하기
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        spentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().spentTimeProperty().asObject());

        sudokuTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSudokuGame(newValue));

        // 테이블에 observable 리스트 데이터를 추가한다.
        SQLiteManager manager = new SQLiteManager();
        manager.selectSudokuList();
        sudokuTable.setItems(manager.getSudokuData());
    }

    @FXML
    public void handleGenerate() {
        generateRandom();
    }

    @FXML
    public void handleConfirm() throws SQLException {
        // 아직 게임이 생성되지 않았을 경우 
        if (answer == null) {
            Alert alert = createAlert("warning", null, "Sudoku 게임 미시작", "Sudoku 게임을 아직 시작하지 않았습니다. \n게임 생성 후, 게임 결과를 확인하기 위해 눌러주세요.");
            alert.showAndWait();
        } else { // 게임이 생성된 경우
            // 정답일 경우
            if (correct()) {
                // 타이머 중지 
                timeline.stop();

                // 게임이 끝났으므로 게임 끝난 시각 저장
                end_time = new Date();

                // 정답을 String 형태로 변환하기
                sudokuAnswer = new StringBuilder();
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        sudokuAnswer.append(answer.get(i).get(j)).append(" ");
                    }
                    sudokuAnswer.append("\n");
                }
                // DB에 데이터 추가
                insertData(start_time, end_time, count, sudokuAnswer.toString(), question.toString());

                // alert 창 생성
                Alert alert = createAlert("information", "Sudoku 게임 결과", "Sudoku 게임 결과입니다.", "정답입니다!! 축하합니다 :) \n게임 소요 시간은 총 " + count + "초 입니다." );

                // 새 게임 시작하기 버튼 생성 
                ButtonType buttonNewGame = new ButtonType("새 게임 시작하기");
                alert.getButtonTypes().setAll(buttonNewGame, OK);

                // 사용자가 어떤 버튼을 눌렀는지 결과값 받아오기 
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == buttonNewGame) { // 새 게임 시작
                    alert.hide();
                    // 시간을 0으로 초기화
                    count = 0;
                    timer_label.setText(Integer.toString(0));
                    // 새 게임을 생성한다
                    generateRandom();
                } else if (result.get() == OK) { // 확인버튼 
                    alert.hide();
                }

            } else { // 정답이 아닐 경우
                // alert 창 생성
                Alert alert  = createAlert("information", "Sudoku 게임 결과", "Sudoku 게임 결과입니다.","정답이 아닙니다. 다시 한 번 시도해보세요 :)"  );
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void handleAnswer() {
        Alert alert;
        if (answer == null ) {
            alert = createAlert("warning", null, "Sudoku 게임 미시작", "Sudoku 게임을 아직 시작하지 않았습니다. \n게임 생성 후, 정답을 확인하기 위해 눌러주세요.");
        } else {
            alert = createAlert("information", "Sudoku 게임 정답", "Sudoku 게임 정답입니다.", null);

            alert.setContentText(sudokuAnswer.toString());
        }
        alert.showAndWait();

    }

    public void timing() {
        // 만약 이미 generate 된 sudoku 퍼즐이 있는데, 다시 한 번 generate 를 눌렀을 경우, timeline 을 멈춘다.
        if (timeline != null) {
            timeline.stop();
        }
        count = 0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            count++;
            timer_label.setText(Integer.toString(count));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /*
     * Generate 버튼 이벤트 생성하기
     */
    private void generateRandom(){
        // 문제를 생성했으므로 시간을 측정하기 시작한다.
        timing();

        // 초기화한다.
        removedArr = new ArrayList<>();
        allElements = new ArrayList<>();

        for (int i = 0; i < 81; i++) {
            allElements.add(i);
        }

        // sudoku board에 있는 값 초기화 (generate 버튼을 여러 번 눌렀을 때에 sudoku board를 비워야 한다.)
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField current = arr.get(i).get(j);
                current.setText("");
                current.setEditable(true);
            }
        }
        for (int i = 0; i < 9; i++ ) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            box[i] = new HashSet<>();
        }

        // 스도쿠 완성하기 (top 3 rows and 1st column)
        generateTopLeftBox();
        generateFirstRow();
        generateTopMiddleBox();
        generateTopRightBox();
        generateFirstCol();

        // 백트래킹으로 스도쿠 완성
        // 만약 스도쿠가 완성되지 않는다면?
        if (!backtracking()) {
            Alert alert = createAlert("warning", null, "Sudoku 게임 생성 오류", "게임 생성에 오류가 발생했습니다. \nGenerate 버튼을 다시 한 번 눌러주세요.");
            alert.showAndWait();
        } else { // 스도쿠가 완성될 경우?
            // Answer 버튼을 위해 정답 배열을 만들어서 담아둔다.
            answer = new ArrayList<>(9);
            for (int i = 0; i < 9; i++) {
                ArrayList<Integer> temp = new ArrayList<>(9);
                for (int j = 0; j < 9; j++) {
                    Integer value = Integer.parseInt(arr.get(i).get(j).getText());
                    temp.add(value);
                }
                answer.add(temp);
            }
            // 스도쿠 문제를 생성하기 위해 element를 하나씩 지운다.
            removeElement();

            // 게임이 시작됐으므로 현재 시간을 저장해놓는다.
            start_time = new Date();
        }
    }

    private void generateTopLeftBox() {
        // 좌상단 박스 생성하기
        ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(numbers);
        for (int k = 0; k < 9; k++) {
            int i = k / 3, j = k % 3;
            arr.get(i).get(j).setText(Integer.toString(numbers.get(k)));

            // set 에 추가한다.
            rows[i].add(numbers.get(k));
            cols[j].add(numbers.get(k));
            box[0].add(numbers.get(k));
        }
    }



    private void generateFirstRow() {
        // 첫번째 col 생성하기
        ArrayList<Integer> firstRowAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (Integer e : rows[0]) {
            firstRowAvailable.remove(e);
        }
        Collections.shuffle(firstRowAvailable);

        for (int j = 3; j < 9; j++) {
            // text 추가한다.
            arr.get(0).get(j).setText(Integer.toString(firstRowAvailable.get(j - 3)));
            // HashSet에 저장해준다.
            rows[0].add(firstRowAvailable.get(j-3));
            cols[j].add(firstRowAvailable.get(j-3));
            int ij = j / 3;
            box[ij].add(firstRowAvailable.get(j-3));
        }
    }

    private void generateTopMiddleBox() {
        // 중앙 상단 박스 생성하기
        // 가능한 숫자 나열
        ArrayList<Integer> middleSecondRowAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // 중앙 박스에 이미 있는 요소들 제거
        for (Integer e : box[1]) {
            middleSecondRowAvailable.remove(e);
        }
        // 2번째 row에 있는 요소들 제거
        for (Integer e : rows[1]) {
            middleSecondRowAvailable.remove(e);
        }

        // 2번째 row에 필수적으로 있어야 하는 값 확인
        ArrayList<Integer> mustBeInMiddleSecondRow = new ArrayList<>();
        // 3번째 row에 숫자가 있어서 2번째 row에 필수적으로 들어가야 하는 값 추가
        for (Integer e: rows[2]) {
            if (middleSecondRowAvailable.contains(e)) {
                mustBeInMiddleSecondRow.add(e);
                middleSecondRowAvailable.remove(e);
            }
        }

        List<Integer> middleSecondRowValues = getNumbers(middleSecondRowAvailable, 3 - mustBeInMiddleSecondRow.size());
        middleSecondRowValues.addAll(mustBeInMiddleSecondRow);
        Collections.shuffle(middleSecondRowValues);

        for (int j = 3; j < 6; j++) {
            arr.get(1).get(j).setText(Integer.toString(middleSecondRowValues.get(j - 3)));
            // HashSet에 저장해준다.
            rows[1].add(middleSecondRowValues.get(j-3));
            cols[j].add(middleSecondRowValues.get(j-3));
            box[1].add(middleSecondRowValues.get(j-3));
        }

        // 마지막 세번째 줄 추가하기
        ArrayList<Integer> middleThirdRowAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        // 중앙 박스에 이미 있는 요소들 제거
        for (Integer e : box[1]) {
            middleThirdRowAvailable.remove(e);
        }
        Collections.shuffle(middleSecondRowValues);

        // Text 추가하기
        for (int j = 3; j < 6; j++) {
            // text 추가한다.
            arr.get(2).get(j).setText(Integer.toString(middleThirdRowAvailable.get(j - 3)));
            // HashSet에 저장해준다.
            rows[2].add(middleThirdRowAvailable.get(j-3));
            cols[j].add(middleThirdRowAvailable.get(j-3));
            box[1].add(middleThirdRowAvailable.get(j-3));
        }
    }

    private void generateTopRightBox() {
        ArrayList<Integer> rightSecondRowAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        ArrayList<Integer> rightThirdRowAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // 2번째 row에 있는 요소들 제거
        for (Integer e : rows[1]) {
            rightSecondRowAvailable.remove(e);
        }
        // 3번째 row에 있는 요소들 제거
        for (Integer e : rows[2]) {
            rightThirdRowAvailable.remove(e);
        }

        Collections.shuffle(rightSecondRowAvailable);
        Collections.shuffle(rightThirdRowAvailable);

        // Text 추가하기
        for (int j = 6; j < 9; j++) {
            // text 추가한다.
            arr.get(1).get(j).setText(Integer.toString(rightSecondRowAvailable.get(j - 6)));
            arr.get(2).get(j).setText(Integer.toString(rightThirdRowAvailable.get(j - 6)));
            // HashSet에 저장해준다.
            rows[1].add(rightSecondRowAvailable.get(j-6));
            rows[2].add(rightThirdRowAvailable.get(j-6));

            cols[j].add(rightSecondRowAvailable.get(j-6));
            cols[j].add(rightThirdRowAvailable.get(j-6));

            box[2].add(rightSecondRowAvailable.get(j-6));
            box[2].add(rightThirdRowAvailable.get(j-6));
        }
    }

    private void generateFirstCol() {
        ArrayList<Integer> firstColAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // 첫번째 column에 있는 요소들 제거
        for (Integer e : cols[0]) {
            firstColAvailable.remove(e);
        }

        Collections.shuffle(firstColAvailable);

        for (int i = 3; i < 9; i++) {
            arr.get(i).get(0).setText(Integer.toString(firstColAvailable.get(i-3)));

            rows[i].add(firstColAvailable.get(i-3));
            cols[0].add(firstColAvailable.get(i-3));
            int ij = (i / 3) * 3 ;
            box[ij].add(firstColAvailable.get(i-3));
        }
    }

    private List<Integer> getNumbers(ArrayList<Integer> list, int totalItems ) {
        Random rand = new Random();
        List<Integer> returnList = new ArrayList<>();
        for (int i = 0; i < totalItems; i++) {
            int randomIndex = rand.nextInt(list.size());
            returnList.add(list.get(randomIndex));
            list.remove(randomIndex);
        }
        return returnList;
    }

    private void removeElement() {
        // 삭제할 element를 랜덤으로 구하고, removedArr 리스트에 추가한다.
        int removeNum = getNumbers(allElements, 1).get(0);
        removedArr.add(removeNum);

        // 없앤 숫자를 allElements 에서 삭제한다.
//        allElements.remove(allElements.indexOf(removeNum)); -> 이게 안되는 이유??
        allElements.remove(new Integer(removeNum));

        // 없애야 할 숫자를 board에서 삭제한다.
        for (Integer e: removedArr) {
            int i = e / 9; int j = e % 9;
            int value = Integer.parseInt(arr.get(i).get(j).getText());
            rows[i].remove(value);
            cols[j].remove(value);
            int ij = (i / 3) * 3 + j / 3;
            box[ij].remove(value);
            arr.get(i).get(j).setText("");

        }

        if (removedArr.size()>= 2) {
            // Sudoku 문제를 String 형태로 변환하기
            question = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    String value = arr.get(i).get(j).getText();
                    if ("".equals(value)) {
                        question.append("_ ");
                    } else {
                        question.append(value).append(" ");
                    }
                }
                question.append("\n");
            }
            setTextFieldStyle();
        } else{
            if (backtracking()) {
                removeElement();
            }
        }
    }

    private boolean backtracking() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // 만약 해당 위치에 숫자가 있다면 패스
                if (!arr.get(i).get(j).getText().equals("")) continue;
                // 비어 있는 경우라면 해당 위치에 어떤 값이 들어갈 수 있는지 확인
                ArrayList<Integer> available = validValues(i, j);
                // 각각의 value를 넣고 가능한지 확인한다.
                for (Integer k : available) {
                    arr.get(i).get(j).setText(Integer.toString(k));
                    rows[i].add(k);
                    cols[j].add(k);
                    int ij = (i / 3) * 3 + j / 3;
                    box[ij].add(k);

                    if (backtracking()) {
                        return true;
                    }
                    else {
                        arr.get(i).get(j).setText("");
                        rows[i].remove(k);
                        cols[j].remove(k);
                        box[ij].remove(k);
                    }
                }
                return false;
            }
        }
        return true;
    }

    private ArrayList<Integer> validValues(int i, int j) {
        // 가능한 값들 선언
        ArrayList<Integer> available = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // 가로에 있는 값들을 다 제거한다.
        for (Integer e : rows[i]) {
            available.remove(e);
        }

        // 세로에 있는 값들을 다 제거한다.
        for (Integer e: cols[j]) {
            available.remove(e);
        }

        // 박스 값을 고려하며 박스 값 제거한다.
        int ij = (i / 3) * 3 + j / 3;

        for (Integer e: box[ij]) {
            available.remove(e);
        }

        return available;
    }

    private Boolean correct() {
        for (int i = 0; i < 9; i++ ) {
            checkRows[i] = new HashSet<>();
            checkCols[i] = new HashSet<>();
            checkBox[i] = new HashSet<>();
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String text = arr.get(i).get(j).getText();
                if ("".equals(text)) {
                    return false;
                }
                Integer current = Integer.parseInt(arr.get(i).get(j).getText());
                int ij = (i / 3) * 3 + j / 3;
                if (checkRows[i].contains(current) || checkCols[j].contains(current) || checkBox[ij].contains(current)) {
                    return false;
                } else {
                    checkRows[i].add(current);
                    checkCols[j].add(current);
                    checkBox[ij].add(current);
                }
            }
        }
        return true;
    }

    private void setTextFieldStyle() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField current = arr.get(i).get(j);
                // 각 textfield마다 키보드 이벤트 추가
                current.setOnKeyPressed(event -> {
                    KeyCode key = event.getCode();
                    String s = event.getText();

                    // alert 창 생성
                    Alert alert = createAlert("warning", null, "문자 기입 오류", null);
                    // Backspace 키를 제외한 후, key 분석
                    if (key != KeyCode.BACK_SPACE) {
                        // char을 통해 알파벳인지/특수문자인지 확인하기 위해
                        char ch = s.charAt(0);
                        if (key == KeyCode.DIGIT0 || key == KeyCode.NUMPAD0) {// 숫자 0을 기입하였을 경우
                            alert.setContentText("숫자 0은 기입이 불가합니다.\n숫자 1~9까지만 기입이 가능합니다.");
                            alert.showAndWait();
                        } else if (ch < 49 || ch > 57) { // 알파벳이나 특수문자를 기입하였을 경우
                            alert.setContentText("알파벳 및 특수문자는 기입이 불가합니다.\n숫자 1~9까지만 기입이 가능합니다.");
                            alert.showAndWait();
                        }
                    }
                });
                if (!"".equals(current.getText())) {
                    current.setEditable(false);
                    if (i % 3 == 2 && j % 3 == 2) {
                        current.setStyle("-fx-border-width: 0 2 2 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else if (i % 3 == 2 && j == 0) {
                        current.setStyle("-fx-border-width: 0 0 2 2; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else if (i == 0 && j ==0) {
                        current.setStyle("-fx-border-width: 2 0 0 2; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else if (i == 0 && j % 3 == 2) {
                        current.setStyle("-fx-border-width: 2 2 0 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else if (i % 3 == 2) {
                        current.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else if (j % 3 == 2) {
                        current.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else if ( j == 0) {
                        current.setStyle("-fx-border-width: 0 0 0 2; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else if ( i == 0) {
                        current.setStyle("-fx-border-width: 2 0 0 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                    } else {
                        current.setStyle("-fx-text-fill:gray");
                    }
                } else {
                    if (i % 3 == 2 && j % 3 == 2) {
                        current.setStyle("-fx-border-width: 0 2 2 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                    } else if (i == 0 && j == 0) {
                        current.setStyle("-fx-border-width: 2 0 0 2; -fx-border-color: #364f6b;-fx-text-fill:black");
                    } else if (i == 0 && j % 3 == 2 ) {
                        current.setStyle("-fx-border-width: 2 2 0 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                    } else if ( i % 3 == 2 && j == 0) {
                        current.setStyle("-fx-border-width: 0 0 2 2; -fx-border-color: #364f6b;-fx-text-fill:black");
                    } else if (i % 3 == 2) {
                        current.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                    } else if (j % 3 == 2) {
                        current.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                    } else if ( i == 0 ) {
                        current.setStyle("-fx-border-width: 2 0 0 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                    } else if ( j == 0 ) {
                        current.setStyle("-fx-border-width: 0 0 0 2; -fx-border-color: #364f6b;-fx-text-fill:black");
                    }
                    else {
                        current.setStyle("-fx-text-fill:black");
                    }
                }

            }
        }
    }



    private Alert createAlert(String type, String title, String header, String content) {
        Alert alert;
        if ("warning".equals(type)) {
            alert = new Alert(Alert.AlertType.WARNING);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        if (title != null) {
            alert.setTitle(title);
        }
        if (header != null) {
            alert.setHeaderText(header);
        }
        if (content != null) {
            alert.setContentText(content);
        }

        return alert;
    }

    // DB에 데이터 추가하는 메서드
    public void insertData(Date start_time, Date end_time, int spent_time, String answer, String problem) throws SQLException {
        SQLiteManager manager = new SQLiteManager();
        Object[] params = {start_time, end_time, spent_time, answer, problem};
        manager.insertGameData(params);
    }


    private void showSudokuGame(Sudoku sudoku) {
        if (sudoku != null) {
            // sudoku board를 현재 선택한 정보로 바꾼다.
            if (timeline != null) {
                timeline.stop();
            }

            // 소요 시간으로 변경하기
            count = sudoku.getSpentTime();
            timer_label.setText(Integer.toString(count));

            // sudoku board 시용자의 기존 보드로 바꾸기
            String problem = sudoku.getProblem();
            String answer = sudoku.getAnswer();

            String[] rowList = problem.split("\n");
            String[] answerRowList = answer.split("\n");

            for (int i = 0; i < rowList.length; i++) {
                String[] problemColList = rowList[i].split(" ");
                String[] answerColList = answerRowList[i].split(" ");

                for (int j = 0; j < problemColList.length; j++) {
                    TextField current = arr.get(i).get(j);
                    // 만약 사용자가 직접 입력한 답이라면?
                    if (problemColList[j].equals("_")) {
                        current.setText(answerColList[j]);
                        if (i % 3 == 2 && j % 3 == 2) {
                            current.setStyle("-fx-border-width: 0 2 2 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                        } else if (i == 0 && j == 0) {
                            current.setStyle("-fx-border-width: 2 0 0 2; -fx-border-color: #364f6b;-fx-text-fill:black");
                        } else if (i == 0 && j % 3 == 2 ) {
                            current.setStyle("-fx-border-width: 2 2 0 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                        } else if ( i % 3 == 2 && j == 0) {
                            current.setStyle("-fx-border-width: 0 0 2 2; -fx-border-color: #364f6b;-fx-text-fill:black");
                        } else if (i % 3 == 2) {
                            current.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                        } else if (j % 3 == 2) {
                            current.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                        } else if ( i == 0 ) {
                            current.setStyle("-fx-border-width: 2 0 0 0; -fx-border-color: #364f6b;-fx-text-fill:black");
                        } else if ( j == 0 ) {
                            current.setStyle("-fx-border-width: 0 0 0 2; -fx-border-color: #364f6b;-fx-text-fill:black");
                        }
                        else {
                            current.setStyle("-fx-text-fill:black");
                        }
                    } else {
                        current.setText(problemColList[j]);
                        if (i % 3 == 2 && j % 3 == 2) {
                            current.setStyle("-fx-border-width: 0 2 2 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else if (i % 3 == 2 && j == 0) {
                            current.setStyle("-fx-border-width: 0 0 2 2; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else if (i == 0 && j ==0) {
                            current.setStyle("-fx-border-width: 2 0 0 2; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else if (i == 0 && j % 3 == 2) {
                            current.setStyle("-fx-border-width: 2 2 0 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else if (i % 3 == 2) {
                            current.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else if (j % 3 == 2) {
                            current.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else if ( j == 0) {
                            current.setStyle("-fx-border-width: 0 0 0 2; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else if ( i == 0) {
                            current.setStyle("-fx-border-width: 2 0 0 0; -fx-border-color: #364f6b; -fx-text-fill:gray");
                        } else {
                            current.setStyle("-fx-text-fill:gray");
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void handleDeleteSudoku() {
        int selectedIndex = sudokuTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int selectedDBIndex = sudokuTable.getSelectionModel().getSelectedItem().getId().getValue().intValue();
            sudokuTable.getItems().remove(selectedIndex);
            SQLiteManager manager = new SQLiteManager();
            manager.deleteGameData(selectedDBIndex);
        } else {
            // 아무 sudoku 게임 기록도 선택하지 않은 경우)
            Alert alert = createAlert("warning", "오류", "선택된 기록이 없습니다.", "스도쿠 게임 기록을 선택해주세요.");
            alert.showAndWait();
        }
    }

}

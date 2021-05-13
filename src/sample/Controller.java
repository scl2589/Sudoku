package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

import static javafx.scene.control.ButtonType.OK;

/**
 * @Class Name : Controller.java
 * @Description : Controller Class
 * @Modification Information
 * @
 * @   수정일      수정자               수정내용
 * @ ----------   ---------   -------------------------------
 * @ 2021.05.11   신채린                최초생성
 *
 * @author S/W 개발팀
 * @since 2021.05.11
 * @version 1.0
 * @see
 */
public class Controller implements Initializable {

    @FXML private GridPane gp_sudoku_pane;
    @FXML private Button btn_generate;
    @FXML private Button btn_confirm;
    @FXML private Button btn_answer;
    @FXML private Label timer_label;


    private ArrayList<ArrayList<TextField>> arr;
    private ArrayList<Integer> removedArr;
    private ArrayList<Integer> allElements;

    private int count;
    private Timeline timeline;


    HashSet<Integer> [] rows = new HashSet[9];
    HashSet<Integer> [] cols = new HashSet[9];
    HashSet<Integer> [] box = new HashSet[9];

    HashSet<Integer> [] checkRows = new HashSet[9];
    HashSet<Integer> [] checkCols = new HashSet[9];
    HashSet<Integer> [] checkBox = new HashSet[9];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        arr = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            ArrayList row = new ArrayList<TextField>();
            for (int j = 0; j < 9; j++) {
                //arr.add(new TextField(i+":"+j));
                TextField tf = new TextField("");
                // textfield의 크기와 정렬 조정
                tf.setPrefSize(50, 55);
                tf.setAlignment(Pos.CENTER);

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
                gp_sudoku_pane.add((TextField)row.get(row.size()-1), j, i);
            }
            arr.add(row);

        }
        timer_label.setStyle("-fx-font-size: 1.5em;");



    }

    @FXML
    public void handleGenerate() {
        generateRandom();

    }

    public void handleConfirm() {
        // 정답일 경우
        if (correct()) {
            // 타이머 중지 
            timeline.stop();
            
            // alert창 생성 
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku 게임 결과");
            alert.setHeaderText("Sudoku 게임 결과입니다.");
            alert.setContentText("정답입니다!! 축하합니다 :) \n게임 소요 시간은 총 " + count + "초 입니다.");
            
            // 새 게임 시작하기 버튼 생성 
            ButtonType buttonNewGame = new ButtonType("새 게임 시작하기");
            alert.getButtonTypes().setAll(buttonNewGame, OK);
            
            // 사용자가 어떤 버튼을 눌렀는지 결과값 받아오기 
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.get() == buttonNewGame) { // 새 게임 시작 
                alert.hide();
                count = 0;
                timer_label.setText(Integer.toString(0));
                generateRandom();
            } else if (result.get() == OK) { // 확인버튼 
                alert.hide();
            }
        // 정답이 아닐 경우
        } else {
            // alert 창 생성
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku 게임 결과");
            alert.setHeaderText("Sudoku 게임 결과입니다.");
            alert.setContentText("정답이 아닙니다. 다시 한 번 시도해보세요 :)");
            alert.showAndWait();
        }


    }

    public void timing() {
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
        timing();
        removedArr = new ArrayList<Integer>();
        allElements = new ArrayList<>();

        for (int i = 0; i < 81; i++) {
            allElements.add(i);
        }

        // sudoku board에 있는 값 초기화
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField current = arr.get(i).get(j);
                current.setText("");
                current.setEditable(true);
            }
        }
        for (int i = 0; i < 9; i++ ) {
            rows[i] = new HashSet<Integer>();
            cols[i] = new HashSet<Integer>();
            box[i] = new HashSet<Integer>();
        }

        generateTopLeftBox();
        generateFirstRow();
        generateTopMiddleBox();
        generateTopRightBox();
        generateFirstCol();

        // 만약 스도쿠가 완성되지 않는다면?
        if (!backtracking()) {
            System.out.println("Generate 버튼을 다시 한 번 눌러주세요.");
        } else {
            removeElement();
        }
    }

    private void generateTopLeftBox() {
        // 좌상단 박스 생성하기
        ArrayList<Integer> numbers = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(numbers);
        for (int k = 0; k < 9; k++) {
            int i = k / 3, j = k % 3;
            arr.get(i).get(j).setText(Integer.toString(numbers.get(k)));

            // set에 추가한다.
            rows[i].add(numbers.get(k));
            cols[j].add(numbers.get(k));
            box[0].add(numbers.get(k));
        }
    }



    private void generateFirstRow() {
        // 첫번째 col 생성하기
        ArrayList<Integer> firstRowAvailable = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
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
        ArrayList<Integer> middleSecondRowAvailable = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // 중앙 박스에 이미 있는 요소들 제거
        for (Integer e : box[1]) {
            middleSecondRowAvailable.remove(e);
        }
        // 2번째 row에 있는 요소들 제거
        for (Integer e : rows[1]) {
            middleSecondRowAvailable.remove(e);
        }

        // 2번째 row에 필수적으로 있어야 하는 값 확인
        ArrayList<Integer> mustBeInMiddleSecondRow = new ArrayList<Integer>();
        // 3번째 row에 숫자가 있어서 2번째 row에 필수적으로 들어가야 하는 값 추가
        for (Integer e: rows[2]) {
            if (middleSecondRowAvailable.contains(e)) {
                mustBeInMiddleSecondRow.add(e);
                middleSecondRowAvailable.remove(middleSecondRowAvailable.indexOf(e));
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
        ArrayList<Integer> middleThirdRowAvailable = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
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
        ArrayList<Integer> rightSecondRowAvailable = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        ArrayList<Integer> rightThirdRowAvailable = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

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
        ArrayList<Integer> firstColAvailable = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // 2번째 row에 있는 요소들 제거
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
//            System.out.println(e);
            int i = e / 9; int j = e % 9;
            int value = Integer.parseInt(arr.get(i).get(j).getText());
            rows[i].remove(value);
            cols[j].remove(value);
            int ij = (i / 3) * 3 + j / 3;
            box[ij].remove(value);
            arr.get(i).get(j).setText("");

        }

        if (removedArr.size()>= 30) {
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
        ArrayList<Integer> available = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

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

    private void setTextFieldStyle() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField current = arr.get(i).get(j);
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

    private Boolean correct() {
        for (int i = 0; i < 9; i++ ) {
            checkRows[i] = new HashSet<Integer>();
            checkCols[i] = new HashSet<Integer>();
            checkBox[i] = new HashSet<Integer>();
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
}

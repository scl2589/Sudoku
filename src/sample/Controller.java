package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.*;

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


    private ArrayList<ArrayList<TextField>> arr;

    HashSet<Integer> [] rows = new HashSet[9];
    HashSet<Integer> [] cols = new HashSet[9];
    HashSet<Integer> [] box = new HashSet[9];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        arr = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            ArrayList row = new ArrayList<TextField>();
            for (int j = 0; j < 9; j++) {
                //arr.add(new TextField(i+":"+j));
                TextField tf = new TextField("");
                // textfield의 크기와 정렬 조정
                tf.setPrefSize(50, 50);
                tf.setAlignment(Pos.CENTER);
                row.add(tf);
                gp_sudoku_pane.add((TextField)row.get(row.size()-1), j, i);
            }
            arr.add(row);
        }


    }

    @FXML
    public void handleGenerate(){
        generateRandom();
    }

    /*
     * Generate 버튼 이벤트 생성하기
     */
    private void generateRandom(){

        // sudoku board에 있는 값 초기화
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                arr.get(i).get(j).setText("");
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
    }

    private void generateTopLeftBox() {
        // 좌상단 박스 생성하기
        ArrayList<Integer> numbers = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(numbers);
        for (int k = 0; k < 9; k++) {
            int i = k / 3, j = k % 3;
            arr.get(i).get(j).setText(Integer.toString(numbers.get(k)));

            // set에 추가한다.
            rows[k/3].add(numbers.get(k));
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
}

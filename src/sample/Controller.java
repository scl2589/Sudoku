package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.lang.reflect.Array;
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

        insertRow();
        insertCol();


    }

    private void insertRow() {
        // 각 row에 들어갈 숫자 정하기
        ArrayList<Integer> rowNums = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // shuffle 진행
        Collections.shuffle(rowNums);

        // 어떤 column에 들어갈지 random order 정하기
        ArrayList<Integer> rowNumsColOrder = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        Collections.shuffle(rowNumsColOrder);

        for (int i = 0; i < 9; i++) {
            arr.get(i).get(rowNumsColOrder.get(i)).setText(Integer.toString(rowNums.get(i)));

            int j = rowNumsColOrder.get(i);
            int ij = (i / 3) * 3 + j / 3;
            rows[i].add(rowNums.get(i));
            cols[rowNumsColOrder.get(i)].add(rowNums.get(i));
            box[ij].add(rowNums.get(i));
        }
    }

    private void insertCol() {
        // 숫자가 차있지 않은 칸을 고려해 row 순서 정하기
        ArrayList<Integer> colNumsRowOrder = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        while (true) {
            Collections.shuffle(colNumsRowOrder);
            boolean flag = false;

            for (int j = 0; j < 9; j++) {
                TextField current = arr.get(colNumsRowOrder.get(j)).get(j);
                if (current.getText() == "") {
                    flag = true;
                    break;
                }
            }
            if (!flag) break;
        }

        // 각 col에 들어갈 수 있는 숫자 리스트 구하기
        ArrayList<ArrayList<Integer>> availableList = new ArrayList<ArrayList<Integer>>(9);
        for (int j = 0; j < 9; j++){
            ArrayList<Integer> available = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            for (Integer row : rows[colNumsRowOrder.get(j)]) {
                available.remove(row);
            }
            for (Integer col: cols[j]) {
                available.remove(col);
            }
            int ij = (colNumsRowOrder.get(j) / 3) * 3 + j / 3;
            for (Integer b: box[ij]) {
                available.remove(b);
            }
            availableList.add(available);
        }

        // 위에서 구한 availableList를 기반으로 col에 들어갈 값들 구하기
        ArrayList<Integer> colNums = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        while (true) {
            Collections.shuffle(colNums);
            boolean flag = false;
            for (int i = 0; i < 9; i++) {
                if (!availableList.get(i).contains(colNums.get(i))) {
                    flag = true;
                    break;
                }
            }
            if (flag) continue;
            else break;
        }

        System.out.println(colNums);

        //숫자를 넣기
        for (int j = 0; j < 9; j++) {
            TextField current = arr.get(colNumsRowOrder.get(j)).get(j);
            current.setStyle("-fx-text-fill:red");
            current.setText(Integer.toString(colNums.get(j)));
        }


    }

}

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
        insertBox();

    }

    private void insertRow() {
        // 각 row에 들어갈 숫자 정하기
        ArrayList<Integer> rowNums = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // shuffle 진행
        Collections.shuffle(rowNums);

        // 어떤 column에 들어갈지 random order 정하기
        ArrayList<Integer> rowNumsColOrder = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        Collections.shuffle(rowNumsColOrder);
        System.out.println(rowNumsColOrder);

        for (int i = 0; i < 9; i++) {
            TextField current = arr.get(i).get(rowNumsColOrder.get(i));
            current.setText(Integer.toString(rowNums.get(i)));
            current.setStyle("-fx-text-fill:black");
            int j = rowNumsColOrder.get(i);
            int ij = (i / 3) * 3 + j / 3;
            rows[i].add(rowNums.get(i));
            cols[rowNumsColOrder.get(i)].add(rowNums.get(i));
            box[ij].add(rowNums.get(i));
        }
        System.out.println(rowNums);
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

        System.out.println("Row order");
        System.out.println(colNumsRowOrder);

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

        //hashset에 넣기
        for (int j = 0; j < 9; j++) {
            rows[colNumsRowOrder.get(j)].add(colNums.get(j));
            cols[j].add(colNums.get(j));
            int i = colNumsRowOrder.get(j);
            int ij = (i / 3) * 3 + j / 3;
            box[ij].add(colNums.get(j));
        }

        System.out.println(colNums);

        //숫자를 넣기
        for (int j = 0; j < 9; j++) {
            int ij = (colNumsRowOrder.get(j) / 3) * 3 + j / 3;
            TextField current = arr.get(colNumsRowOrder.get(j)).get(j);
            current.setStyle("-fx-text-fill:red");
            current.setText(Integer.toString(colNums.get(j)));
        }


    }

    private void insertBox() {
        // 각 박스마다 들어갈 수 있는 random 숫자 정하기
        ArrayList<Integer> insertBoxOrder = new ArrayList<Integer>(Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9));
        while (true) {
            Collections.shuffle(insertBoxOrder);
            boolean flag = false;

            for (int ij = 0; ij < 9; ij++) {
                if (box[ij].contains(insertBoxOrder.get(ij))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) break;
        }
        System.out.println("insertBox : " + insertBoxOrder);

        //각 박스에 들어갈 수 있는 위치 정하기
        int count = -1;
        boolean insertComplete = false;
        for (int i = 0; i < 9; i++) {
            insertComplete = false;
            count++;
            for (int j = 0; j < 9; j++) {
                if (insertComplete == false) {
                    int x = (j/3) + (i/3) * 3;
                    int y = (j%3) + (i%3) * 3;
                    TextField current = arr.get(x).get(y);
                    if ("".equals(current.getText())) {
                        // 해당 숫자가 관련 col, row에 존재하는지 확인하기
                        if (rows[x].contains(insertBoxOrder.get(count)) || cols[y].contains(insertBoxOrder.get(count))) {
                            continue;
                        }
                        // 해당 숫자가 존재하지 않는다면, set에 추가 및 board에 value 넣기
                        rows[x].add(insertBoxOrder.get(count));
                        cols[y].add(insertBoxOrder.get(count));
                        current.setStyle("-fx-text-fill:blue");
                        current.setText(Integer.toString(insertBoxOrder.get(count)));
                        insertComplete = true;
                    }
                }

            }
        }
    }


}

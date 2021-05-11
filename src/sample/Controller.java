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
 * @Class Name : CommonCodeCtrl.java
 * @Description : CommonCodeCtrl Class
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

    private ArrayList<TextField> arr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init");
        arr = new ArrayList<TextField>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                //arr.add(new TextField(i+":"+j));
                TextField tf = new TextField("");
                // textfield의 크기와 정렬 조정
                tf.setPrefSize(50, 50);
                tf.setAlignment(Pos.CENTER);
                arr.add(tf);
                gp_sudoku_pane.add((TextField)arr.get(arr.size()-1), j, i);
            }
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
        for (int i = 0; i < 81; i++) {
            arr.get(i).setText("");
        }

        // 랜덤으로 숫자가 들어갈 위치 정하기 (30곳)
        Set<Integer> locationSet = new HashSet<>();
        while (locationSet.size()<30) {
            Random rand = new Random();
            int num = rand.nextInt(81);
            locationSet.add(num);
        }
        List<Integer> locationList = new ArrayList<>(locationSet);
        Collections.sort(locationList);
        System.out.println(locationList);

        // 숫자가 들어갈 위치에 1~9 숫자 지정하기
        for (int i = 1; i <= 81; i++) {
            if (locationList.contains(i)) {
                Random randNum = new Random();
                arr.get(i).setText(Integer.toString(randNum.nextInt(9) + 1));
            } else {
                continue;
            }

        }

    }
}

package sample;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Long.MAX_VALUE;
import static javafx.scene.control.ButtonType.OK;

public class Controller implements Initializable {

    @FXML private GridPane gp_sudoku_pane;
    @FXML private Button btn_generate;
    @FXML private Button btn_confirm;
    @FXML private Button btn_answer;
    @FXML private Button btn_delete;
    @FXML private Button btn_changenickname;
    @FXML private Label timer_label;
    @FXML private TableView<Sudoku> sudokuTable;
    @FXML private TableColumn<Sudoku, String> nicknameColumn;
    @FXML private TableColumn<Sudoku, String> startTimeColumn;
    @FXML private TableColumn<Sudoku, Integer> spentTimeColumn;

    private String nickname;
    private ArrayList<ArrayList<TextField>> arr;
    private ArrayList<ArrayList<Integer>> answer;
    private StringBuilder question;

    private int count;
    private Timeline timeline;
    private Date start_time;
    private Date end_time;
    private StringBuilder sudokuAnswer;
    private SQLiteManager manager = new SQLiteManager();

    private ObservableList<Sudoku> sudokuData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        arr = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            ArrayList<TextField> row = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                TextField tf = new TextField("");

                // text field 의 크기와 정렬 조정
                tf.setPrefSize(MAX_VALUE, MAX_VALUE);
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

        try {
            initializeTable();
        } catch (JsonMappingException e) {
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //닉네임 받기
        getNickname();
    }

    @FXML
    public void handleGenerate() throws IOException {
        generateRandom();
    }

    @FXML
    public void handleConfirm() throws Exception, JsonMappingException {
        // 아직 게임이 생성되지 않았을 경우
        if (answer == null) {
            Alert alert = createAlert("warning", null, "Sudoku 게임 미시작", "Sudoku 게임을 아직 시작하지 않았습니다. \n게임 생성 후, 게임 결과를 확인하기 위해 눌러주세요.");
            alert.showAndWait();
        } else { // 게임이 생성된 경우
            // 현재 값 String에 담아두기
            StringBuilder currentValue = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if ("".equals(arr.get(i).get(j).getText())) {
                        currentValue.append("0");
                    } else {
                        currentValue.append(arr.get(i).get(j).getText());
                    }
                }
            }

            //정답인지 확인하기
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:8080/correct/" +currentValue.toString());
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            Boolean content = Boolean.parseBoolean(EntityUtils.toString(entity, "UTF-8"));
            if (content == true) { // 정답일 경우
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

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode postParam = mapper.createObjectNode();
                postParam.put("nickname", nickname);
                postParam.put("starttime", df.format(start_time));
                postParam.put("endtime", df.format(end_time));
                postParam.put("count", count);
                postParam.put("answer", sudokuAnswer.toString());
                postParam.put("question", question.toString());
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(postParam);

                // DB에 데이터 추가하기
                HttpPost httpPost = new HttpPost("http://localhost:8080/sudoku/");
                httpPost.addHeader("accept", "application/json");
                httpPost.addHeader("Content-Type", "application/json");
                HttpEntity stringEntity = new StringEntity(json, "UTF-8");
                httpPost.setEntity(stringEntity);
                httpClient.execute(httpPost);

                // TableView 갱신하기
                initializeTable();

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
            StringBuilder sudokuAnswer = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    sudokuAnswer.append(answer.get(i).get(j)).append(" ");
                }
                sudokuAnswer.append("\n");
            }
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
    private void generateRandom() throws IOException {
        // 문제를 생성했으므로 시간을 측정하기 시작한다.
        timing();

        // 초기화하기
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                arr.get(i).get(j).setText("");
            }
        }


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/generate");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        String content = EntityUtils.toString(entity, "UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode nodes = mapper.readTree(content);

        // Answer 버튼을 위해 정답 배열을 만들어서 완성된 스도쿠를 담아둔다.
        answer = new ArrayList<>(9);
        for (JsonNode node : nodes) {
            ArrayList<Integer> temp = new ArrayList<>(9);
            for (int j = 0; j < 9; j++) {
                int value = node.get(j).asInt();
                temp.add(value);
            }
            answer.add(temp);
        }

        for (ArrayList<Integer> h : answer) {
            System.out.println(h);
        }

        // 스도쿠 문제를 생성하기 위해 완성된 스도쿠의 element를 하나씩 지운다.
        httpGet = new HttpGet("http://localhost:8080/generate/remove");
        httpResponse = httpClient.execute(httpGet);
        entity = httpResponse.getEntity();
        content = EntityUtils.toString(entity, "UTF-8");
        mapper = new ObjectMapper();
        nodes = mapper.readTree(content);

        // 문제 저장하기
        question = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String value = nodes.get(i).get(j).toString();
                if ("0".equals(value)) {
                    question.append("_ ");
                    arr.get(i).get(j).setText("");
                } else {
                    question.append(value).append(" ");
                    arr.get(i).get(j).setText(value);
                }
            }
            question.append("\n");
        }
        setTextFieldStyle();
        // 게임이 시작됐으므로 현재 시간을 저장해놓는다.
        start_time = new Date();
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
                    current.setEditable(true);
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

    private void initializeTable() throws Exception, JsonMappingException {
        // 테이블 초기화하기
        sudokuTable.getItems().clear();

        nicknameColumn.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        spentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().spentTimeProperty().asObject());

        // 각각의 row마다 선택된 아이템에 대해 event listener 추가하기
        sudokuTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSudokuGame(newValue));

        // 테이블에 observable 리스트 데이터를 추가한다.
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/initializetable");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        String content = EntityUtils.toString(entity, "UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode nodes = mapper.readTree(content);
        for (JsonNode node : nodes) {
            int id = node.get("id").asInt();
            String nickname = node.get("nickname").asText();
            int spent_time = node.get("spent_time").asInt();
            String problem = node.get("problem").asText();
            String answer = node.get("answer").asText();
            String start_time =node.get("start_time").asText();
            String end_time = node.get("end_time").asText();
            sudokuData.add(new Sudoku(id, nickname, start_time, end_time, spent_time, problem, answer));
        }

        sudokuTable.setItems(sudokuData);
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
    private void handleDeleteSudoku() throws Exception {
        int selectedIndex = sudokuTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int selectedDBIndex = sudokuTable.getSelectionModel().getSelectedItem().getId().getValue().intValue();
            // Delete Http 통신 진행
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete("http://localhost:8080/sudoku/" + selectedDBIndex);
            httpClient.execute(httpDelete);
            sudokuTable.getItems().remove(selectedIndex);
        } else {
            // 아무 sudoku 게임 기록도 선택하지 않은 경우)
            Alert alert = createAlert("warning", "오류", "선택된 기록이 없습니다.", "스도쿠 게임 기록을 선택해주세요.");
            alert.showAndWait();
        }
    }

    @FXML
    private void getNickname() {
        // 닉네임 받기;
        TextInputDialog dialog;
        if (nickname != null) {
            dialog = new TextInputDialog(nickname);
        } else {
            dialog = new TextInputDialog("");
        }

        dialog.setTitle("닉네임 입력");
        dialog.setHeaderText("닉네임을 입력해주세요.");


        // 입력 취소는 disable
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            nickname = name;
        });
    }
}

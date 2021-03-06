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

                // text field ??? ????????? ?????? ??????
                tf.setPrefSize(MAX_VALUE, MAX_VALUE);
                tf.setAlignment(Pos.CENTER);

                // sudoku ?????? ????????? ?????? ??? row/col ?????? ????????? outline ??????
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

        //????????? ??????
        getNickname();
    }

    @FXML
    public void handleGenerate() throws IOException {
        generateRandom();
    }

    @FXML
    public void handleConfirm() throws Exception, JsonMappingException {
        // ?????? ????????? ???????????? ????????? ??????
        if (answer == null) {
            Alert alert = createAlert("warning", null, "Sudoku ?????? ?????????", "Sudoku ????????? ?????? ???????????? ???????????????. \n?????? ?????? ???, ?????? ????????? ???????????? ?????? ???????????????.");
            alert.showAndWait();
        } else { // ????????? ????????? ??????
            // ?????? ??? String??? ????????????
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

            //???????????? ????????????
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://localhost:8080/correct/" +currentValue.toString());
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            Boolean content = Boolean.parseBoolean(EntityUtils.toString(entity, "UTF-8"));
            if (content == true) { // ????????? ??????
                // ????????? ??????

                timeline.stop();

                // ????????? ??????????????? ?????? ?????? ?????? ??????
                end_time = new Date();

                // ????????? String ????????? ????????????
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

                // DB??? ????????? ????????????
                HttpPost httpPost = new HttpPost("http://localhost:8080/sudoku/");
                httpPost.addHeader("accept", "application/json");
                httpPost.addHeader("Content-Type", "application/json");
                HttpEntity stringEntity = new StringEntity(json, "UTF-8");
                httpPost.setEntity(stringEntity);
                httpClient.execute(httpPost);

                // TableView ????????????
                initializeTable();

                // alert ??? ??????
                Alert alert = createAlert("information", "Sudoku ?????? ??????", "Sudoku ?????? ???????????????.", "???????????????!! ??????????????? :) \n?????? ?????? ????????? ??? " + count + "??? ?????????." );

                // ??? ?????? ???????????? ?????? ??????
                ButtonType buttonNewGame = new ButtonType("??? ?????? ????????????");
                alert.getButtonTypes().setAll(buttonNewGame, OK);

                // ???????????? ?????? ????????? ???????????? ????????? ????????????
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == buttonNewGame) { // ??? ?????? ??????
                    alert.hide();
                    // ????????? 0?????? ?????????
                    count = 0;
                    timer_label.setText(Integer.toString(0));

                    // ??? ????????? ????????????
                    generateRandom();
                } else if (result.get() == OK) { // ????????????
                    alert.hide();
                }
            } else { // ????????? ?????? ??????
                // alert ??? ??????
                Alert alert  = createAlert("information", "Sudoku ?????? ??????", "Sudoku ?????? ???????????????.","????????? ????????????. ?????? ??? ??? ?????????????????? :)"  );
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void handleAnswer() {
        Alert alert;
        if (answer == null ) {
            alert = createAlert("warning", null, "Sudoku ?????? ?????????", "Sudoku ????????? ?????? ???????????? ???????????????. \n?????? ?????? ???, ????????? ???????????? ?????? ???????????????.");
        } else {
            alert = createAlert("information", "Sudoku ?????? ??????", "Sudoku ?????? ???????????????.", null);
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
        // ?????? ?????? generate ??? sudoku ????????? ?????????, ?????? ??? ??? generate ??? ????????? ??????, timeline ??? ?????????.
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
     * Generate ?????? ????????? ????????????
     */
    private void generateRandom() throws IOException {
        // ????????? ?????????????????? ????????? ???????????? ????????????.
        timing();

        // ???????????????
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

        // Answer ????????? ?????? ?????? ????????? ???????????? ????????? ???????????? ????????????.
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

        // ????????? ????????? ???????????? ?????? ????????? ???????????? element??? ????????? ?????????.
        httpGet = new HttpGet("http://localhost:8080/generate/remove");
        httpResponse = httpClient.execute(httpGet);
        entity = httpResponse.getEntity();
        content = EntityUtils.toString(entity, "UTF-8");
        mapper = new ObjectMapper();
        nodes = mapper.readTree(content);

        // ?????? ????????????
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
        // ????????? ?????????????????? ?????? ????????? ??????????????????.
        start_time = new Date();
    }


    private void setTextFieldStyle() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField current = arr.get(i).get(j);
                // ??? textfield?????? ????????? ????????? ??????
                current.setOnKeyPressed(event -> {
                    KeyCode key = event.getCode();
                    String s = event.getText();

                    // alert ??? ??????
                    Alert alert = createAlert("warning", null, "?????? ?????? ??????", null);
                    // Backspace ?????? ????????? ???, key ??????
                    if (key != KeyCode.BACK_SPACE) {
                        // char??? ?????? ???????????????/?????????????????? ???????????? ??????
                        char ch = s.charAt(0);
                        if (key == KeyCode.DIGIT0 || key == KeyCode.NUMPAD0) {// ?????? 0??? ??????????????? ??????
                            alert.setContentText("?????? 0??? ????????? ???????????????.\n?????? 1~9????????? ????????? ???????????????.");
                            alert.showAndWait();
                        } else if (ch < 49 || ch > 57) { // ??????????????? ??????????????? ??????????????? ??????
                            alert.setContentText("????????? ??? ??????????????? ????????? ???????????????.\n?????? 1~9????????? ????????? ???????????????.");
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
        // ????????? ???????????????
        sudokuTable.getItems().clear();

        nicknameColumn.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        spentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().spentTimeProperty().asObject());

        // ????????? row?????? ????????? ???????????? ?????? event listener ????????????
        sudokuTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSudokuGame(newValue));

        // ???????????? observable ????????? ???????????? ????????????.
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
            // sudoku board??? ?????? ????????? ????????? ?????????.
            if (timeline != null) {
                timeline.stop();
            }

            // ?????? ???????????? ????????????
            count = sudoku.getSpentTime();
            timer_label.setText(Integer.toString(count));

            // sudoku board ???????????? ?????? ????????? ?????????
            String problem = sudoku.getProblem();
            String answer = sudoku.getAnswer();

            String[] rowList = problem.split("\n");
            String[] answerRowList = answer.split("\n");

            for (int i = 0; i < rowList.length; i++) {
                String[] problemColList = rowList[i].split(" ");
                String[] answerColList = answerRowList[i].split(" ");

                for (int j = 0; j < problemColList.length; j++) {
                    TextField current = arr.get(i).get(j);
                    // ?????? ???????????? ?????? ????????? ?????????????
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
            // Delete Http ?????? ??????
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete("http://localhost:8080/sudoku/" + selectedDBIndex);
            httpClient.execute(httpDelete);
            sudokuTable.getItems().remove(selectedIndex);
        } else {
            // ?????? sudoku ?????? ????????? ???????????? ?????? ??????)
            Alert alert = createAlert("warning", "??????", "????????? ????????? ????????????.", "????????? ?????? ????????? ??????????????????.");
            alert.showAndWait();
        }
    }

    @FXML
    private void getNickname() {
        // ????????? ??????;
        TextInputDialog dialog;
        if (nickname != null) {
            dialog = new TextInputDialog(nickname);
        } else {
            dialog = new TextInputDialog("");
        }

        dialog.setTitle("????????? ??????");
        dialog.setHeaderText("???????????? ??????????????????.");


        // ?????? ????????? disable
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            nickname = name;
        });
    }
}

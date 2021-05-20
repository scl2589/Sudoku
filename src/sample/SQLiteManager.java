package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class SQLiteManager {

    // 상수 설정
    //   - Database 변수
    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String SQLITE_FILE_DB_URL = "jdbc:sqlite:sudoku.db";
    private static final String SQLITE_MEMORY_DB_URL = "jdbc:sqlite::memory";
    private ObservableList<Sudoku> sudokuData = FXCollections.observableArrayList();

    //  - Database 옵션 변수
    private static final boolean OPT_AUTO_COMMIT = false;
    private static final int OPT_VALID_TIMEOUT = 500;


    // 변수 설정
    //   - Database 접속 정보 변수
    private Connection conn = null;
    private String driver = null;
    private String url = null;

    // 생성자
    public SQLiteManager(){
        this(SQLITE_FILE_DB_URL);
    }
    public SQLiteManager(String url) {
        // JDBC Driver 설정
        this.driver = SQLITE_JDBC_DRIVER;
        this.url = url;
    }

    // DB 연결 함수
    public Connection createConnection() {
        try {
            // JDBC Driver Class 로드
            Class.forName(this.driver);

            // DB 연결 객체 생성
            this.conn = DriverManager.getConnection(this.url);

            // 옵션 설정
            //   - 자동 커밋
            this.conn.setAutoCommit(OPT_AUTO_COMMIT);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return this.conn;
    }

    // DB 연결 종료 함수
    public void closeConnection() {
        try {
            if( this.conn != null ) {
                this.conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.conn = null;
        }
    }

    // DB 재연결 함수
    public Connection ensureConnection() {
        try {
            if( this.conn == null || this.conn.isValid(OPT_VALID_TIMEOUT) ) {
                closeConnection();      // 연결 종료
                createConnection();     // 연결
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.conn;
    }

    // DB 연결 객체 가져오기
    public Connection getConnection() {
        return this.conn;
    }

    // DB 삽입
    public void insertGameData(Object[] data) throws SQLException {
        final String sql = "INSERT INTO sudoku(start_time, end_time, spent_time, answer, problem) VALUES(?, ?, ?, ?, ?)";
        Connection conn = ensureConnection();
        PreparedStatement pstmt = null;

        int inserted = 0;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, data[0]);
            pstmt.setObject(2, data[1]);
            pstmt.setObject(3, data[2]);
            pstmt.setObject(4, data[3]);
            pstmt.setObject(5, data[4]);

            // 쿼리 실행
            pstmt.executeUpdate();

            inserted = pstmt.getUpdateCount();

            // 트랜잭션 commit
            conn.commit();
        } catch (SQLException e) {
            e.getMessage();
            // 트랜잭션 ROLLBACK
            if( conn != null ) {
                conn.rollback();
            }

            // 오류
            inserted = -1;
        } finally {
            if( pstmt != null ) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 데이터 조회 함수
//    List<Map<String, Object>>  public void selectSudokuList(Map<String, Object> dataMap) throws SQLException
    public void selectSudokuList() {
        String sql = "SELECT * FROM sudoku";

        try (Connection conn = ensureConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int spent_time = rs.getInt("spent_time");
                String answer = rs.getString("answer");
//                System.out.println(rs.getInt("id") + "\n" +  rs.getInt("spent_time") +"\n"+ rs.getString("answer"));
            }
        } catch (SQLException e) {
            e.getMessage();
        }
//        // 상수설정
//        final String SQL = "SELECT start_time" + "\n"
//                + ", end_time" + "\n"
//                + ", spent_time"+ "\n"
//                + ", answer" + "\n"
//                + ", problem" + "\n"
//                + " FROM sudoku" + "\n";
//
//        // 조회 결과 변수
//        final Set<String> columnNames = new HashSet<String>();
//        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
//
//        // 변수 설정
//        Connection conn = ensureConnection();
//        PreparedStatement pstmt = null;
//        ResultSetMetaData meta = null;
//
//        try {
//            pstmt = conn.prepareStatement(SQL);
//
//            // 조회할 데이터 조건 맵핑
//            pstmt.setObject(1, dataMap.get("START_DATE"));
//            pstmt.setObject(2, dataMap.get("SPENT_TIME"));
//
//            // 데이터조회
//            ResultSet rs = pstmt.executeQuery();
//
//            //조회된 데이터의 컬럼명 저장
//            meta = pstmt.getMetaData();
//            for (int i = 1; i <= meta.getColumnCount(); i++) {
//                columnNames.add(meta.getColumnName(i));
//            }
//
//            Map<String, Object> resultMap = null;
//
//            while (rs.next()) {
//                resultMap = new HashMap<String, Object>();
//
//                for (String column: columnNames) {
//                    resultMap.put(column, rs.getObject(column));
//                }
//
//                if (resultMap != null) {
//                    selected.add(resultMap);
//                }
//            }
//        } catch (SQLException e) {
//            e.getMessage();
//        } finally {
//            try {
//                if (pstmt != null) {
//                    pstmt.close();
//                }
//                closeConnection();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return selected;

    }
    // 조회 결과 출력 함수
    public void printMapList(List<Map<String, Object>> mapList) {
        if( mapList.size() == 0 ) {
            System.out.println("조회된 데이터가 없습니다.");
            return;
        }

        // 상세 데이터 출력
        System.out.println(String.format("데이터 조회 결과: %d건", mapList.size()));

        for(int i = 1; i <= mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i-1);

            StringBuilder sb = new StringBuilder();

            sb.append(i);
            sb.append(": {");
            map.entrySet().forEach(( entry )->{
                sb.append('"')
                        .append(entry.getKey())
                        .append("\": \"")
                        .append(entry.getValue())
                        .append("\", ");
            });
            sb.append("}");

            System.out.println(sb.toString());
        }
    }


}
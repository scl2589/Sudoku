# 스도쿠 만들기

## 개요

- 1차
  - 목표: JavaFx로 스도쿠 만들기 
  - 기간: 5/12(수) ~ 5/14(금) 

- 2차
  - 목표: DB를 추가해 DB와 스도쿠 게임 기록을 연동하는 법과 sql의 기본 구문 익히기 
  - 기간: 5/17(월) ~ 5/20(목)



# 1차 스도쿠 만들기

## **스도쿠 퍼즐을 만드는 알고리즘**

### 참고 자료

- https://dlbeer.co.nz/articles/sudoku.html



## 스도쿠 퍼즐 만들기

### 기본적인 원리

- 81개 중 33개의 cell을 채운 후, backtracking을 통해 나머지 41개의 cell을 채워 스도쿠 퍼즐을 완성한다.
- 스도쿠 문제를 만들기 위해서, 빈칸을 하나씩 없앤다 (30개의 원소를 random으로 삭제한다).
  - 하나씩 없앨 때마다 backtracking을 통해 퍼즐 정답을 맞출 수 있는지 확인한다.

### 구체적으로 구현한 방식

1. 1개의 박스를 random 하게 만들어낸다. → `generateTopLeftBox()`

   ![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/82d05809-8951-4621-9bd4-4d602ceced70/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/82d05809-8951-4621-9bd4-4d602ceced70/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T234624Z&X-Amz-Expires=86400&X-Amz-Signature=3987ef1116a2b72199c2329c76127a5f89f60d48280099377d520f765f631cc4&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

   - 좌측 최상단 자리에 박스를 위치시킨다.

   - 각 row, col, box hashset을 만든다.

     - 이는 가로, 세로, 박스에 숫자가 겹치지 않는지 확인하기 위함이다.

   - 1부터 9까지 `numbers` arraylist를 만들고, 이를 shuffle한다. 그리고 그 순서대로 박스를 채워 나간다.

     - 해당 숫자들은 각각의 hashset에 추가한다.
     
     

2. 가운데 상단 박스의 첫번째 row를 생성한다 → `generateFirstRow()`

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/65d41d20-94e7-4d49-9015-9d571d5373a7/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/65d41d20-94e7-4d49-9015-9d571d5373a7/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T234646Z&X-Amz-Expires=86400&X-Amz-Signature=0683cf0a868ac19922fd5896a9cbeaaf38d6f783d49f33ebc9bd46541c5bc3ea&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

- 첫번째 row에 넣기 가능한 숫자를 파악하기 위해 1부터 9까지 들어있는 `firstRowAvailable` arraylist를 만든다.
- 그리고 HashSet인 `rows[0]` 를 하나씩 돌면서 첫번째 가로줄에 있는 요소를 삭제한다.
- 남은 6개의 숫자를 shuffle한다.
- 차례대로 숫자를 sudoku 퍼즐에 기입한다.
- 숫자들을 추가할때 해당하는 rows, cols, box hashset에 추가한다.

```java
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
```

3. 위 가운데 박스 2번째 박스를 생성한다. →  `generateTopMiddleBox()`

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f8fe95f8-aec7-49fb-865e-5fc882ec2756/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/f8fe95f8-aec7-49fb-865e-5fc882ec2756/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T234705Z&X-Amz-Expires=86400&X-Amz-Signature=f7021051519f501937d41e7db8a48a2b939951854f7f1cd1977651b160ea709e&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

- 현재 박스 내부에 있는 숫자와, 2번째/3번째 각각 row에 위치해 있는 숫자를 제외하고 (hashset 이용) 가능한 숫자를 추가한다.

- 가운데 박스 2번째 가로줄부터 생성한다.

  - 2번째 가로줄에 어떤 숫자가 들어갈 수 있는지 1~9까지의 `middleSecondRowAvailable`ArrayList를 생성한다.
    - 가운데 박스에 이미 있는 요소들을 제거한 후, 2번째 가로줄에 있는 요소들을 제거한다.
    - `box[1]` & `rows[1]`
  - 이후, 2번째 가로줄에 필수로 들어가야 하는 값을 확인하기 위해 `mustBeInMiddleSecondRow`ArrayList를 생성한다.
    - 3번째 row에 있는 값들을 확인해서, 이 값이 `middleSecondRowAvailable` 2번째 가로줄에 들어갈 수 있는 값에도 해당된다면, 무조건 들어가야 하는 값으로 추가한다. (`mustBeInMiddleSecondRow` 에 추가)
  - `middleSecondRowValues` 라는 arrayList를 생성해서, 가운데 박스 2번째 줄에 어떤 값들이 들어갈지 추가한다. 기본적으로 `mustBeMiddleSecond`  값을 추가하고, 만약 3개의 숫자가 되지 않는다면 `middleSecondRowAvailable` 에서 나머지 갯수를 추출해서 추가한다.
  - `middleSecondRowValues`가 3개의 숫자로 채워지면, 해당 칸에 채워준다.
  - 숫자들을 추가할때 해당하는 rows, cols, box hashset에 추가한다.

  ```java
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
  ```

- 가운데 박스 3번째 가로줄을 생성한다.

  - 가능한 숫자들을 확인하기 위해  1부터 9까지 `middleThirdRowAvailable` 배열을 생성한다.
  - 가운데 박스에 이미 있는 요소들을 제거한다.
    - `box[1]` 에 있는 값 제거
  - 남은 3개의 숫자를 shuffle해서, 해당 위치에 추가한다.
  - 숫자들을 추가할때 해당하는 rows, cols, box hashset에 추가한다.

  ```java
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
  ```

4. 맨 위 오른쪽 박스를 생성한다. → `generateTopRightBox()`

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/514f8c1f-a436-48cd-80a6-897a358c18d6/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/514f8c1f-a436-48cd-80a6-897a358c18d6/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T234738Z&X-Amz-Expires=86400&X-Amz-Signature=93e2128de7bbc080eaee20cd21d3fd6c0167e100dcc629a8bb531991dbfe487b&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

- 2번째 Row와 3번째 Row에 가능한 숫자들을 확인하기 위해 1~9까지 담긴 `rightSecondRowAvailable`, `rightThirdRowAvailable` ArrayList를 생성한다.
- 각 row (두번째, 세번째 row)마다 set에 있는 숫자를 제거시킨다.
- 이후, 남는 숫자를 shuffle해서 추가한다.
- 숫자들을 추가할때 해당하는 rows, cols, box hashset에 추가한다.

```java
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
```

**⇒ 1~4 번까지 진행하면 상단의 3개줄이 완성하게 된다.**



5. 첫번째 col을 채운다 → `generateFirstCol()`

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/b4a16784-4206-4b34-95c0-26cffcfc3d3a/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/b4a16784-4206-4b34-95c0-26cffcfc3d3a/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T234838Z&X-Amz-Expires=86400&X-Amz-Signature=6be611ea01bbc946bc147e837de95958c4d5495628a1580583ea3e2434e623f8&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

- 1부터 9까지 가능한 숫자가 담긴 `firstColAvailable` arraylist를 생성한다.
- 그리고 첫번째 column에 있는 요소들을 제거한다.
- 남은 요소들을 shuffle하고, 값을 sudoku arr에 추가한다.
- 숫자들을 추가할때 해당하는 rows, cols, box hashset에 추가한다.

```java
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
```

6. 다른 칸들을 backtracking해서 sudoku를 완성한다.  → `backtracking()`

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e39f7b7d-68af-4935-b46b-5ded77d5c862/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/e39f7b7d-68af-4935-b46b-5ded77d5c862/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T234908Z&X-Amz-Expires=86400&X-Amz-Signature=8011369ca59c91de598b96cb8a5713d79d3ce958b41860a080d7871cdd0b7a1a&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

7. 숫자를 지운다. → `removeElement()`

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/836e86d0-724e-465e-9679-cdabedd3f5b5/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/836e86d0-724e-465e-9679-cdabedd3f5b5/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T234923Z&X-Amz-Expires=86400&X-Amz-Signature=1d2a722b19cdf39942879099a82533272bc864b52420e6494eaac9d064407360&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

- `removedArr` ArrayList만들기
  - 몇 번 위치가 지워졌는지 기록하기 위함이다.
- `allElements` 배열 (숫자 0~80 나열)을 생성한다.
  - 랜덤으로 숫자 고른다.
  - `removedArr`에 중복하지 않는 값 나올 수 있도록한다. → 숫자를 고른 후, 이후 `allElements`에서 해당 숫자를 지운다.
- 랜덤으로 나온 숫자를 arr에서 제거한다.
  - `removedArr`에 있는 모든 값을 하나씩 돌면서, 해당 위치에 있는 값을 빈 값("")으로 만든다.
- 이후, 스도쿠 arr을 `backtracking`하면서, 풀 수 있는지 확인한다.
- **30번 반복하여 sudoku 퍼즐 문제를 만들어낸다**.



## 고려사항

- 초기화
  - generate 버튼을 누를 때마다 TextField의 값들을 빈 스트링으로 가장 먼저 초기화한다.
  - generate 버튼을 여러번 눌렀을 때 요소가 겹칠 수 있으므로 초기화가 필요하다.



## Sudoku 퍼즐 디자인

`setTextFieldStyle()` 메소드 참고

- 각 textbox의 row/column 값을 계산해서 outline 색깔 추가
- Sudoku 문제로 주어진 사용자의 편의성을 고려하여 숫자는 gray 색깔로 주며, disable 한다.



## Timer추가

### 참고 자료 및 TIL

- https://code.makery.ch/blog/javafx-dialogs-official/
- https://palpit.tistory.com/entry/Java-JavaFX-메뉴바-툴바-다이얼로그
- `Alert alert = new Alert()` 으로 Alert를 추가한다.



### Timeline 설명 (TIL)

- `Timeline`이라는 JavaFX의 클래스를 사용한다.
  - JavaFX는 애니메이션의 타임 라인 (시간당 변화를 관리하는 것)이 된다.
  - `Timeline`을 사용하려면 `KeyFrame`이라는 클래스가 필요하다.
    - `KeyFrame`은 타임라인에 설정하는 키 프레임 (특정 시간에 애니메이션의 상태를 설정하는 것) 이 된다.
  - `Timeline`는 먼저 인스턴스를 만들고 거기에 키 프레임이 되는 `KeyFrame`을 필요에 따라 조합해 간다.
    - 그래서 **필요한 설정이 된다면 play 애니메이션을 시작하는 흐름**이 된다.



### 초기화

- JavaFX Scene Builder와 변수를 ID 값(`timer_label`)으로 연결시켜준다.

  - 폰트 사이즈는 1.5em으로 한다.

    ```java
    timer_label.setStyle("-fx-font-size: 1.5em;");
    ```



### 구현 방법

- `Timeline`을 사용하고, `KeyFrame`을 통해 duration은 1초로 설정한다.
- `count` 변수를 만들고, 그 1초마다 count를 늘려간다.
- 그리고, `count`를 `timer_label`에 대입시킨다.

```java
public void timing() {
    count = 0;
    timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
        count++;
        timer_label.setText(Integer.toString(count));
    }));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
}
```



### 고려사항

- timer 초기화
  - 만약 generate 버튼을 여러번 눌렀을 경우, timing 메소드가 여러번 run 되어 count 함수가 엄청나게 빠른 속도로 늘어난다.
  - 이에 대비해, 새로운 generate 버튼을 눌렀을 때 count가 0으로 초기화되고, `timeline`이 멈출 수 있도록 해준다.
    - `timeline`이 존재한다면, `timeline.stop()` 기능을 추가한다.

```java
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
```



## Confirmation

`handleConfirm()` 함수 참고



### Confirmation 로직

- sudoku 규칙에 맞게 사용자가 숫자를 잘 기입했는지 확인한다.  → `correct()`

  1. 체크용 row, col, box HashSet을 생성한다. (`checkRows`, `checkCols`, `checkBox`)

     ```java
     for (int i = 0; i < 9; i++ ) {
         checkRows[i] = new HashSet<>();
         checkCols[i] = new HashSet<>();
         checkBox[i] = new HashSet<>();
     }
     ```

  2. 이중 배열을 하나씩 돌면서 해당하는 칸의 값을 가져온다.

     ```java
     String text = arr.get(i).get(j).getText();
     ```

  3. 만약 text의 값이 빈 string 이라면 모든 숫자가 채워지지 않았다는 뜻이므로 `return false`를 해준다.

     ```java
     if ("".equals(text)) {
         return false;
     }
     ```

  4. 이후, 해당 값을 정수로 변환한다.

     ```java
     Integer current = Integer.parseInt(arr.get(i).get(j).getText());
     ```

  5. 만약 `checkRows[i]`, `checkCols[j]`, `checkBox[ij]` 에 현재 값(`current`)이 하나라도 존재한다면 가로, 세로, 박스에서 unique하지 않다는 뜻이므로 `return false`를 해준다.

     ```java
     int ij = (i / 3) * 3 + j / 3;
     if (checkRows[i].contains(current) || checkCols[j].contains(current) || checkBox[ij].contains(current)) {
         return false
     }
     ```

  6. 그렇지 않다면, `checkRows[i]`, `checkCols[j]`, `checkBox[ij]` 에 현재 값 (`current`)를 추가해준다.

     ```java
     else {
         checkRows[i].add(current);
         checkCols[j].add(current);
         checkBox[ij].add(current);
     }
     ```

  7. 모든 원소를 다 돌았을 때 무사히 통과한다면 유일성이 확보되었다는 뜻이므로 `return true`  를 해준다.

- 정답인지, 오답인지에 따라 2가지 경우가 나뉘어진다.

  - 정답인 경우
    - 게임 진행 시간을 멈추고, 정답과 얼만큼의 시간이 소요되었는지와 알려줘야 한다.
  - 오답일 경우
    - 시간을 그대로 유지하고, 오답이라는 점을 알려줘야 한다

  ```java
  if (correct()) {
      count = 0;
      timeline.stop();
      System.out.println("정답입니다.");
  } else {
      System.out.println("정답이 아닙니다.");
  }
  ```



## Alert 창 추가

사용자에게 직접적으로 알려줄 수 있도록 alert창을 띄울 수 있도록 한다.



### 참고자료 및 TIL

- https://code.makery.ch/blog/javafx-dialogs-official/
- https://studymake.tistory.com/583
- Alert 창은 기본적으로 Alert 클래스로 생성한다.
- 이때 Alert가 어떤 type인지, Alert의 title, header, contents를 정해준다.
  - title과 header의 경우 모두 기본값이 존재한다.
  - 이에 사용자에게 어떤 부분을 보여주고 싶은지에 따라 문구를 선택하면 된다.



### Alert 창 구현 방식

- Sudoku 문제를 맞췄을 경우에는 소요 시간 (count)와 함께 보여준다.

  ```java
  Alert alert = new Alert(Alert.AlertType.INFORMATION);
  alert.setTitle("Sudoku 게임 결과");
  alert.setHeaderText("Sudoku 게임 결과입니다.");
  alert.setContentText("정답입니다!! 축하합니다 :) \\n게임 소요 시간은 총 " + count + "초 입니다.");
  ```

- Sudoku 문제를 틀렸을 때에는 틀렸다는 표시를 해준다.

  ```java
  Alert alert = new Alert(Alert.AlertType.INFORMATION);
  alert.setTitle("Sudoku 게임 결과");
  alert.setHeaderText("Sudoku 게임 결과입니다.");
  alert.setContentText("정답이 아닙니다. 다시 한 번 시도해보세요 :)");
  alert.showAndWait();
  ```

  ![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f6ce1244-4b2c-4937-a0f4-6b4949bb0106/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/f6ce1244-4b2c-4937-a0f4-6b4949bb0106/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T235334Z&X-Amz-Expires=86400&X-Amz-Signature=ab76b784ab0ba3fbea1441654deb34b9ddf1bf4fd66c4712b1c8e07839afd241&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)



### Alert 창 (정답 화면) 구체화

- 정답을 맞힌 경우에는, 사용자가 OK를 눌러 본인이 solve한 sudoku 퍼즐을 다시 살펴보거나, 새로운 게임을 바로 시작하기를 원할 수 있다.

- 그러므로 정답을 맞힌 사용자에게는 2가지 Option을 준다.

  1. 확인 버튼
  2. 새 게임 시작하기

- Alert 창에 사용자 정의 버튼을 추가하려면 다음과 같이 진행하면 된다.

  ```java
  ButtonType buttonNewGame = new ButtonType("새 게임 시작하기");
  alert.getButtonTypes().setAll(buttonNewGame, OK);
  ```

- 그리고 어떤 버튼을 눌렀는지에 따라 로직을 다르게 짜려면 `result`를 가져온다.

  ```java
  Optional<ButtonType> result = alert.showAndWait();
  ```

- `result` 결과값에 따라 (사용자가 선택한 버튼에 따라) 로직을 달리 만든다.

  ```java
  if (result.get() == buttonNewGame) { // 새 게임 시작
      alert.hide();
      count = 0;
      timer_label.setText(Integer.toString(0));
      generateRandom(); // 새로운 퍼즐 생성 
  } else if (result.get() == OK) { // 확인버튼
  	  alert.hide();
  }
  ```



## Answer

`handleAnswer()` 함수 참고



### Answer 로직

- 초기 Sudoku 문제가 생성될 때 2가지를 저장해야 한다.
  1. backtracking을 통해 온전한 sudoku 전체 완성된 배열을 복사해준다 → 이것이 답안! (Answer 배열)
     - `handleGenerate()` 참고!
     - `backtracking()` 이 true를 반환해 스도쿠가 완성되었다면, `answer` 배열에 각각의 elements를 넣어놓는다.
  2. 이후, 30개를 제거한 후, 초기 값들을 저장한다. (Question 배열)
- Answer 버튼을 눌렀을 때 alert 창을 생성한다.
  - 답안(정답) - answer 배열 보여주기



### Alert창 추가 및 로직

- 게임이 생성되지 않았을 경우와, 게임이 생성된 후 answer 버튼을 눌렀을 경우 2가지 경우로 나눔

  - 게임이 생성되지 않은 경우

    - `answer` 배열이 null일 경우, 스도쿠 문제가 생성 되지 않은 것이다.

    - 이에 대해 사용자에게 알려주는 것이 필요하다.

      ![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/ca71b1be-27fe-4d63-9ae5-45c89893dd93/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/ca71b1be-27fe-4d63-9ae5-45c89893dd93/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T235510Z&X-Amz-Expires=86400&X-Amz-Signature=bd53abe01b556e49e35c25c9c1f62d9e798e1a1607b64358dd5a8410a6d2dbe6&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

  - 게임이 생성된 경우

    - `answer` 배열을 하나씩 돌면서 String에 각각의 element를 더해 정답을 alert를 통해 사용자에게 보여준다.

      ![img](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/d5fbf5c7-e633-47fe-ac0b-600062d8327b/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210606%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210606T235533Z&X-Amz-Expires=86400&X-Amz-Signature=2a6fb771deb93caaf1d16a9fcc3dc9b46cf0a7364619c2ae692d4da7b5c59aa5&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

```java
public void handleAnswer(){
        if (answer == null ) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Sudoku 게임 미시작");
            alert.setContentText("Sudoku 게임을 아직 시작하지 않았습니다. \\n게임 생성 후, 정답을 확인하기 위해 눌러주세요.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku 게임 정답");
            alert.setHeaderText("Sudoku 게임 정답입니다.");
            String sudokuAnswer = "";
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    sudokuAnswer += answer.get(i).get(j) + " ";
                }
                sudokuAnswer += "\\n";
            }
            alert.setContentText(sudokuAnswer);
            alert.showAndWait();
        }

    }
```



## 에러 처리

- 사용자가 0 혹은 숫자 이외의 문자를 입력한다면?
  - 이 생각부터 시작해서 에러 처리를 하게 되었다 ㅎㅅㅎ
- `setTextFieldStyle` 메소드 참고



### 잘못된 문자 기입 시 alert 창 생성 로직

- 각 textfield마다 키보드 이벤트를 추가해야 한다.

- `setOnKeyPressed` 라는 메소드를 사용한다.

  ```java
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
              alert.setContentText("숫자 0은 기입이 불가합니다.\\n숫자 1~9까지만 기입이 가능합니다.");
              alert.showAndWait();
          } else if (ch < 49 || ch > 57) { // 알파벳이나 특수문자를 기입하였을 경우
              alert.setContentText("알파벳 및 특수문자는 기입이 불가합니다.\\n숫자 1~9까지만 기입이 가능합니다.");
              alert.showAndWait();
          }
      }
  });
  ```



## 모듈화

### Alert 생성 메소드

- 코드를 작성하다 보니 alert 생성 코드가 너무나도 많이 겹쳤다.

- 중복 방지를 위해 alert를 생성하는 메소드를 하나 만들었다.

  ```java
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
  ```



# 2차 DB추가



## Java와 SQLite 연결하기

- DB인 sqlite와 Java와 연결 하려면 DB connector가 필요하다.
  - Java의 db connector은 JDBC이다.
- Maven을 이용하게 된다면?
  - pom.xml에 어떤 DB를 쓸 것인지 작성하여 sqlite-jdbc 라이브러리가 잘 받아졌는지 확인한다.
- 이번 과제에서는 Maven을 따로 사용하지 않았다.
- 대신, sqlite-jdbc.jar 파일을 설치하여 `src > lib` 폴더 안에 위치시킨 후, [SQLiteManager.java](http://sqlitemanager.java) 파일에 코드를 작성하였다.





### SQLite란?

- 경량 RDBMS 라이브러리이며, 별도로 설치할 필요가 없고 프로그램 내에 자체포함되어 서버가 필요한 MySQL, PostgreSQL와는 달리 서버가 필요 없다.
- 또한, 기본적으로는 DB가 파일로 생성되지만, DB를 메모리에 생성하여 작업을 매우 빠르게 처리할 수 있다.





### 참고

- https://heodolf.tistory.com/139  **SQLite 무작정 시작하기. DB 연결하기/해제하기**





### SQLiteManager.java

```java
package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class SQLiteManager {

    // 상수 설정
    //   - Database 변수
    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String SQLITE_FILE_DB_URL = "jdbc:sqlite:sudoku.db";
    private static final String SQLITE_MEMORY_DB_URL = "jdbc:sqlite::memory";

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
}
```

- ```
  SQLITE_FILE_DB_URL
  ```

  - 어떤 이름으로 DB 파일을 만들 것인지 설정

- ```
  OPT_AUTO_COMMIT
  ```

  - DB에 대해 commit을 자동으로 반영하는 것
  - 대규모 System에서는 해당 기능이 매우 중요하다.

- ```
  OPT_VALID_TIMEOUT
  ```

  - Connection이 connection 객체를 잡는데 시간이 너무 길어지면 프로그램이 중단되기 때문에 안되고, 시간이 너무 짧아도 메모리 부족으로 적정값이 중요하다.

- ```
  SQLITE_MEMORY_DB_URL
  ```

  - SQLite는 특수 `:memory:`를 제공하여 램에 데이터베이스를 만들 수 있다.

- ```
  Connection
  ```

  - sqlite 모듈을 사용하려면, 데이터베이스를 나타내는 `Connection` 객체를 만들어야 한다.

## Schema 정하기

### SQLite Data Type

#### 참고

- http://www.devkuma.com/books/pages/1271
- https://www.sqlite.org/datatype3.html

#### 종류

- NULL
- INTEGER
- REAL
- TEXT
- BLOB

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/71380c3f-2546-4fd9-9996-39e3afe1b8cd/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/71380c3f-2546-4fd9-9996-39e3afe1b8cd/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210607%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210607T001640Z&X-Amz-Expires=86400&X-Amz-Signature=cbf986e4f41ecd6b3b8e82e4999fda157baa7f6a1ce3e6cd0be08e545a15b90e&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

#### Schema

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/bbd8ddc6-bdb8-466c-a6c7-38a087085eba/Untitled.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/bbd8ddc6-bdb8-466c-a6c7-38a087085eba/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210607%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210607T001713Z&X-Amz-Expires=86400&X-Amz-Signature=34d8fca941d78759548da8add5c4c3ce8a6741e83a59165d54e6d0d01f46888b&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

- Sudoku 테이블은 다음과 같이 구성하였다.
  - id
    - PK 값으로 autoincrement, unique key를 설정해주었다.
    - 또한, **PK 값은 Integer이어야 한다**.
  - start_time 게임 시작 시간 → DATETIME
  - end_time 게임 종료 시간 → DATETIME
  - spent_time 게임 소요 시간 → INT
  - problem 스도쿠 퍼즐 문제 → VARCHAR
  - answer 스도쿠 퍼즐 답안 → VARCHAR



## Data Insert 하기

### 데이터/변수 준비하기

- Schema와 같은 형태로 데이터를 insert 하려면 코드를 추가해야 했다.

- start_time은 `generateRandom()` 메소드 내부에, Sudoku 문제가 완성되었을때 현재 시각을 저장할 수 있도록 하였다.

  - `start_time = new Date()`

- end_time은 confirmation 버튼을 눌렀을 때, `handleConfirm()` 메소드 내부에, 정답일 경우 게임 끝난 시각을 저장하였다.

  - `start_time = new Date();`

- answer은 sudokuAnswer이라는 변수에, 정답일 경우 String 형태로 변환할 수 있도록 하였다.

  ```java
  // 정답을 String 형태로 변환하기
  sudokuAnswer = new StringBuilder();
  for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
          sudokuAnswer.append(answer.get(i).get(j)).append(" ");
      }
      sudokuAnswer.append("\\n");
  }
  sudokuAnswer.toString();
  ```

- problem은 `removeElement()` 메소드 내부에, 지워진 숫자가 30개 이상이 된 경우에 question이라는 변수에 저장하였다.

  ```java
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
  		question.append("\\n");
  }
  ```

### 데이터 추가하기

```java
// DB에 데이터 추가하는 메서드
private void insertData(String nickname, Date start_time, Date end_time, int spent_time, String answer, String problem) throws SQLException {
		Object[] params = {nickname, start_time, end_time, spent_time, answer, problem};
    manager.insertGameData(params);
}
```

- 필요한 데이터들을 Object화 시켜서 DB에 삽입할 수 있도록 한다.

```java
// DB 삽입
    public void insertGameData(Object[] data) throws SQLException {
        final String sql = "INSERT INTO SUDOKU(nickname, start_time, end_time, spent_time, answer, problem) VALUES(?, ?, ?, ?, ?, ?)";
        Connection conn = createConnection();
        PreparedStatement pstmt = null;

        int inserted = 0;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, data[0]);
            pstmt.setObject(2, data[1]);
            pstmt.setObject(3, data[2]);
            pstmt.setObject(4, data[3]);
            pstmt.setObject(5, data[4]);
            pstmt.setObject(6, data[5]);

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
            closeConnection();
            if( pstmt != null ) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
```

- ```
  preparedStatement
  ```

  - statement를 상속받는 인터페이스로 SQL 구문을 실행시키는 기능을 갖는 객체이다.

  - PreCompiled된 SQL 문을 표현한다.

  - ```
    statement
    ```

     vs 

    ```
    preapredStatement
    ```

    - `statement` 객체는 실행시 sql 명령어를 지정하여 여러 sql 구문을 하나의 statement 객체로 수행이 가능하다. 재사용이 가능하다.
    - `preparedStatement`는 객체 생성시에 지정된 sql 명령어만을 실행할 수 있다. 이에 다른 sql 구문은 실행하지 못하고 재사용을 못한다.

  - sql문을 실행할 때는 `execute()`, `executeQuery()`, `executeUpdate()` 를 사용한다.

- ```
  connection
  ```

  - ```
    execute()
    ```

    , 

    ```
    executeQuery()
    ```

    , 

    ```
    executeUpdate()
    ```

    - ```
      execute()
      ```

      1. 수행 결과로 Boolean 타입의 값을 반환한다.
      2. 모든 구문을 수행할 수 있다.

      - return 값이 ResultSet일 경우에는 true, 이 외의 경우에는 false로 출력된다.
      - return 값이 ResultSet이라고 하여 ResultSet 객체에 결과값을 담을 수없다.

    - ```
      executeQuery()
      ```

      1. 수행 결과로 ResultSet 객체의 값을 반환한다.
      2. SELECT 구문을 수행할 때 사용되는 함수이다.

      - ResultSet 객체에 결과값을 담을 수 있다.

    - ```
      executeUpdate()
      ```

      1. 수행 결과로 Int 타입의 값을 반환한다.
      2. SELECT 구문을 제외한 다른 구문을 수행할 때 사용되는 함수이다.

      - INSERT/ DELETE/ UPDATE 관련 구문에서는 반영된 레코드의 건수를 반환한다.
      - CREATE/DROP 관련 문구에서는 -1을 반환한다.

  - ```
    commit()
    ```

    - 트랜잭션을 커밋한다.
    - 이 메서드를 호출하지 않으면, 마지막 `commit()` 호출 이후에 수행한 작업은 다른 DB 연결에서 볼 수 없다.
    - 만약 DB에 기록한 데이터가 보이지 않는 경우가 있다면 해당 메서드를 호출해야 한다.

  - ```
    rollback()
    ```

    - 마지막 `commit()` 호출 이후의 DB에 대한 모든 변경 사항을 되돌린다.

  - ```
    close()
    ```

    - 데이터베이스 연결을 닫는다.
    - `commit()` 호출 이후의 DB에 대한 모든 변경 사항을 되돌린다.

### 참고 자료

- https://heodolf.tistory.com/140?category=887835
- https://docs.python.org/ko/3/library/sqlite3.html

## 데이터 조회하기

### 모델 생성하기

- Sudoku 게임 정보를 유지하려면 모델 클래스가 필요하기 때문에, 모델 패키지에 `Sudoku`라는 새로운 클래스를 생성한다.
- 스키마를 기반으로 Model을 생성한다.

#### `Sudoku.java` 파일 생성

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Sudoku {
    private final ObjectProperty<LocalDateTime> startTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final IntegerProperty spentTime;
    private final StringProperty answer;
    private final StringProperty problem;

    // 디폴트 생성자
    public Sudoku(StringProperty problem) {
        this(0, null, null, 0, null, null);
    }

    // 데이터를 초기화하는 생성자
    public Sudoku(int id, LocalDateTime startTime, LocalDateTime endTime, int spentTime, String problem, String answer) {
        this.startTime = new SimpleObjectProperty(startTime);
        this.spentTime = new SimpleIntegerProperty(spentTime);

        // 테스트를 위해 초기화하는 더미 데이터
        this.endTime = new SimpleObjectProperty<LocalDateTime>(endTime);
        this.answer = new SimpleStringProperty(answer);
        this.problem = new SimpleStringProperty(problem);
    }

    public LocalDateTime getStartTime() {
        return startTime.get();
    }

    public ObjectProperty<LocalDateTime> startTimeProperty() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
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
```

- ```
  javafx.beans
  ```

   형태로 생성한다.

  - 빈을 작성하기 위해서는 설계규약을 따라야 한다. 만약 설계규약과 맞지 않는 경우 빈의 특성을 갖지 않는 클래스가 되어버릴수 있다.

  1. 멤버변수마다 별도의 get/set 메소드가 존재해야 한다.
  2. get 메소드는 매개변수가 존재하지 않아야한다.
  3. set 메소드는 반드시 하나 이상의 매개변수가 존재해야 한다.
  4. 생성자는 매개변수가 존재하지 않아야 한다.
  5. 멤버변수의 접근제어자는 private이고 각 set/get메소드의 접근제어자는 public , 클래스의 접근제어자는 public 으로 정의한다.

- 위 개념에 따라 멤버변수와 생성자, 그리고 getter & setter을 이용한 Sudoku 모델을 생성하였다.

  - start_time, end_time은 LocalDateTime 클래스로, spent_time은 Integer, problem과 answer은 String 클래스로 생성하였다.

### 참고

- https://code.makery.ch/kr/library/javafx-tutorial/part2/
- https://gmlwjd9405.github.io/2018/11/10/spring-beans.html



## 데이터 조회 메소드 추가하기

```java
SQLiteManager.java
import javafx.collections.ObservableList;
private ObservableList<Sudoku> sudokuData = FXCollections.observableArrayList();

// 데이터 조회 메소드
    public void selectSudokuList() {
        String sql = "SELECT * FROM sudoku";

        try (Connection conn = createConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nickname = rs.getString("nickname");
                int spent_time = rs.getInt("spent_time");
                String problem = rs.getString("problem");
                String answer = rs.getString("answer");
                LocalDateTime start_time = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end_time = rs.getTimestamp("end_time").toLocalDateTime();
                sudokuData.add(new Sudoku(id, nickname, start_time, end_time, spent_time, problem, answer));
            }
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            closeConnection();
        }
    }

    public ObservableList<Sudoku> getSudokuData() {
        return sudokuData;
    }
```

- `Resultset`

  - 결과값을 저장할 수 있으며, 저장된 값을 한 행 단위로 불러올 수 있다.
  - 또한, 한 행에서 값을 가져올 때는 타입을 지정해 불러올 수 있다.
  - ResultSet은 `Statement`를 통해 값을 저장할 수 있으며, `executeQuery(String sql)` 메소드를 통해 저장할 수 있다.
  - `next()` 메소드를 통해 선택되는 행을 바꿀 수 있다.
    - 그리고 다음행이 내려간 다음, 행이 있을 경우에는 TRUE를 반환하고, 없을 경우에는 FALSE를 반환한다.
  - `get타입()`메소드를 통해 데이터를 불러올 수 있다.
    - `getArray`, `getBlob` 등등 컬럼의 숫자나 이름을 지정해서 값을 불러올 수 있게 된다.

- ResultSet을 통해 sudokuData에 데이터를 추가한다.

- `ObservableList`

  - Observable 인터페이스에는 5개의 하위 인터페이스가 있다. 

    - `ObservableValue`
    - `ObservableList`
    - `ObservableMap`
    - `ObservableSet`
    - `ObservableArray`

    - 이는 관찰 가능한 컬렉션과 배열이다.

  - 그 중 `ObservableList`를 사용하였다.

  - 관찰 가능 기능은 등록한 리스너에 대한 통지 기능이다. InvalidateListener를 등록하면 해당 컬렉션의 내용이 변경될 때마다 통지된다. 관찰가능 컬렉션들은 변경 이벤트를 통해 자세한 변경 사항을 전달할 수 있다.

### 참고

- https://aricode.tistory.com/10
- https://jungjuseong.gitbooks.io/java-fx/content/observable_collection_and_arrays.html
- https://heodolf.tistory.com/142?category=887835
- https://www.sqlitetutorial.net/sqlite-java/select/



## Table View와 DB 레코드 연결하기

```java
private void initializeTable() {
        // 테이블 초기화하기
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        spentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().spentTimeProperty().asObject());

        sudokuTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSudokuGame(newValue));

        // 테이블에 observable 리스트 데이터를 추가한다.
        manager.selectSudokuList();
        sudokuTable.setItems(manager.getSudokuData());
    }
```

- 테이블을 초기화하고 테이블에 observable리스트 데이터를 추가한다.

- `setCellValueFactory`

  - `Sudoku` 객체의 어떤 필드를 각 열에 사용할지 결정하는 데 사용된다.

  - 주의할 점

    - IntegerProperty나 DoubleProperty가 필요하다면 setCellValueFactory(...)는 반드시 추가로 asObject()를 이용해야 한다.

    ```java
    myIntegerColumn.setCellValueFactory(cellData ->
          cellData.getValue().myIntegerProperty().asObject());
    ```

- `getSelectionModel`

  - 현재 설치된 selection model을 선택한다.
  - `getSelectionModel`이라는 것을 호출해서, 반환되는 인스턴스의 getSelectedItem라는 메소드를 호출한다. 이것으로 선택된 항목의 오브젝트를 얻어 올 수 있는 것이다
  - `SelectionModel`이라고 하는 것은, 선택된 항목을 관리하는 "모델 클래스"이다. JavaFX에는 다양한 데이터를 관리하기 위해 "모델"이라는 개념을 도입하고 있다. 모델은 동적으로 조작하는 데이터를 관리하기위한 것이다.
  - 선택된 항목은 `SelectionModel`라는 클래스를 사용하면 관리할 수 있다. `getSElectionModel` 메소드를 호출하는 것으로, ListView에 포함된 `SelectionModel` 인스턴스를 얻을 수 있다.

- `setItems` 를 통해 observable 리스트를 연결해 테이블에 value를 추가한다.

### 참고

- https://stackoverflow.com/questions/18941093/how-to-fill-up-a-tableview-with-database-data
- https://code.makery.ch/kr/library/javafx-tutorial/part2/
- https://araikuma.tistory.com/408

https://stackoverflow.com/questions/50224091/getting-localdate-to-display-in-a-tableview-in-javafx



## TableView의 Date 출력형식 수정하기

- 하다보니 Date 출력형식이 2019-05-11T12:34:25 이런식으로 나와서 날짜를 읽기 편하도록 출력 형식을 수정했다.



### 모델 수정

- 그 대신, LocalDateTime을 String 값으로 변형해서 DB와 Model에 저장할 수 있도록 하였다.

```java
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Sudoku {
    private final StringProperty startTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final IntegerProperty spentTime;
    private final StringProperty answer;
    private final StringProperty problem;

    private static final String DATE_FORMATTER = "yyyy년 MM월 dd일 HH시 mm분 ss초";

    // 데이터를 초기화하는 생성자
    public Sudoku(int id, LocalDateTime startTime, LocalDateTime endTime, int spentTime, String problem, String answer) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String formatStartTime = startTime.format(formatter);
        this.startTime = new SimpleStringProperty(formatStartTime);
        this.spentTime = new SimpleIntegerProperty(spentTime);

    }
```

- `DateTimeFormatter` 를 사용하여 formatting을 진행한다.

### 어려운 점

- 또한 어려웠던 점은 SQLTime과 LocalDateTime을 연동시키는 것이었다.
- 이를 위해 `ResultSet`의 `getTimeStamp()`를 통해 시간을 가져오고, 이를 `toLocalDateTime()` 을 통해 LocalDateTime 형식으로 변경하였다.
- converting java SQL Time to LocalDateTime
  - https://stackoverflow.com/questions/42414172/convert-java-sql-date-to-java-time-localdatetime



## TableView 반응 추가하기

- 각각의 row를 선택하면 해당하는 게임 기록으로 Sudoku board 바뀌게 만들기

- 테이블 선택 감지하기

  `[Controller.java](<http://controller.java>)` → `initializeTable()`

  ```java
  // 각각의 row마다 선택된 아이템에 대해 event listener 추가하기
  personTable.getSelectionModel().selectedItemProperty().addListener(
  		(observable, oldValue, newValue) -> 메소드이름(newValue));
  
  // 실제 사용방식
  // 각각의 row마다 선택된 아이템에 대해 event listener 추가하기
  sudokuTable.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, newValue) -> showSudokuGame(newValue));
  ```

- `showSudokuGame`

  ```java
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
  
              String[] rowList = problem.split("\\n");
              String[] answerRowList = answer.split("\\n");
  
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
  ```

- 현재 게임이 시작되어 시간이 흐른다면 `timeline` 을 멈추고, count를 db에 있는 숫자로 바꾸기. 뿐만 아니라 timer_label의 value 바꾸기

- 이후 각각의 sudoku 숫자들을 하나씩 살펴보면서 기존 게임판으로 value 바꾸기.

### 참고

- https://code.makery.ch/kr/library/javafx-tutorial/part3/
- 문자열 자르기 (question answer String을 split 하기)
  - https://smartpro.tistory.com/9



## 삭제 버튼 추가하기

- DB에서 삭제하기
  - https://heodolf.tistory.com/144



### 삭제 버튼과 연동된 메소드

```java
@FXML
    private void handleDeleteSudoku() {
        int selectedIndex = sudokuTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int selectedDBIndex = sudokuTable.getSelectionModel().getSelectedItem().getId().getValue().intValue();
            sudokuTable.getItems().remove(selectedIndex);
            manager.deleteGameData(selectedDBIndex);
        } else {
            // 아무 sudoku 게임 기록도 선택하지 않은 경우)
            Alert alert = createAlert("warning", "오류", "선택된 기록이 없습니다.", "스도쿠 게임 기록을 선택해주세요.");
            alert.showAndWait();
        }
    }
```

- `int selectedIndex = sudokuTable.getSelectionModel().getSelectedIndex();` 를 통해 table의 index를 찾아 해당 레코드를 지울 수 있도록 한다.

- 선택된 레코드의 DB id를 찾는다.

- 이후, DB id와 함께 deleteGameData 메소드를 통해 DB에 있는 레코드를 지운다.

  ```java
  // 데이터 삭제 메소드 
      public void deleteGameData(int id) {
          final String sql = "DELETE FROM SUDOKU WHERE ID = " + id;
          Connection conn = ensureConnection();
          PreparedStatement pstmt = null;
  
          try {
              pstmt = conn.prepareStatement(sql);
              pstmt.executeUpdate();
              conn.commit();
          } catch (SQLException e) {
              System.out.println(e.getMessage());
          } finally {
              if (pstmt != null) {
                  try {
                      pstmt.close();
                  } catch (SQLException e) {
                      e.printStackTrace();
                  }
              }
          }
      }
  ```

- 만약 아무 레코드가 선택되지 않은 상태에서 해당 버튼을 눌렀다면 경고창 alert 를 띄워준다.



## 닉네임 추가하기

- TableView에 누가 해당 게임을 진행했는지 알 수 있으면 좋을 듯하여 닉네임을 추가하였다.

### DB, 모델을 수정한다.

- DB, 모델, Table을 모두 수정한다!

  - DB에 String 형식의 nickname 스키마를 추가한다.

- 모델 역시 닉네임을 추가하여 수정해준다.

  ```java
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
  ```



### 사용자로부터 닉네임 받기

- TextInputDialog (닉네임받기)를 사용한다.
  - https://www.geeksforgeeks.org/javafx-textinputdialog/

```java
private void getNickname() {
        // 닉네임 받기;
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("닉네임 입력");
        dialog.setHeaderText("닉네임을 입력해주세요.");

        // 입력 취소는 disable
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            nickname = name;
        });
    }
```



### TableView 초기화에 닉네임 추가하기

```java
private void initializeTable() {
        nicknameColumn.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        spentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().spentTimeProperty().asObject());

        // 각각의 row마다 선택된 아이템에 대해 event listener 추가하기
        sudokuTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSudokuGame(newValue));

        // 테이블에 observable 리스트 데이터를 추가한다.
        manager.selectSudokuList();
        sudokuTable.setItems(manager.getSudokuData());
    }
```



## 새로운 기록이 추가될 때 TableView 업데이트하기

- 업데이트를 하면 `initializeTable` 을 통해 sudokuTable에 다시 setItems를 할 수 있도록 하였는데, 이렇게 되면 기존 데이터는 삭제되지 않고 그냥 남아있는 상태에서 모든 db 데이터가 추가되는 현상을 발견하였다.

- 이에 

  ```
  initializeTable
  ```

   을 할 때 TableView의 내용들을 다 삭제할 수 있도록 하였다.

  - TableView 삭제하기
    - https://stackoverflow.com/questions/32176782/how-can-i-clear-the-all-contents-of-the-cell-data-in-every-row-in-my-tableview-i/52770465
    - `tableView.getItems().clear()`






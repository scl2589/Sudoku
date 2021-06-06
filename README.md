# 스도쿠 만들기

## 개요

- 1차
  - 목표: JavaFx로 스도쿠 만들기 
  - 기간: 5/12 ~ 5/20(수) 





# **스도쿠 퍼즐을 만드는 알고리즘**

## 참고 자료

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

   - 1부터 9까지 

     ```
     numbers
     ```

      arraylist를 만들고, 이를 shuffle한다. 그리고 그 순서대로 박스를 채워 나간다.

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



# Sudoku 퍼즐 디자인

`setTextFieldStyle()` 메소드 참고

- 각 textbox의 row/column 값을 계산해서 outline 색깔 추가
- Sudoku 문제로 주어진 사용자의 편의성을 고려하여 숫자는 gray 색깔로 주며, disable 한다.



# Timer추가

## 참고 자료 및 TIL

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



## 초기화

- JavaFX Scene Builder와 변수를 ID 값(`timer_label`)으로 연결시켜준다.

  - 폰트 사이즈는 1.5em으로 한다.

    ```java
    timer_label.setStyle("-fx-font-size: 1.5em;");
    ```



## 구현 방법

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



## 고려사항

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



# Confirmation

`handleConfirm()` 함수 참고



## Confirmation 로직

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



## Alert 창 (정답 화면) 구체화

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



# Answer

`handleAnswer()` 함수 참고



## Answer 로직

- 초기 Sudoku 문제가 생성될 때 2가지를 저장해야 한다.
  1. backtracking을 통해 온전한 sudoku 전체 완성된 배열을 복사해준다 → 이것이 답안! (Answer 배열)
     - `handleGenerate()` 참고!
     - `backtracking()` 이 true를 반환해 스도쿠가 완성되었다면, `answer` 배열에 각각의 elements를 넣어놓는다.
  2. 이후, 30개를 제거한 후, 초기 값들을 저장한다. (Question 배열)
- Answer 버튼을 눌렀을 때 alert 창을 생성한다.
  - 답안(정답) - answer 배열 보여주기



## Alert창 추가 및 로직

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



# 에러 처리

- 사용자가 0 혹은 숫자 이외의 문자를 입력한다면?
  - 이 생각부터 시작해서 에러 처리를 하게 되었다 ㅎㅅㅎ
- `setTextFieldStyle` 메소드 참고



## 잘못된 문자 기입 시 alert 창 생성 로직

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



# 모듈화



## Alert 생성 메소드

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
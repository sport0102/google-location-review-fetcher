# google-location-review-fetcher
## 작동 구성
1. 지정된 파일에서 장소 이름을 가져옴
2. google map api로 각 장소 이름으로 질의를 하여 각 장소의 placeId를 가져옴
3. 가져온 placeId를 다시 질의하여 각 장소의 review 등의 정보를 가져옴
4. 가져온 정보 중 필요한 정보만 scv 파일로 저장
## 사용 방법
1. testdata/raw에 데이터 파일을 넣는다
2. GoogleMap class main 문 실행
3. testdata/result에 파일이 들어가는 것 확인(시간 소요)
## 저장하는 정보
장소 id - 장소 이름 - 장소 평균 평점 - 리뷰한 사람 ID - 리뷰한 사람 이름 - 리뷰한 사람 평점 - 리뷰 내용 - 리뷰 시간 순으로 저장되며, tab으로 구분되어있음
## 기타 사항
1. input data folder 명 : testdata/raw
2. result data folder 명 : testdata/result
3. 수정이 필요하면 코드상에서 수정하면 됨

-- 원본
CREATE TABLE `tb_post` (
    `id`            bigint(20)    NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `title`         varchar(100)  NOT NULL COMMENT '제목',
    `content`       varchar(3000) NOT NULL COMMENT '내용',
    `writer`        varchar(20)   NOT NULL COMMENT '작성자',
    `view_cnt`      int(11)       NOT NULL COMMENT '조회 수',
    `notice_yn`     tinyint(1)    NOT NULL COMMENT '공지글 여부',
    `delete_yn`     tinyint(1)    NOT NULL COMMENT '삭제 여부',
    `created_date`  datetime      NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
    `modified_date` datetime               DEFAULT NULL COMMENT '최종 수정일시',
    PRIMARY KEY (`id`)
) COMMENT '게시글';

-- 댓글
create table tb_comment (
      id bigint not null auto_increment comment '댓글 번호 (PK)'
    , post_id bigint not null comment '게시글 번호 (FK)'
    , content varchar(1000) not null comment '내용'
    , writer varchar(20) not null comment '작성자'
    , delete_yn tinyint(1) not null comment '삭제 여부'
    , created_date datetime not null default CURRENT_TIMESTAMP comment '생성일시'
    , modified_date datetime comment '최종 수정일시'
    , primary key(id)
) comment '댓글';

alter table tb_comment add constraint fk_post_comment foreign key(post_id) references tb_post(id);

show full columns from tb_comment; -- 테이블 구조 확인 1 (코멘트 포함)
desc tb_comment; -- 테이블 구조 확인 2

select *
from information_schema.table_constraints
where table_name = 'tb_comment'; -- where table_schema = 'SAM_TEST';

-- 회원
CREATE TABLE `tb_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '회원 번호 (PK)',
  `login_id` varchar(20) NOT NULL COMMENT '로그인 ID',
  `password` varchar(60) NOT NULL COMMENT '비밀번호',
  `name` varchar(20) NOT NULL COMMENT '이름',
  `gender` enum('M','F') NOT NULL COMMENT '성별',
  `birthday` date NOT NULL comment '생년월일',
  `delete_yn` tinyint(1) NOT NULL COMMENT '삭제 여부',
  `created_date` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `modified_date` datetime DEFAULT NULL COMMENT '최종 수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY uix_member_login_id (`login_id`)
) COMMENT '회원';

CREATE TABLE `tb_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '파일 번호 (PK)',
  `post_id` bigint(20) NOT NULL COMMENT '게시글 번호 (FK)',
  `original_name` varchar(255) NOT NULL COMMENT '원본 파일명',
  `save_name` varchar(40) NOT NULL COMMENT '저장 파일명',
  `size` int(11) NOT NULL COMMENT '파일 크기',
  `delete_yn` tinyint(1) NOT NULL COMMENT '삭제 여부',
  `created_date` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `deleted_date` datetime DEFAULT NULL COMMENT '삭제일시',
  PRIMARY KEY (`id`),
  KEY `fk_post_file` (`post_id`),
  CONSTRAINT `fk_post_file` FOREIGN KEY (`post_id`) REFERENCES `tb_post` (`id`)
) COMMENT '파일';


-- --------------------------------------------------- 대문자 변경
-- drop table TB_COMMENT;
-- drop table TB_FILE;
-- drop table TB_POST;
-- tinyint(1) 대신 bool 로 변경함
CREATE TABLE `TB_POST` (
    `ID`            bigint(20)										NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `TITLE`         varchar(100) COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '제목',
    `CONTENT`       varchar(3000) COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '내용',
    `WRITER`        varchar(20) COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '작성자',
    `VIEW_CNT`      int(11)											NOT NULL COMMENT '조회 수',
    `NOTICE_YN`     bool										NOT NULL COMMENT '공지글 여부',
    `DELETE_YN`     bool										NOT NULL COMMENT '삭제 여부',
    `CREATED_DATE`  datetime										NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '생성일시',
    `MODIFIED_DATE` datetime									DEFAULT NULL COMMENT '최종 수정일시',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_nopad_ci COMMENT '게시글';

CREATE TABLE `TB_COMMENT` (
    `ID`            bigint(20) 										NOT NULL AUTO_INCREMENT COMMENT '댓글 번호 (PK)',
    `POST_ID`       bigint(20)										NOT NULL COMMENT '게시글 번호 (FK)',
    `CONTENT`       varchar(1000) COLLATE utf8mb4_unicode_nopad_ci 	NOT NULL COMMENT '내용',
    `WRITER`        varchar(20) COLLATE utf8mb4_unicode_nopad_ci   	NOT NULL COMMENT '작성자',
    `DELETE_YN`     bool										NOT NULL COMMENT '삭제 여부',
    `CREATED_DATE`  datetime										NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '생성일시',
    `MODIFIED_DATE` datetime									DEFAULT NULL COMMENT '최종 수정일시',
    PRIMARY KEY (`ID`)
    , KEY `FK_TB_POST_TO_TB_COMMENT` (`POST_ID`)
    , CONSTRAINT `FK_TB_POST_TO_TB_COMMENT` FOREIGN KEY (`POST_ID`) REFERENCES `TB_POST` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_nopad_ci COMMENT '댓글';

CREATE TABLE `TB_MEMBER` (
  `ID` 				bigint(20) 										NOT NULL AUTO_INCREMENT COMMENT '회원 번호 (PK)',
  `LOGIN_ID` 		varchar(20) COLLATE utf8mb4_unicode_nopad_ci 	NOT NULL COMMENT '로그인 ID',
  `PASSWORD` 		varchar(60) COLLATE utf8mb4_unicode_nopad_ci 	NOT NULL COMMENT '비밀번호',
  `NAME` 			varchar(20) COLLATE utf8mb4_unicode_nopad_ci 	NOT NULL COMMENT '이름',
  `GENDER` 			enum('M','F') COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '성별',
--ok  `GENDER` 			char(1) COLLATE utf8mb4_unicode_nopad_ci		NOT NULL COMMENT '성별',
  `BIRTHDAY` 		date 											NOT NULL comment '생년월일',
  `DELETE_YN` 		bool 										NOT NULL COMMENT '삭제 여부',
  `CREATED_DATE` 	datetime 										NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `MODIFIED_DATE` 	datetime 									DEFAULT NULL COMMENT '최종 수정일시',
  PRIMARY KEY (`ID`),
  UNIQUE KEY UIX_MEMBER_LOGIN_ID (`LOGIN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_nopad_ci COMMENT '회원';

CREATE TABLE `TB_FILE` (
  `ID` 				bigint(20) 										NOT NULL AUTO_INCREMENT COMMENT '파일 번호 (PK)',
  `POST_ID` 		bigint(20) 										NOT NULL COMMENT '게시글 번호 (FK)',
  `ORIGINAL_NAME` 	varchar(255)  COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '원본 파일명',
  `SAVE_NAME` 		varchar(40)   COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '저장 파일명',
  `SIZE` 			int(11) 										NOT NULL COMMENT '파일 크기',
  `DELETE_YN` 		bool 										NOT NULL COMMENT '삭제 여부',
  `CREATED_DATE` 	datetime 										NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `DELETED_DATE` 	datetime 									DEFAULT NULL COMMENT '삭제일시',
  PRIMARY KEY (`ID`)
  , KEY `FK_TB_POST_TO_TB_FILE` (`POST_ID`)
  , CONSTRAINT `FK_TB_POST_TO_TB_FILE` FOREIGN KEY (`POST_ID`) REFERENCES `TB_POST` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_nopad_ci COMMENT '파일';

-- ------------------------------------------------------------
-- ------------------------------------------------------------
출처: https://congsong.tistory.com/30 [Let's develop:티스토리]
[ REST API 설계 규칙 알아보기 ]
	REST 방식에서 사용할 수 있는 대표적인 HTTP 요청 메서드

	HTTP요청 메소드					역할
	   POST						자원(Resource) 생성
	   GET						자원(Resource) 조회
	   PUT						자원(Resource) 수정
	   PATCH					자원(Resource) 수정
	   DELETE					자원(Resource) 삭제\


	리소스(Resource) - 리소스의 행위는 무조건 HTTP 요청 메소드로 정의
	  서비스를 제공하는 시스템의 자원
      명사를 사용해서 자원만을 표시
	  동사 사용 X (insert, update)

		유형				메소드		올바른 표현			잘못된 표현
	  댓글 등록			POST		/comments			/comments/insert
	  댓글 상세정보 조회		GET			/comments/1			/comments/select/1
	  댓글 수정		PUT or PATCH	/comments/1			/comments/update/1
	  댓글 삭제			DELETE		/comments/1			/comments/delete/1
	  댓글 리스트 조회		GET			/comments			/comments/

	계층 관계
	  URI 에 / (slash) 사용
	  끝에 / 가 포함되면 다음 계층이 존재하는 것으로 오해

	   자원(Resource)					메소드			설명
	  /members/{gildong}			GET		아이디가 "gildong"인 회원을 조회
	  /members/{gildong}/games		GET		아이디가 "gildong"인 회원이 가지고 있는 모든 게임을 조회
	  /members/{gildong}/games 		POST	아이디가 "gildong"인 회원의 게임을 추가(등록)
	  /members/{gildong}			PATCH	아이디가 "gildong"인 회원의 정보를 수정
	  /members/{gildong}/games/{3}	DELETE	아이디가 "gildong"인 회원의 게임 중 key가 3번인 게임을 삭제

	가독성
	  URI는 알파벳 소문자 (대문자는 잘못 호출할 가능성)
	       길어지면 - (_ 언더바는 절대 사용X)

	컬렉션과 다큐먼트 (Collection & Document)
	  리소스 안에 포함됨.
	  컬렉션은 여러 객체가 모인 것 (복수), 다큐먼트는 하나의 객체(단수)

	   자원(Resource)				메소드	유형				설명
	  /libraries/bukgu			GET		Document	"libraries" 컬렉션과 "bukgu" 도큐먼트로 조합된 리소스 (도서관 중 북구 도서관을 조회)
	  /libraries/bukgu/books	GET		Collection	"libraries", "books" 컬렉션과 "bukgu" 도큐먼트로 조합된 리소스 (도서관 중 북구 도서관의 모든 서적을 조회)
	  bukgu/books/역행자			GET		Document	"books" 컬렉션과 "bukgu", "역행자" 다큐먼트로 조합된 리소스 (북구 도서관의 모든 서적 중 역행자를 조회)

-- ------------------------------------------------------------


댓글 로직 해석
	  유형			메소드			올바른 표현		잘못된 표현
	댓글 등록			POST			/comments		/comments/insert
	댓글 상세정보 조회	GET				/comments/1		/comments/select/1
	댓글 수정			PUT or PATCH	/comments/1		/comments/update/1
	댓글 삭제			DELETE			/comments/1		/comments/delete/1
	댓글 리스트 조회	GET				/comments		/comments/






0.
PUT _ingest/pipeline/attachment
{
  "description": "Extract attachment information",
  "processors": [
    {
      "attachment": {
        "field": "data"
      }
    },
    {
      "set" : {
        "description": "If 'attachment.content' exists, set 'content' value",
        "field": "content",
        "value": "{{{_source.attachment.content}}}",
        "if": "ctx.attachment.content != null && ctx.attachment.content != ''"
      }
    }
  ]
}

1. 필드 정의 :
1-1 : 게시판 + 파일을 하나의 인덱스로 합하는 방법
1-2 : 게시판, 파일을 인덱스를 구분하는 방법 (필드는 동일하게 가도록 구성)
   게시판  ingest-test-v1
   파일   ingest-test-v2


-- 1-1. 하나의 index로 합하는 방법으로 해보자

-- 테이블 컬럼 참고
CREATE TABLE `TB_POST` (
    `ID`            bigint(20)										NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `TITLE`         varchar(100) COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '제목',
    `CONTENT`       varchar(3000) COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '내용',
    `WRITER`        varchar(20) COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '작성자',
    `VIEW_CNT`      int(11)											NOT NULL COMMENT '조회 수',
    `NOTICE_YN`     tinyint(1)										NOT NULL COMMENT '공지글 여부',
    `DELETE_YN`     tinyint(1)										NOT NULL COMMENT '삭제 여부',
    `CREATED_DATE`  datetime										NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '생성일시',
    `MODIFIED_DATE` datetime									DEFAULT NULL COMMENT '최종 수정일시',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_nopad_ci COMMENT '게시글';

CREATE TABLE `TB_FILE` (
  `ID` 				bigint(20) 										NOT NULL AUTO_INCREMENT COMMENT '파일 번호 (PK)',
  `POST_ID` 		bigint(20) 										NOT NULL COMMENT '게시글 번호 (FK)',
  `ORIGINAL_NAME` 	varchar(255)  COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '원본 파일명',
  `SAVE_NAME` 		varchar(40)   COLLATE utf8mb4_unicode_nopad_ci	NOT NULL COMMENT '저장 파일명',
  `SIZE` 			int(11) 										NOT NULL COMMENT '파일 크기',
  `DELETE_YN` 		tinyint(1) 										NOT NULL COMMENT '삭제 여부',
  `CREATED_DATE` 	datetime 										NOT NULL DEFAULT current_timestamp() COMMENT '생성일시',
  `DELETED_DATE` 	datetime 									DEFAULT NULL COMMENT '삭제일시',
  PRIMARY KEY (`ID`)
  , KEY `FK_TB_POST_TO_TB_FILE` (`POST_ID`)
  , CONSTRAINT `FK_TB_POST_TO_TB_FILE` FOREIGN KEY (`POST_ID`) REFERENCES `TB_POST` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_nopad_ci COMMENT '파일';


2. 기존 테스트했던 index template 참고
-- 검색은 title, content, writer 컬럼에 한해서


-- PUT _index_template/board-template
PUT _index_template/post-template
{
  "index_patterns": ["post-v*"],
  "template" : {
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 0,
      "index.max_ngram_diff": 50,
      "analysis": {
        "char_filter": {
          "white_remove_char_filter": {
            "type": "pattern_replace",
            "pattern": "\\s+",
            "replacement": ""
          },
          "special_character_filter": {
            "pattern": "[^\\p{L}\\p{Nd}\\p{Blank}]",
            "type": "pattern_replace",
            "replacement": ""
          }
        },
        "tokenizer": {
          "title_nori_tokenizer": {
            "type": "nori_tokenizer",
            "decompound_mode": "mixed",
            "discard_punctuation": "true"
          }
        },
        "filter": {
          "ngram2_filter": {
            "type": "ngram",
            "min_gram": 2,
            "max_gram": 10
          },
          "ngram3_filter": {
            "type": "ngram",
            "min_gram": 3,
            "max_gram": 50
          },
          "ngram4_filter": {
            "type": "ngram",
            "min_gram": 4,
            "max_gram": 50
          }
        },
        "analyzer": {
          "jamo_analyzer": {
            "type": "custom",
            "tokenizer": "keyword",
            "filter": [
              "hanhinsam_jamo"
            ]
          },
          "title_nori_analyzer": {
            "type": "custom",
            "tokenizer": "title_nori_tokenizer",
            "filter": [
              "lowercase",
              "nori_readingform"
            ]
          },
          "ac_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "white_remove_char_filter",
              "special_character_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "lowercase",
              "hanhinsam_jamo",
              "ngram4_filter"
            ]
          },
          "ac_search_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter"
            ],
            "tokenizer": "standard",
            "filter": [
              "lowercase",
              "hanhinsam_jamo"
            ]
          },
          "chosung_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "white_remove_char_filter",
              "special_character_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "lowercase",
              "hanhinsam_chosung",
              "ngram4_filter"
            ]
          },
          "chosung_search_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter"
            ],
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "hantoeng_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter",
              "white_remove_char_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "lowercase",
              "hanhinsam_hantoeng",
              "ngram3_filter"
            ]
          },
          "hantoeng_search_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter"
            ],
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "engtohan_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter",
              "white_remove_char_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "lowercase",
              "hanhinsam_engtohan",
              "ngram4_filter"
            ]
          },
          "engtohan_search_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter"
            ],
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "writer_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter",
              "white_remove_char_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "ngram2_filter"
            ]
          }
        }
      }
    },
    "mappings": {
      "properties" : {
        "createdDate" : {
          "type" : "date"
        },
        "postId" : {
          "type" : "keyword"
        },
        "fileId" : {
          "type" : "keyword"
        },
        "title" : {
          "type" : "keyword",
          "copy_to" : ["title_text", "title_ac", "title_chosung", "title_engtohan", "title_hantoeng"]
        },
        "content" : {
          "type" : "keyword",
          "copy_to" : ["content_text", "content_ac", "content_chosung", "content_engtohan", "content_hantoeng"]
        },
        "writer": {
          "type": "keyword",
          "copy_to": ["writer_text"]
        },
        "writer_text": {
          "type": "text",
          "analyzer": "writer_index_analyzer",
          "search_analyzer": "standard"
        },
        "noticeYn": {
          "type": "boolean"
        },
        "deleteYn": {
          "type": "boolean"
        },
        "savedFilename" : {
          "type" : "keyword"
        },
        "modifiedDate" : {
          "type" : "date"
        },
        "deletedDate" : {
          "type" : "date"
        },
        "title_text": {
          "type": "text",
          "analyzer": "title_nori_analyzer"
        },
        "title_ac": {
          "type": "text",
          "analyzer":        "ac_index_analyzer",
          "search_analyzer": "ac_search_analyzer"
        },
        "title_chosung": {
          "type": "text",
          "analyzer": "chosung_index_analyzer",
          "search_analyzer": "chosung_search_analyzer"
        },
        "title_engtohan": {
          "type": "text",
          "analyzer": "engtohan_index_analyzer",
          "search_analyzer": "engtohan_search_analyzer"
        },
        "title_hantoeng": {
          "type": "text",
          "analyzer": "hantoeng_index_analyzer",
          "search_analyzer": "hantoeng_search_analyzer"
        },
        "content_text": {
          "type": "text",
          "analyzer": "title_nori_analyzer"
        },
        "content_ac": {
          "type": "text",
          "analyzer":        "ac_index_analyzer",
          "search_analyzer": "ac_search_analyzer"
        },
        "content_chosung": {
          "type": "text",
          "analyzer": "chosung_index_analyzer",
          "search_analyzer": "chosung_search_analyzer"
        },
        "content_engtohan": {
          "type": "text",
          "analyzer": "engtohan_index_analyzer",
          "search_analyzer": "engtohan_search_analyzer"
        },
        "content_hantoeng": {
          "type": "text",
          "analyzer": "hantoeng_index_analyzer",
          "search_analyzer": "hantoeng_search_analyzer"
        },
        "attachment" : {
          "properties" : {
            "content" : {
              "type" : "text",
              "fields" : {
                "keyword" : {
                  "type" : "keyword",
                  "ignore_above" : 256
                }
              }
            },
            "content_length" : {
              "type" : "long"
            },
            "content_type" : {
              "type" : "text",
              "fields" : {
                "keyword" : {
                  "type" : "keyword",
                  "ignore_above" : 256
                }
              }
            },
            "date" : {
              "type" : "date"
            },
            "language" : {
              "type" : "text",
              "fields" : {
                "keyword" : {
                  "type" : "keyword",
                  "ignore_above" : 256
                }
              }
            },
            "title" : {
              "type" : "text",
              "fields" : {
                "keyword" : {
                  "type" : "keyword",
                  "ignore_above" : 256
                }
              }
            }
          }
        }
      }
    }
  }
  ,
  "priority": 2147483647,
  "composed_of": [],
  "version": 1,
  "_meta": {
    "description": "Bulletin Board template"
  }
}


-- index 생성
PUT post-v1/_doc/0
{
  "title": "v1 생성"
}

-- 생성시 데이터 삭제
DELETE post-v1/_doc/0


-- 권한 생성 (api-key)

1. Role 생성
Role name : se_writer
Cluster privileges : monitor, read_ilm, read_pipeline
Run As privileges : 입력안함
Index privileges :
   book                  view_index_metadata, create, delete, read
   ingest-test-v*     view_index_metadata, create, delete, read
   post-v*     view_index_metadata, create, delete, read

  내가 생성한 index 들은 선택에 왜 안보이지 ?




  create         (Privilege to index documents.)
  create_doc (Privilege to index documents. It does not grant the permission to update or overwrite existing documents.
                      update나 overwrite 안된다 )
  delete         (delete document)

  write           (Privilege to perform all write operations to documents, which includes the permission to index, update, and delete documents
                           as well as performing bulk operations, while also allowing to dynamically update the index mapping.)
                     제일 큰 권한 ( 다 포함하네.. index, update, delete documents, bulk , dynamically update the index mapping.)

 인덱스에 대해 일단, create 와 delete 를 주면 document 조작은 다 될 듯.

2. 사용자 생성
Profile : se
Password : osstem
Privileges : se_writer, superuser
    키바나에서 하려고 보니 elastic으로 로그인 되어 있어서 user가 elastic으로만 됨
    그래서 superuser 롤 추가해 주고 se로 로그인하여 아래 명령의 api key 생성함

3. API KEY 생성

POST /_security/api_key
{
  "name": "se_key",
  "role_descriptors": {
    "se_writer": {
      "cluster": ["monitor", "read_ilm", "read_pipeline"],
      "index": [
        {
          "names": ["ingest-test-v*", "book", "post-v*"],                                                 // 2줄때 이렇게 줘도 되는지, 각각 줘야 하는지
          "privileges": ["view_index_metadata", "create", "delete", "read"]
        }
      ]
    }
  }
}

==>
{
  "id" : "90nNYI4BVPnI9NQf7Fut",
  "name" : "se_key",
  "api_key" : "RbLE5c-gQ5ilJsLtNCK-iQ",
  "encoded" : "OTBuTllJNEJWUG5JOU5RZjdGdXQ6UmJMRTVjLWdRNWlsSnNMdE5DSy1pUQ=="
}

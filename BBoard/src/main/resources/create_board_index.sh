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


2. 기존 테스트했던 index template 참고
-- 검색은 title, content, writer 컬럼에 한해서
PUT _index_template/ingest-test-template
{
  "index_patterns": ["ingest-test-v*"],
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
          "writer_index_analyzer": {  // author_index_analyzer => writer_index_analyzer
            "type": "custom",
            "char_filter": [
              // "author_trivial_filter",  // 삭제, 근데 writer title_nori_tokenizer를 안썼네....?
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
        "@timestamp" : {                      // created_date
          "type" : "date"
        },
        "post_id" : {
          "type" : "keyword"
        },
        "file_id" : {                           // 게시글은 "0", 첨부파일은 "1","2","3",,,
          "type" : "keyword"
        },
        "title" : {                              // title 로 해서 게시글제목과 파일명(original 파일명)을 한 컬럼에 담자
          "type" : "keyword",
          "copy_to" : ["title_text", "title_ac", "title_chosung", "title_engtohan", "title_hantoeng"]
        },
        "content" : {                            // content 로 해서 게시글내용과 파일 내용을 한 컬럼에 담자
          "type" : "keyword",
          "copy_to" : ["content_text", "content_ac", "content_chosung", "content_engtohan", "content_hantoeng"]
        },
        "writer": {                              // author -> writer
          "type": "keyword",
          "copy_to": ["writer_text"]
        },
        "writer_text": {                         // author => writer
          "type": "text",
          "analyzer": "writer_index_analyzer",   // author_index_analyzer =>  writer_index_analyzer
          "search_analyzer": "standard"
        },
        "notice_yn": {
          "type": "boolean"
        },
        "savedfilename" : {                      // 파일 only, disk 파일 저장 경로 포함 (참고용)
          "type" : "keyword"
        },
        "modified_date" : {   // 게시글 only
          "type" : "date"
        }

        // size 는 attachment.content_length
        ,
        "deleted_date" : {    // 파일 only (아니면 실제 삭제하면 필드 필요 없음)
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
        "content_text": {            // content 의 경우 어디까지 할 것인가?  // writer(author) 처럼 content_text 하나에 할지...?
          "type": "text",            // 일단 title 과 동일하게 만들어보자
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
                               // attachment
        "attachment" : {
          "properties" : {
            "content" : {
              "type" : "text",
              "fields" : {
                "keyword" : {
                  "type" : "keyword",
                  "ignore_above" : 256
                }
              },
              "copy_to" : ["content_text", "content_ac", "content_chosung", "content_engtohan", "content_hantoeng"]
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
                     // attachment 아래 기본 4개 인듯하고 date, language, title은 파일마다 다른 듯 ?
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
    "description": "my ingest test template"
  }
}

2. 인덱스 템플릿 정의
PUT _index_template/ingest-test-template
{
  "index_patterns": ["ingest-test-v*"],
  "template" : {
    "mappings" : {
      "properties" : {
        "@timestamp" : {               // created_date
          "type" : "date"
        },
        "savedfilename" : {
          "type" : "keyword"
        },

        "filename" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
          , "copy_to" : "filename_ko"  //
        },

        "data" : {
          "type" : "text",
          "fields" : {
            "keyword" : {             // keyword 하나마 해도 되나? (둘다 필요 없을 듯)
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
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
              , "copy_to" : "content_ko"    //
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

            // attachment 아래 기본 4개 인듯하고 date, language, title은 파일마다 다른 듯 ?
          }
        }
      }
    },
    "settings" : {
      "index" : {
        "routing" : {
          "allocation" : {
            "include" : {
              "_tier_preference" : "data_content"
            }
          }
        },
        "number_of_shards" : "1"
      }
      ,
// book 추가 부분

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
          //,
          // author_trivial_filter 는 필요 없음
          //"author_trivial_filter": {
          //  "type": "mapping",
          //  "mappings": [
          //    "edited by => ",
          //    "지음 => ",
          //  ]
          //}
        },
//   author_trivial_filter 아래에 막음
// author 를 writer 로 생각하여 변경하자

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

            "char_filter": [
              "html_strip"
            ],
            "tokenizer": "keyword",
            "filter": [
              "hanhinsam_jamo"
            ]
          },
          "title_nori_analyzer": {
            "type": "custom",

            "char_filter": [
              "html_strip"
            ],
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

			  ,"html_strip"
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

			  ,"html_strip"
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

			  ,"html_strip"
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

			  ,"html_strip"
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

			  ,"html_strip"
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

			  ,"html_strip"
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

			  ,"html_strip"
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

			  ,"html_strip"
            ],
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "author_index_analyzer": {
            "type": "custom",
            "char_filter": [
            //   "author_trivial_filter", // 앞에서 삭제함
              "special_character_filter",
              "white_remove_char_filter"

			  ,"html_strip"
            ],
            "tokenizer": "keyword",
            "filter": [
              "ngram2_filter"
            ]
          }
        }
      }
	 // 추가 완료

    }
  }
  ,
  "priority": 2147483647,
  "composed_of": [],
  "version": 1,
  "_meta": {
    "description": "my ingest test template"
  }
}




------------------ index --------------------------------
post ingest-test-v1
{
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
        "@timestamp" : {
          "type" : "date"
        },
        "post_id" : {
          "type" : "keyword"
        },
        "file_id" : {
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
        "notice_yn": {
          "type": "boolean"
        },
        "savedfilename" : {
          "type" : "keyword"
        },
        "modified_date" : {
          "type" : "date"
        },
        "deleted_date" : {
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
              },
              "copy_to" : ["content_text", "content_ac", "content_chosung", "content_engtohan", "content_hantoeng"]
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
}
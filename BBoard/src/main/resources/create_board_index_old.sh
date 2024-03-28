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

            // attachment 아래 기본 4개 인듯하고 date, title은 파일마다 다른 ?
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

//   author_trivial_filter 아래에 막음

// author 를 writer 로 생각하여 변경하자


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
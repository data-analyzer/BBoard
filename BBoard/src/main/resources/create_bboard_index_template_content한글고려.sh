-- tokenizer에 title_nori_tokenizer 가 있는데 content에도 한글 고려
-- analyzer title_nori_analyzer에서 사용

-- DELETE _index_template/post-template
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
          "cv_analyzer": {
            "type": "custom",
            "tokenizer": "keyword",
            "filter": [
              "ose_cv"
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
              "ose_cv",
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
              "ose_cv"
            ]
          },
          "fc_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "white_remove_char_filter",
              "special_character_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "lowercase",
              "ose_fc",
              "ngram4_filter"
            ]
          },
          "fc_search_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter"
            ],
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "kotoen_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter",
              "white_remove_char_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "lowercase",
              "ose_kotoen",
              "ngram3_filter"
            ]
          },
          "kotoen_search_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter"
            ],
            "tokenizer": "standard",
            "filter": [
              "lowercase"
            ]
          },
          "entoko_index_analyzer": {
            "type": "custom",
            "char_filter": [
              "special_character_filter",
              "white_remove_char_filter"
            ],
            "tokenizer": "keyword",
            "filter": [
              "lowercase",
              "ose_entoko",
              "ngram4_filter"
            ]
          },
          "entoko_search_analyzer": {
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
          "copy_to" : ["title_text", "title_ac", "title_fc", "title_entoko", "title_kotoen"]
        },
        "content" : {
          "type" : "keyword",
          "copy_to" : ["content_text", "content_ac", "content_fc", "content_entoko", "content_kotoen"]
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
        "title_fc": {
          "type": "text",
          "analyzer": "fc_index_analyzer",
          "search_analyzer": "fc_search_analyzer"
        },
        "title_entoko": {
          "type": "text",
          "analyzer": "entoko_index_analyzer",
          "search_analyzer": "entoko_search_analyzer"
        },
        "title_kotoen": {
          "type": "text",
          "analyzer": "kotoen_index_analyzer",
          "search_analyzer": "kotoen_search_analyzer"
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
        "content_fc": {
          "type": "text",
          "analyzer": "fc_index_analyzer",
          "search_analyzer": "fc_search_analyzer"
        },
        "content_entoko": {
          "type": "text",
          "analyzer": "entoko_index_analyzer",
          "search_analyzer": "entoko_search_analyzer"
        },
        "content_kotoen": {
          "type": "text",
          "analyzer": "kotoen_index_analyzer",
          "search_analyzer": "kotoen_search_analyzer"
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


PUT post-v2/_doc/0
{
  "title": "신규템플릿적용"
}
-- DELETE post-v2/_doc/0

GET post-v2/_search?_source_excludes=data,attachment,content





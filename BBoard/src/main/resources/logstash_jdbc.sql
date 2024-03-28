-- logstash

참고
https://hanamon.kr/elasticsearch-%ea%b2%80%ec%83%89%ec%97%94%ec%a7%84-logstash-%ec%9d%b4%ec%9a%a9%ed%95%9c-mysql-elasticsearch-%eb%8f%99%ea%b8%b0%ed%99%94-%eb%b0%a9%eb%b2%95/

# 설치확인
$ bin/logstash-plugin list | grep logstash-integration

 C:\logstash-8.1.2>.\bin\logstash-plugin.bat list
 "Using bundled JDK: C:\logstash-8.1.2\jdk\bin\java.exe"
 ArgumentError: invalid byte sequence in CP949
                   split at org/jruby/RubyString.java:4235


1. logstash jdbc 라이브러리 install
 # /usr/share/logstash
 $ bin/logstash-plugin install logstash-integration-jdbc


2. JDBC 라이브러리 jar
 서버에 MySQL 테이블 ES 동기화를 위한 JDBC 라이브러리 jar 파일 업로드



3. logstash.conf

Elastic Cloud Kibana를 이용해서 Logstash PipeLines를 이용해서 logstash.conf 파일 세팅

(중앙집중식) https://www.elastic.co/guide/en/logstash/current/logstash-centralized-pipeline-management.html
중앙 집중식 관리란 /etc/logstash/logstash.conf 파일을 만들어서 pipelines.yml 에서 사용하는 것이 아니라
외부에서 logstash.conf를 관리하고 서버에서는 로그스테시 설정 정보만 받아와서 실행하는 것을 말한다.
Elastic Cloud Kibana에서 이와 같이 구성 가능하다.
아래의 설정 페이지에서 구성가능하다.

----------------------------------------------------------------------
input {
  stdin {} # 테스트 용 Logstash가 정상적으로 실행되었다면 Input이 있으면 output 이 반응한다.

  # MySQL 동기화
  jdbc {
    jdbc_driver_library => "/usr/share/java/mysql-connector-j-8.0.32.jar" # JDBC 드라이버 라이브러리 파일 경로를 지정. Logstash가 MySQL 서버에 연결하기 위한 MySQL JDBC 드라이버 로드.
    jdbc_driver_class => "com.mysql.cj.jdbc.Driver" # JDBC 드라이버 클래스 이름 지정. MySQL JDBC 드라이버 클래스 로드를 위한 것.
    jdbc_connection_string => "jdbc:mysql://<MySQL 호스트 URL>:3306/<MySQL 데이터베이스 이름>" # MySQL 서버와 연결하기 위한 JDBC 연결 문자열.
    jdbc_user => "<MySQL 사용자명>"
    jdbc_password => "<MySQL 비밀번호>"
    jdbc_paging_enabled => true # true 인 경우 JDBC 드라이버의 Paging 기능을 사용해 데이터를 검색. 데이터를 일괄적으로 가져오는 대신 페이지 단위로 가져오는 옵션.
    tracking_column => "unix_ts_in_secs" # 변경된 레코드를 추적하기 위해 사용할 칼럼의 이름을 지정, class.unix_ts_in_secs 칼럼을 추적하여 변경된 레코드를 찾는데 사용됨.
    tracking_column_type => "numeric" # tracking_column 의 데이터 타입을 지정한다.
    use_column_value => true # true 인 경우 tracking_colum 에서 지정한 값을 사용해 변경된 레코드를 추적한다.
    statement => "SELECT *, UNIX_TIMESTAMP(updatedAt) AS unix_ts_in_secs FROM <MySQL 테이블 이름> WHERE (UNIX_TIMESTAMP(updatedAt) > :sql_last_value AND updatedAt < NOW()) ORDER BY updatedAt ASC"
    target => "class" # SQL 결과를 저장할 필드를 동적으로 지정 가능하다. (예: 테이블의 모든 필드가 class 안에 저장된다.)
    schedule => "*/30 * * * * *" # Query 실행 주기 설정 (30초에 한 번씩 실행)
    last_run_metadata_path => "/usr/share/logstash/.logstash_jdbc_last_run_production_class" # 마지막으로 실행된 쿼리의 메타데이터를 저정할 파일 경로 정의, 추적용으로 사용됨.
  }
}

filter {
  # MySQL 동기화
  if [class] {
    mutate {
      add_field => { "[@metadata][db_name]" => "<MySQL 데이터베이스 이름>" }
      add_field => { "[@metadata][table_name]" => "<MySQL 테이블 이름>" }
      add_field => { "[@metadata][record_seq]" => "%{[<MySQL 테이블 이름>][<MySQL 테이블의 고유 번호 칼럼명>]}" }
    }
  }
}

output {
  # MySQL 동기화
  if [@metadata][db_name] {
    stdout {}

    elasticsearch {
      hosts => "<엘라스틱서치 호스트 URL>"
      user => "<엘라스틱서치 사용자명>"
      password => "<엘라스틱서치 비밀번호>"
      index => "%{[@metadata][db_name]}.%{[@metadata][table_name]}"
      # "action => 'update'"와 "doc_as_upsert => true"를 같이 사용하면 elasticsearch가 문서를 찾고 해당 문서가 이미 존재하는 경우 업데이트를 하고 문서가 존재하지 않으면 새로 생성한다.
      # 즉, action => 'update'로 설정하고 doc_as_upasert => false 로 설정하면 elasticsearch가 문서를 찾고 해당 문서가 존재하지 않으면 업데이트하지 않는다.
      action => "update" # elasticsearch에 색인할 동작을 설정한다. 'update'를 설정하면, 이미 존재하는 문서를 업데이트하거나, 없는 경우 새로운 문서를 추가한다.
      document_id => "%{[@metadata][record_seq]}" # elasticsearch에 색인할 고유 식별자를 로그스테시에서 가져온 필드 값으로 사용하기
      doc_as_upsert => true # elasticsearch에 도큐먼트를 추가할 때 이미 존재하는 도큐먼트를 업데이트하려면 true로 설정한다. 업데이트할 도큐먼트가 없다면 새로 추가한다.
    }
  }
}
----------------------------------------------------------------------
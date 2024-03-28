package com.se.board.config;

import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ElasticSearchConfig {

//	@Bean
//	public RestHighLevelClient createRestHighLevelClient_812_http_ok() throws IOException {
//		boolean useSSL = false;
//		String apikey = "N2ZGQjFZMEJraXN4aVhNRndRQUo6MV9uY3ZYVXdUX3FjLW5uOGtmcWhzQQ==";
//
//		Header[] defaultHeaders =
//			    new Header[]{ new BasicHeader("Authorization", "ApiKey " + apikey) };
//
//		//String fingerprint = "43b44b7d3f0dfbee4e226bd56aa72634894ed4f44c049905cece601b58c64271";
//		//SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);
//
//		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//		String ES_HOST = System.getenv("ES_HOST");
//		int ES_PORT = Integer.parseInt(System.getenv("ES_PORT"));
//		String ES_USER = System.getenv("ES_USER");
//		String ES_PASSWORD = System.getenv("ES_PASSWORD");
//		credentialsProvider.setCredentials(AuthScope.ANY,
//				new UsernamePasswordCredentials(ES_USER, ES_PASSWORD));
//        // http -> https
//		RestClientBuilder builder = RestClient.builder(new HttpHost(ES_HOST, ES_PORT, useSSL ? "https" : "http"))
//				.setDefaultHeaders(defaultHeaders)
//				.setHttpClientConfigCallback((httpClientBuilder) -> {
//					return httpClientBuilder
//							//.setSSLContext(sslContext)
//							.setDefaultCredentialsProvider(credentialsProvider);
//				});
//
//		RestHighLevelClient esClient = new RestHighLevelClient(builder);
//		try {
//			if (esClient.ping(RequestOptions.DEFAULT)) {
//				log.info("Successfully connected to elasticsearch cluster");
//			}
//		} catch (IOException e) {
//			log.error("Ping to elasticsearch cluster failed.");
//			throw new IOException("Ping to elasticsearch cluster failed.", e);
//		}
//
//		return esClient;
//	}

	@Bean(destroyMethod = "close")
	public RestHighLevelClient createRestHighLevelClient() throws IOException {
		boolean useSSL = true; // elasticsearch-java-8.5.3 버전으로 변경
//		String apikey = "N2ZGQjFZMEJraXN4aVhNRndRQUo6MV9uY3ZYVXdUX3FjLW5uOGtmcWhzQQ=="; // book-api-key "id" : "7fFB1Y0BkisxiXMFwQAJ"
//		String apikey_se = "OGttRFFJNEJWUG5JOU5RZllWc1Y6a0NFbm0zRFZSMWVVQmVMN0pjZ1lPQQ==";  // encoded se_key, index : ingest-test-v* ("username" : "se")              "id" : "8kmDQI4BVPnI9NQfYVsV",
//		String apikey_se = "OTBuTllJNEJWUG5JOU5RZjdGdXQ6UmJMRTVjLWdRNWlsSnNMdE5DSy1pUQ==";  // encoded se_key, index : post-v* 추가 후 다시 생성("username" : "elastic"),  "id" : "90nNYI4BVPnI9NQf7Fut",
//
		String apikey_se = "LWtuTlk0NEJWUG5JOU5RZl9GdS06VFAxNmJrYXBRYjZUNDdsX2FNZ2ZrZw==";   // "id" : "-knNY44BVPnI9NQf_Fu-", "api_key" : "TP16bkapQb6T47l_aMgfkg",
		Header[] defaultHeaders =
			    //new Header[]{ new BasicHeader("Authorization", "ApiKey " + apikey) };
		        new Header[]{ new BasicHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + apikey_se)
		        		, new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7")
		                , new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.elasticsearch+json;compatible-with=7")
		};
//		    { elastic
//			  "id" : "90nNYI4BVPnI9NQf7Fut",
//			  "name" : "se_key",
//			  "api_key" : "RbLE5c-gQ5ilJsLtNCK-iQ",
//			  "encoded" : "OTBuTllJNEJWUG5JOU5RZjdGdXQ6UmJMRTVjLWdRNWlsSnNMdE5DSy1pUQ=="
//			}
	//		DELETE /_security/api_key
	//		{
	//			"ids":[""],
    //			"owner" : "true"
	//		}
//		DELETE /_security/api_key
//		{
//			"name": "se_key",
//		}
//		Update API key
//		-- PUT /_security/api_key/<id>
//		PUT /_security/api_key/VuaCfGcBCdbkQm-e5aOx
//		{
//			  "role_descriptors": {
//			    "role-a": {
//			      "indices": [
//			        {
//			          "names": ["*"],
//			          "privileges": ["write"]
//			        }
//			      ]
//			    }
//			  },
//			  "metadata": {
//			    "environment": {
//			       "level": 2,
//			       "trusted": true,
//			       "tags": ["production"]
//			    }
//			  }
//			}
//		=> write 권한으로 변경
//		PUT /_security/api_key/VuaCfGcBCdbkQm-e5aOx
//		{
//		  "role_descriptors": {}
//		}
//		=> removes the API key’s previously assigned permissions, making it inherit the owner user’s full permissions.
//		   The API key’s effective permissions after the update will be the same as the owner user’s:
//
//		For the next example, assume that the owner user’s permissions have changed from the original permissions to:
//		The following request auto-updates the snapshot of the user’s permissions associated with the API key:
//		PUT /_security/api_key/VuaCfGcBCdbkQm-e5aOx

// Get API key information
//		GET /_security/api_key?id=VuaCfGcBCdbkQm-e5aOx&with_limited_by=true
//		GET /_security/api_key?name=my-api-key
//		GET /_security/api_key?name=my-*
//		GET /_security/api_key?realm_name=native1
//		GET /_security/api_key?username=myuser

//		GET /_security/api_key?owner=true    -- retrieves all API keys owned by the currently authenticated user
//		GET /_security/api_key?active_only=true    -- retrieves all active API keys if the user is authorized to do so
//		GET /_security/api_key?id=VuaCfGcBCdbkQm-e5aOx&owner=true   -- retrieves the API key identified by the specified id if it is owned by the currently authenticated user:
//		GET /_security/api_key?username=myuser&realm_name=native1   -- retrieves all API keys for the user myuser in the native1 realm immediately


//		DELETE /_security/api_key
//		{
//			"ids":["8kmDQI4BVPnI9NQfYVsV"],
//			"owner": true
//		}
//		{ se
//			  "id" : "-knNY44BVPnI9NQf_Fu-",
//			  "name" : "se_key",
//			  "api_key" : "TP16bkapQb6T47l_aMgfkg",
//			  "encoded" : "LWtuTlk0NEJWUG5JOU5RZl9GdS06VFAxNmJrYXBRYjZUNDdsX2FNZ2ZrZw=="
//			}


		String fingerprint = "43b44b7d3f0dfbee4e226bd56aa72634894ed4f44c049905cece601b58c64271";
		SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);

		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		String ES_HOST = System.getenv("ES_HOST");
		int ES_PORT = Integer.parseInt(System.getenv("ES_PORT"));
		String ES_USER = System.getenv("ES_USER");
		String ES_PASSWORD = System.getenv("ES_PASSWORD");

			ES_USER = "se";
			ES_PASSWORD = "osstem";
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(ES_USER, ES_PASSWORD));
        // http -> https
		RestClientBuilder builder = RestClient.builder(new HttpHost(ES_HOST, ES_PORT, useSSL ? "https" : "http"))
				.setDefaultHeaders(defaultHeaders)
				.setHttpClientConfigCallback((httpClientBuilder) -> {
					return httpClientBuilder
							.setSSLContext(sslContext)
							.setDefaultCredentialsProvider(credentialsProvider)
							.setConnectionReuseStrategy((response, context) -> true) 	// keepAlive use true
							.setKeepAliveStrategy((response, context) -> 300); 			// keepAlive timeout sec
				});

//		keep-alive를 사용해 connection을 재상요 할 수 있도록 함
//		관련 이슈 (https://github.com/elastic/elasticsearch/issues/65213)
//		위 이슈에서 제안하는 방법은 OS TCP 레이어 설정을 직접 바꿔야 하기 때문에, 아래 방법으로 keep-alive 설정 하는것을 추천

		RestHighLevelClient esClient = new RestHighLevelClient(builder);
		try {
			if (esClient.ping(RequestOptions.DEFAULT)) {
				log.info("Successfully connected to elasticsearch cluster");
			}
		} catch (IOException e) {
			log.error("Ping to elasticsearch cluster failed.");
			throw new IOException("Ping to elasticsearch cluster failed.", e);
		}

		return esClient;
	}

	@Bean
	RestClientTransport restClientTransport(RestClient restClient, ObjectProvider<RestClientOptions> restClientOptions) {
	    return new RestClientTransport(restClient, new JacksonJsonpMapper(), restClientOptions.getIfAvailable());
	}
}

package com.ibm.hackthon.firealert.api.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.JsonArray;

@Named("dynamodb-search")
public class DynamoDbSearchHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	Logger logger = LoggerFactory.getLogger(DynamoDbSearchHandler.class);

	private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	private DynamoDB dynamoDB = new DynamoDB(client);

	private String tableName = "wx_data";
	
	private Table table = dynamoDB.getTable(tableName);

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Credentials", "true");
        apiResponse.setHeaders(headers);
        
        JsonArray itemArray = new JsonArray();
        
		try {

			long time = System.currentTimeMillis() - 172800000;
			long time1 = 1676010538873L;
            Map<String, String> queryStringParameters = input.getQueryStringParameters();
			System.out.println("Query params::"+queryStringParameters);
			int deviceId = Integer.parseInt( queryStringParameters.get("device_id"));
			logger.info("Parameters {} - {}", time, deviceId);
			QuerySpec spec = new QuerySpec()
					.withKeyConditionExpression("sample_time = :time and device_id = :deviceId")
					.withValueMap(new ValueMap()
							.withNumber(":time", time1)
							.withNumber(":deviceId", deviceId));

			ItemCollection<QueryOutcome> itemCollection = table.query(spec);
			IteratorSupport<Item, QueryOutcome> iterator = itemCollection.iterator();
			Item item = null;
			while (iterator.hasNext()) {
			    item = iterator.next();
			    System.out.println(item.toJSONPretty());
			    itemArray.add(item.toJSONPretty());
			}
			apiResponse.setBody(itemArray.toString());
			apiResponse.setStatusCode(200);
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e.getMessage());
		}
		return apiResponse;
	}
	}

package com.example.dynamodbdemo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.dynamodbdemo.dao.model.ProductInfo;
import com.example.dynamodbdemo.dao.repository.ProductInfoRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {ApplicationTestConfig.class})
@ActiveProfiles("local")
@EnableAutoConfiguration
@TestPropertySource(
    properties = {
      "amazon.dynamodb.endpoint=http://localhost:8000/",
      "amazon.aws.accesskey=test1",
      "amazon.aws.secretkey=test231"
    })
public class ProductInfoRepositoryIntegrationTest {

  private static DynamoDBMapper dynamoDBMapper;

  @Autowired private AmazonDynamoDB amazonDynamoDB;

  @Autowired private ProductInfoRepository repository;

  private static final String EXPECTED_COST = "20";
  private static final String EXPECTED_PRICE = "50";

  @Before
  public void setup() {
    dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

    var tableRequest = dynamoDBMapper.generateCreateTableRequest(ProductInfo.class);
    tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
    amazonDynamoDB.createTable(tableRequest);
  }

  @Test
  public void givenItemWithExpectedCostAndPrice_whenRunFindAll_thenItemIsFound() {
    var productInfo = new ProductInfo(EXPECTED_PRICE, EXPECTED_COST);
    repository.save(productInfo);
    var result = (List<ProductInfo>) repository.findAll();
    assertTrue(result.size() > 0);
    assertEquals(EXPECTED_COST, result.get(0).getCost());
    assertEquals(EXPECTED_PRICE, result.get(0).getMsrp());
  }

  @After
  public void stop() {
    var tableRequest = dynamoDBMapper.generateDeleteTableRequest(ProductInfo.class);
    amazonDynamoDB.deleteTable(tableRequest);
  }
}

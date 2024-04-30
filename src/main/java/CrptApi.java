import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CrptApi {
    private static final String URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private AtomicInteger atomicInteger;
    private long timeCreatCrpt;
    private final long timeUnit;
    private final int requestLimit;
    private final ObjectMapper objectMapper;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.objectMapper = new ObjectMapper();
        this.timeCreatCrpt = System.currentTimeMillis();
        this.timeUnit = timeUnit.toMillis(1);
        this.atomicInteger = new AtomicInteger(0);
        this.requestLimit = requestLimit;
    }

    public void create(Document document, String signature)  {
       if (!timeLimit(System.currentTimeMillis())){
           String json = convertingJSON(document);
           HttpPost httpPost = new HttpPost(URL);
           StringEntity entity = null;
           try {
               entity = new StringEntity(json);
               httpPost.setEntity(entity);
               httpPost.setHeader("Content-type", "application/json");
               httpPost.setHeader("Authorization", signature);
               HttpClients.createDefault().execute(httpPost).getEntity();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

    }

    public synchronized boolean timeLimit(long timeCreat) {
        if (timeCreat - timeCreatCrpt > timeUnit) {
            atomicInteger.lazySet(0);
            timeCreatCrpt = timeCreat;
        }
        return requestLimit < atomicInteger.incrementAndGet();
    }

    public String convertingJSON(Document document) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(document);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static void main(String[] args) {

    }
    @Data
    @AllArgsConstructor
    private static class Document {

        private String participantInn;

        private String docId;

        private String docStatus;

        private String docType;

        private String importRequest;

        private String ownerInn;

        private String producerInn;

        private String productionDate;

        private String productionType;

        private String regDate;

        private String regNumber;

        private List<Product> products;
    }


    @Data
    @AllArgsConstructor
    private static class Product {

        private String certificateDocument;

        private String certificateDocumentDate;

        private String certificateDocumentNumber;

        private String ownerInn;

        private String producerInn;

        private String productionDate;

        private String tnvedCode;

        private String uit_Code;

        private String uituCode;
    }
}

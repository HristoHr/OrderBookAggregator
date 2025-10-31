import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
public class Kraken extends Exchange {

    public Kraken(URI serverURI, JSONObject subsParams, OrderBook orderBookGlobal) {
        super(serverURI, subsParams, orderBookGlobal);
    }

    @Override
    public void onMessage(String message) {
        //System.out.println("Kraken "+message);
        try {
            JSONArray jsonArray = new JSONArray(message);
            JSONObject jsonObject = new JSONObject(jsonArray.get(1).toString());
            //Parse Input
            if (jsonObject.has("b") || jsonObject.has("bs")) {
                String objKey = jsonObject.has("b") ? "b" : "bs";
                for (Object obj : jsonObject.getJSONArray(objKey)) {
                    JSONArray jsonArrayOrder = new JSONArray(obj.toString());
                    BigDecimal price = new BigDecimal(jsonArrayOrder.get(0).toString());
                    BigDecimal amount = new BigDecimal(jsonArrayOrder.get(1).toString());

                    if (amount.compareTo(BigDecimal.ZERO) == 0) {
                        globalOrderBook.subtractBidOrder(bidsMap,price);
                        bidsMap.remove(price);
                    } else {
                        globalOrderBook.addBidsOrder(bidsMap,price,amount);
                        //Add / update price level form the bidsMap with the new amount.
                        bidsMap.put(price.setScale(1, RoundingMode.CEILING), amount);
                    }
                }
            }
            if (jsonObject.has("a") || jsonObject.has("as")) {
                String objKey = jsonObject.has("a") ? "a" : "as";
                for (Object obj : jsonObject.getJSONArray(objKey)) {
                    JSONArray jsonArrayOrder = new JSONArray(obj.toString());
                    BigDecimal price = new BigDecimal(jsonArrayOrder.get(0).toString());
                    BigDecimal amount = new BigDecimal(jsonArrayOrder.get(1).toString());
                    if (amount.compareTo(BigDecimal.ZERO) == 0) {
                        globalOrderBook.subtractAskOrder(asksMap,price);
                        asksMap.remove(price);
                    } else {
                        globalOrderBook.addAsksOrder(asksMap,price,amount);
                        //Add / update price level form the asksMap with the new amount.
                        asksMap.put(price.setScale(1, RoundingMode.CEILING), amount);
                    }
                }
            }
            System.out.println(globalOrderBook.toString());

        } catch (JSONException ex1) {
            System.out.println("Input not an order");
        }


    }

}

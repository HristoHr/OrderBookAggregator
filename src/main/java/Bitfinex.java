import org.json.*;

import java.math.RoundingMode;
import java.net.URI;
import java.math.BigDecimal;

public class Bitfinex extends Exchange {

    public Bitfinex(URI serverURI, JSONObject subsParams, OrderBook orderBookGlobal) {
        super(serverURI, subsParams, orderBookGlobal);
    }

    private void sortInputStream(JSONArray jsonArray) {
        BigDecimal price = new BigDecimal(jsonArray.get(0).toString());
        BigDecimal amount = new BigDecimal(jsonArray.get(2).toString());
        int count = jsonArray.getInt(1);
        if (count == 0) {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                globalOrderBook.subtractBidOrder(bidsMap, price);
                bidsMap.remove(price);
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                globalOrderBook.subtractAskOrder(asksMap, price);
                asksMap.remove(price);
            }

        } else {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                globalOrderBook.addBidsOrder(bidsMap, price, amount);
                bidsMap.put(price.setScale(1, RoundingMode.CEILING), amount);
            }
            //Add to Asks books
            else {
                //No negative amounts in the final orderbook
                amount = amount.multiply(new BigDecimal(-1));
                globalOrderBook.addAsksOrder(asksMap, price, amount);
                asksMap.put(price.setScale(1, RoundingMode.CEILING), amount);
            }
        }
    }

    @Override
    public void onMessage(String message) {

        try {
            //System.out.println("Bitfinex " + message);

            JSONArray jsonArrayRow = new JSONArray(message);
            JSONArray jsonArrayOrder = new JSONArray(jsonArrayRow.get(1).toString());

            if (jsonArrayOrder.length() == 3) {
                sortInputStream(jsonArrayOrder);
            } else {
                for (int i = 0; i < jsonArrayOrder.length(); i++) {
                    JSONArray initialJsonArrayOrder = new JSONArray(jsonArrayOrder.get(i).toString());
                    sortInputStream(initialJsonArrayOrder);
                }
            }
            System.out.println(globalOrderBook.toString());
        } catch (JSONException ex1) {
            System.out.println("Input not an order");
        }


    }
}

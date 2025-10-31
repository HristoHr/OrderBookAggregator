import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws URISyntaxException {

        OrderBook globalOrderBookBids = new OrderBook();

        JSONObject subParB = new JSONObject();
        subParB.put("event", "subscribe");
        subParB.put("channel", "book");
        subParB.put("pair", "tBTCUSD");
        subParB.put("prec", "P0");
        //subParB.put("len", "1");
        //One could pick different order book depth. however since the tick size for the both exchanges is different.
        //1 USD for Bitfinex and 0.1 USD for Kraken. This will create unnatural gaps in the final combined order book.
        Exchange bitfinex = new Bitfinex(new URI("wss://api.bitfinex.com/ws/2"), subParB,globalOrderBookBids);
        bitfinex.connect();

        JSONObject subParK = new JSONObject();
        subParK.put("event", "subscribe");
        subParK.put("pair", (Object) new String[]{"XBT/USD"});
        subParK.put("subscription", new JSONObject().put("name", "book"));
//
        Exchange kraken = new Kraken(new URI("wss://ws.kraken.com"), subParK,globalOrderBookBids);
        kraken.connect();

    }
}
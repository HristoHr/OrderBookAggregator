import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.TreeMap;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;

/**
 * Creates a websocket connection to a server.
 */
abstract class Exchange extends WebSocketClient {
    JSONObject subsParam;
    OrderBook globalOrderBook;

    TreeMap<BigDecimal, BigDecimal> asksMap;
    TreeMap<BigDecimal, BigDecimal> bidsMap;

    public Exchange(URI serverURI,JSONObject subsParams,OrderBook orderBookBidsGlobal) {
        super(serverURI);
        this.subsParam=subsParams;
        this.globalOrderBook =orderBookBidsGlobal;
        this.asksMap = new TreeMap<BigDecimal, BigDecimal>(Collections.reverseOrder());
        this.bidsMap = new TreeMap<BigDecimal, BigDecimal>(Collections.reverseOrder());
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
        send(subsParam.toString());
    }

    @Override
    public void onMessage(String message) { }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}
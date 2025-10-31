import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Orderbook class uses ConcurrentSkipListMap<>(Collections.reverseOrder())
 * because it is thread-safe and keeps the order from highest ask to lowest bid.
 * Also provides better performance than synchronized collections such as Syncronized TreeMap
 * that allows access to only one thread it a time. This will be necessary if more exchanges' APIs are
 * added to the application.
 */
public class OrderBook {

    ConcurrentSkipListMap<BigDecimal, BigDecimal> globalOrderBookBids = new ConcurrentSkipListMap<BigDecimal, BigDecimal>(Collections.reverseOrder());
    ConcurrentSkipListMap<BigDecimal, BigDecimal> globalOrderBookAsks = new ConcurrentSkipListMap<BigDecimal, BigDecimal>(Collections.reverseOrder());

    public void addBidsOrder(TreeMap<BigDecimal, BigDecimal> bidsMap, BigDecimal price,BigDecimal amount){
        if (globalOrderBookBids.containsKey(price)) {
            if (bidsMap.containsKey(price)) {
                //If both global OrderBook bids and bidsMap have this price level.
                //Update global OrderBook bids price level by subtracting the old amount of the bidsMap.
                //This is done in order to avoid mistakes when the 2 books (Kraken and Bitfinex) have orders for same price levels.
                globalOrderBookBids.put(price, globalOrderBookBids.get(price).subtract(bidsMap.get(price)));
            }
            //Then add the new amount.
            globalOrderBookBids.put(price, globalOrderBookBids.get(price).add(amount));
        } else {
            //If price level not present put it in the book
            globalOrderBookBids.put(price.setScale(1, RoundingMode.CEILING), amount);
        }
    }
    //Same logic as bids
    public void addAsksOrder(TreeMap<BigDecimal, BigDecimal> asksMap, BigDecimal price,BigDecimal amount){
        if (globalOrderBookAsks.containsKey(price)) {
            if (asksMap.containsKey(price)) {
                globalOrderBookAsks.put(price, globalOrderBookAsks.get(price).subtract(asksMap.get(price)));
            }
            globalOrderBookAsks.put(price, globalOrderBookAsks.get(price).add(amount));
        } else {
            globalOrderBookAsks.put(price.setScale(1, RoundingMode.CEILING), amount);
        }
    }

    public void subtractBidOrder(TreeMap<BigDecimal, BigDecimal> bidsMap,BigDecimal price){
        //If amount is 0 price level has to be subtracted form the global OrderBook bids and deleted form bidsMap
        //Check global OrderBook bids contains the price level
        if (globalOrderBookBids.containsKey(price)) {
            //If price level is present, the amount is equal to the last input from Kraken or form Bitfinex or sum of both.
            //Since input source is not stored, if same price level input comes form both exchanges one doesn't know which one to subtract.
            //Therefore, a new amount is put to the global OrderBook bids current price level that is equal to the
            //old amount for the global OrderBook bids minus the old amount for the bidsMap.
            globalOrderBookBids.put(price, globalOrderBookBids.get(price).subtract(bidsMap.get(price)));
            //If after the subtraction the global OrderBook bids has 0 amount at this price level, then delete the level.
            if (globalOrderBookBids.get(price).compareTo(BigDecimal.ZERO) == 0) {
                globalOrderBookBids.remove(price);
            }
        }
    }
    //Same logic as bids
    public void subtractAskOrder(TreeMap<BigDecimal, BigDecimal> asksMap,BigDecimal price){
        if (globalOrderBookAsks.containsKey(price)) {
            globalOrderBookAsks.put(price, globalOrderBookAsks.get(price).subtract(asksMap.get(price)));
            if (globalOrderBookAsks.get(price).compareTo(BigDecimal.ZERO) == 0) {
                globalOrderBookAsks.remove(price);
            }
        }
    }

    public BigDecimal[] getBestBid() {
        if (globalOrderBookBids.isEmpty()) return null;
        BigDecimal bestBidKey = globalOrderBookBids.firstKey();
        BigDecimal bestBidValue = globalOrderBookBids.get(bestBidKey);
        return new BigDecimal[]{bestBidKey, bestBidValue};
    }

    public BigDecimal[] getBestAsk() {
        if (globalOrderBookAsks.isEmpty()) return null;
        BigDecimal bestAskKey = globalOrderBookAsks.lastKey();
        BigDecimal bestAskValue = globalOrderBookAsks.get(bestAskKey);
        return new BigDecimal[]{bestAskKey, bestAskValue};
    }

    public ConcurrentSkipListMap<BigDecimal, BigDecimal> getBids() {
        return globalOrderBookBids;
    }

    public ConcurrentSkipListMap<BigDecimal, BigDecimal> getAsks() {
        return globalOrderBookAsks;
    }


    public String getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts.toString();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\nOrderBook at :");
        str.append(getTimeStamp());

        str.append("\nASKS :");
        for (BigDecimal i : globalOrderBookAsks.keySet()) {
            str.append("\n").append(i).append(" : ").append(globalOrderBookAsks.get(i).toPlainString());
        }
        str.append("\n");
        str.append("\nBIDS :");
        for (BigDecimal i : globalOrderBookBids.keySet()) {
            str.append("\n").append(i).append(" : ").append(globalOrderBookBids.get(i).toPlainString());
        }

        str.append("\nBest Bid: ").append(Arrays.toString(getBestBid()));
        str.append("\nBest Ask: ").append(Arrays.toString(getBestAsk()));
        return str.toString();
    }
}

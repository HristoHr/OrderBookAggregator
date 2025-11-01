# OrderBook Aggregator

A real-time cryptocurrency order book aggregator that combines order book data from multiple exchanges (Bitfinex and Kraken) via WebSocket connections. The application creates a unified, thread-safe order book that displays the best bid and ask prices across all connected exchanges.

## Features

- **Multi-Exchange Support**: Aggregates order book data from Bitfinex and Kraken exchanges
- **Real-Time Updates**: Uses WebSocket connections for live order book updates
- **Thread-Safe**: Implements `ConcurrentSkipListMap` for concurrent access from multiple exchange connections
- **Price Normalization**: Normalizes prices to 1 decimal place to handle different tick sizes across exchanges
- **Best Bid/Ask**: Provides easy access to the best bid and ask prices in the aggregated order book
- **Automatic Order Management**: Handles order additions, updates, and removals in real-time

## Prerequisites

- Java 6 or higher
- Maven 3.x

## Dependencies

The project uses the following dependencies (managed via Maven):

- **Java-WebSocket** (1.5.1): WebSocket client implementation for real-time connections
- **JSON** (20201115): JSON parsing and manipulation
- **SLF4J with Log4j** (1.7.12): Logging framework

## Project Structure

```
OrderBookAggregator/
├── src/
│   └── main/
│       └── java/
│           ├── Main.java          # Entry point - sets up exchange connections
│           ├── Exchange.java      # Abstract base class for WebSocket exchange clients
│           ├── Bitfinex.java      # Bitfinex exchange implementation
│           ├── Kraken.java        # Kraken exchange implementation
│           └── OrderBook.java    # Thread-safe aggregated order book
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## Building the Project

To build the project using Maven:

```bash
mvn clean compile
```

To create a JAR file:

```bash
mvn clean package
```

## Running the Application

After building, run the application:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

Or if you have a JAR file:

## How It Works

1. **Initialization**: The `Main` class creates a global `OrderBook` instance and sets up WebSocket connections to both Bitfinex and Kraken exchanges.

2. **Subscription**: Each exchange connection subscribes to the order book channel for BTC/USD:
   - **Bitfinex**: Subscribes to `tBTCUSD` pair
   - **Kraken**: Subscribes to `XBT/USD` pair

3. **Data Processing**: 
   - Each exchange implementation (`Bitfinex`, `Kraken`) receives WebSocket messages
   - Incoming order updates are parsed and processed
   - Orders are added to exchange-specific maps (`bidsMap`, `asksMap`)
   - The global order book is updated accordingly

4. **Price Normalization**: Prices are normalized to 1 decimal place (using `RoundingMode.CEILING`) to handle different tick sizes:
   - Bitfinex: 1 USD tick size
   - Kraken: 0.1 USD tick size

5. **Thread Safety**: The `OrderBook` class uses `ConcurrentSkipListMap` to ensure thread-safe operations when multiple exchange connections update the order book simultaneously.

6. **Output**: The aggregated order book is printed to the console with:
   - Timestamp
   - Complete ASKS list (sorted from highest to lowest)
   - Complete BIDS list (sorted from highest to lowest)
   - Best Bid and Best Ask prices

## Architecture

### Exchange (Abstract Class)
Base class for exchange implementations. Handles:
- WebSocket connection management
- Subscription message sending
- Connection lifecycle (open, close, error)

### Bitfinex & Kraken
Exchange-specific implementations that:
- Parse exchange-specific message formats
- Handle order updates (additions, modifications, removals)
- Normalize data format before updating the global order book

### OrderBook
Thread-safe order book aggregator that:
- Maintains separate maps for bids and asks
- Provides methods to add/subtract orders
- Tracks best bid and ask prices
- Formats output for display

## Example Output

```
OrderBook at :2024-01-15 10:30:45.123

ASKS :
50000.1 : 1.5
50001.2 : 0.8
...

BIDS :
49999.9 : 2.1
49998.5 : 1.3
...

Best Bid: [49999.9, 2.1]
Best Ask: [50000.1, 1.5]
```

## Notes

- The application connects to real exchange WebSocket APIs. Ensure you have an active internet connection.
- Price normalization to 1 decimal place may create small gaps in the order book due to different tick sizes between exchanges.
- The order book depth is configurable but currently uses the full depth to avoid unnatural gaps from different tick sizes.

## Future Enhancements

- Add support for more exchanges
- Implement configurable price normalization
- Add REST API for querying order book data
- Implement order book depth limits
- Add error handling and reconnection logic
- Implement data persistence or export functionality


# MarketMonarch

**MarketMonarch** is a Maven-based Java prototype trading bot designed for use with [Interactive Brokers](https://www.interactivebrokers.com/).  
It executes trades, performs technical analysis, logs activity, generates session reports, and emails them to you.  
Think of it as a *robotic trader* who is very eager to trade… but not particularly good at making you rich (more on that below).

---

## ⚠️ Disclaimer

1. **I take zero responsibility for any financial losses** this software may cause.  
   If it *did* make money consistently, I probably wouldn't be sharing it for free — I’d be sipping cocktails on a beach somewhere.  
2. **It is a prototype** — lots of features can (and should) be improved.  
   For example:
   - Move trading constants into a config file instead of hard-coding them
   - Implement smarter entry/exit logic
   - Actually use the calculated performance metrics for decision-making  
3. **You must create a configuration file** that contains your database connection details before running.

---

## ✨ Features

- Connects to **Interactive Brokers** API (requires their source code dependency installed locally)
- Uses **TA4J** for technical analysis and trade entry signal detection
- Logs via **Log4j**
- Generates a **password-protected PDF session report**
- Emails the report to the user via **Mailtrap** (credentials stored in database)
- Stores trades and performance metrics for later analysis
- Build process produces:
  1. A **fat JAR**
  2. A **Windows EXE**
  3. An **NSIS installer**

---

## 📦 Dependencies

- **Interactive Brokers Java API** (must be downloaded separately)
  - Install locally with:
    ```bash
    mvn install:install-file -Dfile=path/to/ib-api.jar -DgroupId=com.interactivebrokers -DartifactId=ib-api -Dversion=YOUR_VERSION -Dpackaging=jar
    ```
  - Add it to your `pom.xml` in `MarketMonarch`
- **TA4J** — Technical analysis library
- **Log4j** — Logging
- **MySQL** — Data storage
- **PDF library** — For password-protected session reports
- **JavaMail / SMTP client** — For sending email reports

---

## 🗄 Database Structure

**Database 1: `api_key`**  
Stores third-party API credentials (currently Mailtrap):
- **Table:** `credentials`
- **Columns:**
  - `provider` (VARCHAR)
  - `token` (VARCHAR)

**Database 2: `financial_data`**  
Stores trade history and performance analysis data:
- **Table:** `trade`
- **Columns:**
  - `id` (INT, primary key)
  - `symbol` (VARCHAR)
  - `entry_price` (DECIMAL)
  - `quantity` (INT)
  - `entry_time` (TIMESTAMP)
  - `exit_time` (TIMESTAMP)
  - `stop_loss` (DECIMAL)
  - `take_profit` (DECIMAL)
  - `order_efficiency_ratio` (DECIMAL)

---

## 📊 Performance Metrics

> **Important:** Formulas exist in the code, but their results are **not yet integrated** into trading decisions. They’re like New Year’s resolutions — great in theory, not currently acted upon.

Calculated metrics include:

- **Average Return (Absolute & Decimal)**
- **Sharpe Ratio**
- **Sortino Ratio**
- **Profit Factor**
- **Overall Profit** (planned, not yet implemented)
- **Standard Deviation** of returns
- **Downside Deviation**
- **Holding Period**
- **Expected Risk (absolute, percent, per trade)**
- **Expected Profit**
- **Chance/Risk Ratio**
- **Reward-to-Risk Multiple (R-Multiple)**

---

## ⚙️ Configuration

You must create a **config file** that contains:

- Database host, port, user, password  
- Any relevant API keys (e.g., Mailtrap token)

Example (`Configuration.json`):

```properties
{
	"Password":"yourpassword",
	"URL-test_db":"jdbc:mysql://localhost:3306/test_db",
	"URL-api_key":"jdbc:mysql://localhost:3306/api_key",
	"URL-financial_data":"jdbc:mysql://localhost:3306/financial_data",
	"Username":"yourusername"
}
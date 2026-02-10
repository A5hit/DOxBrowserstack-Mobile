# Selenium Version (Java + TestNG)

This folder is a Java 17 + Selenium + TestNG port of the Python Playwright suite.

## Prereqs
- Java 17
- Maven

## Inputs
- `.env` in the repo root (same as Python): `BASE_URL`, `PDP_URL`, `CART_URL`, `ADDRESS_URL`, `CHECKOUT_URL`
- `auth_state.json` in the repo root (created by the Python flow today). This Selenium suite reuses it by applying cookies + localStorage.

## Run (local)
```bash
mvn -f "Selenium Version/pom.xml" test -Dtarget=local -Dbrowser=chrome -Dheadless=false
```

## Run (BrowserStack)
Set env vars:
- `BROWSERSTACK_USERNAME`
- `BROWSERSTACK_ACCESS_KEY`

Then:
```bash
mvn -f "Selenium Version/pom.xml" test -Dtarget=browserstack
```

### BrowserStack overrides
You can override capabilities via system properties:
- `-Dbs.os=Windows` (default)
- `-Dbs.osVersion=11` (default)
- `-Dbs.browser=Chrome` (default)
- `-Dbs.browserVersion=latest` (default)
- `-Dbs.project=DO_OrderPurchaseJourney`
- `-Dbs.build=local-dev`

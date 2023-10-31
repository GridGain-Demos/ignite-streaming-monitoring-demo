package org.gridgain.demo.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class StockTicker {

	private final String symbol;
	private Map<String, DayData> data;
	private DayData currentDayData;
	private double currentPrice;
	private final Random r = new Random();
	int trend = 1;

	public StockTicker(String symbol, Map<String, DayData> data, String startDate) {
		this.symbol = symbol;
		this.data = data;
		setDate(startDate);
	}

	public void nextDay() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = dateFormat.parse(currentDayData.getDate());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_WEEK, 1);
		String day = dateFormat.format(calendar.getTime());
		while (!setDate(day)) {
			calendar.add(Calendar.DAY_OF_WEEK, 1);
			day = dateFormat.format(calendar.getTime());
		}
	}

	public boolean setDate(String date) {
		currentDayData = data.get(date);
		if (currentDayData == null) {
			return false;
		}
		currentPrice = currentDayData.getOpen();
		System.out.println(date);
		return true;
	}

	public double tick() {
		double range = 0;
		if (trend == 1) {
			range = currentDayData.getHigh() - currentPrice;
		} else {
			range = currentPrice - currentDayData.getLow();
		}
		double change = r.nextDouble() * range * trend;
		currentPrice = currentPrice + change;

		boolean dir = r.nextBoolean();
		if (dir)
			trend = 1;
		else
			trend = -1;

		return currentPrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public String getSymbol() {
		return symbol;
	}

}

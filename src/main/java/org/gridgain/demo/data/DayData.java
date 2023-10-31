package org.gridgain.demo.data;

import org.apache.commons.csv.CSVRecord;

public  class DayData {
	private String date;
	private double close;
	private int volume;
	private double open;
	private double high;
	private double low;

	public DayData(String date, double close, int volume, double open, double high, double low) {
		this.date = date;
		this.close = close;
		this.volume = volume;
		this.open = open;
		this.high = high;
		this.low = low;
	}

	public static DayData get(CSVRecord record) {
		String date = record.get("Date");
		double close = Double.parseDouble(record.get("Close/Last").replace("$", ""));
		int volume = Integer.parseInt(record.get("Volume").replace("$", ""));
		double open = Double.parseDouble(record.get("Open").replace("$", ""));
		double high = Double.parseDouble(record.get("High").replace("$", ""));
		double low = Double.parseDouble(record.get("Low").replace("$", ""));
		return new DayData(date, close, volume, open, high, low);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	@Override
	public String toString() {
		return "DayData [date=" + date + ", close=" + close + ", volume=" + volume + ", open=" + open + ", high="
				+ high + ", low=" + low + "]";
	}

}

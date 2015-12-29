package edu.columbia.locationreceiver;

public class CompReading {
	private double lat;
    private double lon;    
    private double temp;
    private double pressure;
    private double humidity;
    private String name;
    private String weather;
    private String weatherDesc;
    
    public CompReading(double lat, double lon, double temp, double pressure, double humidity, String name, String weather,String weatherDesc) {
    	this.lat = lat;
    	this.lon = lon;
    	this.temp = temp;
    	this.pressure = pressure;
    	this.humidity = humidity;
    	this.name = name;
    	this.weather = weather;
    	this.weatherDesc = weatherDesc;
    }

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getTemp() {
		return temp;
	}

	public double getPressure() {
		return pressure;
	}

	public double getHumidity() {
		return humidity;
	}

	public String getName() {
		return name;
	}

	public String getWeather() {
		return weather;
	}

	public String getWeatherDesc() {
		return weatherDesc;
	}
    
    

}

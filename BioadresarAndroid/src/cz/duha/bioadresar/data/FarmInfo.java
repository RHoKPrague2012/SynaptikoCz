package cz.duha.bioadresar.data;

import java.util.List;

public class FarmInfo {
	long id;
	String name;
	double lat, lon;
	String description;
	String type;
	FarmContact contact;
	List<String> products;
	List<String> categories;
}

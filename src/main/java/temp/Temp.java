package temp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Temp {

	public static void main(String[] args) {
		/*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		//System.out.println(formatter.toString());*/
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		LocalDateTime dt = LocalDateTime.parse("2018-07-17T09:59:51.312Z", formatter);
		System.out.println(dt.toString());
	}
}

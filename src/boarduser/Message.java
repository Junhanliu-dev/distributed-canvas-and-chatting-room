package boarduser;

import static boarduser.MainFrame.DATE_FORMAT_NOW;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message {
	
	final String  userName;
	final String timestamp;
	final String content;
	public static final String DATE_FORMAT_NOW = "HH:mm:ss:SSS";
	
	public Message(String userName, String content) {
		this.userName = userName;
		this.content = content;
		this.timestamp = now();
	}
        
        public Message(String userName, String content, String timeStamp)
        {
            		this.userName = userName;
		this.content = content;
		this.timestamp = timeStamp;
        }

	public Message getMsg() {
		return this;
	}

	public String getTimeStamp() {
		return timestamp;
	}

	public String getMessage() {
		return content;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public String toString() {
		return userName + ": " + this.content+ " [" + this.timestamp +"]";
	}
        private String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
}
package boarduser;

import java.util.*;

public class NotExistException extends Exception {
	public NotExistException() {
		super("The target requested does not exist!");
	}

	public NotExistException(String error) {
		super(error);
	}

	public String getMessage() {
		return super.getMessage();
	}
}
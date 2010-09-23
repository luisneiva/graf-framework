package model.exceptions;

/** Rule did not match when trying to apply transformation */
public class RuleNoMatchException extends RuleException {
	public RuleNoMatchException(String string) {
		super(string);
	}
}

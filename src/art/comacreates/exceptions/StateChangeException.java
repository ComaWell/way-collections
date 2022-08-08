package art.comacreates.exceptions;

public class StateChangeException extends RuntimeException {

	private static final long serialVersionUID = -3712206413935740651L;
	
	private final Object beforeState;
	private final Object afterState;
	
	public StateChangeException(Object beforeState, Object afterState, String msg) {
		super(msg);
		this.beforeState = beforeState;
		this.afterState = afterState;
	}
	
	public StateChangeException(Object beforeState, Object afterState, Exception cause) {
		super(cause);
		this.beforeState = beforeState;
		this.afterState = afterState;
	}
	
	public Object beforeState() {
		return beforeState;
	}
	
	public Object afterState() {
		return afterState;
	}

}

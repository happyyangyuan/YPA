package ypa.model.exception;

public class YpaRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 出错的数据
	 */
	private Object errObj;
	
	public YpaRuntimeException(String errCode, Object errObj, Throwable cause){
		super(errCode,cause);
		this.errObj = errObj;
	}

	public Object getErrObj() {
		return errObj;
	}

	public void setErrObj(Object errObj) {
		this.errObj = errObj;
	}
	
	public String getErrCode(){
		return getMessage();
	}

}

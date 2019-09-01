package utn.proy2k18.vantrack.exceptions;

public class BackendException extends RuntimeException {

    private Integer httpCode;
    private String httpMsg;

    public BackendException() { }

    public BackendException(String message) {
        super(message);
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public String getHttpMsg() {
        return httpMsg;
    }

    public void setHttpMsg(String httpMsg) {
        this.httpMsg = httpMsg;
    }

}

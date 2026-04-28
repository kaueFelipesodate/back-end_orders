package br.com.cotefacil_api1.shared.exceptions;

public class ServiceException extends RuntimeException {
    private String field;

    private String messageKey;

    private String[] arguments;

    private Object object;

    private int errorCode;

    public ServiceException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public ServiceException(String messageKey, Object object) {
        super(messageKey);
        this.messageKey = messageKey;
        this.object = object;
    }
}

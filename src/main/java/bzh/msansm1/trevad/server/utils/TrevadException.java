package bzh.msansm1.trevad.server.utils;

import org.jboss.logging.Logger;

public class TrevadException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(TrevadException.class);

    private final String code;
    private final String reason;

    public TrevadException(Exception e) {
        super();
        LOGGER.warn(e);
        this.code = null;
        this.reason = e.getMessage();
    }

    public TrevadException(String error) {
        super();
        LOGGER.warn(error);
        this.code = null;
        this.reason = error;
    }

    public TrevadException(String code, String reason) {
        super();
        this.code = code;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

}

package net.mosur.spaceagency.domain.exception;

import org.springframework.validation.Errors;

@SuppressWarnings("serial") // class implements interface of java.io.serializeable and doesnt privide a field serialVersionUID
public class InvalidRequestException extends RuntimeException{
    private final Errors errors;

    public InvalidRequestException(Errors errors) {
        super("");
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }

}

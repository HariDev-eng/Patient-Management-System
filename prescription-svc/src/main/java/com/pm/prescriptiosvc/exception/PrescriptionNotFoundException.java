package src.main.java.com.pm.prescriptiosvc.exception;

public class PrescriptionNotFoundException
        extends RuntimeException {

    public PrescriptionNotFoundException(
            String message) {

        super(message);
    }
}
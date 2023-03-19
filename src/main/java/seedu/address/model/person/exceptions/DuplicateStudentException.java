package seedu.address.model.person.exceptions;

/**
 * Signals that the operation will result in duplicate Students (Students are considered duplicates if they have the
 * same name & class & index number).
 */
public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException() {
        super("ERROR, operation would result in duplicate students!");
    }
}

package com.yourbank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class LibraryException extends RuntimeException {

    // Common exceptions
    public static class EntityNotFoundException extends LibraryException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidOperationException extends LibraryException {
        public InvalidOperationException(String message) {
            super(message);
        }
    }

    // Book-related exceptions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BookNotAvailableException extends LibraryException {
        public BookNotAvailableException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class BookAlreadyBorrowedException extends LibraryException {
        public BookAlreadyBorrowedException(String message) {
            super(message);
        }
    }

    // Borrowing-related exceptions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BorrowingNotFoundException extends LibraryException {
        public BorrowingNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BookNotBorrowedException extends LibraryException {
        public BookNotBorrowedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BookAlreadyReturnedException extends LibraryException {
        public BookAlreadyReturnedException(String message) {
            super(message);
        }
    }

    // Member-related exceptions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class MemberNotFoundException extends LibraryException {
        public MemberNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class MemberBlockedException extends LibraryException {
        public MemberBlockedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class MemberHasOverdueBooksException extends LibraryException {
        public MemberHasOverdueBooksException(String message) {
            super(message);
        }
    }

    // Fine-related exceptions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class FineNotFoundException extends LibraryException {
        public FineNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidPaymentException extends LibraryException {
        public InvalidPaymentException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class FineAlreadyPaidException extends LibraryException {
        public FineAlreadyPaidException(String message) {
            super(message);
        }
    }

    // Reservation-related exceptions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ReservationNotFoundException extends LibraryException {
        public ReservationNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ReservationAlreadyExistsException extends LibraryException {
        public ReservationAlreadyExistsException(String message) {
            super(message);
        }
    }

    // Authentication/Authorization exceptions
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class AuthenticationFailedException extends LibraryException {
        public AuthenticationFailedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccessDeniedException extends LibraryException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }

    // Base constructor
    public LibraryException(String message) {
        super(message);
    }

    // Utility method for entity not found
    public static EntityNotFoundException notFound(String entityName, Long id) {
        return new EntityNotFoundException(entityName + " not found with id: " + id);
    }
}

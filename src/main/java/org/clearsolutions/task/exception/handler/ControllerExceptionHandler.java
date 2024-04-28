package org.clearsolutions.task.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.clearsolutions.task.exception.AppProblem;
import org.clearsolutions.task.exception.YoungAgeException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String PROBLEMS = "problemDetails";

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Constraint violation");
        Throwable rootCause = ex.getRootCause();
        String message = Objects.requireNonNullElse(rootCause, ex).getMessage();
        AppProblem appProblem = AppProblem.builder().message(message).build();
        pd.setProperty(PROBLEMS, List.of(appProblem));
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Illegal arguments");
        AppProblem appProblem = AppProblem.builder().message(ex.getMessage()).build();
        pd.setProperty(PROBLEMS, List.of(appProblem));
        return pd;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFoundException(EntityNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(NOT_FOUND, "User is not found");
        AppProblem appProblem = getProblem(ex.getMessage(), "User id",
                StringUtils.substringBetween(ex.getMessage(), "'"));
        pd.setProperty(PROBLEMS, List.of(appProblem));
        return pd;
    }

    @ExceptionHandler(YoungAgeException.class)
    public ProblemDetail handleYoungAgeException(YoungAgeException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Young Age");
        AppProblem appProblem = getProblem(ex.getMessage(), "birthDate",
                StringUtils.substringBetween(ex.getMessage(), "'"));
        pd.setProperty(PROBLEMS, List.of(appProblem));
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Constraint violation");
        Set<ConstraintViolation<?>> cvSet = e.getConstraintViolations();
        List<AppProblem> errors = new ArrayList<>();
        for (var cv : cvSet) {
            String field = cv.getPropertyPath().toString().substring(cv.getPropertyPath().toString().lastIndexOf('.') + 1);
            String value = cv.getInvalidValue().toString();
            String message = String.format(cv.getMessage(), field);
            AppProblem error = getProblem(message, field, value);
            errors.add(error);
        }
        problemDetail.setProperty(PROBLEMS, errors);
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, "Wrong input parameter");
        String actualField = ex.getPropertyName();
        Object wrongValue = Optional.ofNullable(ex.getValue()).orElse("");
        String requiredType = "";
        if (ex.getRequiredType() != null) {
            requiredType = ex.getRequiredType().getSimpleName();
        }
        String message = String.format("The field '%s' must have a valid type of '%s'", actualField, requiredType);
        AppProblem appProblem = getProblem(message, actualField, wrongValue.toString());
        pd.setProperty(PROBLEMS, List.of(appProblem));
        return ResponseEntity.badRequest().body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, "Failed validation");
        List<AppProblem> appProblems = new ArrayList<>();
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        for (FieldError err : fieldErrorList) {
            AppProblem appProblem = AppProblem.builder()
                    .message(err.getDefaultMessage())
                    .field(err.getField())
                    .wrongValue(err.getRejectedValue() == null ? null : err.getRejectedValue().toString()).build();
            appProblems.add(appProblem);
        }
        pd.setProperty(PROBLEMS, appProblems);
        return ResponseEntity.badRequest().body(pd);
    }


    private AppProblem getProblem(String message, String field, String wrongValue) {
        return AppProblem.builder()
                .message(message)
                .field(field)
                .wrongValue(wrongValue)
                .build();
    }

}


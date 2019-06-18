package pl.com.migrate.liquibase.exception;

public class InvalidParamsException  extends Exception
{
    public InvalidParamsException() {}


    public InvalidParamsException(String message)
    {
        super(message);
    }
}

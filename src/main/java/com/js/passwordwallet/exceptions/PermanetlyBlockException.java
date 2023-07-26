package com.js.passwordwallet.exceptions;

public class PermanetlyBlockException extends RuntimeException{
    public PermanetlyBlockException(){
        super("Konto zablokowane! Brak dostępu!");
    }
}

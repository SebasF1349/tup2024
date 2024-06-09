package ar.edu.utn.frbb.tup.service;

public class CuentaNoSoportadaException extends Exception {
  public CuentaNoSoportadaException(String errorMsg) {
    super(errorMsg);
  }
}

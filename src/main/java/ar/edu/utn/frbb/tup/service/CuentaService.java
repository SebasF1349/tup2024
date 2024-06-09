package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuentaService {
  CuentaDao cuentaDao = new CuentaDao();

  private final Map<TipoCuenta, List<TipoMoneda>> cuentasSoportadas =
      Map.ofEntries(
          new AbstractMap.SimpleEntry<TipoCuenta, List<TipoMoneda>>(
              TipoCuenta.CAJA_AHORRO, Arrays.asList(TipoMoneda.PESOS, TipoMoneda.DOLARES)),
          new AbstractMap.SimpleEntry<TipoCuenta, List<TipoMoneda>>(
              TipoCuenta.CUENTA_CORRIENTE, Arrays.asList(TipoMoneda.PESOS)));

  @Autowired ClienteService clienteService;

  public void darDeAltaCuenta(Cuenta cuenta, long dniTitular)
      throws CuentaAlreadyExistsException,
          TipoCuentaAlreadyExistsException,
          CuentaNoSoportadaException {
    if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
      throw new CuentaAlreadyExistsException(
          "La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
    }

    // Chequear cuentas soportadas por el banco CA$ CC$ CAU$S
    if (!tipoCuentaEstaSoportada(cuenta)) {
      throw new CuentaNoSoportadaException(
          "El tipo de cuenta "
              + cuenta.getTipoCuenta()
              + " no soporta la moneda "
              + cuenta.getMoneda());
    }

    clienteService.agregarCuenta(cuenta, dniTitular);
    cuentaDao.save(cuenta);
  }

  public Cuenta find(long id) {
    return cuentaDao.find(id);
  }

  public boolean tipoCuentaEstaSoportada(Cuenta cuenta) {
    TipoMoneda moneda = cuenta.getMoneda();
    TipoCuenta tipoCuenta = cuenta.getTipoCuenta();
    if (!cuentasSoportadas.containsKey(tipoCuenta)) {
      return false;
    }
    if (!cuentasSoportadas.get(tipoCuenta).contains(moneda)) {
      return false;
    }
    return true;
  }
}

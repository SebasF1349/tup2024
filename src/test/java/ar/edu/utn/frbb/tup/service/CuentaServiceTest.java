package ar.edu.utn.frbb.tup.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

  @Mock private CuentaDao cuentaDao;
  @Mock private ClienteService clienteService;
  private final long clienteDni = 12345678;

  @InjectMocks private CuentaService cuentaService;

  @BeforeAll
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // Generar casos de test para darDeAltaCuenta
  //    1 - cuenta existente
  //    2 - cuenta no soportada
  //    3 - cliente ya tiene cuenta de ese tipo
  //    4 - cuenta creada exitosamente
  @Test
  public void testDarDeAltaCuentaExistenteException() {
    Cuenta cuenta = createCuenta();

    when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(cuenta);

    assertThrows(
        CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 12345678));
  }

  @Test
  public void testDarDeAltaCuentaNoSoportadaException() {
    Cuenta cuenta = createCuenta(TipoMoneda.DOLARES, TipoCuenta.CUENTA_CORRIENTE);

    when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);

    assertThrows(
        CuentaNoSoportadaException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 12345678));
  }

  @Test
  public void testDarDeAltaCuentaDuplicadaException() throws TipoCuentaAlreadyExistsException {
    Cuenta cuenta = createCuenta();

    when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);
    doThrow(TipoCuentaAlreadyExistsException.class)
        .when(clienteService)
        .agregarCuenta(cuenta, clienteDni);

    assertThrows(
        TipoCuentaAlreadyExistsException.class,
        () -> cuentaService.darDeAltaCuenta(cuenta, clienteDni));
  }

  @Test
  public void testDarDeAltaCuentaSuccess()
      throws TipoCuentaAlreadyExistsException,
          CuentaNoSoportadaException,
          CuentaAlreadyExistsException {
    Cuenta cuenta = createCuenta();

    when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);
    doNothing().when(clienteService).agregarCuenta(cuenta, clienteDni);

    cuentaService.darDeAltaCuenta(cuenta, clienteDni);

    verify(cuentaDao, times(1)).find(cuenta.getNumeroCuenta());
    assertTrue(cuentaService.tipoCuentaEstaSoportada(cuenta));
    verify(clienteService, times(1)).agregarCuenta(cuenta, clienteDni);
    verify(cuentaDao, times(1)).save(cuenta);
  }

  private Cuenta createCuenta() {
    return new Cuenta()
        .setMoneda(TipoMoneda.PESOS)
        .setBalance(500000)
        .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
  }

  private Cuenta createCuenta(TipoMoneda moneda, TipoCuenta cuenta) {
    return new Cuenta().setMoneda(moneda).setBalance(500000).setTipoCuenta(cuenta);
  }
}

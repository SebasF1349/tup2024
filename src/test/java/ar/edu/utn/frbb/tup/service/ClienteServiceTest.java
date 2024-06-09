package ar.edu.utn.frbb.tup.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import java.time.LocalDate;
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
public class ClienteServiceTest {

  @Mock private ClienteDao clienteDao;
  private final long dni = 12345678;

  @InjectMocks private ClienteService clienteService;

  @BeforeAll
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testClienteMenor18Años() {
    Cliente clienteMenorDeEdad = new Cliente();
    clienteMenorDeEdad.setFechaNacimiento(LocalDate.of(2020, 2, 7));
    assertThrows(
        IllegalArgumentException.class, () -> clienteService.darDeAltaCliente(clienteMenorDeEdad));
  }

  @Test
  public void testClienteSuccess() throws ClienteAlreadyExistsException {
    Cliente cliente = this.createCliente();
    clienteService.darDeAltaCliente(cliente);

    verify(clienteDao, times(1)).save(cliente);
  }

  @Test
  public void testClienteAlreadyExistsException() throws ClienteAlreadyExistsException {
    Cliente cliente = this.createCliente();

    when(clienteDao.find(dni, false)).thenReturn(new Cliente());

    assertThrows(
        ClienteAlreadyExistsException.class, () -> clienteService.darDeAltaCliente(cliente));
  }

  @Test
  public void testAgregarCuentaAClienteSuccess() throws TipoCuentaAlreadyExistsException {
    Cliente cliente = this.createCliente();

    Cuenta cuenta = this.createCuenta();

    when(clienteDao.find(dni, true)).thenReturn(cliente);

    clienteService.agregarCuenta(cuenta, cliente.getDni());

    verify(clienteDao, times(1)).save(cliente);

    assertEquals(1, cliente.getCuentas().size());
    assertEquals(cliente, cuenta.getTitular());
  }

  @Test
  public void testAgregarCuentaAClienteDuplicada() throws TipoCuentaAlreadyExistsException {
    Cliente cliente = this.createCliente();

    Cuenta cuenta = this.createCuenta();

    when(clienteDao.find(dni, true)).thenReturn(cliente);

    clienteService.agregarCuenta(cuenta, cliente.getDni());

    Cuenta cuenta2 = this.createCuenta();

    assertThrows(
        TipoCuentaAlreadyExistsException.class,
        () -> clienteService.agregarCuenta(cuenta2, cliente.getDni()));
    verify(clienteDao, times(1)).save(cliente);
    assertEquals(1, cliente.getCuentas().size());
    assertEquals(cliente, cuenta.getTitular());
  }

  // Agregar una CA$ y CC$ --> success 2 cuentas, titular peperino
  // Agregar una CA$ y CAU$S --> success 2 cuentas, titular peperino...
  // Testear clienteService.buscarPorDni

  private Cliente createCliente() {
    Cliente cliente = new Cliente();
    cliente.setFechaNacimiento(LocalDate.of(1978, 3, 25));
    cliente.setDni(dni);
    cliente.setTipoPersona(TipoPersona.PERSONA_FISICA);
    return cliente;
  }

  private Cuenta createCuenta() {
    return new Cuenta()
        .setMoneda(TipoMoneda.PESOS)
        .setBalance(500000)
        .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
  }
}


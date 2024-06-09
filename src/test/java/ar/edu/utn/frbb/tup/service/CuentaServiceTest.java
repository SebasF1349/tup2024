package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
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
}

package com.inventarios;

import com.inventarios.controlador.ProductoControlador;
import com.inventarios.excepcion.RecursoNoEncontradoExcepcion;
import com.inventarios.modelo.Producto;
import com.inventarios.servicio.ProductoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoControladorTests implements AutoCloseable {

    @Mock
    private ProductoServicio productoServicio;

    @InjectMocks
    private ProductoControlador productoControlador;

    private Producto producto;

    private AutoCloseable mocks;

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        producto = new Producto();
        producto.setIdProducto(1);
        producto.setDescripcion("Descripcion 1");
        producto.setPrecio(100.0);
        producto.setExistencia(10);
    }

    @Override
    public void close() throws Exception {
        mocks.close();
    }

    @Test
    public void testObtenerProductos() {
        List<Producto> productos = Collections.singletonList(producto);
        when(productoServicio.listarProductos()).thenReturn(productos);

        List<Producto> result = productoControlador.obtenerProductos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoServicio, times(1)).listarProductos();
    }

    @Test
    public void testAgregarProducto() {
        when(productoServicio.guardarProducto(any(Producto.class))).thenReturn(producto);

        Producto result = productoControlador.agregarProducto(producto);

        assertNotNull(result);
        assertEquals(1, result.getIdProducto());
        assertEquals("Descripcion 1", result.getDescripcion());
        assertEquals(100.0, result.getPrecio());
        assertEquals(10, result.getExistencia());
        verify(productoServicio, times(1)).guardarProducto(any(Producto.class));
    }

    @Test
    public void testObtenerProductoPorId() {
        when(productoServicio.buscarProductoPorId(1)).thenReturn(producto);

        ResponseEntity<Producto> result = productoControlador.obtenerProductoPorId(1);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Producto returnedProducto = result.getBody();
        assertNotNull(returnedProducto);
        assertEquals(producto.getIdProducto(), returnedProducto.getIdProducto());
        assertEquals(producto.getDescripcion(), returnedProducto.getDescripcion());
        assertEquals(producto.getPrecio(), returnedProducto.getPrecio());
        assertEquals(producto.getExistencia(), returnedProducto.getExistencia());

        // Mostrar el producto por consola
        System.out.println(returnedProducto);

        verify(productoServicio, times(1)).buscarProductoPorId(1);
    }

    @Test
    public void testObtenerProductoPorIdNotFound() {
        when(productoServicio.buscarProductoPorId(1)).thenReturn(null);

        Exception exception = assertThrows(RecursoNoEncontradoExcepcion.class, () -> {
            productoControlador.obtenerProductoPorId(1);
        });

        assertEquals("No se encontró en id 1", exception.getMessage());
        verify(productoServicio, times(1)).buscarProductoPorId(1);
    }

    @Test
    public void testActualizarProducto() {
        // Datos originales
        Producto productoOriginal = new Producto();
        productoOriginal.setIdProducto(1);
        productoOriginal.setDescripcion("Producto Original");
        productoOriginal.setPrecio(150.0);
        productoOriginal.setExistencia(5);

        // Datos actualizados
        Producto productoActualizado = new Producto();
        productoActualizado.setIdProducto(1);
        productoActualizado.setDescripcion("Producto Actualizado");
        productoActualizado.setPrecio(200.0);
        productoActualizado.setExistencia(20);

        when(productoServicio.buscarProductoPorId(1)).thenReturn(productoOriginal);
        when(productoServicio.guardarProducto(any(Producto.class))).thenReturn(productoActualizado);

        // Imprimir datos originales
        System.out.println("Producto original: " + productoOriginal);

        ResponseEntity<Producto> result = productoControlador.actualizarProducto(1, productoActualizado);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Producto returnedProducto = result.getBody();
        assertNotNull(returnedProducto);
        assertEquals(productoActualizado.getIdProducto(), returnedProducto.getIdProducto());
        assertEquals(productoActualizado.getDescripcion(), returnedProducto.getDescripcion());
        assertEquals(productoActualizado.getPrecio(), returnedProducto.getPrecio());
        assertEquals(productoActualizado.getExistencia(), returnedProducto.getExistencia());

        // Imprimir datos actualizados
        System.out.println("Producto actualizado: " + returnedProducto);

        verify(productoServicio, times(1)).buscarProductoPorId(1);
        verify(productoServicio, times(1)).guardarProducto(any(Producto.class));
    }

    @Test
    public void testActualizarProductoNotFound() {
        when(productoServicio.buscarProductoPorId(1)).thenReturn(null);

        Exception exception = assertThrows(RecursoNoEncontradoExcepcion.class, () -> {
            productoControlador.actualizarProducto(1, producto);
        });

        assertEquals("No se encontró el id 1", exception.getMessage());
        verify(productoServicio, times(1)).buscarProductoPorId(1);
    }

    @Test
    public void testEliminarProducto() {
        when(productoServicio.buscarProductoPorId(1)).thenReturn(producto);
        doNothing().when(productoServicio).eliminarProductoPorId(1);

        ResponseEntity<Map<String, Boolean>> result = productoControlador.eliminarProducto(1);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).get("eliminado"));
        verify(productoServicio, times(1)).buscarProductoPorId(1);
        verify(productoServicio, times(1)).eliminarProductoPorId(1);
    }

    @Test
    public void testEliminarProductoNotFound() {
        when(productoServicio.buscarProductoPorId(1)).thenReturn(null);

        Exception exception = assertThrows(RecursoNoEncontradoExcepcion.class, () -> {
            productoControlador.eliminarProducto(1);
        });

        assertEquals("No se encontró el id 1", exception.getMessage());
        verify(productoServicio, times(1)).buscarProductoPorId(1);
    }
}
